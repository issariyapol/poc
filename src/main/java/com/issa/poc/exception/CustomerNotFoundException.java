package com.issa.poc.exception;

public class CustomerNotFoundException extends Throwable {
    public static final String STATUS_CODE = "400";
    public static final String DESCRIPTION = "Customer Not Fund";
}
