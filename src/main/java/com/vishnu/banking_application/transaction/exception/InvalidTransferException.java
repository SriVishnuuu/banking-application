package com.vishnu.banking_application.transaction.exception;

public class InvalidTransferException extends RuntimeException{

    public InvalidTransferException(String message){
        super(message);
    }
}
