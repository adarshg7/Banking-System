package com.bank.account.controller;

import com.bank.account.dto.request.AccountOpeningRequest;
import com.bank.account.dto.response.AccountResponse;
import com.bank.account.service.AccountService;
import com.bank.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> openAccount(@Valid @RequestBody AccountOpeningRequest request) {
        AccountResponse response = accountService.openAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Account opened successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getById(@PathVariable UUID id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Account fetched successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AccountResponse>> getByAccountNumber(@RequestParam String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(response, "Account fetched successfully"));
    }
}