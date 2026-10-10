package com.vishnu.banking_application.transaction.Services;

import com.vishnu.banking_application.transaction.BankTransactionRepository;
import com.vishnu.banking_application.transaction.exception.TransactionReferenceGenerationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class TransactionReferenceGenerator {

    private static final String PREFIX = "TXN";
    private static final String ALPHANUMERIC = "0123456789ABCDEF"; // hex-style suffix
    private static final int SUFFIX_LENGTH = 12;
    private static final SecureRandom random = new SecureRandom();

    private final BankTransactionRepository transactionRepository;

    public String generate() {
        String reference;
        int attempts = 0;

        do {
            reference = buildReference();
            attempts++;
            if (attempts > 10) {
                throw new TransactionReferenceGenerationException(
                        "Unable to generate a unique transaction reference after " + attempts + " attempts"
                );
            }
        } while (transactionRepository.existsByTransactionReference(reference));

        return reference;
    }

    private String buildReference() {
        StringBuilder suffix = new StringBuilder(SUFFIX_LENGTH);
        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            suffix.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return PREFIX + "-" + suffix;
    }
}