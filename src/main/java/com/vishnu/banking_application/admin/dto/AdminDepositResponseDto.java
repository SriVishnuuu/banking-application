package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.transaction.TransactionStatus;
import com.vishnu.banking_application.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminDepositResponseDto(String transactionReference,
                                      String accountNumber,
                                      BigDecimal amount,
                                      TransactionType transactionType,
                                      TransactionStatus status,
                                      String description,
                                      Instant createdAt) {
}
