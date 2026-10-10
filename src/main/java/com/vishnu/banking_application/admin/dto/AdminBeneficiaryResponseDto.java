package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.beneficiary.BeneficiaryStatus;

import java.time.Instant;

public record AdminBeneficiaryResponseDto(Long id,
                                          String customerName,
                                          String customerEmail,
                                          String name,
                                          String accountNumber,
                                          String ifscCode,
                                          String nickname,
                                          BeneficiaryStatus status,
                                          Instant createdAt,
                                          Instant updatedAt) {
}
