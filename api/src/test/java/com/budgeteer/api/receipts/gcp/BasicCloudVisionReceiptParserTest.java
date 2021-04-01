package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.dto.receipts.ReceiptParseResponse;
import com.budgeteer.api.receipts.gcp.model.response.ApiResponse;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class BasicCloudVisionReceiptParserTest {

    @Inject
    BasicCloudVisionReceiptParser parser;

    @Test
    void testLeftwardReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("rimi_leftward_response.json");
        assertNotNull(apiResponse);
        ReceiptParseResponse parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals(new BigDecimal("4.38"), parsedResponse.getTotal());
        assertEquals("2021-03-08", parsedResponse.getDate());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "maxima_rightward_response.json",
            "maxima_downward_response.json",
            "maxima_upward_crumpled_response.json"})
    void testMaximaResponseParsing(String fileName) throws IOException, ParseException {
        ApiResponse apiResponse = getResponse(fileName);
        assertNotNull(apiResponse);
        ReceiptParseResponse parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals(new BigDecimal("11.00"), parsedResponse.getTotal());
        assertNull(parsedResponse.getDate());
    }

    @Test
    void testMcDonaldsReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("mcdonalds_response.json");
        assertNotNull(apiResponse);
        ReceiptParseResponse parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals(new BigDecimal("16.90"), parsedResponse.getTotal());
        assertNull(parsedResponse.getDate());
    }

    @Test
    void testDanijaReceiptParsing() throws IOException, ParseException {
        ApiResponse apiResponse = getResponse("danija_response.json");
        assertNotNull(apiResponse);
        ReceiptParseResponse parsedResponse = parser.parseReceipt(apiResponse);
        assertEquals(new BigDecimal("80.00"), parsedResponse.getTotal());
        assertEquals("2020-09-04", parsedResponse.getDate());
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
