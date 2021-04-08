package com.budgeteer.api.imports;

import com.budgeteer.api.imports.ynab.YnabClient;
import com.budgeteer.api.imports.ynab.model.Budget;
import com.budgeteer.api.imports.ynab.model.CategoryGroup;
import com.budgeteer.api.imports.ynab.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class YnabClientTest {

    @Inject
    YnabClient client;

    String personalToken = "token";

    @Test
    @Disabled
    public void testGetBudgets() throws JsonProcessingException {
        Budget budget = client.getBudget(personalToken);
        assertNotNull(budget);
    }

    @Test
    @Disabled
    public void testGetCategories() throws JsonProcessingException {
        Budget budget = client.getBudget(personalToken);
        List<CategoryGroup> categories = client.getCategories(budget.getId(), personalToken);
        assertFalse(categories.isEmpty());
    }

    @Test
    @Disabled
    public void testGetTransactions() throws JsonProcessingException {
        Budget budget = client.getBudget(personalToken);
        List<Transaction> transactions = client.getTransactions(budget.getId(), personalToken);
        assertFalse(transactions.isEmpty());
    }

}
