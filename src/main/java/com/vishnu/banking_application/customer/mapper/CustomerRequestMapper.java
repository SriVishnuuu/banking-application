package com.vishnu.banking_application.customer.mapper;


import com.vishnu.banking_application.customer.Customer;
import com.vishnu.banking_application.customer.dto.CustomerProfileRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerRequestMapper {

    Customer toEntity(CustomerProfileRequestDto dto);

}
