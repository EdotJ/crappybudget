package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.dto.ErrorResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.InternalServerException;

import javax.inject.Singleton;

@Produces
@Singleton
public class InternalServerExceptionHandler extends BaseExceptionHandler<InternalServerException> {

    @Override
    @Error(status = HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpResponse<ErrorResponse> handle(HttpRequest request, InternalServerException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("INTERNAL_ERROR");
        String defaultMessage = "Internal server error has occured. Contact administrator";
        String msg = messageSource.getMessageWithDefaultLocale("INTERNAL_ERROR.text", defaultMessage);
        errorResponse.setMessage(msg);
        errorResponse.setDetail(exception.getMessage());
        return HttpResponse.badRequest().body(errorResponse);
    }
}
