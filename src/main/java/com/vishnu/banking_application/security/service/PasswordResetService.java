package com.vishnu.banking_application.security.service;

import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLogService;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import com.vishnu.banking_application.security.PasswordResetToken;
import com.vishnu.banking_application.security.repository.PasswordResetTokenRepository;
import com.vishnu.banking_application.security.exception.InvalidPasswordResetTokenException;
import com.vishnu.banking_application.user.User;
import com.vishnu.banking_application.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetNotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return;
        }

        passwordResetTokenRepository
                .deleteByUserIdAndUsedFalse(user.getId());

        String rawToken = tokenGenerator.generateToken();

        String tokenHash = tokenGenerator.hashToken(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setUser(user);
        resetToken.setTokenHash(tokenHash);
        resetToken.setExpiresAt(
                Instant.now()
                        .plus(RESET_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES)
        );
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        notificationService.sendResetLink(user, rawToken);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {

        String tokenHash = tokenGenerator.hashToken(rawToken);

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new InvalidPasswordResetTokenException(
                                        "Invalid or expired reset token"
                                )
                        );

        if (resetToken.isUsed()) {
            throw new InvalidPasswordResetTokenException(
                    "Invalid or expired reset token"
            );
        }

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidPasswordResetTokenException(
                    "Invalid or expired reset token"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));

        user.setTokenVersion(user.getTokenVersion() + 1);

        resetToken.setUsed(true);

        auditLogService.record(
                user,
                AuditAction.PASSWORD_RESET,
                AuditResourceType.AUTH,
                null,
                AuditStatus.SUCCESS,
                "Password reset successfully"
        );
    }
}