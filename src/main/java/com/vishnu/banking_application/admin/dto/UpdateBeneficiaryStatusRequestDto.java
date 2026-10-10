package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.beneficiary.BeneficiaryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBeneficiaryStatusRequestDto {

    @NotNull(message = "Status is required")
    BeneficiaryStatus status;
}
