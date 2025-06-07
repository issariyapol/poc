package com.issa.poc.exception;

public class DepositAmountLessThanOneException extends Throwable {
    public static final String STATUS_CODE = "400";
    public static final String DESCRIPTION = "Amount must greater than 1";
}
