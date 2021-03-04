package com.budgeteer.api.imports;

import com.budgeteer.api.dto.imports.CsvImportRequest;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MicronautTest
@ExtendWith(MockitoExtension.class)
public class CsvImporterTest {

    @Mock
    private CompletedFileUpload mockUpload;

    @Test
    public void testDataParsingWithOnlyNecessaryFields() throws IOException, ParseException {
        String testString = "name,value,date"
                + "\nPotato,10.561,2020-01-30"
                + "\nCarrot,12345.61,2020-05-30";
        Map<String, String> mapping = new HashMap<>();
        mapping.put("name", "name");
        mapping.put("value", "value");
        mapping.put("date", "date");
        when(mockUpload.getInputStream()).thenReturn(new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8)));
        CsvImporter csvImporter = new CsvImporter();
        ImportResult result = csvImporter.getData(new CsvImporterData(mapping, mockUpload));
        assertEquals(2, result.getImportedEntries().size());
        assertEquals(2, result.getTotalLines());
        ImportEntry importEntry = result.getImportedEntries().get(0);
        assertNotNull(importEntry);
        assertEquals("Potato", importEntry.getName());
        assertEquals(0, new BigDecimal("10.561").compareTo(importEntry.getValue()));
        assertEquals("2020-01-30", importEntry.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        assertNull(importEntry.getDescription());
        assertNull(importEntry.getCategory());
        assertNull(importEntry.getExpense());
    }

    @Test
    public void testDataParsingWithDescriptionField() throws IOException, ParseException {
        String testString = "name,value,date,desc"
                + "\nPotato,10.561,2020-01-30,Just a potato"
                + "\nCarrot,12345.61,2020-05-30,Just a carrot";
        Map<String, String> mapping = new HashMap<>();
        mapping.put("name", "name");
        mapping.put("value", "value");
        mapping.put("date", "date");
        mapping.put("desc", "description");
        when(mockUpload.getInputStream()).thenReturn(new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8)));
        CsvImporter csvImporter = new CsvImporter();
        ImportResult result = csvImporter.getData(new CsvImporterData(mapping, mockUpload));
        assertEquals(2, result.getImportedEntries().size());
        assertEquals(2, result.getTotalLines());
        ImportEntry importEntry = result.getImportedEntries().get(0);
        assertNotNull(importEntry);
        assertEquals("Potato", importEntry.getName());
        assertEquals(0, new BigDecimal("10.561").compareTo(importEntry.getValue()));
        assertEquals("2020-01-30", importEntry.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        assertNotNull(importEntry.getDescription());
    }

    @Test
    public void testDataWithEmptyFields() throws IOException, ParseException {
        String testString = "name,value,date,desc"
                + "\nPotato,10.561,Just a potato"
                + "\nCarrot,12345.61,2020-05-30,Just a carrot"
                + "\n"
                + "\nCarrot,12345.61,2020-05-30,Just a carrot";
        Map<String, String> mapping = new HashMap<>();
        mapping.put("name", "name");
        mapping.put("value", "value");
        mapping.put("date", "date");
        mapping.put("desc", "description");
        when(mockUpload.getInputStream()).thenReturn(new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8)));
        CsvImporter csvImporter = new CsvImporter();
        ImportResult result = csvImporter.getData(new CsvImporterData(mapping, mockUpload));
        assertEquals(2, result.getImportedEntries().size());
        assertEquals(4, result.getTotalLines());
    }

    @Test
    public void shouldThrowExceptionWhenNoNameHeaderGiven() {
        CsvImporter importer = new CsvImporter();
        CsvImportRequest importRequest = getRequest();
        importRequest.setNameHeader("");
        UnsatisfiedRouteException e = assertThrows(UnsatisfiedRouteException.class, () ->
                importer.validateRequest(importRequest));
        assertEquals("nameHeader", e.getArgument().getName());
    }

    @Test
    public void shouldThrowExceptionWhenNoDateHeaderGiven() {
        CsvImporter importer = new CsvImporter();
        CsvImportRequest importRequest = getRequest();
        importRequest.setDateHeader("");
        UnsatisfiedRouteException e = assertThrows(UnsatisfiedRouteException.class, () ->
                importer.validateRequest(importRequest));
        assertEquals("dateHeader", e.getArgument().getName());
    }

    @Test
    public void shouldThrowExceptionWhenNoValueHeaderGiven() {
        CsvImporter importer = new CsvImporter();
        CsvImportRequest importRequest = getRequest();
        importRequest.setValueHeader("");
        UnsatisfiedRouteException e = assertThrows(UnsatisfiedRouteException.class, () ->
                importer.validateRequest(importRequest));
        assertEquals("valueHeader", e.getArgument().getName());
    }

    @Test
    public void shouldThrowExceptionWhenNoAccountGiven() {
        CsvImporter importer = new CsvImporter();
        CsvImportRequest importRequest = getRequest();
        importRequest.setAccountId(null);
        UnsatisfiedRouteException e = assertThrows(UnsatisfiedRouteException.class, () ->
                importer.validateRequest(importRequest));
        assertEquals("accountId", e.getArgument().getName());
    }

    private CsvImportRequest getRequest() {
        CsvImportRequest request = new CsvImportRequest();
        request.setNameHeader("name");
        request.setDateHeader("date");
        request.setValueHeader("value");
        request.setAccountId(5L);
        return request;
    }
}
