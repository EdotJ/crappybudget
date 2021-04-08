package com.budgeteer.api.imports.ynab;

import com.budgeteer.api.dto.imports.YnabImportRequest;
import com.budgeteer.api.imports.ynab.model.YnabResponseAccount;
import com.budgeteer.api.imports.ynab.model.Budget;
import com.budgeteer.api.imports.ynab.model.CategoryGroup;
import com.budgeteer.api.imports.ynab.model.YnabResponseCategory;
import com.budgeteer.api.imports.ynab.model.Transaction;
import com.budgeteer.api.model.*;
import com.budgeteer.api.repository.YnabAccountRepository;
import com.budgeteer.api.repository.YnabCategoryRepository;
import com.budgeteer.api.service.AccountService;
import com.budgeteer.api.service.CategoryService;
import com.budgeteer.api.service.EntryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class YnabImporter {

    private final YnabClient client;

    private final AccountService accountService;

    private final CategoryService categoryService;

    private final EntryService entryService;

    private final YnabAccountRepository ynabAccountRepository;

    private final YnabCategoryRepository ynabCategoryRepository;

    public YnabImporter(YnabClient client,
                        AccountService accountService,
                        CategoryService categoryService,
                        EntryService entryService,
                        YnabAccountRepository ynabAccountRepository,
                        YnabCategoryRepository ynabCategoryRepository) {
        this.client = client;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.entryService = entryService;
        this.ynabAccountRepository = ynabAccountRepository;
        this.ynabCategoryRepository = ynabCategoryRepository;
    }

    public void validateRequest(YnabImportRequest request) {
        if (!StringUtils.hasText(request.getPersonalToken())) {
            throw UnsatisfiedRouteException.create(Argument.of(String.class, "personalToken"));
        }
    }

    public YnabImporterData makeRequest(String personalToken) throws JsonProcessingException {
        Budget budget = client.getBudget(personalToken);
        List<CategoryGroup> categories = client.getCategories(budget.getId(), personalToken);
        List<Transaction> transactions = client.getTransactions(budget.getId(), personalToken);
        return new YnabImporterData(budget, categories, transactions);
    }

    public void createEntries(YnabImporterData data, User user) {
        Map<String, Account> accounts = saveAccounts(data.getBudget().getAccounts(), user);
        Map<String, Category> categories = saveCategories(data.getCategories(), user);
        saveEntries(accounts, categories, user, data.getTransactions());
    }

    private Map<String, Account> saveAccounts(List<YnabResponseAccount> accounts, User user) {
        Map<String, Account> accountsByYnabKey = new HashMap<>();
        Map<String, YnabAccount> mappedAccounts = ynabAccountRepository
                .findByUser(user).stream()
                .collect(Collectors.toMap(YnabAccount::getYnabId, a -> a));
        for (YnabResponseAccount account : accounts) {
            Account newAccount = getAccount(user, mappedAccounts, account);
            accountsByYnabKey.put(account.getId(), newAccount);
        }
        return accountsByYnabKey;
    }

    private Account getAccount(User user, Map<String, YnabAccount> mappedAccounts, YnabResponseAccount account) {
        Account newAccount;
        YnabAccount ynabAccount = mappedAccounts.get(account.getId());
        if (ynabAccount != null) {
            newAccount = ynabAccount.getAccount();
        } else {
            newAccount = new Account();
            newAccount.setName(account.getName());
            newAccount.setUser(user);
            newAccount = accountService.save(newAccount);
            createYnabAccount(account, newAccount, user);
        }
        return newAccount;
    }

    private void createYnabAccount(YnabResponseAccount ynabRespnoseAccount, Account account, User user) {
        YnabAccount ynabAccount = new YnabAccount();
        ynabAccount.setAccount(account);
        ynabAccount.setUser(user);
        ynabAccount.setYnabId(ynabRespnoseAccount.getId());
        ynabAccountRepository.save(ynabAccount);
    }

    private Map<String, Category> saveCategories(List<CategoryGroup> categories, User user) {
        Map<String, Category> categoriesByYnabKey = new HashMap<>();
        Map<String, YnabCategory> mappedCategories = ynabCategoryRepository
                .findByUser(user).stream()
                .collect(Collectors.toMap(YnabCategory::getYnabId, a -> a));
        for (CategoryGroup categoryGroup : categories) {
            Category parent = getParent(user, mappedCategories, categoryGroup);
            categoriesByYnabKey.put(categoryGroup.getId(), parent);
            for (YnabResponseCategory category : categoryGroup.getCategories()) {
                Category child = getChild(user, mappedCategories, parent, category);
                categoriesByYnabKey.put(category.getId(), child);
            }
        }
        return categoriesByYnabKey;
    }

    private Category getParent(User user, Map<String, YnabCategory> mappedCategories, CategoryGroup categoryGroup) {
        Category parent;
        YnabCategory ynabCategory = mappedCategories.get(categoryGroup.getId());
        if (ynabCategory != null && ynabCategory.getCategory() != null) {
            parent = ynabCategory.getCategory();
        } else {
            parent = new Category();
            parent.setUser(user);
            parent.setName(categoryGroup.getName());
            parent = categoryService.save(parent);
            createYnabCategory(categoryGroup.getId(), parent, user);
        }
        return parent;
    }

    private void createYnabCategory(String id, Category category, User user) {
        YnabCategory ynabCategory = new YnabCategory();
        ynabCategory.setCategory(category);
        ynabCategory.setUser(user);
        ynabCategory.setYnabId(id);
        ynabCategoryRepository.save(ynabCategory);
    }

    private Category getChild(User user,
                              Map<String, YnabCategory> mappedCategories,
                              Category parent,
                              YnabResponseCategory category) {
        Category child;
        YnabCategory ynabCategory = mappedCategories.get(category.getId());
        if (ynabCategory != null && ynabCategory.getCategory() != null) {
            child = ynabCategory.getCategory();
        } else {
            child = new Category();
            child.setUser(user);
            child.setName(category.getName());
            child.setParent(parent);
            child = categoryService.save(child);
            createYnabCategory(category.getId(), child, user);
        }
        return child;
    }

    private void saveEntries(Map<String, Account> accounts,
                             Map<String, Category> categories,
                             User user,
                             List<Transaction> transactions) {
        List<Entry> entries = new ArrayList<>();
        for (Transaction transaction : transactions) {
            Entry entry = new Entry();
            Account account = accounts.get(transaction.getAccountId());
            if (account == null) {
                continue;
            }
            BigDecimal amount = transaction.getAmount()
                    .abs()
                    .divide(BigDecimal.valueOf(1000), RoundingMode.CEILING)
                    .setScale(2, RoundingMode.CEILING);
            entry.setAccount(account)
                    .setCategory(categories.get(transaction.getCategoryId()))
                    .setUser(user)
                    .setValue(amount)
                    .setDescription(transaction.getDescription())
                    .setName(transaction.getName())
                    .setDate(LocalDate.parse(transaction.getDate()))
                    .setIsExpense(transaction.getAmount().compareTo(BigDecimal.ZERO) < 0);
            entries.add(entry);
        }
        entryService.saveAllEntries(entries);
    }
}
