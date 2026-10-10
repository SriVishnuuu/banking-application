package com.vishnu.banking_application.account;

import com.vishnu.banking_application.account.exception.AccountNumberGenerationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private static final int ACCOUNT_NUMBER_LENGTH = 12;
    private static final SecureRandom random = new SecureRandom();

    private final AccountRepository accountRepository;

    public String generate() {
        String accountNumber;
        int attempts = 0;

        do {
            accountNumber = generateRandomNumber();
            attempts++;
            if (attempts > 10) {
                throw new AccountNumberGenerationException("Failed to generate a unique account number after 10 attempts");
            }
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private String generateRandomNumber() {
        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);
        sb.append(1 + random.nextInt(9)); // first digit never 0
        for (int i = 1; i < ACCOUNT_NUMBER_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}