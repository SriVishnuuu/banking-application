package com.vishnu.banking_application.admin.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminDepositRequestDto {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.00", message = "Amount must be greater than 1000")
    BigDecimal amount;

    String description;
}
