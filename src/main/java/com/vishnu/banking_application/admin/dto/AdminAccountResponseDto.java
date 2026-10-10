package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.account.AccountStatus;
import com.vishnu.banking_application.account.AccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminAccountResponseDto(Long id,
                                      String accountNumber,
                                      String customerName,
                                      String customerEmail,
                                      String branchName,
                                      String branchIfscCode,
                                      AccountType accountType,
                                      BigDecimal balance,
                                      AccountStatus status,
                                      Instant createdAt,
                                      Instant updatedAt) {
}
