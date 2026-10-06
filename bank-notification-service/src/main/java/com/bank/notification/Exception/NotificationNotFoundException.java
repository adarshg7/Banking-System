package com.bank.notification.exception;

import com.bank.common.exception.ResourceNotFoundException;

public class NotificationNotFoundException extends ResourceNotFoundException {
    public NotificationNotFoundException(String message) {
        super(message, "NTF-001");
    }
}