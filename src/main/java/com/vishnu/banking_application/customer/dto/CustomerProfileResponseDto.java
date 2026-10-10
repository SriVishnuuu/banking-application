package com.vishnu.banking_application.customer.dto;

import java.time.LocalDate;

public record CustomerProfileResponseDto(Long id,
                                         String firstName,
                                         String lastName,
                                         LocalDate dateOfBirth,
                                         String address,
                                         String phoneNumber,
                                         String email) {
                                }
