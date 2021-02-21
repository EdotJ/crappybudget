package com.budgeteer.api.controller;

import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.entry.EntryListDto;
import com.budgeteer.api.dto.entry.SingleEntryDto;
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
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class EntryControllerTest {

    @Inject
    UserRepository userRepository;

    @Inject
    AccountRepository accountRepository;

    @Inject
    EntryRepository entryRepository;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    @Client(value = "/${api.base-path}", errorType = ErrorResponse.class)
    RxHttpClient client;

    private User testUser;
    private Account testAccount;
    private Category testCategory;
    private Entry testEntry;

    @BeforeEach
    public void setup() {
        testEntry = TestUtils.createTestEntry();
        testUser = userRepository.save(TestUtils.createTestUser());
        testCategory = categoryRepository.save(TestUtils.createTestCategory(testUser));
        testAccount = accountRepository.save(TestUtils.createTestAccount(testUser));
        testEntry.setCategory(testCategory);
        testEntry.setAccount(testAccount);
        testEntry.setUser(testUser);
        entryRepository.save(testEntry);
    }

    @Test
    public void shouldReturnListOfOneEntryForUser() {
        HttpResponse<EntryListDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/entries?userId=" + testUser.getId()), EntryListDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnListOfOneEntryForAccount() {
        HttpResponse<EntryListDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/entries?accountId=" + testAccount.getId()), EntryListDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnSingleEntry() {
        HttpResponse<SingleEntryDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/entries/" + testEntry.getId()), SingleEntryDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals("Milk", response.getBody().get().getName());
    }

    @Test
    public void shouldReturnNotFound() {
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.GET("/entries/123456789"), SingleEntryDto.class));
        assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("NOT_FOUND", errorResponse.getCode());
    }

    @Test
    public void shouldCreateEntry() {
        SingleEntryDto dto = createEntry();
        HttpResponse<SingleEntryDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        SingleEntryDto responseDto = response.getBody().get();
        assertEquals(dto.getName(), responseDto.getName());
        assertEquals(dto.getDate(), responseDto.getDate());
        assertEquals(dto.getValue(), responseDto.getValue());
        assertEquals(dto.getUserId(), responseDto.getUserId());
        assertEquals(dto.getAccountId(), responseDto.getAccountId());
    }

    private SingleEntryDto createEntry() {
        SingleEntryDto dto = new SingleEntryDto();
        dto.setName("Test Entry");
        dto.setValue(BigDecimal.valueOf(10));
        dto.setDate(LocalDate.parse("2021-05-30"));
        dto.setIsExpense(true);
        dto.setAccountId(testAccount.getId());
        dto.setUserId(testUser.getId());
        dto.setCategoryId(testCategory.getId());
        return dto;
    }

    @Test
    public void shouldFailWhenAccountNotFound() {
        SingleEntryDto dto = createEntry();
        dto.setAccountId(123456789L);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("NOT_FOUND", errorResponse.getCode());
        assertTrue(errorResponse.getMessage().contains("account"));
    }

    @Test
    @Disabled("User is taken from selected account")
    public void shouldFailWhenUserNotFound() {
        SingleEntryDto dto = createEntry();
        dto.setUserId(123456789L);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("NOT_FOUND", errorResponse.getCode());
        assertTrue(errorResponse.getMessage().contains("user"));
    }

    @Test
    public void shouldFailWhenCategoryNotFound() {
        SingleEntryDto dto = createEntry();
        dto.setCategoryId(123456789L);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("NOT_FOUND", errorResponse.getCode());
        assertTrue(errorResponse.getMessage().contains("category"));
    }

    @Test
    public void shouldFailWhenNoNameAdded() {
        SingleEntryDto dto = createEntry();
        dto.setName("");
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_ENTRY_NAME", errorResponse.getCode());
    }

    @Test
    public void shouldFailWhenNoValueAdded() {
        SingleEntryDto dto = createEntry();
        dto.setValue(null);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_ENTRY_VALUE", errorResponse.getCode());
    }

    @Test
    public void shouldFailWhenNegativeValue() {
        SingleEntryDto dto = createEntry();
        dto.setValue(BigDecimal.valueOf(-1617));
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_ENTRY_VALUE", errorResponse.getCode());
    }

    @Test
    public void shouldFailWhenNoDateAdded() {
        SingleEntryDto dto = createEntry();
        dto.setDate(null);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_ENTRY_DATE", errorResponse.getCode());
    }

    @Test
    public void shouldFailWhenNoCategoryAdded() {
        SingleEntryDto dto = createEntry();
        dto.setCategoryId(null);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_ENTRY", errorResponse.getCode());
    }

    @Test
    public void shouldFailWhenNoAccountAdded() {
        SingleEntryDto dto = createEntry();
        dto.setAccountId(null);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/entries", dto), SingleEntryDto.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_ENTRY", errorResponse.getCode());
    }
}
