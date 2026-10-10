package com.vishnu.banking_application.branch.exception;

public class BranchUnavailableException extends RuntimeException{

    public BranchUnavailableException(String message){
        super(message);
    }
}
