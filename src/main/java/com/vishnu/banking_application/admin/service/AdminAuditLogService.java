package com.vishnu.banking_application.admin.service;

import com.vishnu.banking_application.admin.dto.AdminAuditLogResponseDto;
import com.vishnu.banking_application.admin.dto.AdminAuditLogSummaryDto;
import com.vishnu.banking_application.admin.exception.AuditLogNotFoundException;
import com.vishnu.banking_application.admin.exception.InvalidAuditLogFilterException;
import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLog;
import com.vishnu.banking_application.audit.AuditLogRepository;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AdminAuditLogService {

    private static final int PAGE_SIZE = 20;

    private static final ZoneId INDIA_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public Page<AdminAuditLogSummaryDto> getAuditLogs(
            int page,
            AuditAction action,
            AuditResourceType resourceType,
            AuditStatus status,
            LocalDate from,
            LocalDate to) {

        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidAuditLogFilterException(
                    "From date cannot be after To date");
        }

        PageRequest pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(
                        Sort.Direction.DESC,
                        "timestamp"
                )
        );

        Specification<AuditLog> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (action != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("action"),
                                    action
                            )
            );
        }

        if (resourceType != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("resourceType"),
                                    resourceType
                            )
            );
        }

        if (status != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("status"),
                                    status
                            )
            );
        }

        if (from != null) {

            Instant fromInstant = from
                    .atStartOfDay(INDIA_ZONE)
                    .toInstant();

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("timestamp"),
                                    fromInstant
                            )
            );
        }

        if (to != null) {

            Instant toExclusive = to
                    .plusDays(1)
                    .atStartOfDay(INDIA_ZONE)
                    .toInstant();

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThan(
                                    root.get("timestamp"),
                                    toExclusive
                            )
            );
        }

        return auditLogRepository
                .findAll(specification, pageable)
                .map(this::toSummaryDto);
    }

    @Transactional(readOnly = true)
    public AdminAuditLogResponseDto getAuditLog(Long auditLogId) {

        AuditLog auditLog = auditLogRepository.findById(auditLogId)
                .orElseThrow(() ->
                        new AuditLogNotFoundException(
                                "Audit log not found"
                        ));

        String userEmail = auditLog.getUser() != null
                ? auditLog.getUser().getEmail()
                : null;

        return new AdminAuditLogResponseDto(
                auditLog.getId(),
                userEmail,
                auditLog.getAction(),
                auditLog.getResourceType(),
                auditLog.getResourceId(),
                auditLog.getStatus(),
                auditLog.getDescription(),
                auditLog.getTimestamp()
        );
    }

    private AdminAuditLogSummaryDto toSummaryDto(
            AuditLog auditLog) {

        String userEmail = auditLog.getUser() != null
                ? auditLog.getUser().getEmail()
                : null;

        return new AdminAuditLogSummaryDto(
                auditLog.getId(),
                userEmail,
                auditLog.getAction(),
                auditLog.getResourceType(),
                auditLog.getResourceId(),
                auditLog.getStatus(),
                auditLog.getDescription(),
                auditLog.getTimestamp()
        );
    }
}