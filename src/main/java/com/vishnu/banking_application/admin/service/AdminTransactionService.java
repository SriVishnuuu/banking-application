package com.vishnu.banking_application.admin.service;

import com.vishnu.banking_application.admin.dto.AdminTransactionResponseDto;
import com.vishnu.banking_application.admin.dto.AdminTransactionSummaryDto;
import com.vishnu.banking_application.admin.mapper.AdminTransactionMapper;
import com.vishnu.banking_application.transaction.BankTransaction;
import com.vishnu.banking_application.transaction.Services.BankTransactionSpecification;
import com.vishnu.banking_application.transaction.TransactionStatus;
import com.vishnu.banking_application.transaction.TransactionType;
import com.vishnu.banking_application.transaction.BankTransactionRepository;
import com.vishnu.banking_application.transaction.exception.InvalidTransactionFilterException;
import com.vishnu.banking_application.transaction.exception.TransactionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AdminTransactionService {

    private static final int PAGE_SIZE = 20;
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final BankTransactionRepository transactionRepository;
    private final AdminTransactionMapper adminTransactionMapper;

    public Page<AdminTransactionSummaryDto> getAllTransactions(
            int page,
            String accountNumber,
            LocalDate from,
            LocalDate to,
            TransactionType transactionType,
            TransactionStatus status) {

        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidTransactionFilterException(
                    "From date cannot be after to date"
            );
        }

        Instant fromInstant = from != null
                ? from.atStartOfDay(IST).toInstant()
                : null;

        Instant toInstant = to != null
                ? to.plusDays(1).atStartOfDay(IST).toInstant()
                : null;

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<BankTransaction> specification =
                (root, query, cb) -> cb.conjunction();

        if (accountNumber != null && !accountNumber.isBlank()) {
            specification = specification.and(
                    BankTransactionSpecification.accountNumber(accountNumber)
            );
        }

        if (fromInstant != null) {
            specification = specification.and(
                    BankTransactionSpecification.createdFrom(fromInstant)
            );
        }

        if (toInstant != null) {
            specification = specification.and(
                    BankTransactionSpecification.createdBefore(toInstant)
            );
        }

        if (transactionType != null) {
            specification = specification.and(
                    BankTransactionSpecification.hasTransactionType(
                            transactionType
                    )
            );
        }

        if (status != null) {
            specification = specification.and(
                    BankTransactionSpecification.hasStatus(status)
            );
        }

        return transactionRepository
                .findAll(specification, pageable)
                .map(adminTransactionMapper::toSummaryDto);
    }

    public AdminTransactionResponseDto getTransaction(
            String transactionReference) {

        BankTransaction transaction =
                transactionRepository.findByTransactionReference(
                        transactionReference
                ).orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found with reference: "
                                        + transactionReference
                        ));

        return adminTransactionMapper.toResponseDto(transaction);
    }
}