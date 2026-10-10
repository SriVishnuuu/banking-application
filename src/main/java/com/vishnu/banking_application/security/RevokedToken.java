package com.vishnu.banking_application.security;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "revoked_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_revoked_token_jti", columnNames = "token_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, unique = true)
    private String tokenId;

    @Column(nullable = false)
    private Instant expiresAt;
}