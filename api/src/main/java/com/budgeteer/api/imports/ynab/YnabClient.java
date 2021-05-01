package com.budgeteer.api.imports.ynab;

import com.budgeteer.api.exception.BadRequestException;
import com.budgeteer.api.imports.ynab.model.Data;
import com.budgeteer.api.imports.ynab.model.Error;
import com.budgeteer.api.imports.ynab.model.ErrorResponse;
import com.budgeteer.api.imports.ynab.model.SuccessResponse;
import com.budgeteer.api.imports.ynab.model.Budget;
import com.budgeteer.api.imports.ynab.model.CategoryGroup;
import com.budgeteer.api.imports.ynab.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.jackson.annotation.JacksonFeatures;
import io.reactivex.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Singleton
@JacksonFeatures(disabledDeserializationFeatures = DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
public class YnabClient {

    Logger logger = LoggerFactory.getLogger(YnabClient.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Client(value = "https://api.youneedabudget.com/v1", errorType = ErrorResponse.class)
    RxHttpClient client;

    public YnabClient(RxHttpClient client) {
        this.client = client;
    }

    public Budget getBudget(String personalToken) throws JsonProcessingException {
        Maybe<SuccessResponse> response = constructRequest("/budgets?include_accounts=true", personalToken);
        try {
            SuccessResponse finalResponse = response.blockingGet();
            if (logger.isDebugEnabled()) {
                logger.debug(objectMapper.writeValueAsString(finalResponse));
            }
            Data data = finalResponse.getData();
            if (data.getDefaultBudget() == null) {
                return data.getBudgets().get(0);
            }
            return data.getDefaultBudget();
        } catch (HttpClientResponseException e) {
            handleError(e);
            throw e;
        }
    }

    private Maybe<SuccessResponse> constructRequest(String uri, String personalToken) {
        MutableHttpRequest<Object> request = HttpRequest.GET(uri)
                .headers(getAuthHeader(personalToken));
        return client.retrieve(
                request,
                Argument.of(SuccessResponse.class),
                Argument.of(ErrorResponse.class)).firstElement();
    }

    public List<CategoryGroup> getCategories(String budget, String personalToken) throws JsonProcessingException {
        Maybe<SuccessResponse> response = constructRequest("/budgets/" + budget + "/categories", personalToken);
        try {
            SuccessResponse finalResponse = response.blockingGet();
            if (logger.isDebugEnabled()) {
                logger.debug(objectMapper.writeValueAsString(finalResponse));
            }
            Data data = finalResponse.getData();
            return data.getCategories();
        } catch (HttpClientResponseException e) {
            handleError(e);
            throw e;
        }
    }

    public List<Transaction> getTransactions(String budget, String personalToken) throws JsonProcessingException {
        Maybe<SuccessResponse> response = constructRequest("/budgets/" + budget + "/transactions", personalToken);
        try {
            SuccessResponse finalResponse = response.blockingGet();
            if (logger.isDebugEnabled()) {
                logger.debug(objectMapper.writeValueAsString(finalResponse));
            }
            Data data = finalResponse.getData();
            return data.getTransactions();
        } catch (HttpClientResponseException e) {
            handleError(e);
            throw e;
        }
    }

    public void handleError(HttpClientResponseException e) {
        var error = e.getResponse().getBody(ErrorResponse.class);
        if (error.isPresent()) {
            Error ynabError = error.get().getError();
            if (ynabError.getId().equals("401")) {
                String msg = "The personal token for YNAB was deemed expired";
                throw new BadRequestException("YNAB", "bad_token", msg, "unauthorized");
            }
            logger.error(error.get().getError().getDetail());
        } else {
            logger.error("Failed getting budgets from YNAB");
        }
    }

    private Map<CharSequence, CharSequence> getAuthHeader(String personalToken) {
        return Map.of("Authorization", "Bearer " + personalToken);
    }
}
