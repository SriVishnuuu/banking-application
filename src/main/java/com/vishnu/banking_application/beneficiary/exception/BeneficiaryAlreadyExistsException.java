package com.vishnu.banking_application.beneficiary.exception;

public class BeneficiaryAlreadyExistsException extends RuntimeException{

    public BeneficiaryAlreadyExistsException(String message){
        super(message);
    }
}
