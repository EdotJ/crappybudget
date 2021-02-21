package com.budgeteer.api.exception.handlers;


import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.exception.BadRequestException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;

import javax.inject.Singleton;

@Singleton
@Requires(classes = {BadRequestException.class})
public class BadRequestHandler extends BaseExceptionHandler<BadRequestException> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, BadRequestException exception) {
        ErrorResponse errorResponse = getErrorResponse(exception);
        return HttpResponse.badRequest(errorResponse);
    }

}
