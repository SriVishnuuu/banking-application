package com.vishnu.banking_application.admin.dto;

public record AdminAccountStatisticsDto(
        long totalAccounts,
        long activeAccounts,
        long inactiveAccounts,
        long frozenAccounts,
        long closedAccounts
) {
}