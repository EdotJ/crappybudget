package com.budgeteer.api.exception;

public class TokenExpiredException extends BaseException {

    public TokenExpiredException(String code, String reason, String defaultMessage, String detail) {
        super(code, reason, defaultMessage, detail);
    }
}
