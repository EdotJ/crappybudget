package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.Entry;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.AccountRepository;
import com.budgeteer.api.repository.CategoryRepository;
import com.budgeteer.api.repository.EntryRepository;
import com.budgeteer.api.repository.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
@Tag("Integration")
public class ExportControllerTest {

    @RegisterExtension
    AuthenticationExtension authExtension = new AuthenticationExtension();

    @Inject
    UserRepository userRepository;

    @Inject
    AccountRepository accountRepository;

    @Inject
    EntryRepository entryRepository;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    @Client(value = "/api", errorType = ErrorResponse.class)
    RxHttpClient client;

    private Account testAccount;

    @BeforeEach
    public void setup() {
        Entry testEntry = TestUtils.createTestEntry();
        User testUser = userRepository.save(TestUtils.createSecureTestUser());
        Category testCategory = categoryRepository.save(TestUtils.createTestCategory(testUser));
        testAccount = TestUtils.createTestAccount(testUser);
        testAccount.setName("Test Account 2");
        testAccount = accountRepository.save(testAccount);
        testEntry.setCategory(testCategory);
        testEntry.setAccount(testAccount);
        testEntry.setUser(testUser);
        entryRepository.save(testEntry);
        testAccount = accountRepository.save(TestUtils.createTestAccount(testUser));
        testEntry = TestUtils.createTestEntry();
        testEntry.setCategory(testCategory);
        testEntry.setAccount(testAccount);
        testEntry.setUser(testUser);
        entryRepository.save(testEntry);
    }

    @Test
    public void shouldGetTwoExportedEntriesForUser() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/export")
                .headers(authExtension.getAuthHeader());
        HttpResponse<String> response = client.toBlocking().exchange(httpRequest, String.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertTrue(response.getBody().get().contains("\"date\",\"name\",\"value\",\"category\",\"account\""));
        String testString = LocalDate.now() + "\",\"\"\"Milk\"\"\",\"1.39\",\"\"\"Example Category\"\"\",\"\"\"Test Account\"\"\"";
        assertTrue(response.getBody().get().contains(testString));
        String bodyString = response.getBody().get();
        int substringCount = bodyString.replace("\n", "\nx").length() - bodyString.length();
        assertEquals(3, substringCount);
    }

    @Test
    public void shouldGetOneExportedEntryForAccount() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/export?fetchId=" + testAccount.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<String> response = client.toBlocking().exchange(httpRequest, String.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertTrue(response.getBody().get().contains("\"date\",\"name\",\"value\",\"category\",\"account\""));
        String testString = LocalDate.now() + "\",\"\"\"Milk\"\"\",\"1.39\",\"\"\"Example Category\"\"\",\"\"\"Test Account\"\"\"";
        assertTrue(response.getBody().get().contains(testString));
        String bodyString = response.getBody().get();
        int substringCount = bodyString.replace("\n", "\nx").length() - bodyString.length();
        assertEquals(2, substringCount);
    }
}
