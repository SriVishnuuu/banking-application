package com.vishnu.banking_application.branch.exception;

public class BranchNotFoundException extends RuntimeException{

    public BranchNotFoundException(String message){
        super(message);
    }
}
