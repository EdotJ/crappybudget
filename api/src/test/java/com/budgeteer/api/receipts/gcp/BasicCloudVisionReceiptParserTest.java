package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.receipts.gcp.BasicCloudVisionReceiptParser;
import com.budgeteer.api.receipts.gcp.model.response.ApiResponse;
import com.budgeteer.api.receipts.gcp.model.response.BoundingPoly;
import com.budgeteer.api.receipts.gcp.model.response.TextAnnotation;
import com.budgeteer.api.receipts.gcp.model.response.Vertex;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
public class BasicCloudVisionReceiptParserTest {

    @Inject
    BasicCloudVisionReceiptParser parser;

    @Test
    void testLeftwardReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("rimi_leftward_response.json");
        assertNotNull(apiResponse);
        List<Object> parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals("Moketi", ((TextAnnotation) parsedResponse.get(0)).getDescription());
        TextAnnotation textAnnotation = (TextAnnotation) parsedResponse.get(1);
        assertEquals("4,38", textAnnotation.getDescription());
    }

    @Test
    void testRightwardReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("maxima_rightward_response.json");
        assertNotNull(apiResponse);
        List<Object> parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals("Moketi", ((TextAnnotation) parsedResponse.get(0)).getDescription());
        TextAnnotation textAnnotation = (TextAnnotation) parsedResponse.get(1);
        assertEquals("11,00", textAnnotation.getDescription());
    }

    @Test
    void testDownwardReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("maxima_downward_response.json");
        assertNotNull(apiResponse);
        List<Object> parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals("Moketi", ((TextAnnotation) parsedResponse.get(0)).getDescription());
        TextAnnotation textAnnotation = (TextAnnotation) parsedResponse.get(1);
        assertEquals("11,00", textAnnotation.getDescription());
    }

    @Test
    void testUpwardReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("maxima_upward_response.json");
        assertNotNull(apiResponse);
        List<Object> parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals("Moketi", ((TextAnnotation) parsedResponse.get(0)).getDescription());
        TextAnnotation textAnnotation = (TextAnnotation) parsedResponse.get(1);
        assertEquals("11,00", textAnnotation.getDescription());
    }

    @Test
    void testMcDonaldsReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("mcdonalds_response.json");
        assertNotNull(apiResponse);
        List<Object> parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals("Moketi", ((TextAnnotation) parsedResponse.get(0)).getDescription());
        TextAnnotation textAnnotation = (TextAnnotation) parsedResponse.get(1);
        assertEquals("16,90", textAnnotation.getDescription());
    }

    @Test
    void testDanijaReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("danija_response.json");
        assertNotNull(apiResponse);
        List<Object> parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals("Moketi", ((TextAnnotation) parsedResponse.get(0)).getDescription());
        TextAnnotation textAnnotation = (TextAnnotation) parsedResponse.get(1);
        assertEquals("80,00", textAnnotation.getDescription());
    }

    @Test
    void testOrientationShouldReturnRightward() {
        TextAnnotation textAnnotation = new TextAnnotation();
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(new Vertex(1010, 621));
        boundingPoly.getVertices().add(new Vertex(1017, 792));
        boundingPoly.getVertices().add(new Vertex(946, 795));
        boundingPoly.getVertices().add(new Vertex(939, 624));
        textAnnotation.setBoundingPoly(boundingPoly);
        BasicCloudVisionReceiptParser.Orientation orientation = parser.getOrientation(textAnnotation);
        assertEquals(BasicCloudVisionReceiptParser.Orientation.RIGHT, orientation);
    }

    @Test
    void testOrientationShouldReturnUpward() {
        TextAnnotation textAnnotation = new TextAnnotation();
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(new Vertex(148, 2673));
        boundingPoly.getVertices().add(new Vertex(326, 2673));
        boundingPoly.getVertices().add(new Vertex(326, 2752));
        boundingPoly.getVertices().add(new Vertex(148, 2752));
        textAnnotation.setBoundingPoly(boundingPoly);
        BasicCloudVisionReceiptParser.Orientation orientation = parser.getOrientation(textAnnotation);
        assertEquals(BasicCloudVisionReceiptParser.Orientation.UP, orientation);
    }

    @Test
    void testOrientationShouldReturnDownward() {
        TextAnnotation textAnnotation = new TextAnnotation();
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(new Vertex(2401, 1008));
        boundingPoly.getVertices().add(new Vertex(2227, 1011));
        boundingPoly.getVertices().add(new Vertex(2226, 945));
        boundingPoly.getVertices().add(new Vertex(2400, 942));
        textAnnotation.setBoundingPoly(boundingPoly);
        BasicCloudVisionReceiptParser.Orientation orientation = parser.getOrientation(textAnnotation);
        assertEquals(BasicCloudVisionReceiptParser.Orientation.DOWN, orientation);
    }

    @Test
    void testOrientationShouldReturnLeftward() {
        TextAnnotation textAnnotation = new TextAnnotation();
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(new Vertex(2840, 2134));
        boundingPoly.getVertices().add(new Vertex(2839, 1957));
        boundingPoly.getVertices().add(new Vertex(2897, 1957));
        boundingPoly.getVertices().add(new Vertex(2898, 2134));
        textAnnotation.setBoundingPoly(boundingPoly);
        BasicCloudVisionReceiptParser.Orientation orientation = parser.getOrientation(textAnnotation);
        assertEquals(BasicCloudVisionReceiptParser.Orientation.LEFT, orientation);
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
