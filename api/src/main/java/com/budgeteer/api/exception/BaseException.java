package com.budgeteer.api.exception;

public abstract class BaseException extends RuntimeException {

    private final String code;
    private final String detail;
    private final String reason;

    public BaseException(String code, String reason, String defaultMessage, String detail) {
        super(defaultMessage);
        this.code = code;
        this.detail = detail;
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }

    public String getReason() {
        return reason;
    }
}
