package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.beneficiary.BeneficiaryStatus;

public record AdminBeneficiarySummaryDto(Long id,
                                         String customerName,
                                         String customerEmail,
                                         String accountNumber,
                                         BeneficiaryStatus status) {
}
