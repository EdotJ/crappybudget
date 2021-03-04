package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.imports.CsvImportRequest;
import com.budgeteer.api.dto.imports.CsvImportResponse;
import com.budgeteer.api.imports.CsvImporter;
import com.budgeteer.api.imports.CsvImporterData;
import com.budgeteer.api.imports.ImportResult;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.security.RestrictedResourceHandler;
import com.budgeteer.api.service.AccountService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.utils.SecurityService;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Controller("${api.base-path}/imports")
public class ImportController extends RestrictedResourceHandler {

    private final CsvImporter csvImporter;
    private final AccountService accountService;

    public ImportController(CsvImporter importer, AccountService accountService, SecurityService securityService) {
        super(securityService);
        csvImporter = importer;
        this.accountService = accountService;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<CsvImportResponse> importEntries(CsvImportRequest importRequest, CompletedFileUpload file)
            throws ParseException {
        csvImporter.validateRequest(importRequest);
        Account acc = accountService.getSingle(importRequest.getAccountId());
        checkIfCanAccessResource(acc.getUser());
        Map<String, String> mappings = constructCsvMappings(importRequest);
        CsvImporterData importerData = new CsvImporterData(mappings, file);
        ImportResult importResult = csvImporter.getData(importerData);
        csvImporter.parse(importResult.getImportedEntries(), acc, importRequest.isAddUnknownCategory());
        CsvImportResponse res = new CsvImportResponse(importResult.getTotalLines(), importResult.getSkippedLines());
        return HttpResponse.ok(res);
    }

    private Map<String, String> constructCsvMappings(CsvImportRequest importRequest) {
        Map<String, String> mappings = new HashMap<>();
        mappings.put(importRequest.getNameHeader(), "name");
        mappings.put(importRequest.getDateHeader(), "date");
        mappings.put(importRequest.getValueHeader(), "value");
        if (importRequest.getDescriptionHeader() != null) {
            mappings.put(importRequest.getDescriptionHeader(), "description");
        }
        if (importRequest.getCategoryHeader() != null) {
            mappings.put(importRequest.getCategoryHeader(), "category");
        }
        if (importRequest.getIsExpenseHeader() != null) {
            mappings.put(importRequest.getIsExpenseHeader(), "isExpense");
        }
        return mappings;
    }

}
