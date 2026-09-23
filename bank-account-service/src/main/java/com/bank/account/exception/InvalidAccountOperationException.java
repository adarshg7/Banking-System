package com.bank.account.exception;

import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidAccountOperationException extends BusinessException {
    public InvalidAccountOperationException(String message) {
        super(message, "ACC-007", HttpStatus.BAD_REQUEST);
    }
}