package com.issa.poc.exception;

public class TransInsufficientFundException extends Throwable {
    public static final String STATUS_CODE = "400";
    public static final String DESCRIPTION = "Insufficient Fund";

    public TransInsufficientFundException() {
        super(DESCRIPTION);
    }
}
