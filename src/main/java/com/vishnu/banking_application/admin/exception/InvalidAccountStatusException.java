package com.vishnu.banking_application.admin.exception;

public class InvalidAccountStatusException extends RuntimeException{

    public InvalidAccountStatusException(String message) {
        super(message);
    }
}
