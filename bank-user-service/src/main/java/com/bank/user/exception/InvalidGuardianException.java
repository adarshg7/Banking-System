package com.bank.user.exception;

import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidGuardianException extends BusinessException {
    public InvalidGuardianException(String message){
        super(message,"USR-006", HttpStatus.BAD_REQUEST);
    }
}
