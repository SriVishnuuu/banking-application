package com.vishnu.banking_application.admin.mapper;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.admin.dto.AdminDepositResponseDto;
import com.vishnu.banking_application.admin.dto.AdminTransactionResponseDto;
import com.vishnu.banking_application.admin.dto.AdminTransactionSummaryDto;
import com.vishnu.banking_application.transaction.BankTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminTransactionMapper {

    @Mapping(target = "accountNumber",
            source = "destinationAccount.accountNumber")
    AdminDepositResponseDto toDto(BankTransaction transaction);

    @Mapping(target = "sourceAccountNumber",
            source = "sourceAccount.accountNumber")
    @Mapping(target = "destinationAccountNumber",
            source = "destinationAccount.accountNumber")
    AdminTransactionSummaryDto toSummaryDto(BankTransaction transaction);

    @Mapping(target = "sourceAccountNumber",
            source = "sourceAccount.accountNumber")
    @Mapping(target = "sourceCustomerName",
            expression = "java(getCustomerName(transaction.getSourceAccount()))")
    @Mapping(target = "destinationAccountNumber",
            source = "destinationAccount.accountNumber")
    @Mapping(target = "destinationCustomerName",
            expression = "java(getCustomerName(transaction.getDestinationAccount()))")
    AdminTransactionResponseDto toResponseDto(BankTransaction transaction);

    default String getCustomerName(Account account) {

        if (account == null || account.getCustomer() == null) {
            return null;
        }

        return account.getCustomer().getFirstName()
                + " "
                + account.getCustomer().getLastName();
    }
}