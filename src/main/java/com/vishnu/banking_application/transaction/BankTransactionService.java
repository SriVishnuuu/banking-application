package com.vishnu.banking_application.transaction;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.account.AccountRepository;
import com.vishnu.banking_application.account.exception.AccountNotFoundException;
import com.vishnu.banking_application.account.exception.AccountStatusException;
import com.vishnu.banking_application.beneficiary.Beneficiary;
import com.vishnu.banking_application.beneficiary.BeneficiaryRepository;
import com.vishnu.banking_application.beneficiary.exception.BeneficiaryNotFoundException;
import com.vishnu.banking_application.beneficiary.exception.BeneficiaryStatusException;
import com.vishnu.banking_application.customer.Customer;
import com.vishnu.banking_application.customer.CustomerRepository;
import com.vishnu.banking_application.customer.exception.CustomerNotFoundException;
import com.vishnu.banking_application.transaction.Services.*;
import com.vishnu.banking_application.transaction.dto.*;
import com.vishnu.banking_application.transaction.exception.*;
import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


@Service
@RequiredArgsConstructor
public class BankTransactionService {

    private final BankTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final TransactionReferenceGenerator referenceGenerator;
    private final FailedTransactionService failedTransactionService;
    private final TransactionLimitService transactionLimitService;
    private final WithdrawalExecutionService withdrawalExecutionService;
    private final TransferExecutionService transferExecutionService;


    private static final int TRANSACTION_PAGE_SIZE = 20;
    private static final ZoneId BANK_ZONE =
            ZoneId.of("Asia/Kolkata");

