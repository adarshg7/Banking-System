package com.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class JpaAuditingConfig {
    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareConfig();
    }
}
