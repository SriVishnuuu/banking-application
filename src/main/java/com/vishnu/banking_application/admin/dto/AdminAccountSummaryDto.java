package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.account.AccountStatus;
import com.vishnu.banking_application.account.AccountType;

import java.math.BigDecimal;

public record AdminAccountSummaryDto(Long id,
                                     String accountNumber,
                                     String customerName,
                                     String branchCode,
                                     AccountType accountType,
                                     BigDecimal balance,
                                     AccountStatus status) {
}
