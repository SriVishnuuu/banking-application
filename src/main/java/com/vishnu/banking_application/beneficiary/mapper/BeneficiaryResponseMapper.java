package com.vishnu.banking_application.beneficiary.mapper;


import com.vishnu.banking_application.beneficiary.Beneficiary;
import com.vishnu.banking_application.beneficiary.dto.BeneficiaryResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BeneficiaryResponseMapper {

    BeneficiaryResponseDto toDto(Beneficiary beneficiary);
}
