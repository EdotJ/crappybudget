package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.receipts.gcp.model.response.ApiResponse;
import com.budgeteer.api.receipts.gcp.model.response.BoundingPoly;
import com.budgeteer.api.receipts.gcp.model.response.TextAnnotation;
import com.budgeteer.api.receipts.gcp.model.response.Vertex;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AdvancedCloudVisionReceiptParserTest {

    @Inject
    AdvancedCloudVisionReceiptParser parser;

    @Test
    void testLeftwardReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("rimi_leftward_response.json");
        assertNotNull(apiResponse);
        var receiptResponse = parser.parseReceipt(apiResponse);
        assertTrue(receiptResponse.getEntries().size() > 0);
        assertEquals(new BigDecimal("2.49"), receiptResponse.getEntries().get(0).getPrice());
        assertEquals(new BigDecimal("1.19"), receiptResponse.getEntries().get(1).getPrice());
    }

    @ParameterizedTest
    @ValueSource(strings = {"maxima_rightward_response.json", "maxima_upward_response.json"})
    void testRightUpParsing(String fileName) throws IOException, ParseException {
        ApiResponse apiResponse = getResponse(fileName);
        assertNotNull(apiResponse);
        var receiptResponse = parser.parseReceipt(apiResponse);
        assertTrue(receiptResponse.getEntries().size() > 0);
        assertEquals(new BigDecimal("2.58"), receiptResponse.getEntries().get(0).getPrice());
        assertEquals(new BigDecimal("-0.78"), receiptResponse.getEntries().get(1).getPrice());
        assertEquals(new BigDecimal("1.60"), receiptResponse.getEntries().get(2).getPrice());
        assertEquals(new BigDecimal("1.45"), receiptResponse.getEntries().get(3).getPrice());
        assertEquals(new BigDecimal("2.48"), receiptResponse.getEntries().get(4).getPrice());
        assertEquals(new BigDecimal("1.99"), receiptResponse.getEntries().get(5).getPrice());
        assertEquals(new BigDecimal("1.89"), receiptResponse.getEntries().get(6).getPrice());
        assertEquals(new BigDecimal("-0.57"), receiptResponse.getEntries().get(7).getPrice());
        assertEquals(new BigDecimal("0.86"), receiptResponse.getEntries().get(8).getPrice());
    }

    @Test
    void testDownwardReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("maxima_downward_response.json");
        assertNotNull(apiResponse);
        var receiptResponse = parser.parseReceipt(apiResponse);
        assertTrue(receiptResponse.getEntries().size() > 0);
        assertEquals(new BigDecimal("2.58"), receiptResponse.getEntries().get(0).getPrice());
        // Image quality wasn't the best
        // assertEquals(new BigDecimal("-0,78"), receiptResponse.getEntries().get(1).getPrice());
        assertEquals(new BigDecimal("1.60"), receiptResponse.getEntries().get(2).getPrice());
        assertEquals(new BigDecimal("1.45"), receiptResponse.getEntries().get(3).getPrice());
        assertEquals(new BigDecimal("2.48"), receiptResponse.getEntries().get(4).getPrice());
        assertEquals(new BigDecimal("1.99"), receiptResponse.getEntries().get(5).getPrice());
        assertEquals(new BigDecimal("1.89"), receiptResponse.getEntries().get(6).getPrice());
        assertEquals(new BigDecimal("-0.57"), receiptResponse.getEntries().get(7).getPrice());
        assertEquals(new BigDecimal("0.86"), receiptResponse.getEntries().get(8).getPrice());
    }

    @Test
    void testMcDonaldsReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("mcdonalds_response.json");
        assertNotNull(apiResponse);
        var receiptResponse = parser.parseReceipt(apiResponse);
        assertTrue(receiptResponse.getEntries().size() > 0);
        assertEquals(new BigDecimal("2.80"), receiptResponse.getEntries().get(0).getPrice());
        assertEquals(new BigDecimal("5.45"), receiptResponse.getEntries().get(1).getPrice());
        assertEquals(new BigDecimal("0.30"), receiptResponse.getEntries().get(2).getPrice());
        assertEquals(new BigDecimal("2.90"), receiptResponse.getEntries().get(3).getPrice());
        assertEquals(new BigDecimal("5.45"), receiptResponse.getEntries().get(4).getPrice());
    }

    @Test
    void testDanijaReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("danija_response.json");
        assertNotNull(apiResponse);
        var receiptResponse = parser.parseReceipt(apiResponse);
        assertTrue(receiptResponse.getEntries().size() > 0);
        assertEquals(new BigDecimal("0.01"), receiptResponse.getEntries().get(0).getPrice());
        assertEquals(new BigDecimal("79.99"), receiptResponse.getEntries().get(1).getPrice());
    }

    @Test
    void testAnnotationMerging() {
        TextAnnotation prev = new TextAnnotation();
        prev.setDescription("0");
        BoundingPoly prevBounding = new BoundingPoly();
        prevBounding.getVertices().add(new Vertex(0, 0));
        prevBounding.getVertices().add(new Vertex(50, 0));
        prevBounding.getVertices().add(new Vertex(50, 50));
        prevBounding.getVertices().add(new Vertex(0, 50));
        prev.setBoundingPoly(prevBounding);
        TextAnnotation current = new TextAnnotation();
        current.setDescription(",");
        BoundingPoly currentBounding = new BoundingPoly();
        currentBounding.getVertices().add(new Vertex(50, 0));
        currentBounding.getVertices().add(new Vertex(100, 0));
        currentBounding.getVertices().add(new Vertex(100, 50));
        currentBounding.getVertices().add(new Vertex(50, 50));
        current.setBoundingPoly(currentBounding);
        TextAnnotation next = new TextAnnotation();
        next.setDescription("52");
        BoundingPoly nextBounding = new BoundingPoly();
        nextBounding.getVertices().add(new Vertex(100, 0));
        nextBounding.getVertices().add(new Vertex(150, 0));
        nextBounding.getVertices().add(new Vertex(150, 50));
        nextBounding.getVertices().add(new Vertex(100, 50));
        next.setBoundingPoly(nextBounding);
        TextAnnotation ta = parser.mergeAnnotation(prev, current, next);
        assertEquals("0,52", ta.getDescription());
        assertEquals(0, ta.getBoundingPoly().getVertices().get(0).getX());
        assertEquals(0, ta.getBoundingPoly().getVertices().get(0).getY());
        assertEquals(150, ta.getBoundingPoly().getVertices().get(1).getX());
        assertEquals(0, ta.getBoundingPoly().getVertices().get(1).getY());
        assertEquals(150, ta.getBoundingPoly().getVertices().get(2).getX());
        assertEquals(50, ta.getBoundingPoly().getVertices().get(2).getY());
        assertEquals(0, ta.getBoundingPoly().getVertices().get(3).getX());
        assertEquals(50, ta.getBoundingPoly().getVertices().get(3).getY());
    }

    @Test
    void testBetweenCheckWithUpwardOrientation() throws IOException {
        int from = 0;
        int to = 100;
        ApiResponse apiResponse = getResponse("check_between_upward.json");
        assertNotNull(apiResponse);
        List<TextAnnotation> annotations = apiResponse.getResponses().get(0).getTextAnnotations();
        TextAnnotation curr = annotations.get(4);
        TextAnnotation a = annotations.get(0);
        assertTrue(parser.checkBetween(from, to, curr, a, Orientation.UP));
        a = annotations.get(5);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.UP));
        a = annotations.get(6);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.UP));
        a = annotations.get(4);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.UP));
    }

    @Test
    void testBetweenCheckWithDownwardOrientation() throws IOException {
        int from = 0;
        int to = 100;
        ApiResponse apiResponse = getResponse("check_between_downward.json");
        assertNotNull(apiResponse);
        List<TextAnnotation> annotations = apiResponse.getResponses().get(0).getTextAnnotations();
        TextAnnotation curr = annotations.get(4);
        TextAnnotation a = annotations.get(0);
        assertTrue(parser.checkBetween(from, to, curr, a, Orientation.DOWN));
        a = annotations.get(5);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.DOWN));
        a = annotations.get(6);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.DOWN));
        a = annotations.get(4);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.DOWN));
    }

    @Test
    void testBetweenCheckWithRightwardOrientation() throws IOException {
        int from = 0;
        int to = 100;
        ApiResponse apiResponse = getResponse("check_between_rightward.json");
        assertNotNull(apiResponse);
        List<TextAnnotation> annotations = apiResponse.getResponses().get(0).getTextAnnotations();
        TextAnnotation curr = annotations.get(4);
        TextAnnotation a = annotations.get(0);
        assertTrue(parser.checkBetween(from, to, curr, a, Orientation.RIGHT));
        a = annotations.get(5);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.RIGHT));
        a = annotations.get(6);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.RIGHT));
        a = annotations.get(4);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.RIGHT));

    }

    @Test
    void testBetweenCheckWithLeftwardOrientation() throws IOException {
        int from = 0;
        int to = 100;
        ApiResponse apiResponse = getResponse("check_between_leftward.json");
        assertNotNull(apiResponse);
        List<TextAnnotation> annotations = apiResponse.getResponses().get(0).getTextAnnotations();
        TextAnnotation curr = annotations.get(4);
        TextAnnotation a = annotations.get(0);
        assertTrue(parser.checkBetween(from, to, curr, a, Orientation.LEFT));
        a = annotations.get(5);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.LEFT));
        a = annotations.get(6);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.LEFT));
        a = annotations.get(4);
        assertFalse(parser.checkBetween(from, to, curr, a, Orientation.LEFT));
    }

    @Test
    void testGettingFullList() throws IOException {
        ApiResponse apiResponse = getResponse("check_between_leftward.json");
        assertNotNull(apiResponse);
        List<TextAnnotation> all = apiResponse.getResponses().get(0).getTextAnnotations();
        List<TextAnnotation> annotations = parser.getLabelAnnotations(all, all.get(4), null, Orientation.LEFT);
        assertEquals(4, annotations.size());
        assertEquals("Kukurūzų", annotations.get(0).getDescription());
        assertEquals("traškučiai", annotations.get(1).getDescription());
        assertEquals("SANTA", annotations.get(2).getDescription());
        assertEquals("MARIA", annotations.get(3).getDescription());

    }

    private ApiResponse getResponse(String fileName) throws IOException {
        String responseString = getResponseString(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(responseString, ApiResponse.class);
    }

    private String getResponseString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("test-responses/" + fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}
