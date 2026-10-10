package com.vishnu.banking_application.security.exception;

public class InvalidPasswordResetTokenException extends RuntimeException{
    public InvalidPasswordResetTokenException(String message) {
        super(message);
    }
}
