package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.exception.ResourceNotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;

import javax.inject.Singleton;

@Singleton
@Requires(classes = {ResourceNotFoundException.class})
public class ResourceNotFoundHandler extends BaseExceptionHandler<ResourceNotFoundException> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, ResourceNotFoundException exception) {
        ErrorResponse errorResponse = getErrorResponse(exception);
        return HttpResponse.notFound(errorResponse);
    }
}
