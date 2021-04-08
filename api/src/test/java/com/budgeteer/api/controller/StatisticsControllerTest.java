package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.statistics.CategoryBreakdown;
import com.budgeteer.api.dto.statistics.TopExpenses;
import com.budgeteer.api.dto.statistics.YearlyBreakdown;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class StatisticsControllerTest {

    @RegisterExtension
    AuthenticationExtension authExtension = new AuthenticationExtension();

    @Inject
    @Client(value = "/${api.base-path}", errorType = ErrorResponse.class)
    private RxHttpClient client;

    @Inject
    UserRepository userRepository;

    @Inject
    AccountRepository accountRepository;

    @Inject
    EntryRepository entryRepository;

    @Inject
    CategoryRepository categoryRepository;

    private User testUser;
    private Account testAccount;
    private Category testCategory;

    @BeforeEach
    public void setup() {
        testUser = userRepository.save(TestUtils.createSecureTestUser());
        testCategory = categoryRepository.save(TestUtils.createTestCategory(testUser));
        testAccount = TestUtils.createTestAccount();
        testAccount.setUser(testUser);
        testAccount = accountRepository.save(testAccount);
        saveEntryWithValue(new BigDecimal("2.00"));
        saveEntryWithValue(new BigDecimal("1.00"));
        saveEntryWithValue(new BigDecimal("10.00"));
    }

    @Test
    public void shouldReturnThirteenForTestCategory() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/statistics/category-breakdown")
                .headers(authExtension.getAuthHeader());
        HttpResponse<CategoryBreakdown> response = client.toBlocking().exchange(httpRequest, CategoryBreakdown.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        CategoryBreakdown body = response.getBody().get();
        assertEquals(1, body.getCategories().size());
        CategoryBreakdown.Entry entry = body.getCategories().get(0);
        assertNotNull(entry);
        assertEquals(testCategory.getId(), entry.getCategoryId());
        assertNull(entry.getParentId());
        assertEquals(new BigDecimal("13.00"), entry.getValue());
    }

    @Test
    public void shouldReturnZeroesInTotal() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/statistics/category-breakdown?year=2019")
                .headers(authExtension.getAuthHeader());
        HttpResponse<CategoryBreakdown> response = client.toBlocking().exchange(httpRequest, CategoryBreakdown.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        CategoryBreakdown body = response.getBody().get();
        assertEquals(1, body.getCategories().size());
        CategoryBreakdown.Entry entry = body.getCategories().get(0);
        assertNotNull(entry);
        assertEquals(testCategory.getId(), entry.getCategoryId());
        assertNull(entry.getParentId());
        assertEquals(new BigDecimal("0.00"), entry.getValue());
        assertEquals(new BigDecimal("0.00"), body.getTotal());

    }

    @Test
    public void shouldReturnFirstThreeExpenses() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/statistics/top-expenses")
                .headers(authExtension.getAuthHeader());
        HttpResponse<TopExpenses> response = client.toBlocking().exchange(httpRequest, TopExpenses.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        TopExpenses body = response.getBody().get();
        assertEquals(3, body.getEntries().size());
        TopExpenses.Entry entry = body.getEntries().get(0);
        assertNotNull(entry);
        assertEquals(testAccount.getName(), entry.getAccount());
        assertEquals(new BigDecimal("10.00"), entry.getValue());
        entry = body.getEntries().get(1);
        assertNotNull(entry);
        assertEquals(testAccount.getName(), entry.getAccount());
        assertEquals(new BigDecimal("2.00"), entry.getValue());
        entry = body.getEntries().get(2);
        assertNotNull(entry);
        assertEquals(testAccount.getName(), entry.getAccount());
        assertEquals(new BigDecimal("1.00"), entry.getValue());
    }

    @Test
    public void shouldReturnNoExpenses() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/statistics/top-expenses?year=2019")
                .headers(authExtension.getAuthHeader());
        HttpResponse<TopExpenses> response = client.toBlocking().exchange(httpRequest, TopExpenses.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        TopExpenses body = response.getBody().get();
        assertEquals(0, body.getEntries().size());
    }

    @Test
    public void shouldReturnYearlyBreakdown() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/statistics/yearly-breakdown")
                .headers(authExtension.getAuthHeader());
        HttpResponse<YearlyBreakdown> response = client.toBlocking().exchange(httpRequest, YearlyBreakdown.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        YearlyBreakdown body = response.getBody().get();
        assertEquals(1, body.getEntries().size());
        assertEquals(LocalDate.now().getMonthValue(), body.getEntries().get(0).getMonth());
        assertEquals(new BigDecimal("0.00"), body.getEntries().get(0).getIncome());
        assertEquals(new BigDecimal("13.00"), body.getEntries().get(0).getExpenses());
    }

    @Test
    public void shouldReturnNoBreakdown() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/statistics/yearly-breakdown?year=2019")
                .headers(authExtension.getAuthHeader());
        HttpResponse<YearlyBreakdown> response = client.toBlocking().exchange(httpRequest, YearlyBreakdown.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        YearlyBreakdown body = response.getBody().get();
        assertEquals(0, body.getEntries().size());
    }

    private void saveEntryWithValue(BigDecimal value) {
        Entry entry = TestUtils.createTestEntry();
        entry.setValue(value);
        entry.setIsExpense(true);
        entry.setCategory(testCategory);
        entry.setAccount(testAccount);
        entry.setUser(testUser);
        entryRepository.save(entry);
    }

}
