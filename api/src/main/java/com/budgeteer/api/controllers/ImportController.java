package com.budgeteer.api.controllers;

import com.budgeteer.api.dto.imports.CsvImportRequest;
import com.budgeteer.api.dto.imports.CsvImportResponse;
import com.budgeteer.api.dto.imports.YnabImportRequest;
import com.budgeteer.api.imports.csv.CsvImporter;
import com.budgeteer.api.imports.csv.CsvImporterData;
import com.budgeteer.api.imports.csv.ImportResult;
import com.budgeteer.api.imports.ynab.YnabImporter;
import com.budgeteer.api.imports.ynab.YnabImporterData;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.security.RestrictedResourceHandler;
import com.budgeteer.api.service.AccountService;
import com.budgeteer.api.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.utils.SecurityService;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("${api.base-path}/imports")
public class ImportController extends RestrictedResourceHandler {

    private final CsvImporter csvImporter;
    private final YnabImporter ynabImporter;
    private final AccountService accountService;
    private final UserService userService;

    public ImportController(CsvImporter csvImporter,
                            AccountService accountService,
                            SecurityService securityService,
                            YnabImporter ynabImporter,
                            UserService userService) {
        super(securityService);
        this.csvImporter = csvImporter;
        this.ynabImporter = ynabImporter;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Post(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA)
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

    @Post(value = "/ynab")
    public HttpResponse<Object> importEntries(@Body YnabImportRequest request)
            throws JsonProcessingException {
        ynabImporter.validateRequest(request);
        YnabImporterData data = ynabImporter.getData(request.getPersonalToken());
        Optional<Authentication> auth = securityService.getAuthentication();

        if (auth.isEmpty() || auth.get().getAttributes().get("id") == null) {
            throw new AuthorizationException(securityService.getAuthentication().orElse(null));
        }
        ynabImporter.parse(data, userService.getById((Long) auth.get().getAttributes().get("id")));
        return HttpResponse.ok();
    }

    protected Map<String, String> constructCsvMappings(CsvImportRequest importRequest) {
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
