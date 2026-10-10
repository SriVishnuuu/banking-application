package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;

import java.time.Instant;

public record AdminAuditLogResponseDto(
        Long id,
        String userEmail,
        AuditAction action,
        AuditResourceType resourceType,
        Long resourceId,
        AuditStatus status,
        String description,
        Instant timestamp
) {
}