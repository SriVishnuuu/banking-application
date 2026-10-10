package com.vishnu.banking_application.user;

import com.vishnu.banking_application.security.service.PasswordResetService;
import com.vishnu.banking_application.security.dto.ForgotPasswordRequestDto;
import com.vishnu.banking_application.security.dto.ResetPasswordRequestDto;
import com.vishnu.banking_application.user.dto.LoginRequestDto;
import com.vishnu.banking_application.user.dto.LoginResponseDto;
import com.vishnu.banking_application.user.dto.RegistrationRequestDto;
import com.vishnu.banking_application.security.CustomUserDetails;
import com.vishnu.banking_application.security.LogoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class UserController {

    private final UserService userService;
    private final LogoutService logoutService;
    private final PasswordResetService passwordResetService;


    public UserController(UserService userService, LogoutService logoutService, PasswordResetService passwordResetService){
        this.userService = userService;
        this.logoutService = logoutService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegistrationRequestDto dto) {

        userService.register(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto) {

        LoginResponseDto response = userService.authenticate(dto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);

        logoutService.logout(
                token,
                userDetails.getUser()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto request) {

        passwordResetService.forgotPassword(request.getEmail());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto request) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.noContent().build();
    }
}
