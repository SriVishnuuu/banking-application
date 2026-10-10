package com.vishnu.banking_application.branch.exception;

public class BranchAlreadyExistsException extends RuntimeException{

    public BranchAlreadyExistsException(String message) {
        super(message);
    }
}
