package com.vishnu.banking_application.transaction.Services;

import com.vishnu.banking_application.account.Account;
import com.vishnu.banking_application.transaction.BankTransactionRepository;
import com.vishnu.banking_application.transaction.TransactionType;
import com.vishnu.banking_application.transaction.dto.TransactionLimitDto;
import com.vishnu.banking_application.transaction.dto.TransactionLimitsResponseDto;
import com.vishnu.banking_application.transaction.exception.DailyTransactionLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TransactionLimitService {

    private static final ZoneId BANK_ZONE =
            ZoneId.of("Asia/Kolkata");

    private static final BigDecimal DAILY_TRANSFER_LIMIT =
            new BigDecimal("100000");

    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT =
            new BigDecimal("100000");

    private final BankTransactionRepository transactionRepository;


    public void checkTransferLimit(
            Account account,
            BigDecimal amount) {

        checkLimit(
                account,
                amount,
                TransactionType.TRANSFER,
                DAILY_TRANSFER_LIMIT,
                "Daily transfer limit of ₹1,00,000 exceeded"
        );
    }


    public void checkWithdrawalLimit(
            Account account,
            BigDecimal amount) {

        checkLimit(
                account,
                amount,
                TransactionType.WITHDRAW,
                DAILY_WITHDRAWAL_LIMIT,
                "Daily withdrawal limit of ₹1,00,000 exceeded"
        );
    }


    public TransactionLimitsResponseDto viewLimits(
            Account account) {

        Instant startOfDay = getStartOfDay();
        Instant startOfNextDay = getStartOfNextDay();

        BigDecimal transferUsed =
                getUsedAmount(
                        account,
                        TransactionType.TRANSFER,
                        startOfDay,
                        startOfNextDay
                );

        BigDecimal withdrawalUsed =
                getUsedAmount(
                        account,
                        TransactionType.WITHDRAW,
                        startOfDay,
                        startOfNextDay
                );

        TransactionLimitDto transfer =
                createLimitResponse(
                        DAILY_TRANSFER_LIMIT,
                        transferUsed
                );

        TransactionLimitDto withdrawal =
                createLimitResponse(
                        DAILY_WITHDRAWAL_LIMIT,
                        withdrawalUsed
                );

        return new TransactionLimitsResponseDto(
                transfer,
                withdrawal
        );
    }


    private void checkLimit(
            Account account,
            BigDecimal amount,
            TransactionType transactionType,
            BigDecimal dailyLimit,
            String errorMessage) {

        Instant startOfDay = getStartOfDay();
        Instant startOfNextDay = getStartOfNextDay();

        BigDecimal usedAmount =
                getUsedAmount(
                        account,
                        transactionType,
                        startOfDay,
                        startOfNextDay
                );

        if (usedAmount.add(amount)
                .compareTo(dailyLimit) > 0) {

            throw new DailyTransactionLimitExceededException(
                    errorMessage
            );
        }
    }


    private BigDecimal getUsedAmount(
            Account account,
            TransactionType transactionType,
            Instant startOfDay,
            Instant startOfNextDay) {

        return transactionRepository
                .sumSuccessfulOutgoingTransactions(
                        account.getId(),
                        transactionType,
                        startOfDay,
                        startOfNextDay
                );
    }


    private TransactionLimitDto createLimitResponse(
            BigDecimal dailyLimit,
            BigDecimal usedAmount) {

        BigDecimal remainingAmount =
                dailyLimit.subtract(usedAmount);

        return new TransactionLimitDto(
                dailyLimit,
                usedAmount,
                remainingAmount
        );
    }


    private Instant getStartOfDay() {

        return ZonedDateTime
                .now(BANK_ZONE)
                .toLocalDate()
                .atStartOfDay(BANK_ZONE)
                .toInstant();
    }


    private Instant getStartOfNextDay() {

        return ZonedDateTime
                .now(BANK_ZONE)
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay(BANK_ZONE)
                .toInstant();
    }
}