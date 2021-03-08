package com.budgeteer.api.service;

import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.exception.VerificationTokenExpiredException;
import com.budgeteer.api.model.User;
import com.budgeteer.api.model.VerificationToken;
import com.budgeteer.api.repository.UserRepository;
import com.budgeteer.api.repository.VerificationTokenRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
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
public class UserServiceTest {

    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @Inject
    VerificationTokenRepository tokenRepository;

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
        when(tokenRepository.findByValue(eq("supercoolverylongtoken"))).thenReturn(Optional.of(token));
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
        verify(tokenRepository, times(1)).findByValue(any());
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
        assertThrows(VerificationTokenExpiredException.class, () ->
                userService.activateUser("supercoolverylongtoken"));
    }

    @Test
    public void testCreateVerificationToken() {
        User user = new User();
        userService.createVerificationToken(user, "token");
        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(tokenRepository).save(captor.capture());
        assertEquals("token", captor.getValue().getValue());
        assertEquals(user, captor.getValue().getUser());
    }

    @MockBean(VerificationTokenRepository.class)
    VerificationTokenRepository verificationTokenRepository() {
        return mock(VerificationTokenRepository.class);
    }
}
