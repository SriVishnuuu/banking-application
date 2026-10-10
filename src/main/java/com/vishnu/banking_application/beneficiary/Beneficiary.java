package com.vishnu.banking_application.beneficiary;

import com.vishnu.banking_application.customer.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "beneficiaries",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"customer_id", "account_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Beneficiary {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customer_id", nullable = false)
        private Customer customer;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false)
        private String accountNumber;

        @Column(nullable = false)
        private String ifscCode;

        private String nickname;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private BeneficiaryStatus status = BeneficiaryStatus.ACTIVE;

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
