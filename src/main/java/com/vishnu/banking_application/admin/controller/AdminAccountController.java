package com.vishnu.banking_application.admin.controller;


import com.vishnu.banking_application.admin.dto.*;
import com.vishnu.banking_application.admin.service.AdminAccountService;
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
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    @GetMapping("/accounts")
    public ResponseEntity<Page<AdminAccountSummaryDto>> getAllAccounts(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,

            @RequestParam(required = false)
            String search) {

        return ResponseEntity.ok(
                adminAccountService.getAllAccounts(page, search)
        );
    }

    @GetMapping("/accounts/statistics")
    public ResponseEntity<AdminAccountStatisticsDto> getAccountStatistics() {

        return ResponseEntity.ok(
                adminAccountService.getAccountStatistics()
        );
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AdminAccountResponseDto> getAccount(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                adminAccountService.getAccount(accountId)
        );
    }

    @PatchMapping("/accounts/{accountId}/status")
    public ResponseEntity<Void> changeAccountStatus(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountStatusRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        adminAccountService.changeAccountStatus(accountId, dto, userDetails.getUser());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accounts/{accountId}/deposit")
    public ResponseEntity<AdminDepositResponseDto> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody AdminDepositRequestDto dto) {

        AdminDepositResponseDto response =
                adminAccountService.deposit(accountId, dto);

        return ResponseEntity.ok(response);
    }
}
