package com.bank.security.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException(String message){
        super(message, ErrorCodes.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }
}
