package com.bank.security.exception;

import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException(String message){
        super(message,"AUTH-002", HttpStatus.UNAUTHORIZED);
    }
}
