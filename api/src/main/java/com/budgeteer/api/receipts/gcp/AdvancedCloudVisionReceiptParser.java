package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.core.Pair;
import com.budgeteer.api.dto.receipts.ReceiptParseEntry;
import com.budgeteer.api.dto.receipts.ReceiptParseResponse;
import com.budgeteer.api.receipts.OnlineReceiptParser;
import com.budgeteer.api.receipts.gcp.model.response.*;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
@Named("AdvancedCloudVision")
public class AdvancedCloudVisionReceiptParser
        implements OnlineReceiptParser<InputStream, ApiResponse, ReceiptParseResponse> {

    private static final int TOP_LEFT = 0;
    private static final int TOP_RIGHT = 1;
    private static final int BOT_RIGHT = 2;
    private static final int BOT_LEFT = 3;

    private static final int HEIGHT_THRESHOLD = 30;

    private final CloudVisionClient client;

    private final ParserUtils parserUtils;

    private final Pattern pricePattern = Pattern.compile("-?\\d+[,.]\\d{2}");
    private final Pattern digitsOnly = Pattern.compile("-?\\d+");
    private final Pattern positiveDigitsOnly = Pattern.compile("\\d{2}");
    private final Pattern sep = Pattern.compile("[,.]");

    private final BasicCloudVisionReceiptParser basicParser;

    private final List<String> endWords = List.of("MOKEJIMAS", "MOKĖJIMAS", "MOKETI",
            "MOKĖTI", "ATSISKAITYTA", "LOJALUMO", "VISO",
            "PREKIAUTOJO", "SUMA", "AKCIJU", "AKCIJŲ", "TARPINĖ", "21,0");

    public AdvancedCloudVisionReceiptParser(CloudVisionClient cloudVisionClient,
                                            ParserUtils parserUtils,
                                            BasicCloudVisionReceiptParser basicParser) {
        this.client = cloudVisionClient;
        this.parserUtils = parserUtils;
        this.basicParser = basicParser;
    }

    @Override
    public ApiResponse makeRequest(InputStream receipt) throws IOException {
        return this.client.getImageAnnotations(receipt);
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

        ReceiptParseResponse parseResponse = this.basicParser.parseReceipt(request);
        TextAnnotation vatTa = fixAnnotations(response.getTextAnnotations());
        List<TextAnnotation> prices = new ArrayList<>();
        prices.add(vatTa);
        for (TextAnnotation ta : response.getTextAnnotations()) {
            if (endWords.stream().anyMatch(e -> e.equalsIgnoreCase(ta.getDescription())) && prices.size() > 1) {
                break;
            }
            if (pricePattern.matcher(ta.getDescription()).matches()) {
                prices.add(ta);
            }
        }
        Pair<Integer, Integer> center = calculateCenter(response.getTextAnnotations().get(0));

        var pricesWithLabels = getPricesWithLabels(response.getTextAnnotations(), prices, orientation, center).listIterator();
        while (pricesWithLabels.hasNext()) {
            Pair<String, String> pair = pricesWithLabels.next();
            BigDecimal price = new BigDecimal(pair.getSecond().replace(",", "."));
            if (price.compareTo(BigDecimal.ZERO) < 0 && pricesWithLabels.hasPrevious()) {
                ReceiptParseEntry entry = parseResponse.getEntries().get(pricesWithLabels.previousIndex() - 1);
                entry.setPrice(entry.getPrice().subtract(price.abs()));
                pricesWithLabels.remove();
                continue;
            }
            parseResponse.getEntries().add(new ReceiptParseEntry(pair.getFirst().replaceAll("#", ""), price));
        }
        return parseResponse;
    }

    /**
     * Fixes annotations that have been unnecessarily broken out.
     *
     * @param textAnnotations all annotations
     * @return annotations with merged pricing info
     */
    private TextAnnotation fixAnnotations(List<TextAnnotation> textAnnotations) {
        ListIterator<TextAnnotation> iterator = textAnnotations.listIterator();
        TextAnnotation previous;
        TextAnnotation current;
        TextAnnotation next;
        TextAnnotation vat = null;
        while (iterator.hasNext()) {
            if (!iterator.hasPrevious()) {
                iterator.next();
            }
            previous = iterator.previous();
            iterator.next();
            current = iterator.next();
            if (!iterator.hasNext()) {
                break;
            }
            next = iterator.next();
            iterator.previous();
            iterator.previous();
            boolean isPrice = digitsOnly.matcher(previous.getDescription()).matches()
                    && sep.matcher(current.getDescription()).matches()
                    && positiveDigitsOnly.matcher(next.getDescription()).matches();
            boolean isVat = previous.getDescription().equalsIgnoreCase("PVM")
                    && (current.getDescription().equalsIgnoreCase("mokėtojo")
                    || current.getDescription().equalsIgnoreCase("moketojo"))
                    && next.getDescription().equalsIgnoreCase("kodas");
            if (!isPrice && !isVat) {
                iterator.next();
                continue;
            }
            // Replace all 3 elements with a single joined element
            iterator.previous();
            iterator.remove();
            iterator.next();
            iterator.remove();
            iterator.next();
            iterator.remove();
            if (isVat) {
                vat = mergeAnnotation(previous, current, next);
                iterator.add(vat);
            } else {
                iterator.add(mergeAnnotation(previous, current, next));
            }
            iterator.next();
        }
        return vat;
    }

    protected TextAnnotation mergeAnnotation(TextAnnotation previous, TextAnnotation current, TextAnnotation next) {
        TextAnnotation annotation = new TextAnnotation();
        if (previous.getDescription().equalsIgnoreCase("PVM")) {
            annotation.setDescription(previous.getDescription()
                    + " " + current.getDescription()
                    + " " + next.getDescription());
        } else {
            annotation.setDescription(previous.getDescription() + current.getDescription() + next.getDescription());
        }
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(previous.getBoundingPoly().getVertices().get(TOP_LEFT));
        boundingPoly.getVertices().add(next.getBoundingPoly().getVertices().get(TOP_RIGHT));
        boundingPoly.getVertices().add(next.getBoundingPoly().getVertices().get(BOT_RIGHT));
        boundingPoly.getVertices().add(previous.getBoundingPoly().getVertices().get(BOT_LEFT));
        annotation.setBoundingPoly(boundingPoly);
        return annotation;
    }

    private Pair<Integer, Integer> calculateCenter(TextAnnotation annotation) {
        List<Vertex> vertices = annotation.getBoundingPoly().getVertices();
        int verticalCenter = (vertices.get(TOP_LEFT).getY() + vertices.get(BOT_LEFT).getY()) / 2;
        int horizontalCenter = (vertices.get(TOP_LEFT).getX() + vertices.get(TOP_RIGHT).getX()) / 2;
        return new Pair<>(horizontalCenter, verticalCenter);
    }

    private List<Pair<String, String>> getPricesWithLabels(List<TextAnnotation> allAnnotations,
                                                           List<TextAnnotation> priceAnnotations,
                                                           Orientation orientation,
                                                           Pair<Integer, Integer> center) {
        int centerCoord = orientation.isVertical() ? center.getFirst() : center.getSecond();
        // keep first annotation for VAT
        List<TextAnnotation> rightAnnotations = priceAnnotations.stream()
                .filter(a -> (a == null || (a.getDescription().contains("PVM") && a.equals(priceAnnotations.get(0)))
                        || isRightOfCenter(a, centerCoord, orientation)))
                .collect(Collectors.toList());
        List<Pair<String, String>> pricesWithLabels = new ArrayList<>();
        double avgDistance = 0;
        double sum = 0;
        for (int i = 1; i < rightAnnotations.size(); i++) {
            TextAnnotation prev = rightAnnotations.get(i - 1);
            TextAnnotation current = rightAnnotations.get(i);

            int diff;
            if (prev != null) {
                List<Vertex> prevVertices = prev.getBoundingPoly().getVertices();
                List<Vertex> currVertices = current.getBoundingPoly().getVertices();
                diff = orientation.isVertical()
                        ? Math.abs(prevVertices.get(BOT_LEFT).getY() - currVertices.get(TOP_LEFT).getY())
                        : Math.abs(prevVertices.get(BOT_LEFT).getX() - currVertices.get(TOP_LEFT).getX());
                if (i != 1 && (diff > avgDistance && diff > getLineHeight(orientation, currVertices, 2))) {
                    continue;
                }
            } else {
                List<Vertex> currVertices = current.getBoundingPoly().getVertices();

                diff = getLineHeight(orientation, currVertices, 2);
            }

            List<TextAnnotation> textAnnotations = getLabelAnnotations(allAnnotations, current, prev, orientation);
            String label = textAnnotations.stream()
                    .map(TextAnnotation::getDescription)
                    .collect(Collectors.joining(" "))
                    .replace("PVM moketojo kodas", "")
                    .replace("PVM mokėtojo kodas", "")
                    .replace("Kvitas", "");
            pricesWithLabels.add(new Pair<>(label, current.getDescription()));

            sum += diff;
            avgDistance = sum / i;
        }

        return pricesWithLabels;
    }

    private int getLineHeight(Orientation orientation, List<Vertex> currVertices, int lineCount) {
        return orientation.isVertical()
                ? Math.abs(currVertices.get(BOT_LEFT).getY() - currVertices.get(TOP_LEFT).getY() * lineCount)
                : Math.abs(currVertices.get(BOT_LEFT).getX() - currVertices.get(TOP_LEFT).getX() * lineCount);
    }

    private boolean isRightOfCenter(TextAnnotation a, int avgCoord, Orientation orientation) {
        if (orientation.equals(Orientation.UP)) {
            return a.getBoundingPoly().getVertices().get(TOP_RIGHT).getX() > avgCoord + avgCoord * 0.2;
        } else if (orientation.equals(Orientation.DOWN)) {
            return a.getBoundingPoly().getVertices().get(TOP_RIGHT).getX() < avgCoord - avgCoord * 0.2;
        } else if (orientation.equals(Orientation.RIGHT)) {
            return a.getBoundingPoly().getVertices().get(TOP_RIGHT).getY() > avgCoord + avgCoord * 0.2;
        } else {
            return a.getBoundingPoly().getVertices().get(TOP_RIGHT).getY() < avgCoord - avgCoord * 0.2;
        }
    }

    protected List<TextAnnotation> getLabelAnnotations(List<TextAnnotation> allAnnotations,
                                                       TextAnnotation current,
                                                       TextAnnotation previous,
                                                       Orientation orientation) {
        var currentVertices = current.getBoundingPoly().getVertices();
        int from;
        int to;
        if (previous == null) {
            if (orientation.isVertical()) {
                from = Math.min(currentVertices.get(TOP_LEFT).getY(), currentVertices.get(BOT_LEFT).getY());
                to = Math.max(currentVertices.get(TOP_LEFT).getY(), currentVertices.get(BOT_LEFT).getY());
            } else {
                from = Math.min(currentVertices.get(TOP_LEFT).getX(), currentVertices.get(BOT_LEFT).getX());
                to = Math.max(currentVertices.get(TOP_LEFT).getX(), currentVertices.get(BOT_LEFT).getX());
            }
        } else {
            if (orientation.isVertical()) {
                from = previous.getBoundingPoly().getVertices().get(BOT_LEFT).getY();
                to = currentVertices.get(BOT_LEFT).getY();
            } else {
                from = previous.getBoundingPoly().getVertices().get(BOT_LEFT).getX();
                to = currentVertices.get(BOT_LEFT).getX();
            }
        }
        if (from > to) {
            int temp = from;
            from = to;
            to = temp;
        }
        if (previous == null) {
            from = from * 2;
        }
        return findBetween(from, to, current, allAnnotations, orientation);
    }

    private List<TextAnnotation> findBetween(int from,
                                             int to,
                                             TextAnnotation curr,
                                             List<TextAnnotation> annotations,
                                             Orientation o) {
        return annotations.stream().filter(a -> checkBetween(from, to, curr, a, o)).collect(Collectors.toList());
    }

    protected boolean checkBetween(int from, int to, TextAnnotation curr, TextAnnotation a, Orientation o) {
        return getMinVertex(a, o) >= from - HEIGHT_THRESHOLD
                && getMaxVertex(a, o) <= to + HEIGHT_THRESHOLD
                && isBeside(curr, a, o);
    }

    private int getMinVertex(TextAnnotation a, Orientation o) {
        List<Vertex> vertices = a.getBoundingPoly().getVertices();
        return o.isVertical()
                ? Math.min(vertices.get(TOP_LEFT).getY(), vertices.get(BOT_LEFT).getY())
                : Math.min(vertices.get(TOP_LEFT).getX(), vertices.get(BOT_LEFT).getX());
    }

    private int getMaxVertex(TextAnnotation a, Orientation o) {
        List<Vertex> vertices = a.getBoundingPoly().getVertices();
        return o.isVertical()
                ? Math.max(vertices.get(TOP_LEFT).getY(), vertices.get(BOT_LEFT).getY())
                : Math.max(vertices.get(TOP_LEFT).getX(), vertices.get(BOT_LEFT).getX());
    }

    private boolean isBeside(TextAnnotation price, TextAnnotation label, Orientation o) {
        List<Vertex> priceVertices = price.getBoundingPoly().getVertices();
        List<Vertex> labelVertices = label.getBoundingPoly().getVertices();
        if (o.equals(Orientation.UP)) {
            return priceVertices.get(TOP_LEFT).getX() > labelVertices.get(TOP_RIGHT).getX();
        } else if (o.equals(Orientation.RIGHT)) {
            return priceVertices.get(TOP_LEFT).getY() > labelVertices.get(TOP_RIGHT).getY();
        } else if (o.equals(Orientation.DOWN)) {
            return priceVertices.get(TOP_LEFT).getX() < labelVertices.get(TOP_RIGHT).getX();
        }
        return priceVertices.get(TOP_LEFT).getY() < labelVertices.get(TOP_RIGHT).getY();
    }

}
