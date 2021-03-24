package com.budgeteer.api.exception.handlers;

import com.budgeteer.api.core.TranslatedMessageSource;
import com.budgeteer.api.dto.ErrorResponse;
import com.budgeteer.api.exception.BaseException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Inject;

@Produces
@Requires(classes = {ExceptionHandler.class})
public abstract class BaseExceptionHandler<EXC extends Throwable>
        implements ExceptionHandler<EXC, HttpResponse<ErrorResponse>> {

    @Inject
    TranslatedMessageSource messageSource;

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, EXC exception) {
        if (exception instanceof BaseException) {
            return HttpResponse.badRequest().body(getErrorResponse((BaseException) exception));
        }
        return HttpResponse.badRequest();
    }

    protected ErrorResponse getErrorResponse(BaseException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(exception.getCode());
        errorResponse.setDetail(exception.getDetail());
        String property = exception.getCode() + "." + exception.getReason();
        String localizedString = messageSource.getMessageWithDefaultLocale(property, exception.getMessage());
        errorResponse.setMessage(localizedString);
        return errorResponse;
    }
}
