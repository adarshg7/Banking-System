package com.bank.account.client;

import com.bank.user.dto.response.UserResponse;
import com.bank.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserLookupClient {
    private final UserService userService;

    public UserLookupClient(UserService userService){
        this.userService = userService;
    }

    public UserResponse getUser(UUID userId){
        return userService.getUserById(userId);
    }
}
