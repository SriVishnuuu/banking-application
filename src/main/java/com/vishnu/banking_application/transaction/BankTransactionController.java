package com.vishnu.banking_application.transaction;


import com.vishnu.banking_application.transaction.dto.*;
import com.vishnu.banking_application.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping("/api/me/transactions")
public class BankTransactionController {

    private final BankTransactionService bankTransactionService;

    public BankTransactionController(BankTransactionService bankTransactionService){
        this.bankTransactionService = bankTransactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDto> transferMoney(@Valid @RequestBody TransactionRequestDto transactionRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        return new ResponseEntity<>(bankTransactionService.transferMoney(transactionRequestDto, userDetails.getUser()), HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<Page<TransactionResponseDto>> viewTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) TransactionDirection direction,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be 0 or greater")
            int page) {

        return ResponseEntity.ok(
                bankTransactionService.viewTransactions(
                        userDetails.getUser(),
                        accountNumber,
                        from,
                        to,
                        direction,
                        page
                )
        );
    }

    @GetMapping("/{transactionReference}")
    public ResponseEntity<TransactionResponseDto> viewTransaction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String transactionReference) {

        return ResponseEntity.ok(
                bankTransactionService.viewTransaction(
                        userDetails.getUser(),
                        transactionReference)
        );
    }

    @PostMapping("/self-transfer")
    public ResponseEntity<TransactionResponseDto> selfTransferMoney(
            @Valid @RequestBody SelfTransferRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return new ResponseEntity<>(
                bankTransactionService.selfTransferMoney(
                        dto,
                        userDetails.getUser()
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdrawMoney(
            @Valid @RequestBody WithdrawalRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return new ResponseEntity<>(
                bankTransactionService.withdrawMoney(dto, userDetails.getUser()),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/limits/{accountNumber}")
    public ResponseEntity<TransactionLimitsResponseDto> viewTransactionLimits(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                bankTransactionService.viewTransactionLimits(
                        userDetails.getUser(),
                        accountNumber
                )
        );
    }
}
