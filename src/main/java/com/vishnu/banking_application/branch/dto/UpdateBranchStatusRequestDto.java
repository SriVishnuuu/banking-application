package com.vishnu.banking_application.branch.dto;

import com.vishnu.banking_application.branch.BranchStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBranchStatusRequestDto {

    @NotNull(message = "Status is required")
    private BranchStatus status;
}