package com.budgeteer.api.imports;

import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.imports.ynab.YnabClient;
import com.budgeteer.api.imports.ynab.model.*;
import com.budgeteer.api.imports.ynab.model.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
public class YnabClientTest {

    @Inject
    YnabClient client;

    String personalToken = "token";

    @Inject
    RxHttpClient httpClient;

    @Test
    public void testGetBudgets() throws JsonProcessingException {
        SuccessResponse mockResponse = new SuccessResponse();
        Data data = new Data();
        data.setBudgets(List.of(new Budget()));
        mockResponse.setData(data);
        when(httpClient.retrieve(any(), eq(Argument.of(SuccessResponse.class)), eq(Argument.of(ErrorResponse.class))))
                .thenReturn(Flowable.just(mockResponse));
        Budget budget = client.getBudget(personalToken);
        assertNotNull(budget);
    }

    @Test
    public void testGetCategories() throws JsonProcessingException {
        SuccessResponse mockResponse = new SuccessResponse();
        Data data = new Data();
        data.setBudgets(List.of(new Budget()));
        data.setBudgets(List.of(new Budget()));
        data.setCategories(List.of(new CategoryGroup()));
        mockResponse.setData(data);
        when(httpClient.retrieve(any(), eq(Argument.of(SuccessResponse.class)), eq(Argument.of(ErrorResponse.class))))
                .thenReturn(Flowable.just(mockResponse));
        Budget budget = client.getBudget(personalToken);
        List<CategoryGroup> categories = client.getCategories(budget.getId(), personalToken);
        assertFalse(categories.isEmpty());
    }

    @Test
    public void testGetTransactions() throws JsonProcessingException {
        SuccessResponse mockResponse = new SuccessResponse();
        Data data = new Data();
        data.setBudgets(List.of(new Budget()));
        data.setCategories(List.of(new CategoryGroup()));
        data.setTransactions(List.of(new Transaction()));
        mockResponse.setData(data);
        when(httpClient.retrieve(any(), eq(Argument.of(SuccessResponse.class)), eq(Argument.of(ErrorResponse.class))))
                .thenReturn(Flowable.just(mockResponse));
        Budget budget = client.getBudget(personalToken);
        List<Transaction> transactions = client.getTransactions(budget.getId(), personalToken);
        assertFalse(transactions.isEmpty());
    }

    @Test
    public void testErrorHandling() {
        given(httpClient.retrieve(any(), eq(Argument.of(SuccessResponse.class)), eq(Argument.of(ErrorResponse.class))))
                .willThrow(new HttpClientResponseException("Bad Request", HttpResponse.badRequest()));
        assertThrows(HttpClientResponseException.class, () -> client.getBudget(personalToken));
    }

    @Test
    public void testAuthErrorHandling() {
        ErrorResponse errorResponse = new ErrorResponse();
        Error error = new Error();
        error.setId("401");
        errorResponse.setError(error);
        HttpResponse<Object> mockResponse = HttpResponse.unauthorized().body(errorResponse);
        HttpClientResponseException e = new HttpClientResponseException("Bad Request", mockResponse);
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> client.handleError(e));
        assertEquals("YNAB", exception.getCode());
    }

    @MockBean(RxHttpClient.class)
    RxHttpClient httpClient() {
        return mock(RxHttpClient.class);
    }

}
