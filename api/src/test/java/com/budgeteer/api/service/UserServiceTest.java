package com.budgeteer.api.service;

import com.budgeteer.api.base.DatabaseCleanupExtension;
import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.UserRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
public class UserServiceTest {

    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.save(TestUtils.createTestUser());
        User secondUser = TestUtils.createTestUser();
        secondUser.setEmail("testy@protonmail.com");
        secondUser.setUsername("testUser");
        userRepository.save(secondUser);
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
}
