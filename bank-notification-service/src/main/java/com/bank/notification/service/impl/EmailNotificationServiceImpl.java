package com.bank.notification.service.impl;

import com.bank.notification.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Logs to console instead of sending a real email — setting up a real
 * SMTP server/credentials is out of scope for this project. The
 * interface boundary (EmailNotificationService) means swapping this for
 * a real JavaMailSender implementation later requires touching only
 * this one class.
 */
@Slf4j
@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

    @Override
    public void sendTransactionEmail(String toEmail, String subject, String body) {
        log.info("=== SIMULATED EMAIL ===\nTo: {}\nSubject: {}\nBody: {}\n=======================",
                toEmail, subject, body);
    }
}