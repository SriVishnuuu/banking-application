package com.vishnu.banking_application.admin.dto;

import com.vishnu.banking_application.user.UserStatus;

import java.time.Instant;
import java.time.LocalDate;

public record AdminCustomerResponseDto(Long id,
                                       String firstName,
                                       String lastName,
                                       LocalDate dateOfBirth,
                                       String address,
                                       String phoneNumber,
                                       String email,
                                       UserStatus status,
                                       Instant createdAt) {
}
