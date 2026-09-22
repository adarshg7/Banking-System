package com.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TEMPORARY — permits all requests without authentication.
 * This exists only because spring-security-crypto on the classpath
 * triggers Spring Security's default auto-configuration (login form +
 * random password), which blocks testing our endpoints right now.
 *
 * This will be REPLACED entirely once bank-security-service is built
 * with real JWT-based authentication, role-based authorization, and
 * proper endpoint-level security rules.
 */
@Configuration
public class TempSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}