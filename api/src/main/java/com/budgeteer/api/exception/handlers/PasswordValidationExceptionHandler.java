package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.exception.PasswordValidationException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;

import javax.inject.Singleton;

@Produces
@Singleton
public class PasswordValidationExceptionHandler extends BaseExceptionHandler<PasswordValidationException> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, PasswordValidationException exception) {
        ErrorResponse errorResponse = getErrorResponse(exception);
        return HttpResponse.badRequest(errorResponse);
    }
}
