package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.BalanceDto;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.entry.EntryListDto;
import com.budgeteer.api.dto.entry.ReceiptEntriesDto;
import com.budgeteer.api.dto.entry.ReceiptEntryDto;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
@Tag("Integration")
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
    private Entry additionalTestEntry;

    @BeforeEach
    public void setup() {
        testEntry = TestUtils.createTestEntry();
        additionalTestEntry = TestUtils.createTestEntry();
        testUser = userRepository.save(TestUtils.createSecureTestUser());
        User secondTestUser = userRepository.save(TestUtils.createAdditionalTestUser());
        testCategory = categoryRepository.save(TestUtils.createTestCategory(testUser));
        testAccount = TestUtils.createTestAccount(testUser);
        testAccount = accountRepository.save(testAccount);
        testEntry.setCategory(testCategory);
        testEntry.setAccount(testAccount);
        testEntry.setUser(testUser);
        testEntry = entryRepository.save(testEntry);
        additionalTestEntry.setCategory(testCategory);
        additionalTestEntry.setAccount(testAccount);
        additionalTestEntry.setUser(secondTestUser);
        additionalTestEntry = entryRepository.save(additionalTestEntry);
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
    public void shouldReturnListOfOneEntriesForAccount() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/entries?accountId=" + testAccount.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<EntryListDto> response = client.toBlocking().exchange(request, EntryListDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().getCount());
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
    public void shouldFailFetchingOtherUserEntry() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/entries/" + additionalTestEntry.getId())
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
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
    public void shouldCreateEntryFromReceiptRequest() {
        ReceiptEntriesDto dto = new ReceiptEntriesDto();
        dto.setAccountId(testAccount.getId());
        dto.setCategoryId(testCategory.getId());
        dto.setDate(LocalDate.of(2020, 11, 11));
        dto.getEntries().add(new ReceiptEntryDto("Entry", BigDecimal.valueOf(5.50)));
        MutableHttpRequest<ReceiptEntriesDto> request = HttpRequest.POST("/entries/receipt", dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<EntryListDto> response = client.toBlocking().exchange(request, EntryListDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        EntryListDto responseDto = response.getBody().get();
        assertEquals(1, responseDto.getCount());
        SingleEntryDto responseEntry = responseDto.getList().get(0);
        assertEquals("Entry", responseEntry.getName());
        assertEquals(LocalDate.of(2020, 11, 11), responseEntry.getDate());
        assertEquals(BigDecimal.valueOf(5.50), responseEntry.getValue());
        assertEquals(testAccount.getId(), responseEntry.getAccountId());
        assertEquals(testCategory.getId(), responseEntry.getCategoryId());
    }

    @Test
    public void shouldCreateEntryFromReceiptRequestWithDiscounts() {
        ReceiptEntriesDto dto = new ReceiptEntriesDto();
        dto.setAccountId(testAccount.getId());
        dto.setCategoryId(testCategory.getId());
        dto.setDate(LocalDate.of(2020, 11, 11));
        dto.getEntries().add(new ReceiptEntryDto("Entry", BigDecimal.valueOf(5.50)));
        dto.getEntries().add(new ReceiptEntryDto("Entry Discount", BigDecimal.valueOf(1.20), true));
        MutableHttpRequest<ReceiptEntriesDto> request = HttpRequest.POST("/entries/receipt", dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<EntryListDto> response = client.toBlocking().exchange(request, EntryListDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        EntryListDto responseDto = response.getBody().get();
        assertEquals(1, responseDto.getCount());
        SingleEntryDto responseEntry = responseDto.getList().get(0);
        assertEquals("Entry", responseEntry.getName());
        assertEquals(LocalDate.of(2020, 11, 11), responseEntry.getDate());
        assertEquals(BigDecimal.valueOf(4.30), responseEntry.getValue());
        assertEquals(testAccount.getId(), responseEntry.getAccountId());
        assertEquals(testCategory.getId(), responseEntry.getCategoryId());
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

    @Test
    public void shouldFailUpdatingOtherUserCategory() {
        SingleEntryDto dto = createEntry();
        MutableHttpRequest<SingleEntryDto> req = HttpRequest.PUT("/entries/" + additionalTestEntry.getId(), dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(req, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    public void shouldDeleteEntry() {
        MutableHttpRequest<Object> request = HttpRequest.DELETE("/entries/" + testEntry.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<Object> response = client.toBlocking().exchange(request, Object.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        List<Entry> entryList = entryRepository.findByUserIdOrderByDateDescAndCreatedDesc(testUser.getId());
        assertEquals(0, entryList.size());
    }

    @Test
    public void shouldFailDeletingOtherUserCategory() {
        MutableHttpRequest<Object> req = HttpRequest.DELETE("/entries/" + additionalTestEntry.getId())
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(req, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
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

    @Test
    public void shouldReturnBalance() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/entries/balance")
                .headers(authExtension.getAuthHeader());
        HttpResponse<BalanceDto> response = client.toBlocking().exchange(request, BalanceDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(new BigDecimal("-1.39"), response.getBody().get().getBalance());
    }
}
