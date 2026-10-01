package com.bank.transaction.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UnauthorizedAccountAccessException extends BusinessException {
    public UnauthorizedAccountAccessException(String message) {
        super(message, ErrorCodes.UNAUTHORIZED_ACCOUNT_ACCESS, HttpStatus.FORBIDDEN);
    }
}