package com.vishnu.banking_application.account.dto;


import com.vishnu.banking_application.account.AccountType;
import com.vishnu.banking_application.branch.Branch;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountCreateRequestDto {

    @NotBlank(message = "Branch code is required")
    private String branchCode;

    @NotNull(message = "Account type is required")
    private AccountType accountType;
}
