package com.vishnu.banking_application.transaction.Services;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.account.AccountRepository;
import com.vishnu.banking_application.account.AccountStatus;
import com.vishnu.banking_application.account.exception.AccountNotFoundException;
import com.vishnu.banking_application.account.exception.AccountStatusException;
import com.vishnu.banking_application.beneficiary.Beneficiary;
import com.vishnu.banking_application.beneficiary.BeneficiaryStatus;
import com.vishnu.banking_application.beneficiary.exception.BeneficiaryStatusException;
import com.vishnu.banking_application.transaction.BankTransaction;
import com.vishnu.banking_application.transaction.BankTransactionRepository;
import com.vishnu.banking_application.transaction.TransactionStatus;
import com.vishnu.banking_application.transaction.TransactionType;
import com.vishnu.banking_application.transaction.exception.DailyTransactionLimitExceededException;
import com.vishnu.banking_application.transaction.exception.InsufficientBalanceException;
import com.vishnu.banking_application.transaction.exception.InvalidTransferException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferExecutionService {

    private final AccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;
    private final TransactionLimitService transactionLimitService;

    @Transactional
    public BankTransaction executeTransfer(
            Account sourceAccount,
            Account destinationAccount,
            Beneficiary beneficiary,
            BigDecimal amount,
            String transactionReference,
            String description) {

        Account lockedSourceAccount;
        Account lockedDestinationAccount;

        if (sourceAccount.getId()
                < destinationAccount.getId()) {

            lockedSourceAccount = accountRepository
                    .findByIdForUpdate(
                            sourceAccount.getId())
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "Source account not found"));

            lockedDestinationAccount = accountRepository
                    .findByIdForUpdate(
                            destinationAccount.getId())
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "Destination account not found"));

        } else {

            lockedDestinationAccount = accountRepository
                    .findByIdForUpdate(
                            destinationAccount.getId())
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "Destination account not found"));

            lockedSourceAccount = accountRepository
                    .findByIdForUpdate(
                            sourceAccount.getId())
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "Source account not found"));
        }

        validateTransfer(
                lockedSourceAccount,
                lockedDestinationAccount,
                beneficiary,
                amount
        );

        transactionLimitService.checkTransferLimit(
                lockedSourceAccount,
                amount
        );

        lockedSourceAccount.setBalance(
                lockedSourceAccount.getBalance()
                        .subtract(amount)
        );

        lockedDestinationAccount.setBalance(
                lockedDestinationAccount.getBalance()
                        .add(amount)
        );

        BankTransaction transaction = new BankTransaction();

        transaction.setTransactionReference(
                transactionReference);

        transaction.setSourceAccount(
                lockedSourceAccount);

        transaction.setDestinationAccount(
                lockedDestinationAccount);

        transaction.setAmount(amount);

        transaction.setTransactionType(
                TransactionType.TRANSFER);

        transaction.setStatus(
                TransactionStatus.SUCCESS);

        transaction.setDescription(description);

        transactionRepository.save(transaction);

        return transaction;
    }

    private void validateTransfer(
            Account sourceAccount,
            Account destinationAccount,
            Beneficiary beneficiary,
            BigDecimal amount) {

        if (sourceAccount.getId()
                .equals(destinationAccount.getId())) {

            throw new InvalidTransferException(
                    "Source and destination accounts cannot be the same");
        }

        if (sourceAccount.getStatus()
                != AccountStatus.ACTIVE) {

            throw new AccountStatusException(
                    "Source account is not active");
        }

        if (beneficiary != null
                && beneficiary.getStatus()
                != BeneficiaryStatus.ACTIVE) {

            throw new BeneficiaryStatusException(
                    "Beneficiary is not active");
        }

        if (destinationAccount.getStatus()
                != AccountStatus.ACTIVE) {

            throw new AccountStatusException(
                    "Destination account is not active");
        }

        if (sourceAccount.getBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance in your account");
        }
    }
}