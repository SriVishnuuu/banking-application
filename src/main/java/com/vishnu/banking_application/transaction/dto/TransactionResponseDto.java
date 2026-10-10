package com.vishnu.banking_application.transaction.dto;


import com.vishnu.banking_application.transaction.TransactionDirection;
import com.vishnu.banking_application.transaction.TransactionStatus;
import com.vishnu.banking_application.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDto(String transactionReference,
                                     String sourceAccountNumber,
                                     String destinationAccountNumber,
                                     BigDecimal amount,
                                     TransactionType transactionType,
                                     TransactionStatus status,
                                     TransactionDirection direction,
                                     String description,
                                     String failureReason,
                                     Instant createdAt) {
}
