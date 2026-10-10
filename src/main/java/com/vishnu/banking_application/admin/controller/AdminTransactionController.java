package com.vishnu.banking_application.admin.controller;

import com.vishnu.banking_application.admin.dto.AdminTransactionResponseDto;
import com.vishnu.banking_application.admin.dto.AdminTransactionSummaryDto;
import com.vishnu.banking_application.admin.service.AdminTransactionService;
import com.vishnu.banking_application.transaction.TransactionStatus;
import com.vishnu.banking_application.transaction.TransactionType;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/admin")
public class AdminTransactionController {

    private final AdminTransactionService adminTransactionService;

    @GetMapping("/transactions")
    public ResponseEntity<Page<AdminTransactionSummaryDto>> getAllTransactions(

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(required = false)
            String accountNumber,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(required = false)
            TransactionType transactionType,

            @RequestParam(required = false)
            TransactionStatus status) {

        Page<AdminTransactionSummaryDto> response =
                adminTransactionService.getAllTransactions(
                        page,
                        accountNumber,
                        from,
                        to,
                        transactionType,
                        status
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/{transactionReference}")
    public ResponseEntity<AdminTransactionResponseDto> getTransaction(
            @PathVariable String transactionReference) {

        AdminTransactionResponseDto response =
                adminTransactionService.getTransaction(
                        transactionReference
                );

        return ResponseEntity.ok(response);
    }
}