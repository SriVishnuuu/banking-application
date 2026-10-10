package com.vishnu.banking_application.account;


import com.vishnu.banking_application.account.dto.AccountBalanceResponseDto;
import com.vishnu.banking_application.account.dto.AccountCreateRequestDto;
import com.vishnu.banking_application.account.dto.AccountCreationOptionsDto;
import com.vishnu.banking_application.account.dto.AccountResponseDto;
import com.vishnu.banking_application.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> viewAllAccounts(@AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(accountService.getAllAccounts(userDetails.getUser()),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountCreateRequestDto accountCreateRequestDto , @AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(accountService.createAccount(accountCreateRequestDto, userDetails.getUser()), HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDto> viewAccount(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String accountNumber){
        return new ResponseEntity<>(accountService.viewAccount(userDetails.getUser(), accountNumber), HttpStatus.OK);
    }

    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<AccountResponseDto> changeAccountStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.changeAccountStatus(userDetails.getUser(), accountNumber)
        );
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<AccountBalanceResponseDto> viewBalance(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String accountNumber){
        return new ResponseEntity<>(accountService.viewBalance(userDetails.getUser(), accountNumber), HttpStatus.OK);
    }

    @GetMapping("/options")
    public ResponseEntity<AccountCreationOptionsDto> viewAccountOptions(){
        return new ResponseEntity<>(accountService.viewAccountOptions(),HttpStatus.OK);
    }

}
