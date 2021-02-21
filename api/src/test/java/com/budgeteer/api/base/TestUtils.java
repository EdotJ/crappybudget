package com.budgeteer.api.base;

import com.budgeteer.api.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestUtils {

    public static final String TEST_USER_EMAIL = "email@email.com";

    public static User createTestUser() {
        User testUser = new User();
        testUser.setEmail(TEST_USER_EMAIL);
        testUser.setPassword("unsecurepassword");
        testUser.setUsername("username");
        return testUser;
    }

    public static Account createTestAccount(User user) {
        Account account = createTestAccount();
        account.setUser(user);
        return account;
    }

    public static Account createTestAccount() {
        Account account = new Account();
        account.setName("Test Account");
        return account;
    }

    public static Category createTestCategory(User user) {
        Category category = createTestCategory();
        category.setUser(user);
        return category;
    }

    public static Category createTestCategory() {
        Category category = new Category();
        category.setName("Example Category");
        category.setBudgeted(BigDecimal.ZERO);
        return category;
    }

    public static Goal createTestGoal() {
        Goal goal = new Goal();
        goal.setName("Tesla #2060");
        goal.setDate(LocalDate.parse("2060-01-01"));
        goal.setValue(BigDecimal.valueOf(53990));
        goal.setDescription("A Tesla to be bought in 2060. Hopefully it's cheaper by then");
        return goal;
    }

    public static Entry createTestEntry() {
        Entry entry = new Entry();
        entry.setName("Milk");
        entry.setDescription("Just a carton of milk");
        entry.setValue(BigDecimal.valueOf(1.39));
        entry.setDate(LocalDate.parse("2021-02-21"));
        entry.setIsExpense(true);
        return entry;
    }
}
