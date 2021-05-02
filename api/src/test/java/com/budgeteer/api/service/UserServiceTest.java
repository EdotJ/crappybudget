package com.budgeteer.api.service;

import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.dto.user.ResetPasswordRequest;
import com.budgeteer.api.exception.PasswordValidationException;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.exception.TokenExpiredException;
import com.budgeteer.api.model.PasswordResetToken;
import com.budgeteer.api.model.User;
import com.budgeteer.api.model.VerificationToken;
import com.budgeteer.api.repository.PasswordTokenRepository;
import com.budgeteer.api.repository.UserRepository;
import com.budgeteer.api.repository.VerificationTokenRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
@Tag("Integration")
public class UserServiceTest {

    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @Inject
    VerificationTokenRepository verificationTokenRepository;

    @Inject
    PasswordTokenRepository passwordTokenRepository;

    @Inject
    EmailService emailService;

    VerificationToken token;

    @BeforeEach
    public void setup() {
        userRepository.save(TestUtils.createTestUser());
        User secondUser = TestUtils.createTestUser();
        secondUser.setEmail("testy@protonmail.com");
        secondUser.setUsername("testUser");
        userRepository.save(secondUser);
        token = new VerificationToken();
        token.setExpiration(LocalDateTime.now().plusHours(24));
        token.setValue("supercoolverylongtoken");
        token.setUser(secondUser);
        when(verificationTokenRepository.findByValue(eq("supercoolverylongtoken"))).thenReturn(Optional.of(token));
    }

    @Test
    public void shouldReturnUserByUsername() {
        User actual = userService.getByUsername("testUser");
        assertEquals("testUser", actual.getUsername());
        assertEquals("testy@protonmail.com", actual.getEmail());
    }

    @Test
    public void shouldReturnUserByEmail() {
        User actual = userService.getByEmail(TestUtils.TEST_USER_EMAIL);
        assertEquals(TestUtils.TEST_USER_USERNAME, actual.getUsername());
        assertEquals(TestUtils.TEST_USER_EMAIL, actual.getEmail());
    }

    @Test
    public void shouldReturnNotFoundWhenSearchingByUnknownUsername() {
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () ->
                userService.getByUsername("unknownusername"));
        assertEquals("NOT_FOUND", e.getCode());
    }

    @Test
    public void shouldReturnNotFoundWhenSearchingByUnknownEmail() {
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () ->
                userService.getByEmail("unknownemail"));
        assertEquals("NOT_FOUND", e.getCode());
    }

    @Test
    public void testActivateUser() {
        userService.activateUser("supercoolverylongtoken");
        verify(verificationTokenRepository, times(1)).findByValue(any());
    }

    @Test
    public void shouldFailWithUnknownToken() {
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () ->
                userService.activateUser("unknown token"));
        assertEquals("NOT_FOUND", e.getCode());
    }

    @Test
    public void shouldFailWithExpiredToken() {
        token.setExpiration(LocalDateTime.now().minusHours(1));
        assertThrows(TokenExpiredException.class, () ->
                userService.activateUser("supercoolverylongtoken"));
    }

    @Test
    public void testCreateVerificationToken() {
        User user = new User();
        userService.createVerificationToken(user, "token");
        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository).save(captor.capture());
        assertEquals("token", captor.getValue().getValue());
        assertEquals(user, captor.getValue().getUser());
    }

    @Test
    public void testPasswordResetInit() {
        userService.initiatePasswordReset("testy@protonmail.com");
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordTokenRepository).save(tokenCaptor.capture());
        assertNotNull(tokenCaptor.getValue().getUser());
        assertNotNull(tokenCaptor.getValue().getValue());
        verify(emailService, times(1)).sendEmail(any(), any(), any());
    }

    @Test
    public void testPasswordReset() {
        ResetPasswordRequest passwordRequest = new ResetPasswordRequest("token", "passwordpassword");
        User user = new User();
        user.setUsername("veryuniqueusername");
        user.setEmail("anewmail@email.com");
        user.setPassword("superduperpassword");
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, "token");
        when(passwordTokenRepository.findByValue("token")).thenReturn(Optional.of(passwordResetToken));
        userService.resetPassword(passwordRequest);
        verify(passwordTokenRepository, times(1)).delete(passwordResetToken);
    }

    @Test
    public void shouldFailPasswordResetWhenPasswordTooShort() {
        ResetPasswordRequest passwordRequest = new ResetPasswordRequest("token", "paswd");
        User user = new User();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, "token");
        when(passwordTokenRepository.findByValue("token")).thenReturn(Optional.of(passwordResetToken));
        PasswordValidationException e = assertThrows(PasswordValidationException.class, () ->
                userService.resetPassword(passwordRequest));
        assertEquals("short", e.getReason());
    }

    @Test
    public void shouldFailResetWhenTokenNotFound() {
        ResetPasswordRequest passwordRequest = new ResetPasswordRequest("token1", "passwordpassword");
        User user = new User();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, "token");
        when(passwordTokenRepository.findByValue("token")).thenReturn(Optional.of(passwordResetToken));
        assertThrows(ResourceNotFoundException.class, () ->
                userService.resetPassword(passwordRequest));
    }

    @Test
    public void shouldFailResetWhenTokenExpired() {
        ResetPasswordRequest passwordRequest = new ResetPasswordRequest("token", "passwordpassword");
        User user = new User();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, "token");
        passwordResetToken.setExpiration(LocalDateTime.now().minusHours(1));
        when(passwordTokenRepository.findByValue("token")).thenReturn(Optional.of(passwordResetToken));
        TokenExpiredException e = assertThrows(TokenExpiredException.class, () ->
                userService.resetPassword(passwordRequest));
        assertEquals("PASSWORD_RESET_TOKEN", e.getCode());
    }

    @MockBean(VerificationTokenRepository.class)
    VerificationTokenRepository verificationTokenRepository() {
        return mock(VerificationTokenRepository.class);
    }

    @MockBean(PasswordTokenRepository.class)
    PasswordTokenRepository passwordTokenRepository() {
        return mock(PasswordTokenRepository.class);
    }

    @MockBean(EmailService.class)
    EmailService emailService() {
        return mock(EmailService.class);
    }
}
