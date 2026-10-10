package com.vishnu.banking_application.admin.controller;

import com.vishnu.banking_application.admin.dto.AdminAuditLogResponseDto;
import com.vishnu.banking_application.admin.dto.AdminAuditLogSummaryDto;
import com.vishnu.banking_application.admin.service.AdminAuditLogService;
import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditLogController {

    private final AdminAuditLogService adminAuditLogService;

    @GetMapping
    public ResponseEntity<Page<AdminAuditLogSummaryDto>> getAuditLogs(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(required = false)
            AuditAction action,

            @RequestParam(required = false)
            AuditResourceType resourceType,

            @RequestParam(required = false)
            AuditStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        Page<AdminAuditLogSummaryDto> response =
                adminAuditLogService.getAuditLogs(
                        page,
                        action,
                        resourceType,
                        status,
                        from,
                        to
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{auditLogId}")
    public ResponseEntity<AdminAuditLogResponseDto> getAuditLog(
            @PathVariable Long auditLogId) {

        AdminAuditLogResponseDto response =
                adminAuditLogService.getAuditLog(auditLogId);

        return ResponseEntity.ok(response);
    }
}