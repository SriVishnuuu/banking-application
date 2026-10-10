package com.vishnu.banking_application.admin.service;
import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.account.AccountRepository;
import com.vishnu.banking_application.account.AccountStatus;
import com.vishnu.banking_application.transaction.*;
import com.vishnu.banking_application.account.exception.AccountStatusException;
import com.vishnu.banking_application.account.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;



@Service
@RequiredArgsConstructor
public class AdminDepositExecutionService {

    private final AccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;

    @Transactional
    public BankTransaction executeDeposit(
            Long accountId,
            BigDecimal amount,
            String transactionReference,
            String description) {

        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + accountId));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStatusException(
                    "Deposit cannot be made to an inactive account");
        }

        account.setBalance(
                account.getBalance().add(amount)
        );

        BankTransaction transaction = new BankTransaction();

        transaction.setTransactionReference(transactionReference);
        transaction.setSourceAccount(null);
        transaction.setDestinationAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(description);

        transactionRepository.save(transaction);

        return transaction;
    }
}
