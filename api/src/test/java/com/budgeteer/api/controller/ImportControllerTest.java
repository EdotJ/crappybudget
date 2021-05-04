package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.controllers.ImportController;
import com.budgeteer.api.dto.imports.CsvImportRequest;
import com.budgeteer.api.dto.imports.CsvImportResponse;
import com.budgeteer.api.dto.imports.YnabImportRequest;
import com.budgeteer.api.imports.ImportEntry;
import com.budgeteer.api.imports.csv.CsvImporter;
import com.budgeteer.api.imports.csv.CsvImporterData;
import com.budgeteer.api.imports.csv.ImportResult;
import com.budgeteer.api.imports.ynab.YnabImporter;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.EntryRepository;
import com.budgeteer.api.repository.UserRepository;
import com.budgeteer.api.security.IdentifierUserDetails;
import com.budgeteer.api.security.PasswordAuthenticationProvider;
import com.budgeteer.api.service.AccountService;
import com.budgeteer.api.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
@Tag("Integration")
public class ImportControllerTest {

    @RegisterExtension
    AuthenticationExtension authExtension = new AuthenticationExtension();

    @Inject
    CsvImporter csvImporter;

    @Inject
    YnabImporter ynabImporter;

    @Inject
    AccountService accountService;

    @Inject
    UserService userService;

    @Inject
    SecurityService securityService;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordAuthenticationProvider passwordAuthenticationProvider;

    @Inject
    EntryRepository entryRepository;

    private ImportController controller;

    @BeforeEach
    public void setUp() throws ParseException {
        User testUser = userRepository.save(TestUtils.createSecureTestUser());
        when(userService.getByUsername(any())).thenCallRealMethod();
        IdentifierUserDetails userDetails = new IdentifierUserDetails(
                TestUtils.TEST_USER_USERNAME,
                List.of("ROLE_VERIFIED"),
                testUser.getId());
        when(passwordAuthenticationProvider.authenticate(any(), any())).thenReturn(Flowable.just(userDetails));
        Account account = TestUtils.createTestAccount();
        account.setUser(testUser);
        when(accountService.getSingle(1L)).thenReturn(account);
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

        controller = new ImportController(csvImporter, accountService, securityService, ynabImporter, userService);
    }

    @Test
    public void testCsvImport() throws ParseException {
        ImportEntry mockEntry = new ImportEntry();
        mockEntry.setName("bread");
        mockEntry.setDate(LocalDate.of(2020, 12, 12));
        mockEntry.setValue(BigDecimal.valueOf(10.00));
        doAnswer(invocation -> {
            final Object[] args = invocation.getArguments();
            CsvImporterData data = (CsvImporterData) args[0];
            assertEquals("name", data.getMappings().get("testName"));
            assertEquals("date", data.getMappings().get("date"));
            assertEquals("value", data.getMappings().get("value"));
            return new ImportResult(List.of(mockEntry), 0);
        }).when(csvImporter).getData(any());

        CsvImportRequest request = new CsvImportRequest();
        request.setNameHeader("testName");
        request.setValueHeader("value");
        request.setDateHeader("date");
        request.setAccountId(1L);
        CompletedFileUpload fileUpload = mock(CompletedFileUpload.class);
        HttpResponse<CsvImportResponse> response = controller.importEntries(request, fileUpload);
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(csvImporter, times(1)).validateRequest(eq(request));
        verify(csvImporter, times(1)).getData(any());
        verify(accountService, times(1)).getSingle(eq(1L));
    }

    @Test
    public void testYnabImport() throws JsonProcessingException {
        YnabImportRequest request = new YnabImportRequest();
        request.setPersonalToken("token");
        HttpResponse<Object> response = controller.importEntries(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(ynabImporter, times(1)).validateRequest(any());
        verify(ynabImporter, times(1)).getData(eq("token"));
        verify(ynabImporter, times(1)).parse(any(), any());
    }

    @MockBean(CsvImporter.class)
    public CsvImporter csvImporter() {
        return mock(CsvImporter.class);
    }

    @MockBean(YnabImporter.class)
    public YnabImporter ynabImporter() {
        return mock(YnabImporter.class);
    }

    @MockBean(AccountService.class)
    public AccountService accountService() {
        return mock(AccountService.class);
    }

    @MockBean(UserService.class)
    public UserService userService() {
        return mock(UserService.class);
    }

    @MockBean(SecurityService.class)
    public SecurityService securityService() {
        return mock(SecurityService.class);
    }

    @MockBean(PasswordAuthenticationProvider.class)
    public PasswordAuthenticationProvider passwordAuthenticationProvider() {
        return mock(PasswordAuthenticationProvider.class);
    }
}
