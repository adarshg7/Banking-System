package com.bank.security.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private UUID userId;
    private String email;
    private String role;
    private long expiresInMs;
}