package com.budgeteer.api.controller;

import com.budgeteer.api.base.AuthenticationExtension;
import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.dto.goal.GoalListDto;
import com.budgeteer.api.dto.goal.SingleGoalDto;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.Goal;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.AccountRepository;
import com.budgeteer.api.repository.GoalRepository;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class GoalControllerTest {

    @RegisterExtension
    AuthenticationExtension authExtension = new AuthenticationExtension();

    @Inject
    private GoalRepository goalRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    @Client(value = "${api.base-path}", errorType = ErrorResponse.class)
    private RxHttpClient client;

    private Goal testGoal;
    private Goal additionalGoal;
    private User testUser;
    private Account testAccount;

    @BeforeEach
    public void setup() {
        testGoal = TestUtils.createTestGoal();
        additionalGoal = TestUtils.createTestGoal();
        testUser = userRepository.save(TestUtils.createSecureTestUser());
        User secondUser = userRepository.save(TestUtils.createAdditionalTestUser());
        testAccount = TestUtils.createTestAccount();
        testAccount.setUser(testUser);
        testAccount = accountRepository.save(testAccount);
        testGoal.setUser(testUser);
        testGoal.setAccount(testAccount);
        testGoal = goalRepository.save(testGoal);
        additionalGoal.setAccount(testAccount);
        additionalGoal.setUser(secondUser);
        additionalGoal = goalRepository.save(additionalGoal);
    }

    @Test
    public void shouldReturnListOfOneGoal() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/goals?userId=" + testUser.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<GoalListDto> response = client.toBlocking().exchange(request, GoalListDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnSingleGoal() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/goals/" + testGoal.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleGoalDto> response = client.toBlocking().exchange(request, SingleGoalDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals("Tesla #2060", response.getBody().get().getName());
        assertEquals(LocalDate.parse("2060-01-01"), response.getBody().get().getDate());
        assertEquals(BigDecimal.valueOf(53990), response.getBody().get().getValue());
    }

    @Test
    public void shouldFailFetchingOtherUserGoal() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/goals/" + additionalGoal.getId())
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    public void shouldReturnNotFound() {
        MutableHttpRequest<Object> request = HttpRequest.GET("/goals/123456789")
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleGoalDto.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("NOT_FOUND", errorResponse.getCode());
    }

    @Test
    public void shouldCreateGoalWithoutAccount() {
        SingleGoalDto dto = getTestGoalDto();
        dto.setAccountId(null);
        MutableHttpRequest<SingleGoalDto> request = HttpRequest.POST("/goals", dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleGoalDto> response = client.toBlocking().exchange(request, SingleGoalDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        Optional<Goal> goal = goalRepository.findById(response.getBody().get().getId());
        assertTrue(goal.isPresent());
        assertEquals(dto.getName(), goal.get().getName());
        assertEquals(dto.getDescription(), goal.get().getDescription());
        assertEquals(dto.getDate(), goal.get().getDate());
        assertEquals(dto.getValue(), goal.get().getValue());
        assertEquals(dto.getUserId(), goal.get().getUser().getId());
        assertNull(goal.get().getAccount());
    }

    @Test
    public void shouldCreateGoalWithAccount() {
        SingleGoalDto dto = getTestGoalDto();
        MutableHttpRequest<SingleGoalDto> request = HttpRequest.POST("/goals", dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleGoalDto> response = client.toBlocking().exchange(request, SingleGoalDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        Optional<Goal> goal = goalRepository.findById(response.getBody().get().getId());
        assertTrue(goal.isPresent());
        assertEquals(dto.getName(), goal.get().getName());
        assertEquals(dto.getDescription(), goal.get().getDescription());
        assertEquals(dto.getDate(), goal.get().getDate());
        assertEquals(dto.getValue(), goal.get().getValue());
        assertEquals(dto.getUserId(), goal.get().getUser().getId());
        assertNotNull(goal.get().getAccount().getId());
    }

    @Test
    public void shouldUpdateGoal() {
        SingleGoalDto dto = getTestGoalDto();
        MutableHttpRequest<SingleGoalDto> request = HttpRequest.PUT("/goals/" + testGoal.getId(), dto)
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleGoalDto> response = client.toBlocking().exchange(request, SingleGoalDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        Optional<Goal> goal = goalRepository.findById(response.getBody().get().getId());
        assertTrue(goal.isPresent());
        assertEquals(dto.getName(), goal.get().getName());
        assertEquals(dto.getDescription(), goal.get().getDescription());
        assertEquals(dto.getDate(), goal.get().getDate());
        assertEquals(dto.getValue(), goal.get().getValue());
        assertEquals(dto.getUserId(), goal.get().getUser().getId());
        assertNotNull(goal.get().getAccount().getId());
    }

    @Test
    public void shouldFailUpdatingOtherUserGoal() {
        SingleGoalDto dto = getTestGoalDto();
        MutableHttpRequest<SingleGoalDto> request = HttpRequest.PUT("/goals/" + additionalGoal.getId(), dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    public void shouldDeleteGoal() {
        MutableHttpRequest<Object> request = HttpRequest.DELETE("/goals/" + testGoal.getId())
                .headers(authExtension.getAuthHeader());
        HttpResponse<SingleGoalDto> response = client.toBlocking().exchange(request, SingleGoalDto.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        List<Goal> goalList = goalRepository.findByUserId(testUser.getId());
        assertEquals(0, goalList.size());
    }

    @Test
    public void shouldFailDeletingOtherUserGoal() {
        MutableHttpRequest<Object> request = HttpRequest.DELETE("/goals/" + additionalGoal.getId())
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, ErrorResponse.class)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    public void shouldFailWhenNoNameSupplied() {
        SingleGoalDto dto = getTestGoalDto();
        dto.setName("");
        MutableHttpRequest<SingleGoalDto> request = HttpRequest.POST("/goals", dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleGoalDto.class)
        );
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_GOAL_NAME", errorResponse.getCode());
    }

    @Test
    public void shouldFailWhenNoDateSupplied() {
        SingleGoalDto dto = getTestGoalDto();
        dto.setDate(null);
        MutableHttpRequest<SingleGoalDto> request = HttpRequest.POST("/goals", dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleGoalDto.class)
        );
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_GOAL_DATE", errorResponse.getCode());
    }

    @Test
    public void shouldFailWhenDateInPast() {
        SingleGoalDto dto = getTestGoalDto();
        dto.setDate(LocalDate.now().minusDays(10));
        MutableHttpRequest<SingleGoalDto> request = HttpRequest.POST("/goals", dto)
                .headers(authExtension.getAuthHeader());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, SingleGoalDto.class)
        );
        assertEquals(HttpStatus.BAD_REQUEST, e.getResponse().status());
        Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
        assertTrue(optionalError.isPresent());
        ErrorResponse errorResponse = optionalError.get();
        assertEquals("BAD_GOAL_DATE", errorResponse.getCode());
    }

    private SingleGoalDto getTestGoalDto() {
        SingleGoalDto dto = new SingleGoalDto();
        dto.setName("Tesla #2030");
        dto.setDescription("Tesla 2030 baby");
        dto.setDate(LocalDate.parse("2030-01-01"));
        dto.setValue(BigDecimal.valueOf(53990));
        dto.setUserId(testUser.getId());
        dto.setAccountId(testAccount.getId());
        return dto;
    }
}
