package com.vishnu.banking_application.admin.mapper;

import com.vishnu.banking_application.admin.dto.AdminBeneficiaryResponseDto;
import com.vishnu.banking_application.admin.dto.AdminBeneficiarySummaryDto;
import com.vishnu.banking_application.beneficiary.Beneficiary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminBeneficiaryMapper {

    @Mapping(target = "customerName",
            expression = "java(getCustomerName(beneficiary))")
    @Mapping(target = "customerEmail",
            source = "customer.user.email")
    AdminBeneficiarySummaryDto toSummaryDto(
            Beneficiary beneficiary
    );

    @Mapping(target = "customerName",
            expression = "java(getCustomerName(beneficiary))")
    @Mapping(target = "customerEmail",
            source = "customer.user.email")
    AdminBeneficiaryResponseDto toResponseDto(
            Beneficiary beneficiary
    );

    default String getCustomerName(Beneficiary beneficiary) {

        if (beneficiary.getCustomer() == null) {
            return null;
        }

        return beneficiary.getCustomer().getFirstName()
                + " "
                + beneficiary.getCustomer().getLastName();
    }
}