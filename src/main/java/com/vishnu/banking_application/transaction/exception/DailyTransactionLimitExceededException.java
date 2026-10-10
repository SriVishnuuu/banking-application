package com.vishnu.banking_application.transaction.exception;

public class DailyTransactionLimitExceededException  extends RuntimeException{

    public DailyTransactionLimitExceededException(String message) {
        super(message);
    }
}
