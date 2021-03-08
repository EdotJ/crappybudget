package com.budgeteer.api.security;

import com.budgeteer.api.model.User;
import com.budgeteer.api.service.UserService;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.Maybe;
import org.apache.commons.validator.routines.EmailValidator;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class PasswordAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    private final PasswordManager passwordManager;

    public PasswordAuthenticationProvider(UserService userService, PasswordManager passwordManager) {
        this.userService = userService;
        this.passwordManager = passwordManager;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Maybe.<AuthenticationResponse>create(emitter -> {
            User user;
            if (EmailValidator.getInstance().isValid(authenticationRequest.getIdentity().toString())) {
                user = userService.getByEmail(authenticationRequest.getIdentity().toString());
            } else {
                user = userService.getByUsername(authenticationRequest.getIdentity().toString());
            }
            Optional<AuthenticationFailed> authFailed = validate(user, authenticationRequest);
            if (authFailed.isPresent()) {
                emitter.onError(new AuthenticationException(authFailed.get()));
            } else {
                List<String> roles = new ArrayList<>();
                if (user.isVerified()) {
                    roles.add("ROLE_VERIFIED");
                }
                emitter.onSuccess(new IdentifierUserDetails(user.getUsername(), roles, user.getId()));
            }
        }).toFlowable();
    }

    private Optional<AuthenticationFailed> validate(User user, AuthenticationRequest<?, ?> authenticationRequest) {
        AuthenticationFailed authFailed = null;
        if (!passwordManager.matches(authenticationRequest.getSecret().toString(), user.getPassword())) {
            authFailed = new AuthenticationFailed(FailReason.not_matching.name());
        }
        return Optional.ofNullable(authFailed);
    }
}
