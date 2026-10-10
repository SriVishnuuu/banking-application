package com.vishnu.banking_application.transaction.Services;


import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.account.AccountRepository;
import com.vishnu.banking_application.account.AccountStatus;
import com.vishnu.banking_application.account.exception.AccountNotFoundException;
import com.vishnu.banking_application.account.exception.AccountStatusException;
import com.vishnu.banking_application.transaction.BankTransaction;
import com.vishnu.banking_application.transaction.BankTransactionRepository;
import com.vishnu.banking_application.transaction.TransactionStatus;
import com.vishnu.banking_application.transaction.TransactionType;
import com.vishnu.banking_application.transaction.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WithdrawalExecutionService {

    private final AccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;
    private final TransactionLimitService transactionLimitService;

    @Transactional
    public BankTransaction executeWithdrawal(
            Account account,
            BigDecimal amount,
            String transactionReference,
            String description) {

        Account lockedAccount = accountRepository
                .findByIdForUpdate(account.getId())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found"));

        if (lockedAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStatusException(
                    "Account is not active");
        }

        if (lockedAccount.getBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance in your account");
        }

        transactionLimitService.checkWithdrawalLimit(
                lockedAccount,
                amount
        );

        lockedAccount.setBalance(
                lockedAccount.getBalance()
                        .subtract(amount)
        );

        BankTransaction transaction = new BankTransaction();

        transaction.setTransactionReference(
                transactionReference);

        transaction.setSourceAccount(lockedAccount);
        transaction.setDestinationAccount(null);
        transaction.setAmount(amount);
        transaction.setTransactionType(
                TransactionType.WITHDRAW);
        transaction.setStatus(
                TransactionStatus.SUCCESS);
        transaction.setDescription(description);

        transactionRepository.save(transaction);

        return transaction;
    }
}
