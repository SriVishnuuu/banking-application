package com.vishnu.banking_application.customer.exception;

public class PhoneNumberAlreadyExistsException extends RuntimeException{

    public PhoneNumberAlreadyExistsException(String message){
        super(message);
    }
}
