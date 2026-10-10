package com.vishnu.banking_application.transaction.Services;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.transaction.BankTransaction;
import com.vishnu.banking_application.transaction.TransactionDirection;
import com.vishnu.banking_application.transaction.TransactionStatus;
import com.vishnu.banking_application.transaction.TransactionType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class BankTransactionSpecification {

    private BankTransactionSpecification() {
    }

    public static Specification<BankTransaction> belongsToCustomer(
            Long customerId) {

        return (root, query, cb) -> {

            Join<BankTransaction, Account> sourceAccount =
                    root.join("sourceAccount", JoinType.LEFT);

            Join<BankTransaction, Account> destinationAccount =
                    root.join("destinationAccount", JoinType.LEFT);

            return cb.or(

                    cb.equal(
                            sourceAccount.get("customer").get("id"),
                            customerId
                    ),

                    cb.equal(
                            destinationAccount.get("customer").get("id"),
                            customerId
                    )
            );
        };
    }

    public static Specification<BankTransaction> accountNumber(
            String accountNumber) {

        return (root, query, cb) -> {

            Join<BankTransaction, Account> sourceAccount =
                    root.join("sourceAccount", JoinType.LEFT);

            Join<BankTransaction, Account> destinationAccount =
                    root.join("destinationAccount", JoinType.LEFT);

            return cb.or(

                    cb.equal(
                            sourceAccount.get("accountNumber"),
                            accountNumber
                    ),

                    cb.equal(
                            destinationAccount.get("accountNumber"),
                            accountNumber
                    )
            );
        };
    }

    public static Specification<BankTransaction> createdFrom(
            Instant from) {

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        from
                );
    }

    public static Specification<BankTransaction> createdBefore(
            Instant to) {

        return (root, query, cb) ->
                cb.lessThan(
                        root.get("createdAt"),
                        to
                );
    }

    public static Specification<BankTransaction> direction(
            Long customerId,
            TransactionDirection direction) {

        return (root, query, cb) -> {

            Join<BankTransaction, Account> sourceAccount =
                    root.join("sourceAccount", JoinType.LEFT);

            Join<BankTransaction, Account> destinationAccount =
                    root.join("destinationAccount", JoinType.LEFT);

            if (direction == TransactionDirection.DEBIT) {

                return cb.equal(
                        sourceAccount.get("customer").get("id"),
                        customerId
                );
            }

            return cb.equal(
                    destinationAccount.get("customer").get("id"),
                    customerId
            );
        };
    }


    public static Specification<BankTransaction> hasTransactionType(
            TransactionType transactionType) {

        return (root, query, cb) ->
                cb.equal(root.get("transactionType"), transactionType);
    }

    public static Specification<BankTransaction> hasStatus(
            TransactionStatus status) {

        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }
}