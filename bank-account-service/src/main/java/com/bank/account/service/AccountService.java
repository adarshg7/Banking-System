package com.bank.account.service;

import com.bank.account.dto.request.AccountOpeningRequest;
import com.bank.account.dto.response.AccountResponse;

import java.util.UUID;

public interface AccountService {
    AccountResponse openAccount(AccountOpeningRequest request);
    AccountResponse getAccountById(UUID accountId);
    AccountResponse getAccountByNumber(String accountNumber);
}