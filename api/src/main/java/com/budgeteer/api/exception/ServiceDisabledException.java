package com.budgeteer.api.exception;

public class ServiceDisabledException extends BaseException {
    public ServiceDisabledException(String reason, String defaultMessage, String detail) {
        super("SERVICE_DISABLED", reason, defaultMessage, detail);
    }
}
