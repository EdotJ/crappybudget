package com.budgeteer.api.base;

import com.budgeteer.api.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class TestUtils {

    public static final String TEST_USER_EMAIL = "email@email.com";
    public static final String TEST_USER_USERNAME = "username";
    public static final String TEST_USER_PASSWORD = "unsecurepassword";

    public static User createTestUser() {
        User testUser = new User();
        testUser.setEmail(TEST_USER_EMAIL);
        testUser.setPassword(TEST_USER_PASSWORD);
        testUser.setUsername(TEST_USER_USERNAME);
        testUser.setIsVerified(true);
        return testUser;
    }

    public static User createSecureTestUser() {
        User testUser = createTestUser();
        testUser.setPassword("$2a$10$IZ44SyZWqAMgBZZhQ0FE9uUjQYdCpDDoFBDvMcDYn2dCddIq7iGAu");
        return testUser;
    }

    public static User createAdditionalTestUser() {
        User user = createSecureTestUser();
        user.setUsername("secondTestUser");
        user.setEmail("secondTestUser@mail.com");
        user.setIsVerified(false);
        return user;
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
        goal.setCurrentValue(BigDecimal.ZERO);
        goal.setGoalValue(BigDecimal.valueOf(53990));
        goal.setDescription("A Tesla to be bought in 2060. Hopefully it's cheaper by then");
        return goal;
    }

    public static Entry createTestEntry() {
        Entry entry = new Entry();
        entry.setName("Milk");
        entry.setDescription("Just a carton of milk");
        entry.setValue(BigDecimal.valueOf(1.39));
        entry.setDate(LocalDate.now());
        entry.setIsExpense(true);
        return entry;
    }

    public static GoalType createTestGoalType() {
        GoalType goalType = new GoalType();
        goalType.setId(1L);
        goalType.setName("SAVE");
        return goalType;
    }

    public static String getResponseString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("test-responses/" + fileName)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}
