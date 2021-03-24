package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.core.TranslatedMessageSource;
import com.budgeteer.api.dto.ErrorResponse;
import com.nimbusds.jose.shaded.json.JSONArray;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.authentication.DefaultAuthorizationExceptionHandler;

import javax.inject.Singleton;

@Singleton
@Replaces(DefaultAuthorizationExceptionHandler.class)
public class CustomAuthorizationExceptionHandler extends DefaultAuthorizationExceptionHandler {

    private final TranslatedMessageSource messageSource;

    public CustomAuthorizationExceptionHandler(TranslatedMessageSource src) {
        super();
        this.messageSource = src;
    }

    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, AuthorizationException exception) {
        Authentication auth = exception.getAuthentication();
        if (auth == null || auth.getAttributes().get("roles") == null) {
            return super.httpResponseWithStatus(request, exception);
        }
        JSONArray roles = (JSONArray) auth.getAttributes().get("roles");
        if (!roles.contains("ROLE_VERIFIED")) {
            return returnUnverifiedResponse();
        }
        return super.httpResponseWithStatus(request, exception);
    }

    private MutableHttpResponse<?> returnUnverifiedResponse() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("UNVERIFIED");
        String defaultMessage = "User is not verified. Please check your email inbox for a verification email";
        String property = errorResponse.getCode();
        String msg = messageSource.getMessageWithDefaultLocale(property, defaultMessage);
        errorResponse.setMessage(msg);
        errorResponse.setDetail("User verification incomplete");
        return HttpResponse.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
