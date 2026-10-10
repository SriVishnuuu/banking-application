package com.vishnu.banking_application.branch;

import com.vishnu.banking_application.branch.BranchRepository;
import com.vishnu.banking_application.branch.exception.IfscGenerationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class IfscGenerator {

    private static final String PREFIX = "VISH0";
    private static final int RANDOM_DIGITS_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 10;

    private final BranchRepository branchRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {

            StringBuilder randomDigits = new StringBuilder();

            for (int i = 0; i < RANDOM_DIGITS_LENGTH; i++) {
                randomDigits.append(secureRandom.nextInt(10));
            }

            String ifscCode = PREFIX + randomDigits;

            if (!branchRepository.existsByIfscCode(ifscCode)) {
                return ifscCode;
            }
        }

        throw new IfscGenerationException(
                "Unable to generate a unique IFSC code"
        );
    }
}