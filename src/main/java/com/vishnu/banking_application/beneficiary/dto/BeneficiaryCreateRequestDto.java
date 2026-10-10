package com.vishnu.banking_application.beneficiary.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BeneficiaryCreateRequestDto {
    @NotBlank(message = "Beneficiary name is required")
    @Size(max = 50, message = "Beneficiary name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Account number is required")
    @Pattern(
            regexp = "^[1-9][0-9]{11}$",
            message = "Account number must be exactly 12 digits"
    )
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    @Size(max = 20, message = "IFSC code must not exceed 20 characters")
    private String ifscCode;

    @Size(max = 20, message = "Nickname must not exceed 20 characters")
    private String nickname;
}
