package com.bank.security.service;

import java.util.UUID;

public interface OtpService {
    String generateOtp(UUID userId, String purpose, String referenceId);
    boolean verifyOtp(UUID userId, String purpose, String referenceId, String submittedOtp);
}