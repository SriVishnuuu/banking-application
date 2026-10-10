package com.vishnu.banking_application.admin.dto;

public record AdminCustomerStatisticsDto(
        long totalCustomers,
        long activeCustomers,
        long suspendedCustomers,
        long blockedCustomers
) {
}