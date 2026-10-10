package com.vishnu.banking_application.account.dto;

public record BranchSummaryDto(String branchCode,
                               String branchName,
                               String ifscCode,
                               String address) {
}
