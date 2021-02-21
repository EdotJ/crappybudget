package com.budgeteer.api.exception;

public class BadRequestException extends BaseException {

    public BadRequestException(String code, String reason, String defaultMessage, String detail) {
        super(code, reason, defaultMessage, detail);
    }
}
