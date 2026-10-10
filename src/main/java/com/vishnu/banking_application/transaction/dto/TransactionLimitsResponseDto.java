package com.vishnu.banking_application.transaction.dto;

public record TransactionLimitsResponseDto(TransactionLimitDto transfer,
                                           TransactionLimitDto withdrawal) {
}
