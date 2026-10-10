package com.vishnu.banking_application.admin.mapper;

import com.vishnu.banking_application.admin.dto.AdminCustomerResponseDto;
import com.vishnu.banking_application.admin.dto.AdminCustomerSummaryDto;
import com.vishnu.banking_application.customer.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminCustomerResponseMapper {

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "status", source = "user.status")
    AdminCustomerResponseDto toDto(Customer customer);

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "status", source = "user.status")
    AdminCustomerSummaryDto toSummaryDto(Customer customer);
}
