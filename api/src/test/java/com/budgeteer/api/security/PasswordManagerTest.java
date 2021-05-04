package com.budgeteer.api.security;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Tag("Unit")
public class PasswordManagerTest {

    @Inject
    private PasswordManager passwordManager;

    @Test
    public void shouldReturnEncodedPassword() {
        String rawPassword = "potato";
        String encodedPassword = passwordManager.encode(rawPassword);
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2"));
    }

    @Test
    public void shouldMatchRawPasswordWithEncodedPassword() {
        String rawPassword = "potato";
        String encodedPassword = passwordManager.encode(rawPassword);
        assertTrue(passwordManager.matches(rawPassword, encodedPassword));
    }

    @Test
    public void shouldNotMatchWhenRawPasswordNotMatching() {
        String rawPassword = "potato";
        String encodedPassword = passwordManager.encode("rawPotato");
        assertFalse(passwordManager.matches(rawPassword, encodedPassword));
    }
}
