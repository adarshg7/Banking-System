package com.bank.security.service;

import com.bank.security.dto.request.LoginRequest;
import com.bank.security.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
