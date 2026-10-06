package com.bank.security.service.impl;

import com.bank.security.entity.RefreshToken;
import com.bank.security.exception.InvalidRefreshTokenException;
import com.bank.security.repository.RefreshTokenRepository;
import com.bank.security.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken createRefreshToken(UUID userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token has expired. Please log in again.");
        }

        return refreshToken;
    }

    @Override
    public void revokeAllForUser(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

}
