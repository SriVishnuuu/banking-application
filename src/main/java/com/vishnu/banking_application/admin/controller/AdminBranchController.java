package com.vishnu.banking_application.admin.controller;

import com.vishnu.banking_application.admin.dto.AdminBranchStatisticsDto;
import com.vishnu.banking_application.branch.dto.AdminBranchResponseDto;
import com.vishnu.banking_application.branch.dto.AdminBranchSummaryDto;
import com.vishnu.banking_application.branch.dto.CreateBranchRequestDto;
import com.vishnu.banking_application.branch.dto.UpdateBranchStatusRequestDto;
import com.vishnu.banking_application.branch.BranchService;
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
@RequestMapping("/api/admin/branches")
@RequiredArgsConstructor
@Validated
public class AdminBranchController {

    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<Page<AdminBranchSummaryDto>> getAllBranches(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(
                branchService.getAllBranches(page, search)
        );
    }

    @GetMapping("/statistics")
    public ResponseEntity<AdminBranchStatisticsDto> getBranchStatistics() {

        return ResponseEntity.ok(
                branchService.getBranchStatistics()
        );
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<AdminBranchResponseDto> getBranchById(
            @PathVariable Long branchId) {

        return ResponseEntity.ok(
                branchService.getBranchById(branchId)
        );
    }

    @PostMapping
    public ResponseEntity<AdminBranchResponseDto> createBranch(
            @Valid @RequestBody CreateBranchRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(
                branchService.createBranch(dto, userDetails.getUser())
        );
    }

    @PatchMapping("/{branchId}/status")
    public ResponseEntity<Void> changeBranchStatus(
            @PathVariable Long branchId,
            @Valid @RequestBody UpdateBranchStatusRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        branchService.changeBranchStatus(branchId, dto, userDetails.getUser());

        return ResponseEntity.noContent().build();
    }
}