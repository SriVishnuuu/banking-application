package com.vishnu.banking_application.transaction.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDto {


    @NotBlank(message = "Source account number is required")
    private String sourceAccountNumber;

    @NotNull(message = "Beneficiary is required")
    private Long beneficiaryId;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be greater than zero")
    private BigDecimal amount;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
}
