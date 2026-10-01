package com.bank.ledger.exception;

import com.bank.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LedgerImbalanceException extends BusinessException {
    public LedgerImbalanceException(String message) {
        super(message, "LDG-001", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}