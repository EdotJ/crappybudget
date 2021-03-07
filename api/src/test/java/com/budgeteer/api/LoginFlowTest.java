package com.budgeteer.api;

import com.budgeteer.api.base.AuthRequest;
import com.budgeteer.api.base.AuthResponse;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.repository.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class LoginFlowTest {

    @Inject
    @Client(value = "/${api.base-path}", errorType = ErrorResponse.class)
    private RxHttpClient client;

    @Inject
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(TestUtils.createSecureTestUser());
    }

    @Test
    public void shouldLoginAndGetAccessTokenAndRefreshToken() {
        HttpResponse<AuthResponse> response = makeLoginRequest();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        AuthResponse body = response.getBody().get();
        assertEquals(TestUtils.TEST_USER_USERNAME, body.getUsername());
        assertNotNull(body.getAccessToken());
        assertNotNull(body.getRefreshToken());
        Map<CharSequence, CharSequence> headers = Map.of("Authorization", "Bearer " + body.getAccessToken());
        HttpRequest<Object> testRequest = HttpRequest.GET("/users").headers(headers);
        HttpResponse<Object> testResponse = client.toBlocking().exchange(testRequest);
        assertEquals(HttpStatus.OK, testResponse.getStatus());
    }

    @Test
    public void shouldLoginAndRequestNewTokenWithRefreshToken() {
        HttpResponse<AuthResponse> response = makeLoginRequest();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        AuthResponse body = response.getBody().get();
        assertEquals(TestUtils.TEST_USER_USERNAME, body.getUsername());
        assertNotNull(body.getAccessToken());
        assertNotNull(body.getRefreshToken());
        response = makeRefreshTokenRequest(body.getRefreshToken());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        body = response.getBody().get();
        assertEquals(TestUtils.TEST_USER_USERNAME, body.getUsername());
        assertNotNull(body.getAccessToken());
        assertNotNull(body.getRefreshToken());
    }

    @Test
    public void shouldFailWithRevokedToken() {
        HttpResponse<AuthResponse> response = makeLoginRequest();
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        AuthResponse body = response.getBody().get();
        assertEquals(TestUtils.TEST_USER_USERNAME, body.getUsername());
        assertNotNull(body.getAccessToken());
        assertNotNull(body.getRefreshToken());
        String originalToken = body.getRefreshToken();
        response = makeRefreshTokenRequest(originalToken);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        body = response.getBody().get();
        assertEquals(TestUtils.TEST_USER_USERNAME, body.getUsername());
        assertNotNull(body.getAccessToken());
        assertNotNull(body.getRefreshToken());
        Map<String, String> reqMap = Map.of("grant_type", "refresh_token", "refresh_token", originalToken);
        HttpRequest<Object> request = HttpRequest.POST("/refresh_token", reqMap);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, ErrorResponse.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        assertEquals("INVALID_TOKEN", optionalError.get().getCode());
        assertTrue(optionalError.get().getDetail().contains("revoked"));
    }

    private HttpResponse<AuthResponse> makeLoginRequest() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(TestUtils.TEST_USER_USERNAME);
        authRequest.setPassword(TestUtils.TEST_USER_PASSWORD);
        HttpRequest<AuthRequest> request = HttpRequest.POST("/login", authRequest);
        return client.toBlocking().exchange(request, AuthResponse.class);
    }

    private HttpResponse<AuthResponse> makeRefreshTokenRequest(String refreshToken) {
        HttpResponse<AuthResponse> response;
        Map<String, String> reqMap = Map.of("grant_type", "refresh_token", "refresh_token", refreshToken);
        HttpRequest<Object> request = HttpRequest.POST("/refresh_token", reqMap);
        response = client.toBlocking().exchange(request, AuthResponse.class);
        return response;
    }
}
