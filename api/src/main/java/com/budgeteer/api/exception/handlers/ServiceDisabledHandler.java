package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.exception.ServiceDisabledException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;

import javax.inject.Singleton;

@Singleton
@Produces
public class ServiceDisabledHandler extends BaseExceptionHandler<ServiceDisabledException> {
    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, ServiceDisabledException exception) {
        ErrorResponse errorResponse = getErrorResponse(exception);
        // TODO: maybe forbidden is not the best choice here
        return HttpResponse.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
