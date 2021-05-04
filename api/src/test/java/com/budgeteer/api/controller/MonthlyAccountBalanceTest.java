package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.account.AccountBalanceDto;
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
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
@Tag("Integration")
public class MonthlyAccountBalanceTest {

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
        Entry additionalTestEntry = TestUtils.createTestEntry();
        User testUser = userRepository.save(TestUtils.createSecureTestUser());
        User secondTestUser = userRepository.save(TestUtils.createAdditionalTestUser());
        Category testCategory = categoryRepository.save(TestUtils.createTestCategory(testUser));
        testAccount = TestUtils.createTestAccount(testUser);
        testAccount = accountRepository.save(testAccount);
        testEntry.setCategory(testCategory);
        testEntry.setAccount(testAccount);
        testEntry.setUser(testUser);
        entryRepository.save(testEntry);
        additionalTestEntry.setCategory(testCategory);
        additionalTestEntry.setAccount(testAccount);
        additionalTestEntry.setUser(secondTestUser);
        additionalTestEntry.setIsExpense(false);
        additionalTestEntry.setValue(BigDecimal.valueOf(1.60));
        entryRepository.save(additionalTestEntry);
    }

    @Test
    public void shouldGetMonthlyBalances() {
        HttpRequest<Object> request = HttpRequest.GET("/entries/currentMonth?accountId=" + testAccount.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<AccountBalanceDto> response = client.toBlocking().exchange(request, AccountBalanceDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        AccountBalanceDto body = response.getBody().get();
        assertEquals(BigDecimal.valueOf(1.39), body.getExpenses());
        assertEquals(new BigDecimal("1.60"), body.getIncome());
        assertEquals(BigDecimal.valueOf(0.21), body.getNet());

    }

}