    public TransactionResponseDto transferMoney(
            TransactionRequestDto dto,
            User user) {

        Customer customer = customerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer was not found"));

        Account sourceAccount = accountRepository
                .findByCustomerIdAndAccountNumber(
                        customer.getId(),
                        dto.getSourceAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Source account not found"));

        Beneficiary beneficiary = beneficiaryRepository
                .findByIdAndCustomerId(
                        dto.getBeneficiaryId(),
                        customer.getId())
                .orElseThrow(() ->
                        new BeneficiaryNotFoundException(
                                "Beneficiary not found"));

        Account destinationAccount = accountRepository
                .findByAccountNumberAndBranchIfscCode(
                        beneficiary.getAccountNumber(),
                        beneficiary.getIfscCode())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Destination account not found"));

        String transactionReference =
                referenceGenerator.generate();

        try {

            BankTransaction transaction =
                    transferExecutionService.executeTransfer(
                            sourceAccount,
                            destinationAccount,
                            beneficiary,
                            dto.getAmount(),
                            transactionReference,
                            dto.getDescription()
                    );

            return toResponse(
                    transaction,
                    customer
            );

        } catch (InsufficientBalanceException
                 | AccountStatusException
                 | DailyTransactionLimitExceededException
                 | InvalidTransferException
                 | BeneficiaryStatusException exception) {

            failedTransactionService.recordFailure(
                    transactionReference,
                    sourceAccount,
                    destinationAccount,
                    dto.getAmount(),
                    TransactionType.TRANSFER,
                    dto.getDescription(),
                    exception.getMessage()
            );

            throw exception;
        }
    }

    public TransactionResponseDto withdrawMoney(
            WithdrawalRequestDto dto,
            User user) {

        Customer customer = customerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer was not found"));

        Account account = accountRepository
                .findByCustomerIdAndAccountNumber(
                        customer.getId(),
                        dto.getAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found"));

        String transactionReference =
                referenceGenerator.generate();

        try {

            BankTransaction transaction =
                    withdrawalExecutionService.executeWithdrawal(
                            account,
                            dto.getAmount(),
                            transactionReference,
                            dto.getDescription()
                    );

            return toResponse(transaction, customer);

        } catch (InsufficientBalanceException |
                 DailyTransactionLimitExceededException |
                 AccountStatusException exception) {

            failedTransactionService.recordFailure(
                    transactionReference,
                    account,
                    null,
                    dto.getAmount(),
                    TransactionType.WITHDRAW,
                    dto.getDescription(),
                    exception.getMessage()
            );

            throw exception;
        }
    }

    public TransactionResponseDto selfTransferMoney(
            SelfTransferRequestDto dto,
            User user) {

        Customer customer = customerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer was not found"));

        Account sourceAccount = accountRepository
                .findByCustomerIdAndAccountNumber(
                        customer.getId(),
                        dto.getSourceAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Source account not found"));

        Account destinationAccount = accountRepository
                .findByCustomerIdAndAccountNumber(
                        customer.getId(),
                        dto.getDestinationAccountNumber())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Destination account not found"));

        String transactionReference =
                referenceGenerator.generate();

        try {

            BankTransaction transaction =
                    transferExecutionService.executeTransfer(
                            sourceAccount,
                            destinationAccount,
                            null,
                            dto.getAmount(),
                            transactionReference,
                            dto.getDescription()
                    );

            return toResponse(
                    transaction,
                    customer
            );

        } catch (InsufficientBalanceException
                 | AccountStatusException
                 | DailyTransactionLimitExceededException
                 | InvalidTransferException exception) {

            failedTransactionService.recordFailure(
                    transactionReference,
                    sourceAccount,
                    destinationAccount,
                    dto.getAmount(),
                    TransactionType.TRANSFER,
                    dto.getDescription(),
                    exception.getMessage()
            );

            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> viewTransactions(
            User user,
            String accountNumber,
            LocalDate from,
            LocalDate to,
            TransactionDirection direction,
            int page) {

        Customer customer = customerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer was not found"));

        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidTransactionFilterException(
                    "From date cannot be after to date"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                TRANSACTION_PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<BankTransaction> specification =
                BankTransactionSpecification
                        .belongsToCustomer(customer.getId());

        if (accountNumber != null && !accountNumber.isBlank()) {
            specification = specification.and(
                    BankTransactionSpecification.accountNumber(
                            accountNumber
                    )
            );
        }

        if (from != null) {
            Instant fromInstant = from
                    .atStartOfDay(BANK_ZONE)
                    .toInstant();

            specification = specification.and(
                    BankTransactionSpecification.createdFrom(
                            fromInstant
                    )
            );
        }

        if (to != null) {
            Instant toInstant = to
                    .plusDays(1)
                    .atStartOfDay(BANK_ZONE)
                    .toInstant();

            specification = specification.and(
                    BankTransactionSpecification.createdBefore(
                            toInstant
                    )
            );
        }

        if (direction != null) {
            specification = specification.and(
                    BankTransactionSpecification.direction(
                            customer.getId(),
                            direction
                    )
            );
        }

        return transactionRepository
                .findAll(specification, pageable)
                .map(transaction ->
                        toResponse(transaction, customer)
                );
    }

    @Transactional(readOnly = true)
    public TransactionResponseDto viewTransaction(User user, String transactionReference) {
        Customer customer = customerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer was not found"));

        BankTransaction transaction = transactionRepository
                .findByReferenceAndCustomerId(
                        transactionReference,
                        customer.getId())
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction was not found"));

        return toResponse(transaction, customer);
    }

    @Transactional(readOnly = true)
    public TransactionLimitsResponseDto viewTransactionLimits(
            User user,
            String accountNumber) {

        Customer customer = customerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer was not found"
                        ));

        Account account = accountRepository
                .findByCustomerIdAndAccountNumber(
                        customer.getId(),
                        accountNumber
                )
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found"
                        ));

        return transactionLimitService.viewLimits(account);
    }

    private TransactionResponseDto toResponse(
            BankTransaction transaction,
            Customer customer) {

        TransactionDirection direction;

        if (transaction.getTransactionType()
                == TransactionType.WITHDRAW) {

            direction = TransactionDirection.DEBIT;

        } else if (transaction.getTransactionType()
                == TransactionType.DEPOSIT) {

            direction = TransactionDirection.CREDIT;

        } else if (transaction.getSourceAccount()
                .getCustomer()
                .getId()
                .equals(customer.getId())) {

            direction = TransactionDirection.DEBIT;

        } else {

            direction = TransactionDirection.CREDIT;
        }

        return new TransactionResponseDto(
                transaction.getTransactionReference(),
                transaction.getSourceAccount() != null
                        ? transaction.getSourceAccount().getAccountNumber()
                        : null,
                transaction.getDestinationAccount() != null
                        ? transaction.getDestinationAccount().getAccountNumber()
                        : null,
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                direction,
                transaction.getDescription(),
                transaction.getFailureReason(),
                transaction.getCreatedAt()
        );
    }
}