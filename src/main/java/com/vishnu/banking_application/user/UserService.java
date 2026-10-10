package com.vishnu.banking_application.user;


import com.vishnu.banking_application.audit.AuditAction;
import com.vishnu.banking_application.audit.AuditLogService;
import com.vishnu.banking_application.audit.AuditResourceType;
import com.vishnu.banking_application.audit.AuditStatus;
import com.vishnu.banking_application.user.dto.LoginRequestDto;
import com.vishnu.banking_application.user.dto.LoginResponseDto;
import com.vishnu.banking_application.user.dto.RegistrationRequestDto;
import com.vishnu.banking_application.user.exception.InvalidCredentialsException;
import com.vishnu.banking_application.user.exception.UserAlreadyExistsException;
import com.vishnu.banking_application.security.CustomUserDetails;
import com.vishnu.banking_application.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public LoginResponseDto authenticate(LoginRequestDto dto) {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                );

        Authentication authenticated;

        try {
            authenticated = authenticationManager.authenticate(authentication);
        } catch (AuthenticationException exception) {
            User user = userRepository.findByEmail(dto.getEmail())
                    .orElse(null);

            auditLogService.record(
                    user,
                    AuditAction.LOGIN_FAILED,
                    AuditResourceType.AUTH,
                    null,
                    AuditStatus.FAILED,
                    "Login failed"
            );

            throw new InvalidCredentialsException("Invalid email or password");
        }
        User user = ((CustomUserDetails) authenticated.getPrincipal()).getUser();

        auditLogService.record(
                user,
                AuditAction.LOGIN,
                AuditResourceType.AUTH,
                null,
                AuditStatus.SUCCESS,
                "User login successful"
        );

        String token = jwtService.generateToken(authenticated);
        Role role = user.getRole();

        return new LoginResponseDto(token,role);
    }

    public void register(RegistrationRequestDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );

        userRepository.save(user);
    }

}
