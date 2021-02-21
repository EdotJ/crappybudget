package com.budgeteer.api.controller;

import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.user.SingleUserDto;
import com.budgeteer.api.dto.user.UserListDto;
import com.budgeteer.api.model.User;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class UserControllerTest {

    @Inject
    @Client(value = "/${api.base-path}", errorType = ErrorResponse.class)
    private RxHttpClient client;

    @Inject
    private UserRepository repository;

    private User testUser;

    @BeforeEach
    public void beforeEach() {
        createTestUser();
    }

    private void createTestUser() {
        testUser = repository.save(TestUtils.createTestUser());
    }

    @Test
    public void shouldReturnListOfSingleUser() {
        HttpResponse<UserListDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/users"), UserListDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnSingleUser() {
        HttpResponse<SingleUserDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/users/" + testUser.getId()), SingleUserDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals(TestUtils.TEST_USER_EMAIL, response.getBody().get().getEmail());
    }

    @Test
    public void shouldCreateNewUser() {
        SingleUserDto dto = new SingleUserDto();
        dto.setUsername("username2");
        dto.setEmail("email2@gmail.com");
        dto.setPassword("unsecurepassword");
        HttpResponse<SingleUserDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/users", dto), SingleUserDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals(dto.getEmail(), response.getBody().get().getEmail());
        assertEquals(dto.getUsername(), response.getBody().get().getUsername());
    }

    @Test
    public void shouldUpdateUser() {
        SingleUserDto dto = new SingleUserDto();
        dto.setUsername("testuser");
        dto.setEmail("email@gmail.com");
        HttpResponse<SingleUserDto> response = client.toBlocking()
                .exchange(HttpRequest.PUT("/users" + "/" + testUser.getId(), dto), SingleUserDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals("username", response.getBody().get().getUsername());
        assertEquals("email@gmail.com", response.getBody().get().getEmail());
    }

    @Test
    public void shouldDeleteTestUser() {
        HttpResponse<Object> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/users" + "/" + testUser.getId()), Object.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        List<User> users = repository.findAll();
        assertEquals(0, users.size());
    }

    @Test
    public void shouldFailEmailVerificationWithInvalidFormat() {
        SingleUserDto dto = new SingleUserDto();
        dto.setUsername("username");
        dto.setEmail("email@g");
        dto.setPassword("unsecurepassword");
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/users", dto), ErrorResponse.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_EMAIL", errorResponse.getCode());
        assertTrue(errorResponse.getMessage().contains("format"));
        assertTrue(errorResponse.getDetail().contains("Invalid email format"));
    }

    @Test
    public void shouldFailEmailVerificationWithEmptyEmail() {
        SingleUserDto dto = new SingleUserDto();
        dto.setUsername("username");
        dto.setEmail("");
        dto.setPassword("unsecurepassword");
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.POST("/users", dto), ErrorResponse.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_EMAIL", errorResponse.getCode());
        assertTrue(errorResponse.getDetail().contains("empty"));
    }

    @Test
    public void shouldFailPasswordVerification() {
        SingleUserDto dto = new SingleUserDto();
        dto.setUsername("username");
        dto.setEmail("email@gmail.com");
        dto.setPassword("");
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/users", dto), ErrorResponse.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_PASSWORD", errorResponse.getCode());
        assertTrue(errorResponse.getDetail().contains("empty"));
    }

    @Test
    public void shouldFailUsernameVerification() {
        SingleUserDto dto = new SingleUserDto();
        dto.setUsername("");
        dto.setEmail("email@go.com");
        dto.setPassword("unsecurepassword");
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/users", dto), ErrorResponse.class));
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_USERNAME", errorResponse.getCode());
        assertTrue(errorResponse.getDetail().contains("empty"));
    }
}
