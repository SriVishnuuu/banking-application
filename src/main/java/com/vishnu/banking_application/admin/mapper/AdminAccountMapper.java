package com.vishnu.banking_application.admin.mapper;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.admin.dto.AdminAccountResponseDto;
import com.vishnu.banking_application.admin.dto.AdminAccountSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminAccountMapper {

    @Mapping(
            target = "customerName",
            expression = "java(account.getCustomer().getFirstName() + \" \" + account.getCustomer().getLastName())"
    )
    @Mapping(target = "branchCode", source = "branch.branchCode")
    AdminAccountSummaryDto toSummaryDto(Account account);

    @Mapping(
            target = "customerName",
            expression = "java(account.getCustomer().getFirstName() + \" \" + account.getCustomer().getLastName())"
    )
    @Mapping(target = "customerEmail", source = "customer.user.email")
    @Mapping(target = "branchName", source = "branch.branchName")
    @Mapping(target = "branchIfscCode", source = "branch.ifscCode")
    AdminAccountResponseDto toDto(Account account);
}