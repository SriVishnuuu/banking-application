package com.vishnu.banking_application.branch.dto;

import com.vishnu.banking_application.branch.BranchStatus;

public record AdminBranchSummaryDto(Long id,
                                    String branchCode,
                                    String branchName,
                                    String ifscCode,
                                    BranchStatus status) {
}
