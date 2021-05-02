package com.budgeteer.api.imports;

import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.imports.CsvImportRequest;
import com.budgeteer.api.imports.csv.CsvImporter;
import com.budgeteer.api.imports.csv.CsvImporterData;
import com.budgeteer.api.imports.csv.ImportResult;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.service.CategoryService;
import com.budgeteer.api.service.EntryService;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MicronautTest
@ExtendWith(MockitoExtension.class)
@Tag("Unit")
public class CsvImporterTest {

    @Mock
    private CompletedFileUpload mockUpload;

    @Inject
    EntryService entryService;

    @Inject
    SecurityService securityService;

    @Inject
    CategoryService categoryService;

    @Inject
    CsvImporter importer;

    @BeforeEach
    public void setup() {
        when(securityService.getAuthentication()).thenReturn(Optional.of(new Authentication() {
            @NotNull
            @Override
            public Map<String, Object> getAttributes() {
                return Map.of("id", 1L);
            }

            @Override
            public String getName() {
                return TestUtils.TEST_USER_USERNAME;
            }
        }));
    }

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
        ImportResult result = importer.getData(new CsvImporterData(mapping, mockUpload));
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
        ImportResult result = importer.getData(new CsvImporterData(mapping, mockUpload));
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
        ImportResult result = importer.getData(new CsvImporterData(mapping, mockUpload));
        assertEquals(2, result.getImportedEntries().size());
        assertEquals(4, result.getTotalLines());
    }

    @Test
    public void shouldThrowExceptionWhenNoNameHeaderGiven() {
        CsvImportRequest importRequest = getRequest();
        importRequest.setNameHeader("");
        UnsatisfiedRouteException e = assertThrows(UnsatisfiedRouteException.class, () ->
                importer.validateRequest(importRequest));
        assertEquals("nameHeader", e.getArgument().getName());
    }

    @Test
    public void shouldThrowExceptionWhenNoDateHeaderGiven() {
        CsvImportRequest importRequest = getRequest();
        importRequest.setDateHeader("");
        UnsatisfiedRouteException e = assertThrows(UnsatisfiedRouteException.class, () ->
                importer.validateRequest(importRequest));
        assertEquals("dateHeader", e.getArgument().getName());
    }

    @Test
    public void shouldThrowExceptionWhenNoValueHeaderGiven() {
        CsvImportRequest importRequest = getRequest();
        importRequest.setValueHeader("");
        UnsatisfiedRouteException e = assertThrows(UnsatisfiedRouteException.class, () ->
                importer.validateRequest(importRequest));
        assertEquals("valueHeader", e.getArgument().getName());
    }

    @Test
    public void shouldThrowExceptionWhenNoAccountGiven() {
        CsvImportRequest importRequest = getRequest();
        importRequest.setAccountId(null);
        UnsatisfiedRouteException e = assertThrows(UnsatisfiedRouteException.class, () ->
                importer.validateRequest(importRequest));
        assertEquals("accountId", e.getArgument().getName());
    }

    @Test
    public void testParsing() {
        Category category = TestUtils.createTestCategory();
        Account account = TestUtils.createTestAccount();
        when(categoryService.getAll(eq(1L))).thenReturn(List.of(category));
        ImportEntry testEntry = new ImportEntry("Bread",
                "A loaf of bread",
                BigDecimal.valueOf(10.00),
                LocalDate.of(2020, 10, 10),
                true,
                "Example Category");
        List<ImportEntry> importEntries = List.of(testEntry);
        importer.parse(importEntries, account, true);
        verify(categoryService, times(0)).create(any());
        verify(entryService, times(1)).saveAllEntries(any());
    }

    private CsvImportRequest getRequest() {
        CsvImportRequest request = new CsvImportRequest();
        request.setNameHeader("name");
        request.setDateHeader("date");
        request.setValueHeader("value");
        request.setAccountId(5L);
        return request;
    }

    @MockBean(SecurityService.class)
    SecurityService securityService() {
        return mock(SecurityService.class);
    }

    @MockBean(EntryService.class)
    EntryService emailService() {
        return mock(EntryService.class);
    }

    @MockBean(CategoryService.class)
    CategoryService categoryService() {
        return mock(CategoryService.class);
    }
}
