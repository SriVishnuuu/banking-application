package com.vishnu.banking_application.admin.exception;

public class InvalidCustomerStatusException extends RuntimeException{

    public InvalidCustomerStatusException(String message) {
        super(message);
    }
}
