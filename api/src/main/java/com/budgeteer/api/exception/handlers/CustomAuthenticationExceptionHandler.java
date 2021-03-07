package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.config.TranslatedMessageSource;
import com.budgeteer.api.dto.ErrorResponse;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
@Replaces(AuthenticationExceptionHandler.class)
public class CustomAuthenticationExceptionHandler extends AuthenticationExceptionHandler {

    private final TranslatedMessageSource messageSource;

    public CustomAuthenticationExceptionHandler(ApplicationEventPublisher eventPublisher, TranslatedMessageSource src) {
        super(eventPublisher);
        this.messageSource = src;
    }

    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, AuthenticationException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("AUTH_ERROR");
        String defaultMessage = "Authorization error occurred. Please check credentials and try again later";
        String property = errorResponse.getCode() + "." + exception.getMessage();
        String msg = messageSource.getMessageWithDefaultLocale(property, defaultMessage);
        errorResponse.setMessage(msg);
        errorResponse.setDetail(exception.getMessage());
        return HttpResponse.unauthorized().body(errorResponse);
    }
}
