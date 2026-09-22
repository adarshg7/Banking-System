package com.bank.user.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message){
        super(message, ErrorCodes.USER_NOT_FOUND);
    }

}
