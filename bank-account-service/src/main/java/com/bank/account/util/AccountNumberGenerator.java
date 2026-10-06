package com.bank.account.util;

import com.bank.account.enums.AccountType;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int RANDOM_DIGITS = 7;

    public String generate(String branchCode, AccountType accountType) {

        validateBranchCode(branchCode);

        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }

        int typeCode = resolveTypeCode(accountType);

        StringBuilder accountNumber = new StringBuilder(12);

        accountNumber.append(branchCode);

        accountNumber.append(typeCode);

        for (int i = 0; i < RANDOM_DIGITS; i++) {
            accountNumber.append(RANDOM.nextInt(10));
        }

        return accountNumber.toString();
    }

    private int resolveTypeCode(AccountType type) {
        return switch (type) {
            case SAVINGS -> 1;
            case CURRENT -> 2;
            case WALLET -> 3;
            case FIXED_DEPOSIT -> 4;
            case RECURRING_DEPOSIT -> 5;
        };
    }

    private void validateBranchCode(String branchCode) {

        if (branchCode == null || !branchCode.matches("\\d{4}")) {
            throw new IllegalArgumentException(
                    "Branch code must contain exactly 4 digits"
            );
        }
    }
}