package com.vishnu.banking_application.transaction.mapper;

import com.vishnu.banking_application.transaction.BankTransaction;
import com.vishnu.banking_application.transaction.dto.TransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionResponseMapper {
    @Mapping(
            target = "sourceAccountNumber",
            source = "sourceAccount.accountNumber"
    )
    @Mapping(
            target = "destinationAccountNumber",
            source = "destinationAccount.accountNumber"
    )
    TransactionResponseDto toDto(BankTransaction bankTransaction);
}
