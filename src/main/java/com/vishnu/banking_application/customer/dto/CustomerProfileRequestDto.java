package com.vishnu.banking_application.customer.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;


@Data
public class CustomerProfileRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    @Size(max = 100)
    private String address;

    @NotBlank(message = "phone number is required")
    @Pattern(
            regexp = "^(\\+91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Phone number must be a valid 10-digit Indian mobile number"
    )
    private String phoneNumber;
}
