package com.bank.security.service.impl;

import com.bank.security.dto.request.LoginRequest;
import com.bank.security.dto.response.LoginResponse;
import com.bank.security.entity.RefreshToken;
import com.bank.security.exception.InvalidCredentialsException;
import com.bank.security.jwt.JwtTokenProvider;
import com.bank.security.service.AuthService;
import com.bank.security.service.RefreshTokenService;
import com.bank.user.entity.User;
import com.bank.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new InvalidCredentialsException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken.getToken());
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setExpiresInMs(jwtTokenProvider.getExpirationMs());

        return response;
    }

    @Override
    public LoginResponse refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.verifyAndGet(refreshTokenValue);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new InvalidCredentialsException("User no longer exists"));

        String newAccessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        LoginResponse response = new LoginResponse();
        response.setToken(newAccessToken);
        response.setRefreshToken(refreshTokenValue); // same refresh token reused
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setExpiresInMs(jwtTokenProvider.getExpirationMs());

        return response;
    }

    @Override
    public void logout(UUID userId) {
        refreshTokenService.revokeAllForUser(userId);
    }
}
