package com.issa.poc.exception;

public class AccountNotFoundException extends Throwable {
    public static final String STATUS_CODE = "400";
    public static final String DESCRIPTION = "Account Not Found";
}
