package com.budgeteer.api.exception;

public class PasswordValidationException extends BaseException {

    public PasswordValidationException(String reason, String defaultMessage, String detail) {
        super("PASSWORD_VALIDATION", reason, defaultMessage, detail);
    }
}
