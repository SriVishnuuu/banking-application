package com.vishnu.banking_application.transaction.dto;

import java.math.BigDecimal;

public record TransactionLimitDto(BigDecimal dailyLimit,
                                  BigDecimal usedAmount,
                                  BigDecimal remainingAmount) {
}
