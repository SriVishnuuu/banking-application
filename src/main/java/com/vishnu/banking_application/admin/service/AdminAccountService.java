package com.vishnu.banking_application.admin.service;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.account.AccountRepository;
import com.vishnu.banking_application.account.AccountStatus;
import com.vishnu.banking_application.admin.dto.*;
import com.vishnu.banking_application.admin.exception.InvalidAccountStatusException;
import com.vishnu.banking_application.admin.mapper.AdminAccountMapper;
import com.vishnu.banking_application.account.exception.AccountNotFoundException;
import com.vishnu.banking_application.admin.mapper.AdminTransactionMapper;
import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLogService;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import com.vishnu.banking_application.transaction.BankTransaction;
import com.vishnu.banking_application.transaction.Services.FailedTransactionService;
import com.vishnu.banking_application.transaction.Services.TransactionReferenceGenerator;
import com.vishnu.banking_application.transaction.TransactionType;
import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private static final int PAGE_SIZE = 20;

    private final AccountRepository accountRepository;
    private final AdminAccountMapper adminAccountMapper;
    private final FailedTransactionService failedTransactionService;
    private final TransactionReferenceGenerator transactionReferenceGenerator;
    private final AdminDepositExecutionService adminDepositExecutionService;
    private final AuditLogService auditLogService;
    private final AdminTransactionMapper adminTransactionMapper;

    public Page<AdminAccountSummaryDto> getAllAccounts(
            int page,
            String search) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        String normalizedSearch = search == null
                ? null
                : search.trim();

        if (normalizedSearch != null && normalizedSearch.isBlank()) {
            normalizedSearch = null;
        }

        Page<Account> accounts;

        if (normalizedSearch == null) {
            accounts = accountRepository.findAll(pageable);
        } else {
            accounts = accountRepository.searchAccounts(
                    normalizedSearch,
                    pageable
            );
        }

        return accounts.map(adminAccountMapper::toSummaryDto);
    }

    public AdminAccountResponseDto getAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + accountId
                        ));

        return adminAccountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public AdminAccountStatisticsDto getAccountStatistics() {

        long totalAccounts = accountRepository.count();

        long activeAccounts =
                accountRepository.countByStatus(AccountStatus.ACTIVE);

        long inactiveAccounts =
                accountRepository.countByStatus(AccountStatus.INACTIVE);

        long frozenAccounts =
                accountRepository.countByStatus(AccountStatus.FROZEN);

        long closedAccounts =
                accountRepository.countByStatus(AccountStatus.CLOSED);

        return new AdminAccountStatisticsDto(
                totalAccounts,
                activeAccounts,
                inactiveAccounts,
                frozenAccounts,
                closedAccounts
        );
    }

    @Transactional
    public void changeAccountStatus(
            Long accountId,
            UpdateAccountStatusRequestDto dto,
            User actor) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + accountId));

        AccountStatus currentStatus = account.getStatus();
        AccountStatus requestedStatus = dto.getStatus();

        if (currentStatus == requestedStatus) {
            throw new InvalidAccountStatusException(
                    "Account already has status: " + requestedStatus);
        }

        if (currentStatus == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException(
                    "Closed account status cannot be changed");
        }

        if (requestedStatus == AccountStatus.CLOSED) {

            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new InvalidAccountStatusException(
                        "Account cannot be closed while balance is non-zero");
            }
        }

        account.setStatus(requestedStatus);

        accountRepository.save(account);

        auditLogService.record(
                actor,
                AuditAction.CHANGE_ACCOUNT_STATUS,
                AuditResourceType.ACCOUNT,
                account.getId(),
                AuditStatus.SUCCESS,
                "Account status changed from "
                        + currentStatus
                        + " to "
                        + requestedStatus
        );
    }

    public AdminDepositResponseDto deposit(Long accountId, AdminDepositRequestDto dto) {

        String transactionReference = transactionReferenceGenerator.generate();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        try {

            BankTransaction transaction =
                    adminDepositExecutionService.executeDeposit(
                            accountId,
                            dto.getAmount(),
                            transactionReference,
                            dto.getDescription()
                    );

            return adminTransactionMapper.toDto(transaction);

        } catch (RuntimeException ex) {

            failedTransactionService.recordFailure(
                    transactionReference,
                    null,
                    account,
                    dto.getAmount(),
                    TransactionType.DEPOSIT,
                    dto.getDescription(),
                    ex.getMessage()
            );

            throw ex;
        }
    }
}