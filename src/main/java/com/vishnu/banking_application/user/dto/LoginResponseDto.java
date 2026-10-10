package com.vishnu.banking_application.user.dto;

import com.vishnu.banking_application.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String token;

    private Role role;
}