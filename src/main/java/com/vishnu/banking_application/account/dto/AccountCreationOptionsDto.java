package com.vishnu.banking_application.account.dto;

import com.vishnu.banking_application.account.AccountType;

import java.util.List;

public record AccountCreationOptionsDto(List<AccountType> accountTypes,
                                        List<String> branches) {
}
