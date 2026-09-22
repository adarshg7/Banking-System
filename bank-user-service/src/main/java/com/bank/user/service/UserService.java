package com.bank.user.service;

import com.bank.user.dto.request.UserRegistrationRequest;
import com.bank.user.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse registerUser(UserRegistrationRequest request);

    UserResponse getUserById(UUID id);

    UserResponse getUserByEmail(String email);


}
