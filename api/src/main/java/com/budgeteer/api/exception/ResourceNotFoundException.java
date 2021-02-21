package com.budgeteer.api.exception;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String code, String reason, String defaultMessage, String detail) {
        super(code, reason, defaultMessage, detail);
    }
}
