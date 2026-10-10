package com.vishnu.banking_application.transaction;

import com.vishnu.banking_application.account.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction,Long> ,
        JpaSpecificationExecutor<BankTransaction>
{

    boolean existsByTransactionReference(String transactionReference);

    Optional<BankTransaction> findByTransactionReference(
            String transactionReference
    );


    @Query("""
    SELECT t
    FROM BankTransaction t
    LEFT JOIN t.sourceAccount sa
    LEFT JOIN t.destinationAccount da
    WHERE t.transactionReference = :reference
      AND (
          sa.customer.id = :customerId
          OR
          da.customer.id = :customerId
      )
""")
    Optional<BankTransaction> findByReferenceAndCustomerId(
            @Param("reference") String reference,
            @Param("customerId") Long customerId
    );

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM BankTransaction t
    WHERE t.sourceAccount.id = :accountId
      AND t.transactionType = :transactionType
      AND t.status = com.vishnu.banking_application.transaction.TransactionStatus.SUCCESS
      AND t.createdAt >= :start
      AND t.createdAt < :end
""")
    BigDecimal sumSuccessfulOutgoingTransactions(
            @Param("accountId") Long accountId,
            @Param("transactionType") TransactionType transactionType,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

}
