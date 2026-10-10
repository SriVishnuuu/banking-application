package com.vishnu.banking_application.admin.dto;

public record AdminBranchStatisticsDto(
        long totalBranches,
        long activeBranches,
        long inactiveBranches
) {
}