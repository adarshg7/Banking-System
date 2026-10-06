package com.bank.notification.service;

public interface EmailNotificationService {
    void sendTransactionEmail(String toEmail, String subject, String body);
}
