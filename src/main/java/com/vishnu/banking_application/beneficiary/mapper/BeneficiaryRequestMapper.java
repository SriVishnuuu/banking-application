package com.vishnu.banking_application.beneficiary.mapper;

import com.vishnu.banking_application.beneficiary.Beneficiary;
import com.vishnu.banking_application.beneficiary.dto.BeneficiaryCreateRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BeneficiaryRequestMapper {

    Beneficiary toEntity(BeneficiaryCreateRequestDto beneficiaryCreateRequestDto);
}
