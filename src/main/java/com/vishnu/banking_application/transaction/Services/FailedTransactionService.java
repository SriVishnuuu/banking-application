package com.vishnu.banking_application.transaction.Services;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.transaction.BankTransaction;
import com.vishnu.banking_application.transaction.BankTransactionRepository;
import com.vishnu.banking_application.transaction.TransactionStatus;
import com.vishnu.banking_application.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FailedTransactionService {

    private final BankTransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(
            String transactionReference,
            Account sourceAccount,
            Account destinationAccount,
            BigDecimal amount,
            TransactionType transactionType,
            String description,
            String failureReason) {

        BankTransaction transaction = new BankTransaction();

        transaction.setTransactionReference(transactionReference);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setDescription(description);
        transaction.setFailureReason(failureReason);

        transactionRepository.save(transaction);
    }
}