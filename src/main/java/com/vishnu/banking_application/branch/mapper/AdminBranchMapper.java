package com.vishnu.banking_application.branch.mapper;

import com.vishnu.banking_application.branch.dto.AdminBranchResponseDto;
import com.vishnu.banking_application.branch.dto.AdminBranchSummaryDto;
import com.vishnu.banking_application.branch.Branch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminBranchMapper {

    AdminBranchSummaryDto toSummaryDto(Branch branch);

    AdminBranchResponseDto toResponseDto(Branch branch);
}