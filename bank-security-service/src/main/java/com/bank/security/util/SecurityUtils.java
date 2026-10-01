package com.bank.security.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {
    private SecurityUtils(){

    }

    public static UUID getCurrentUserId(){
        String principal = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return UUID.fromString(principal);
    }
}
