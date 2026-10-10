package com.vishnu.banking_application.transaction.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequestDto {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Withdrawal amount is required")
    @DecimalMin(
            value = "500.00",
            message = "Withdrawal amount must be greater than 500"
    )
    private BigDecimal amount;

    @Size(
            max = 200,
            message = "Description must not exceed 200 characters"
    )
    private String description;
}
