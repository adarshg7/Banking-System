package com.bank.security.service;

import com.bank.security.dto.request.LoginRequest;
import com.bank.security.dto.response.LoginResponse;

import java.util.UUID;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refreshAccessToken(String refreshTokenValue);
    void logout(UUID userId);
}
