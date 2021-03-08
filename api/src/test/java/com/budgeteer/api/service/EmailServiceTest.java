package com.budgeteer.api.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@MicronautTest
public class EmailServiceTest {

    @Inject
    EmailService emailService;

    @Test
    @Disabled("Useful for testing emails locally")
    public void testEmail() {
        emailService.sendEmail("", "", "");
    }
}
