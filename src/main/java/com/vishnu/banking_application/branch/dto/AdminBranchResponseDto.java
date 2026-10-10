package com.vishnu.banking_application.branch.dto;

import com.vishnu.banking_application.branch.BranchStatus;

import java.time.Instant;

public record AdminBranchResponseDto(Long id,
                                     String branchCode,
                                     String branchName,
                                     String ifscCode,
                                     String address,
                                     String phoneNumber,
                                     BranchStatus status,
                                     Instant createdAt,
                                     Instant updatedAt) {
}
