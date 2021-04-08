package com.budgeteer.api.imports;

import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.imports.ynab.YnabClient;
import com.budgeteer.api.imports.ynab.YnabImporter;
import com.budgeteer.api.imports.ynab.YnabImporterData;
import com.budgeteer.api.imports.ynab.model.SuccessResponse;
import com.budgeteer.api.imports.ynab.model.Budget;
import com.budgeteer.api.model.Account;
import com.budgeteer.api.model.Category;
import com.budgeteer.api.model.Entry;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.AccountRepository;
import com.budgeteer.api.repository.CategoryRepository;
import com.budgeteer.api.repository.EntryRepository;
import com.budgeteer.api.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
@ExtendWith(MockitoExtension.class)
@ExtendWith(DatabaseCleanupExtension.class)
public class YnabImporterTest {

    @Inject
    YnabClient client;

    @Inject
    YnabImporter ynabImporter;

    @Inject
    UserRepository userRepository;

    @Inject
    EntryRepository entryRepository;

    @Inject
    AccountRepository accountRepository;

    @Inject
    CategoryRepository categoryRepository;

    User testUser;

    @BeforeEach
    public void setup() throws IOException {
        when(client.getBudget(any())).thenReturn(getResponse("ynab_budgets.json", Budget.class));
        SuccessResponse categoryResponse = getResponse("ynab_categories.json", SuccessResponse.class);
        when(client.getCategories(any(), any())).thenReturn(categoryResponse.getData().getCategories());
        SuccessResponse transactionResponse = getResponse("ynab_transactions.json", SuccessResponse.class);
        when(client.getTransactions(any(), any())).thenReturn(transactionResponse.getData().getTransactions());
        testUser = userRepository.save(TestUtils.createTestUser());
    }

    @Test
    public void testImportingData() throws JsonProcessingException {
        YnabImporterData data = ynabImporter.makeRequest("token");
        ynabImporter.createEntries(data, testUser);
        List<Entry> entryList = entryRepository.findByUserIdOrderByDateDesc(testUser.getId());
        assertEquals(3, entryList.size());
        Entry entry = entryList.get(0);
        assertEquals(new BigDecimal("150.00"), entry.getValue());
        assertEquals("Rent", entry.getName());
        entry = entryList.get(1);
        assertEquals(new BigDecimal("500.00"), entry.getValue());
        assertEquals("Starting Balance", entry.getName());
        entry = entryList.get(2);
        assertEquals(new BigDecimal("7415.00"), entry.getValue());
        assertEquals("Starting Balance", entry.getName());
    }

    @Test
    public void testThatNoDuplicateAccountsWereCreated() throws JsonProcessingException {
        YnabImporterData data = ynabImporter.makeRequest("token");
        ynabImporter.createEntries(data, testUser);
        ynabImporter.createEntries(data, testUser);
        List<Account> accounts = accountRepository.findAll();
        assertEquals(2, accounts.size());
        List<Entry> entries = entryRepository.findByUserIdOrderByDateDesc(testUser.getId());
        assertEquals(6, entries.size());
    }

    @Test
    public void testThatNoDuplicateCategoriesWereCreated() throws JsonProcessingException {
        YnabImporterData data = ynabImporter.makeRequest("token");
        ynabImporter.createEntries(data, testUser);
        ynabImporter.createEntries(data, testUser);
        List<Category> categories = categoryRepository.findAll();
        assertEquals(39, categories.size());
        List<Entry> entries = entryRepository.findByUserIdOrderByDateDesc(testUser.getId());
        assertEquals(6, entries.size());
    }

    private <T> T getResponse(String fileName, Class<?> clazz) throws IOException {
        String responseString = TestUtils.getResponseString(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return (T) objectMapper.readValue(responseString, clazz);
    }

    @MockBean(YnabClient.class)
    YnabClient ynabClient() {
        return mock(YnabClient.class);
    }
}
