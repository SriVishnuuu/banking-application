package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.user.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCustomerStatusRequestDto {

    @NotNull(message = "Status is required")
    private UserStatus status;
}
