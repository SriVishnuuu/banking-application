package com.vishnu.banking_application.account;

import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findAllByCustomerId(Long customerId);

    Optional<Account> findByCustomerIdAndAccountNumber(Long customerId, String accountNumber);

    boolean existsByCustomerIdAndAccountNumber(Long customerId, String accountNumber);

    long countByStatus(AccountStatus status);

    Optional<Account> findByAccountNumberAndBranchIfscCode(String accountNumber, String ifscCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a
        FROM Account a
        WHERE a.customer.id = :customerId
          AND a.accountNumber = :accountNumber
        """)
    Optional<Account> findByCustomerIdAndAccountNumberForUpdate(
            @Param("customerId") Long customerId,
            @Param("accountNumber") String accountNumber
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a
        FROM Account a
        WHERE a.id = :accountId
        """)
    Optional<Account> findByIdForUpdate(
            @Param("accountId") Long accountId
    );

    @Query("""
        SELECT a
        FROM Account a
        JOIN a.customer c
        JOIN a.branch b
        WHERE LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(CONCAT(c.firstName, ' ', c.lastName))
                LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(b.branchCode) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
    Page<Account> searchAccounts(
            @Param("search") String search,
            Pageable pageable
    );
}
