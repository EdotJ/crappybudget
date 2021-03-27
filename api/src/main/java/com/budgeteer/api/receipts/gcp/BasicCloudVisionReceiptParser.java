package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.core.Pair;
import com.budgeteer.api.receipts.OnlineReceiptParser;
import com.budgeteer.api.receipts.gcp.model.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Singleton
public class BasicCloudVisionReceiptParser implements OnlineReceiptParser<InputStream, ApiResponse, List<Object>> {

    Logger logger = LoggerFactory.getLogger(BasicCloudVisionReceiptParser.class);

    @Inject
    ObjectMapper objectMapper;

    List<String> possibleTotalStrings = List.of("Mokėti", "Moketi", "Viso", "Iš viso", "Mokėjimas", "Sumokėti");

    private final Pattern pricePattern = Pattern.compile("-?\\d+(,|.)\\d+");

    private final CloudVisionClient client;

    public BasicCloudVisionReceiptParser(CloudVisionClient cloudVisionClient) {
        this.client = cloudVisionClient;
    }

    @Override
    public ApiResponse makeRequest(InputStream receipt) throws IOException {
        return client.getAnnotatedImage(receipt);
    }

    @Override
    public List<Object> parseReceipt(ApiResponse request) throws ParseException {
        if (request.getResponses().get(0).getTextAnnotations() == null || request.getResponses().get(0).getTextAnnotations().size() < 3) {
            throw new ParseException("Did not get annotations from Cloud Vision", 0);
        }
        // Safe to take the first receipt since we only allow single file upload
        Response response = request.getResponses().get(0);
        Orientation orientation = getOrientation(response.getTextAnnotations().get(2));
        TextAnnotation title = findTotalPriceTitleAnnotation(response.getTextAnnotations());
        TextAnnotation price = findTotalPriceAnnotation(title, response.getTextAnnotations(), orientation);

        if (logger.isDebugEnabled()) {
            try {
                logger.debug(objectMapper.writeValueAsString(title));
                logger.debug(objectMapper.writeValueAsString(price));
            } catch (Exception e) {
                logger.error("Failed marshalling value for logging");
            }
        }

        return List.of(title, price);
    }

    private TextAnnotation findTotalPriceTitleAnnotation(List<TextAnnotation> allAnnotations) {
        int minLength = possibleTotalStrings.stream().map(String::length).min(Comparator.naturalOrder()).get();
        int maxLength = possibleTotalStrings.stream().map(String::length).max(Comparator.naturalOrder()).get();
        TextAnnotation closestMatch = allAnnotations.get(0);
        String foundWordMatch = "";
        LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
        for (int i = 1; i < allAnnotations.size(); i++) {
            TextAnnotation textAnnotation = allAnnotations.get(i);
            if (textAnnotation.getDescription().length() >= minLength &&
                    textAnnotation.getDescription().length() <= maxLength) {
                for (String word : possibleTotalStrings) {
                    int distance = levenshtein.apply(textAnnotation.getDescription(), word);
                    if (distance <= levenshtein.apply(closestMatch.getDescription(), foundWordMatch)) {
                        closestMatch = textAnnotation;
                        foundWordMatch = word;
                    }
                }
            }
        }
        return closestMatch;
    }

    protected Orientation getOrientation(TextAnnotation textAnnotation) {
        BoundingPoly boundingPoly = textAnnotation.getBoundingPoly();
        List<Vertex> vertices = boundingPoly.getVertices();
        Pair<Double, Double> centerCoords = getCenterVertex(vertices);

        return determineOrientation(centerCoords, vertices.get(0));
    }

    private Pair<Double, Double> getCenterVertex(List<Vertex> vertices) {
        double centerX = 0;
        double centerY = 0;
        for (Vertex v : vertices) {
            centerX += v.getX();
            centerY += v.getY();
        }
        centerX = centerX / 4;
        centerY = centerY / 4;
        return new Pair<>(centerX, centerY);
    }

    private Orientation determineOrientation(Pair<Double, Double> centerCoords, Vertex vertex) {
        if (vertex.getX() < centerCoords.getFirst()) {
            if (vertex.getY() < centerCoords.getSecond()) {
                return Orientation.UP;
            } else {
                return Orientation.LEFT;
            }
        } else {
            if (vertex.getY() < centerCoords.getSecond()) {
                return Orientation.RIGHT;
            } else {
                return Orientation.DOWN;
            }
        }
    }


    private TextAnnotation findTotalPriceAnnotation(TextAnnotation title,
                                                    List<TextAnnotation> textAnnotations,
                                                    Orientation orientation) throws ParseException {
        List<Vertex> vertices = title.getBoundingPoly().getVertices();
        TextAnnotation priceAnnotation = null;
        int currentDistance = Integer.MAX_VALUE;
        for (TextAnnotation potentialPrice : textAnnotations) {
            if (!pricePattern.matcher(potentialPrice.getDescription()).matches()) {
                continue;
            }
            List<Vertex> annotationVertices = potentialPrice.getBoundingPoly().getVertices();
            Pair<TextAnnotation, Integer> pair = null;
            if (matchesVertical(orientation, vertices, annotationVertices)) {
                pair = getDistance(potentialPrice, title, currentDistance, false);
            } else if (matchesHorizontal(orientation, vertices, annotationVertices)) {
                pair = getDistance(potentialPrice, title, currentDistance, true);
            }
            if (pair != null && pair.getFirst() != title) {
                priceAnnotation = pair.getFirst();
                currentDistance = pair.getSecond();
            }
        }
        if (priceAnnotation == null) {
            throw new ParseException("Failed parsing receipt. Could not find total price", 0);
        }
        return priceAnnotation;
    }

    /**
     * Checks to see if the vertices are actually on the different sides when orientation is vertical (up / down)
     */
    private boolean matchesVertical(Orientation orientation, List<Vertex> titleVertices, List<Vertex> priceVertices) {
        return (orientation.equals(Orientation.UP) && priceVertices.get(1).getX() > titleVertices.get(1).getX())
                || (orientation.equals(Orientation.DOWN) && priceVertices.get(1).getX() < titleVertices.get(1).getX());
    }

    /**
     * Checks to see if the vertices are actually on the different sides when orientation is horizontal (left / right)
     */
    private boolean matchesHorizontal(Orientation orientation, List<Vertex> titleVertices, List<Vertex> priceVertices) {
        return (orientation.equals(Orientation.RIGHT) && priceVertices.get(1).getY() > titleVertices.get(1).getY())
                || (orientation.equals(Orientation.LEFT) && priceVertices.get(1).getY() < titleVertices.get(1).getY());

    }

    private Pair<TextAnnotation, Integer> getDistance(TextAnnotation ta1,
                                                      TextAnnotation ta2,
                                                      int currentDistance,
                                                      boolean horizontal) {
        int distance;
        List<Vertex> vertices1 = ta1.getBoundingPoly().getVertices();
        List<Vertex> vertices2 = ta2.getBoundingPoly().getVertices();
        if (horizontal) {
            distance = Math.abs(vertices1.get(0).getX() - vertices2.get(0).getX());
            distance += Math.abs(vertices1.get(3).getX() - vertices2.get(3).getX());
        } else {
            distance = Math.abs(vertices1.get(0).getY() - vertices2.get(0).getY());
            distance += Math.abs(vertices1.get(3).getY() - vertices2.get(3).getY());
        }
        if (distance < currentDistance) {
            return new Pair<>(ta1, distance);
        }

        return new Pair<>(ta2, currentDistance);
    }

    protected enum Orientation {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
}
