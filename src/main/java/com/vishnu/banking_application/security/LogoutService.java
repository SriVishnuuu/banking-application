package com.vishnu.banking_application.security;

import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLogService;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import com.vishnu.banking_application.security.repository.RevokedTokenRepository;
import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public void logout(String token, User user) {

        String tokenId = jwtService.extractTokenId(token);
        Instant expiresAt = jwtService.extractExpiration(token);

        if (!revokedTokenRepository.existsByTokenId(tokenId)) {

            RevokedToken revokedToken = new RevokedToken();

            revokedToken.setTokenId(tokenId);
            revokedToken.setExpiresAt(expiresAt);

            revokedTokenRepository.save(revokedToken);
        }

        auditLogService.record(
                user,
                AuditAction.LOGOUT,
                AuditResourceType.AUTH,
                null,
                AuditStatus.SUCCESS,
                "User logout successful"
        );
    }
}