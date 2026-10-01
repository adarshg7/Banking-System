package com.bank.transaction.exception;

import com.bank.common.constants.ErrorCodes;
import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SelfTransferException extends BusinessException {
    public SelfTransferException(String message) {
        super(message, ErrorCodes.SELF_TRANSFER_NOT_ALLOWED, HttpStatus.BAD_REQUEST);
    }
}