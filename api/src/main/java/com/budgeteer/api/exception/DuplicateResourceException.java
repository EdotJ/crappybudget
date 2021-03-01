package com.budgeteer.api.exception;

public class DuplicateResourceException extends BaseException {
    public DuplicateResourceException(String code, String reason, String defaultMessage, String detail) {
        super(code, reason, defaultMessage, detail);
    }
}
