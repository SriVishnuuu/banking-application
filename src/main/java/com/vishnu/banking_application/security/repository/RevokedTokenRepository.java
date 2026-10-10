package com.vishnu.banking_application.security.repository;

import com.vishnu.banking_application.security.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface RevokedTokenRepository
        extends JpaRepository<RevokedToken, Long> {

    boolean existsByTokenId(String tokenId);

    void deleteByExpiresAtBefore(Instant time);
}