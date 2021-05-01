package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.core.Pair;
import com.budgeteer.api.dto.receipts.ReceiptParseResponse;
import com.budgeteer.api.receipts.OnlineReceiptParser;
import com.budgeteer.api.receipts.gcp.model.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Singleton
@Named("BasicCloudVision")
public class BasicCloudVisionReceiptParser
        implements OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> {

    private final Logger logger = LoggerFactory.getLogger(BasicCloudVisionReceiptParser.class);

    @Inject
    ObjectMapper objectMapper;

    List<String> possibleTotalStrings = List.of("Mokėti", "Moketi", "Viso", "Iš viso", "Mokėjimas", "Sumokėti",
            "VISO MOKETI", "VISO MOKĖTI", "IŠ VISO", "IS VISO");

    private final Pattern pricePattern = Pattern.compile("-?\\d+(,|.)\\d+");

    private final Pattern datePattern = Pattern.compile("([12]\\d{3}[.-](0[1-9]|1[0-2])[.-](0[1-9]|[12]\\d|3[01]))");

    private final CloudVisionClient client;

    private final ParserUtils parserUtils;

    public BasicCloudVisionReceiptParser(CloudVisionClient cloudVisionClient, ParserUtils parserUtils) {
        this.client = cloudVisionClient;
        this.parserUtils = parserUtils;
    }

    @Override
    public ApiResponse makeRequest(InputStream receipt) throws IOException {
        return client.getImageAnnotations(receipt);
    }

    @Override
    public ReceiptParseResponse parseReceipt(ApiResponse request) throws ParseException {
        if (request.getResponses().get(0).getTextAnnotations() == null
                || request.getResponses().get(0).getTextAnnotations().size() < 3) {
            throw new ParseException("Did not get annotations from Cloud Vision", 0);
        }
        // Safe to take the first receipt since we only allow single file upload
        Response response = request.getResponses().get(0);
        Orientation orientation = parserUtils.getOrientation(response.getTextAnnotations().get(2));
        List<TextAnnotation> title = findTotalPriceTitleAnnotation(response.getTextAnnotations());
        var titlePricePair = findTotalPriceAnnotation(title, response.getTextAnnotations(), orientation);
        Optional<LocalDate> date = findDateAnnotation(response.getTextAnnotations());

        if (logger.isDebugEnabled()) {
            try {
                logger.debug(objectMapper.writeValueAsString(title));
                logger.debug(objectMapper.writeValueAsString(titlePricePair));
                logger.debug(objectMapper.writeValueAsString(date));
            } catch (Exception e) {
                logger.error("Failed marshalling value for logging");
            }
        }
        ReceiptParseResponse generatedResponse = new ReceiptParseResponse();
        generatedResponse.setTotal(new BigDecimal(titlePricePair.getSecond().getDescription().replace(",", ".")));
        date.ifPresent(localDate -> generatedResponse.setDate(localDate.format(DateTimeFormatter.ISO_DATE)));
        return generatedResponse;
    }

    private List<TextAnnotation> findTotalPriceTitleAnnotation(List<TextAnnotation> allAnnotations) {
        int minLength = possibleTotalStrings.stream().map(String::length).min(Comparator.naturalOrder()).get();
        int maxLength = possibleTotalStrings.stream().map(String::length).max(Comparator.naturalOrder()).get();
        TextAnnotation closestMatch = allAnnotations.get(0);
        String foundWordMatch = "";
        LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
        List<TextAnnotation> matches = new ArrayList<>();
        for (int i = 1; i < allAnnotations.size(); i++) {
            TextAnnotation textAnnotation = allAnnotations.get(i);
            if (textAnnotation.getDescription().length() >= minLength
                    && textAnnotation.getDescription().length() <= maxLength) {
                for (String word : possibleTotalStrings) {
                    String[] words = word.split(" ");
                    String label = textAnnotation.getDescription();
                    if (words.length > 1 && i > words.length) {
                        StringBuilder sb = new StringBuilder();
                        for (int j = i - words.length + 1; j <= i; j++) {
                            sb.append(allAnnotations.get(j).getDescription());
                            if (j < i) {
                                sb.append(" ");
                            }
                        }
                        label = sb.toString();
                    }
                    int distance = levenshtein.apply(label, word);
                    if (distance <= levenshtein.apply(closestMatch.getDescription(), foundWordMatch)) {
                        if (distance == 0) {
                            matches.add(textAnnotation);
                        }
                        closestMatch = textAnnotation;
                        foundWordMatch = word;
                    }
                }
            }
        }
        return matches.size() > 0 ? matches : List.of(closestMatch);
    }

    private Pair<TextAnnotation, TextAnnotation> findTotalPriceAnnotation(List<TextAnnotation> titles,
                                                                      List<TextAnnotation> textAnnotations,
                                                                      Orientation orientation) throws ParseException {
        List<Pair<TextAnnotation, TextAnnotation>> pricesByTitle = new ArrayList<>();
        for (TextAnnotation title : titles) {
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
            pricesByTitle.add(new Pair<>(title, priceAnnotation));
        }
        Optional<Pair<TextAnnotation, TextAnnotation>> textAnnotationOpt = pricesByTitle.stream()
                .max((p1, p2) -> comparePriceAnnotations(p1.getSecond(), p2.getSecond()));
        if (textAnnotationOpt.isPresent()) {
            return textAnnotationOpt.get();
        }
        throw new ParseException("Could not get total price", 0);
    }

    private int comparePriceAnnotations(TextAnnotation a1, TextAnnotation a2) {
        BigDecimal price1 = new BigDecimal(a1.getDescription().replace(",", "."));
        BigDecimal price2 = new BigDecimal(a2.getDescription().replace(",", "."));
        return price1.compareTo(price2);
    }

    /**
     * Checks to see if the vertices are actually on the different sides when orientation is vertical (up / down).
     */
    private boolean matchesVertical(Orientation orientation, List<Vertex> titleVertices, List<Vertex> priceVertices) {
        return (orientation.equals(Orientation.UP) && priceVertices.get(1).getX() > titleVertices.get(1).getX())
                || (orientation.equals(Orientation.DOWN) && priceVertices.get(1).getX() < titleVertices.get(1).getX());
    }

    /**
     * Checks to see if the vertices are actually on the different sides when orientation is horizontal (left / right).
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

    private Optional<LocalDate> findDateAnnotation(List<TextAnnotation> textAnnotations) {
        List<LocalDate> localDates = new ArrayList<>();
        for (TextAnnotation ta : textAnnotations) {
            if (datePattern.matcher(ta.getDescription()).matches()) {
                localDates.add(LocalDate.parse(ta.getDescription().replace(".", "-")));
            }
        }
        return localDates.stream().min(Comparator.naturalOrder());
    }
}
