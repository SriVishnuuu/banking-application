package com.vishnu.banking_application.account;


import com.vishnu.banking_application.account.dto.AccountBalanceResponseDto;
import com.vishnu.banking_application.account.dto.AccountCreateRequestDto;
import com.vishnu.banking_application.account.dto.AccountCreationOptionsDto;
import com.vishnu.banking_application.account.dto.AccountResponseDto;
import com.vishnu.banking_application.account.exception.AccountNotFoundException;
import com.vishnu.banking_application.account.exception.AccountStatusException;
import com.vishnu.banking_application.account.mapper.AccountResponseMapper;
import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLogService;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import com.vishnu.banking_application.branch.Branch;
import com.vishnu.banking_application.branch.BranchRepository;
import com.vishnu.banking_application.branch.BranchStatus;
import com.vishnu.banking_application.branch.exception.BranchNotFoundException;
import com.vishnu.banking_application.branch.exception.BranchUnavailableException;
import com.vishnu.banking_application.customer.Customer;
import com.vishnu.banking_application.customer.CustomerRepository;
import com.vishnu.banking_application.customer.exception.CustomerNotFoundException;
import com.vishnu.banking_application.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final BranchRepository branchRepository;

    private final CustomerRepository customerRepository;

    private final AccountNumberGenerator accountNumberGenerator;

    private final AccountResponseMapper mapper;

    private final AuditLogService auditLogService;

    @Transactional
    public AccountResponseDto createAccount( AccountCreateRequestDto accountCreateRequestDto, User user) {
        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(()-> new CustomerNotFoundException("Customer was not found"));

        Branch branch = branchRepository.findByBranchCode(accountCreateRequestDto.getBranchCode()).orElseThrow(() -> new BranchNotFoundException("Bank Branch is not exists!"));

        if (branch.getStatus() == BranchStatus.INACTIVE) {
            throw new BranchUnavailableException(
                    "Branch is unavailable right now!"
            );
        }

        Account account = new Account();

        account.setCustomer(customer);
        account.setBranch(branch);
        account.setAccountNumber(accountNumberGenerator.generate());
        account.setAccountType(accountCreateRequestDto.getAccountType());

        accountRepository.save(account);

        auditLogService.record(
                user,
                AuditAction.CREATE_ACCOUNT,
                AuditResourceType.ACCOUNT,
                account.getId(),
                AuditStatus.SUCCESS,
                "Account created successfully"
        );

        return mapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllAccounts(User user) {

        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(()-> new CustomerNotFoundException("Customer was not found"));

        List<AccountResponseDto> accounts = accountRepository.findAllByCustomerId(customer.getId()).stream()
                .map(account -> mapper.toDto(account))
                .toList();

        return accounts;
    }

    @Transactional(readOnly = true)
    public AccountResponseDto viewAccount(User user, String accountNo) {

        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(()-> new CustomerNotFoundException("Customer was not found"));

        Account account = accountRepository.findByCustomerIdAndAccountNumber(customer.getId(), accountNo).orElseThrow(() -> new AccountNotFoundException("Account was not found"));

        return mapper.toDto(account);
    }

    @Transactional
    public AccountResponseDto changeAccountStatus(User user, String accountNumber) {

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer was not found"));

        Account account = accountRepository
                .findByCustomerIdAndAccountNumber(customer.getId(), accountNumber)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account was not found"));

        AccountStatus currentStatus = account.getStatus();

        if (currentStatus == AccountStatus.ACTIVE) {
            account.setStatus(AccountStatus.INACTIVE);
        } else if (currentStatus == AccountStatus.INACTIVE) {
            account.setStatus(AccountStatus.ACTIVE);
        } else {
            throw new AccountStatusException(
                    "Account status cannot be changed by the customer");
        }

        accountRepository.save(account);

        auditLogService.record(
                user,
                AuditAction.CHANGE_ACCOUNT_STATUS,
                AuditResourceType.ACCOUNT,
                account.getId(),
                AuditStatus.SUCCESS,
                "Account status changed from "
                        + currentStatus
                        + " to "
                        + account.getStatus()
        );

        return mapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public AccountBalanceResponseDto viewBalance(User user, String accountNumber) {
        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(()-> new CustomerNotFoundException("Customer was not found"));

        Account account = accountRepository.findByCustomerIdAndAccountNumber(customer.getId(), accountNumber).orElseThrow(() -> new AccountNotFoundException("Account was not found"));

        return new AccountBalanceResponseDto(account.getBalance());
    }

    @Transactional(readOnly = true)
    public AccountCreationOptionsDto viewAccountOptions() {
        List<AccountType> accountTypes = List.of(AccountType.values());
        List<String> branches = branchRepository.findAllBranchCodeByStatus(BranchStatus.ACTIVE);

        return new AccountCreationOptionsDto(accountTypes,branches);
    }
}
