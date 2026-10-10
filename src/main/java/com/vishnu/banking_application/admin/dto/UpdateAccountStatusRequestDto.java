package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.account.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAccountStatusRequestDto {

    @NotNull(message = "Status is required")
    private AccountStatus status;
}
