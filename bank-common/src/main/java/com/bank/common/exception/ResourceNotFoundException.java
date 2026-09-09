package com.bank.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException{
    public ResourceNotFoundException(String message, String errorCode){
        super(message,errorCode, HttpStatus.NOT_FOUND);
    }
}
