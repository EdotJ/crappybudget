package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.dto.ErrorResponse;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.errors.OauthErrorResponseExceptionHandler;

import javax.inject.Singleton;

@Singleton
@Replaces(OauthErrorResponseExceptionHandler.class)
public class RefreshTokenErrorHandler extends BaseExceptionHandler<OauthErrorResponseException> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, OauthErrorResponseException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("INVALID_TOKEN");
        String defaultMessage = "Authorization error occurred. Please check credentials and try again later";
        String property = errorResponse.getCode() + ".refresh";
        String msg = messageSource.getMessageWithDefaultLocale(property, defaultMessage);
        errorResponse.setMessage(msg);
        errorResponse.setDetail(exception.getErrorDescription());
        return HttpResponse.badRequest().body(errorResponse);
    }
}
