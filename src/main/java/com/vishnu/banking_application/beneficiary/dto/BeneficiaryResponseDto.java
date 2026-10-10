package com.vishnu.banking_application.beneficiary.dto;

import com.vishnu.banking_application.beneficiary.BeneficiaryStatus;

import java.time.Instant;

public record BeneficiaryResponseDto(Long id,
                                     String name,
                                     String accountNumber,
                                     String ifscCode,
                                     String nickname,
                                     BeneficiaryStatus status,
                                     Instant createdAt) {
}
