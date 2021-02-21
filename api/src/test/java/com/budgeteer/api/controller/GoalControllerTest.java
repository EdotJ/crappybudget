package com.budgeteer.api.controller;

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
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class GoalControllerTest {

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
    private User testUser;
    private Account testAccount;

    @BeforeEach
    public void setup() {
        testGoal = TestUtils.createTestGoal();
        testUser = userRepository.save(TestUtils.createTestUser());
        testAccount = TestUtils.createTestAccount();
        testAccount.setUser(testUser);
        testAccount = accountRepository.save(testAccount);
        testGoal.setUser(testUser);
        testGoal.setAccount(testAccount);
        testGoal = goalRepository.save(testGoal);
    }

    @Test
    public void shouldReturnListOfOneGoal() {
        HttpResponse<GoalListDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/goals?userId=" + testUser.getId()), GoalListDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertTrue(response.getBody().isPresent());
        assertEquals(1, response.getBody().get().getCount());
    }

    @Test
    public void shouldReturnSingleGoal() {
        HttpResponse<SingleGoalDto> response = client.toBlocking()
                .exchange(HttpRequest.GET("/goals/" + testGoal.getId()), SingleGoalDto.class);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals("Tesla #2060", response.getBody().get().getName());
        assertEquals(LocalDate.parse("2060-01-01"), response.getBody().get().getDate());
        assertEquals(BigDecimal.valueOf(53990), response.getBody().get().getValue());
    }

    @Test
    public void shouldReturnNotFound() {
        try {
            client.toBlocking().exchange(HttpRequest.GET("/goals/123456789"), SingleGoalDto.class);
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getResponse().status());
            Optional<ErrorResponse> optionalError = e.getResponse().getBody(ErrorResponse.class);
            assertTrue(optionalError.isPresent());
            ErrorResponse errorResponse = optionalError.get();
            assertEquals("NOT_FOUND", errorResponse.getCode());
        }
    }

    @Test
    public void shouldCreateGoalWithoutAccount() {
        SingleGoalDto dto = getTestGoalDto();
        dto.setAccountId(null);
        HttpResponse<SingleGoalDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/goals", dto), SingleGoalDto.class);
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
        HttpResponse<SingleGoalDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/goals", dto), SingleGoalDto.class);
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
        HttpResponse<SingleGoalDto> response = client.toBlocking()
                .exchange(HttpRequest.PUT("/goals/" + testGoal.getId(), dto), SingleGoalDto.class);
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
    public void shouldDeleteGoal() {
        HttpResponse<SingleGoalDto> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/goals/" + testGoal.getId()), SingleGoalDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        List<Goal> goalList = goalRepository.findAll();
        assertEquals(0, goalList.size());
    }

    @Test
    public void shouldFailWhenNoNameSupplied() {
        SingleGoalDto dto = getTestGoalDto();
        dto.setName("");
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/goals", dto), SingleGoalDto.class));
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
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/goals", dto), SingleGoalDto.class));
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
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(HttpRequest.POST("/goals", dto), SingleGoalDto.class));
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
