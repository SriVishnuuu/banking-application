package com.vishnu.banking_application.account.dto;

import com.vishnu.banking_application.account.AccountStatus;
import com.vishnu.banking_application.account.AccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponseDto(String accountNumber,
                                 AccountType accountType,
                                 AccountStatus status,
                                 BranchSummaryDto branch,
                                 Instant createdAt) {}

