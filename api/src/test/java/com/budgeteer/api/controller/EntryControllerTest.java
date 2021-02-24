package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
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
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class EntryControllerTest {

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

    private User testUser;
    private Account testAccount;
    private Category testCategory;
    private Entry testEntry;

    @BeforeEach
    public void setup() {
        testEntry = TestUtils.createTestEntry();
        testUser = userRepository.save(TestUtils.createSecureTestUser());
        testCategory = categoryRepository.save(TestUtils.createTestCategory(testUser));
        testAccount = accountRepository.save(TestUtils.createTestAccount(testUser));
        testEntry.setCategory(testCategory);
        testEntry.setAccount(testAccount);
        testEntry.setUser(testUser);
        entryRepository.save(testEntry);
    }

    @Test
    public void shouldReturnListOfOneEntryForUser() {
        MutableHttpRequest<Object> httpRequest = HttpRequest.GET("/entries?userId=" + testUser.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<EntryListDto> response = client.toBlocking().exchange(httpRequest, EntryListDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnListOfOneEntryForAccount() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/entries?accountId=" + testAccount.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<EntryListDto> response = client.toBlocking().exchange(request, EntryListDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnSingleEntry() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/entries/" + testEntry.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleEntryDto> response = client.toBlocking().exchange(request, SingleEntryDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals("Milk", response.getBody().get().getName());
    }

    @Test
    public void shouldReturnNotFound() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/entries/123456789")
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("NOT_FOUND", errorResponse.getCode());
    }

    @Test
    public void shouldCreateEntry() {
        SingleEntryDto dto = createEntry();
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleEntryDto> response = client.toBlocking().exchange(request, SingleEntryDto.class);
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

    @Test
    public void shouldUpdateEntry() {
        Entry entry = TestUtils.createTestEntry();
        entry.setUser(testUser);
        entry.setCategory(testCategory);
        entry.setAccount(testAccount);
        SingleEntryDto dto = new SingleEntryDto(entry);
        dto.setName("Another Milk Carton");
        dto.setValue(BigDecimal.valueOf(10.99));
        dto.setIsExpense(true);
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.PUT("/entries/" + testEntry.getId(), dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleEntryDto> response = client.toBlocking().exchange(request, SingleEntryDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto).headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto).headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto).headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto).headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
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
        MutableHttpRequest<SingleEntryDto> request = HttpRequest.POST("/entries", dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleEntryDto.class)
        );
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_ENTRY", errorResponse.getCode());
    }
}
