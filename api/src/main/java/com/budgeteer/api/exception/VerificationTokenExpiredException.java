package com.budgeteer.api.exception;

public class VerificationTokenExpiredException extends BaseException {

    public VerificationTokenExpiredException() {
        super("VERIFICATION_TOKEN",
                "expired",
                "Verification period has passed. Do you want to try again?",
                "Verification period has passed");
    }
}
