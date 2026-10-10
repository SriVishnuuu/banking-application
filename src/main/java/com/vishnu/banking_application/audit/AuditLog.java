package com.vishnu.banking_application.audit;

import com.vishnu.banking_application.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;


@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditResourceType resourceType;

    private Long resourceId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Instant timestamp;

    @PrePersist
    public void created(){
        timestamp = Instant.now();
    }
}
