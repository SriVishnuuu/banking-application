package com.vishnu.banking_application.customer.mapper;


import com.vishnu.banking_application.customer.Customer;
import com.vishnu.banking_application.customer.dto.CustomerProfileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerResponseMapper {
    @Mapping(target = "email", source = "user.email")
    CustomerProfileResponseDto toDto(Customer customer);

}
