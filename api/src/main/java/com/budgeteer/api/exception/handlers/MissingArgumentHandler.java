package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.dto.ErrorResponse;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.netty.converters.UnsatisfiedRouteHandler;
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException;

import javax.inject.Singleton;

@Singleton
@Requires(classes = {UnsatisfiedRouteException.class})
@Replaces(UnsatisfiedRouteHandler.class)
public class MissingArgumentHandler extends BaseExceptionHandler<UnsatisfiedRouteException> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, UnsatisfiedRouteException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("BAD_REQUEST");
        String defaultMessage = "Argument {0} is missing";
        String property = errorResponse.getCode() + ".missingArgument";
        String argName = exception.getArgument().getName();
        String msg = messageSource.getMessageWithDefaultLocale(property, defaultMessage, argName);
        errorResponse.setMessage(msg);
        errorResponse.setDetail("Missing argument: " + argName);
        return HttpResponse.badRequest().body(errorResponse);
    }
}
