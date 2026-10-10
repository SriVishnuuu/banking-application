package com.vishnu.banking_application.admin.controller;

import com.vishnu.banking_application.admin.dto.AdminBeneficiaryResponseDto;
import com.vishnu.banking_application.admin.dto.AdminBeneficiarySummaryDto;
import com.vishnu.banking_application.admin.dto.UpdateBeneficiaryStatusRequestDto;
import com.vishnu.banking_application.admin.service.AdminBeneficiaryService;
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
public class AdminBeneficiaryController {

    private final AdminBeneficiaryService adminBeneficiaryService;

    @GetMapping("/beneficiaries")
    public ResponseEntity<Page<AdminBeneficiarySummaryDto>> getAllBeneficiaries(

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page) {

        Page<AdminBeneficiarySummaryDto> response =
                adminBeneficiaryService.getAllBeneficiaries(page);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/beneficiaries/{beneficiaryId}")
    public ResponseEntity<AdminBeneficiaryResponseDto> getBeneficiary(
            @PathVariable Long beneficiaryId) {

        AdminBeneficiaryResponseDto response =
                adminBeneficiaryService.getBeneficiary(beneficiaryId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/beneficiaries/{beneficiaryId}/status")
    public ResponseEntity<Void> changeBeneficiaryStatus(
            @PathVariable Long beneficiaryId,
            @Valid @RequestBody UpdateBeneficiaryStatusRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        adminBeneficiaryService.changeBeneficiaryStatus(
                beneficiaryId,
                dto, userDetails.getUser()
        );

        return ResponseEntity.noContent().build();
    }
}