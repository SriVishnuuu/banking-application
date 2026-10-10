package com.vishnu.banking_application.admin.controller;

import com.vishnu.banking_application.admin.dto.*;
import com.vishnu.banking_application.admin.service.AdminCustomerService;
import com.vishnu.banking_application.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/admin")
public class AdminCustomerController {

    private final AdminCustomerService adminCustomerService;

    @GetMapping("/customers")
    public ResponseEntity<Page<AdminCustomerSummaryDto>> getAllCustomers(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,

            @RequestParam(required = false)
            String search) {

        return ResponseEntity.ok(
                adminCustomerService.getAllCustomers(page, search)
        );
    }

    @GetMapping("/customers/statistics")
    public ResponseEntity<AdminCustomerStatisticsDto> getCustomerStatistics() {

        return ResponseEntity.ok(
                adminCustomerService.getCustomerStatistics()
        );
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<AdminCustomerResponseDto> getCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                adminCustomerService.getCustomer(customerId)
        );
    }

    @PatchMapping("/customers/{customerId}/status")
    public ResponseEntity<Void> changeCustomerStatus(
            @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerStatusRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        adminCustomerService.changeUserStatus(customerId, dto, userDetails.getUser());

        return ResponseEntity.noContent().build();
    }
}