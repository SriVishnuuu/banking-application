package com.vishnu.banking_application.audit;

import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void record(
            User user,
            AuditAction action,
            AuditResourceType resourceType,
            Long resourceId,
            AuditStatus status,
            String description) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setStatus(status);
        auditLog.setDescription(description);

        auditLogRepository.save(auditLog);
    }
}