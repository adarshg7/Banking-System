package com.bank.transaction.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(String message) {
        super(message, ErrorCodes.INSUFFICIENT_BALANCE, HttpStatus.BAD_REQUEST);
    }
}