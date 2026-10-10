package com.vishnu.banking_application.branch;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
public class Branch {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String branchCode;

    @Column(nullable = false)
    private String branchName;

    @Column(nullable = false,unique = true)
    private String ifscCode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BranchStatus status;

    private Instant createdAt;

    private Instant updatedAt;


    @PrePersist
    public void created(){
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void update(){
        updatedAt = Instant.now();
    }
}
