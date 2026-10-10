package com.vishnu.banking_application.security.service;


import com.vishnu.banking_application.security.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RevokedTokenCleanupService {

    private final RevokedTokenRepository revokedTokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {

        revokedTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}