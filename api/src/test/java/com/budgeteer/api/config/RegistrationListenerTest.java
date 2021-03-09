package com.budgeteer.api.config;

import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.VerificationTokenRepository;
import com.budgeteer.api.service.EmailService;
import com.budgeteer.api.service.UserService;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.util.StringUtils;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@MicronautTest
public class RegistrationListenerTest {

    @Inject
    ApplicationEventPublisher publisher;

    @Inject
    UserService userService;

    @Inject
    EmailService emailService;

    @Test
    public void testEventHandling() {
        User user = new User();
        user.setEmail("testemail@email.com");
        publisher.publishEvent(new OnRegistrationCompleteEvent(user, "http://localhost/"));
        verify(userService).createVerificationToken(eq(user), any());
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), messageCaptor.capture());
        assertEquals("testemail@email.com", emailCaptor.getValue());
        assertEquals("Registration Confirmation on Budget Site", subjectCaptor.getValue());
        assertTrue(StringUtils.hasText(messageCaptor.getValue()));
    }


    @MockBean(UserService.class)
    UserService userService() {
        return mock(UserService.class);
    }

    @MockBean(EmailService.class)
    EmailService emailService() {
        return mock(EmailService.class);
    }
}
