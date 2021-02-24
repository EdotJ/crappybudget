package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.account.AccountListDto;
import com.budgeteer.api.dto.account.SingleAccountDto;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.AccountRepository;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class AccountControllerTest {

    @RegisterExtension
    AuthenticationExtension authExtension = new AuthenticationExtension();

    @Inject
    @Client(value = "/${api.base-path}", errorType = ErrorResponse.class)
    private RxHttpClient client;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AccountRepository accountRepository;

    private Account testAccount;
    private User testUser;

    @BeforeEach
    public void setup() {
        testAccount = TestUtils.createTestAccount();
        testUser = userRepository.save(TestUtils.createSecureTestUser());
        testAccount.setUser(testUser);
        accountRepository.save(testAccount);
    }

    @Test
    public void shouldReturnListOfOneAccount() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/accounts?userId=" + testUser.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<AccountListDto> response = client.toBlocking()
                .exchange(request, AccountListDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnSingleAccount() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/accounts/" + testAccount.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleAccountDto> response = client.toBlocking()
                .exchange(request, SingleAccountDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        SingleAccountDto account = response.getBody().get();
        assertNotNull(account);
        assertEquals("Test Account", account.getName());
        assertEquals(testAccount.getId(), account.getUserId());
    }

    @Test
    public void shouldReturnNotFound() {
        MutableHttpRequest<Object> req = HttpRequest.GET("/accounts/123456789").headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
            client.toBlocking().exchange(req, ErrorResponse.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("NOT_FOUND", errorResponse.getCode());
    }

    @Test
    public void shouldUpdateAccount() {
        SingleAccountDto dto = new SingleAccountDto();
        dto.setName("New Account Renamed");
        dto.setUserId(testUser.getId());
        MutableHttpRequest<SingleAccountDto> request = HttpRequest.PUT("/accounts/" + testAccount.getId(), dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleAccountDto> response = client.toBlocking().exchange(request, SingleAccountDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals(dto.getName(), response.getBody().get().getName());
        assertEquals(dto.getUserId(), response.getBody().get().getUserId());
    }

    @Test
    public void shouldInsertNewAccount() {
        SingleAccountDto dto = new SingleAccountDto();
        dto.setName("New Account");
        dto.setUserId(testUser.getId());
        MutableHttpRequest<SingleAccountDto> request = HttpRequest.POST("/accounts", dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleAccountDto> response = client.toBlocking().exchange(request, SingleAccountDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals(dto.getName(), response.getBody().get().getName());
        assertEquals(dto.getUserId(), response.getBody().get().getUserId());
        accountRepository.deleteById(response.getBody().get().getId());
    }

    @Test
    public void shouldDeleteAccount() {
        MutableHttpRequest<Object> request = HttpRequest.DELETE("/accounts/" + testAccount.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleAccountDto> response = client.toBlocking().exchange(request, SingleAccountDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        List<Account> accounts = accountRepository.findAll();
        assertEquals(0, accounts.size());
    }

    @Test
    public void shouldNotAllowEmptyAccountName() {
        SingleAccountDto dto = new SingleAccountDto();
        dto.setName("");
        dto.setUserId(1L);
        MutableHttpRequest<SingleAccountDto> request = HttpRequest.POST("/accounts", dto).headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, ErrorResponse.class)
        );
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_ACCOUNT_NAME", errorResponse.getCode());
    }

}
