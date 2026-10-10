package com.vishnu.banking_application.security.exception;

public class PasswordResetEmailSendingException extends RuntimeException{
    public PasswordResetEmailSendingException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
