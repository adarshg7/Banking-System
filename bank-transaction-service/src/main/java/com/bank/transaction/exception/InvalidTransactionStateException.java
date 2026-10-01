package com.bank.transaction.exception;

import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidTransactionStateException extends BusinessException {
    public InvalidTransactionStateException(String message) {
        super(message, "TXN-007", HttpStatus.BAD_REQUEST);
    }
}