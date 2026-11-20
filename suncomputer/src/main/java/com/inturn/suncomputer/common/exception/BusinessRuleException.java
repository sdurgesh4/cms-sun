package com.inturn.suncomputer.common.exception;

public class BusinessRuleException
        extends RuntimeException {

    public BusinessRuleException(
            String message
    ) {
        super(message);
    }
}