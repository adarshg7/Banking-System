package com.bank.security.controller;

import com.bank.common.response.ApiResponse;
import com.bank.security.dto.request.LoginRequest;
import com.bank.security.dto.request.RefreshTokenRequest;
import com.bank.security.dto.response.LoginResponse;
import com.bank.security.service.AuthService;
import com.bank.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response,"Login successful"));
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

}
