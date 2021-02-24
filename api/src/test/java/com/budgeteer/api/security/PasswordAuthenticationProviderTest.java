package com.budgeteer.api.security;

import com.budgeteer.api.base.TestUtils;
import com.budgeteer.api.exception.ResourceNotFoundException;
import com.budgeteer.api.model.User;
import com.budgeteer.api.repository.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import java.util.Optional;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
public class PasswordAuthenticationProviderTest {

    @Inject
    UserRepository userRepository;

    @Inject
    private PasswordAuthenticationProvider authProvider;

    @BeforeEach
    public void setup() {
        User user = TestUtils.createSecureTestUser();
        when(userRepository.findByEmail(eq(TestUtils.TEST_USER_EMAIL))).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(not(eq(TestUtils.TEST_USER_EMAIL)))).thenReturn(Optional.empty());
        when(userRepository.findByUsername(eq(TestUtils.TEST_USER_USERNAME))).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(not(eq(TestUtils.TEST_USER_USERNAME)))).thenReturn(Optional.empty());

    }

    @Test
    public void shouldAuthenticateWithUsernameAndPassword() {
        AuthenticationRequest<String, String> authRequest =
                new UsernamePasswordCredentials("username", "unsecurepassword");
        Publisher<AuthenticationResponse> resp = authProvider.authenticate(mock(HttpRequest.class), authRequest);
        TestSubscriber<AuthenticationResponse> testSubscriber = new TestSubscriber<>();
        resp.subscribe(testSubscriber);
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
    }

    @Test
    public void shouldAuthenticateWithEmailAndPassword() {
        AuthenticationRequest<String, String> authRequest =
                new UsernamePasswordCredentials(TestUtils.TEST_USER_EMAIL, "unsecurepassword");
        Publisher<AuthenticationResponse> resp = authProvider.authenticate(mock(HttpRequest.class), authRequest);
        TestSubscriber<AuthenticationResponse> testSubscriber = new TestSubscriber<>();
        resp.subscribe(testSubscriber);
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
    }

    @Test
    public void shouldFailWhenUserNotFound() {
        AuthenticationRequest<String, String> authRequest =
                new UsernamePasswordCredentials("unknownuser", "unsecurepassword");
        Publisher<AuthenticationResponse> resp = authProvider.authenticate(mock(HttpRequest.class), authRequest);
        TestSubscriber<AuthenticationResponse> testSubscriber = new TestSubscriber<>();
        resp.subscribe(testSubscriber);
        testSubscriber.assertError(ResourceNotFoundException.class);
        testSubscriber.assertNotComplete();
    }

    @Test
    public void shouldFailWhenNotMatching() {
        AuthenticationRequest<String, String> authRequest =
                new UsernamePasswordCredentials(TestUtils.TEST_USER_USERNAME, "badpassword");
        Publisher<AuthenticationResponse> resp = authProvider.authenticate(mock(HttpRequest.class), authRequest);
        TestSubscriber<AuthenticationResponse> testSubscriber = new TestSubscriber<>();
        resp.subscribe(testSubscriber);
        testSubscriber.assertError(AuthenticationException.class);
        testSubscriber.assertNotComplete();
    }

    @MockBean(UserRepository.class)
    UserRepository userRepository() {
        return mock(UserRepository.class);
    }
}
