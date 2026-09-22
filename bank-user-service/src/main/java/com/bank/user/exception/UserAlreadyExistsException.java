package com.bank.user.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException(String message){
        super(message, ErrorCodes.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}
