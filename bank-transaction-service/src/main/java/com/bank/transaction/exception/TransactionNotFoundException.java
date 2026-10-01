package com.bank.transaction.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.exception.ResourceNotFoundException;

public class TransactionNotFoundException extends ResourceNotFoundException {
    public TransactionNotFoundException(String message) {
        super(message, ErrorCodes.TRANSACTION_NOT_FOUND);
    }
}