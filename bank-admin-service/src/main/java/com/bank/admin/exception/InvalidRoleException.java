package com.bank.admin.exception;

import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidRoleException extends BusinessException {
    public InvalidRoleException(String message) {
        super(message, "ADM-001", HttpStatus.BAD_REQUEST);
    }
}