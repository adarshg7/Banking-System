package com.bank.account.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.exception.ResourceNotFoundException;

public class AccountNotFoundException extends ResourceNotFoundException {
    public AccountNotFoundException(String message) {
        super(message, ErrorCodes.ACCOUNT_NOT_FOUND);
    }
}