package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableAsync
@EnableMethodSecurity
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.bank")
@EntityScan(basePackages = "com.bank")
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.bank")
public class BankApplication {
    public static void main(String[] args){
        SpringApplication.run(BankApplication.class,args);
    }
}