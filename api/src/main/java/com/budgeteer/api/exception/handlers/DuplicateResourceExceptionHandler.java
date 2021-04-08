package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.exception.DuplicateResourceException;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.authentication.AuthenticationExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
public class DuplicateResourceExceptionHandler extends BaseExceptionHandler<DuplicateResourceException> {
    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, DuplicateResourceException exception) {
        ErrorResponse errorResponse = getErrorResponse(exception);
        return HttpResponse.badRequest(errorResponse);
    }
}
