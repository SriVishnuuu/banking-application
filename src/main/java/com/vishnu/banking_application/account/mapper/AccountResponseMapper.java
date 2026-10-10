package com.vishnu.banking_application.account.mapper;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.account.dto.AccountResponseDto;
import com.vishnu.banking_application.account.dto.BranchSummaryDto;
import com.vishnu.banking_application.branch.Branch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountResponseMapper {
    AccountResponseDto toDto(Account account);

    BranchSummaryDto toDto(Branch branch);
}
