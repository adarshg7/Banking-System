package com.bank.security.service;

import com.bank.security.entity.RefreshToken;

import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(UUID userId);
    RefreshToken verifyAndGet(String token);
    void revokeAllForUser(UUID userId);
}