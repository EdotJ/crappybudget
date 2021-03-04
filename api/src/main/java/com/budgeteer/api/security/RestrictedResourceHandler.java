package com.budgeteer.api.security;

import com.budgeteer.api.model.User;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.utils.SecurityService;

import java.util.Optional;

public class RestrictedResourceHandler {

    protected SecurityService securityService;

    public RestrictedResourceHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    protected Long getAuthenticatedUserId() {
        if (securityService.getAuthentication().isEmpty()) {
            throw new AuthorizationException(securityService.getAuthentication().orElse(null));
        }
        return (Long) securityService.getAuthentication().get().getAttributes().get("id");
    }

    protected void checkIfCanAccessResource(User user) {
        Optional<Authentication> auth = securityService.getAuthentication();

        if (auth.isEmpty() || !user.getId().equals(auth.get().getAttributes().get("id"))) {
            throw new AuthorizationException(securityService.getAuthentication().orElse(null));
        }
    }
}
