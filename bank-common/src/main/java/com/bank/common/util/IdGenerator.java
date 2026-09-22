package com.bank.common.util;

import com.bank.common.constants.AppConstants;

import java.security.SecureRandom;
import java.time.Instant;

public final class IdGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private IdGenerator(){
    }

    public static String generateAccountNumber(){
        StringBuilder sb = new StringBuilder(AppConstants.ACCOUNT_NUMBER_PREFIX);
        for(int i =0; i< AppConstants.ACCOUNT_NUMBER_LENGTH - AppConstants.ACCOUNT_NUMBER_PREFIX.length(); i++){
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateTransactionReference(){
        return AppConstants.TXN_REFERENCE_PREFIX + Instant.now().toEpochMilli() + RANDOM.nextInt(1000);
    }

    public static String generateOtp(int length){
        StringBuilder otp = new StringBuilder();
        for(int i =0; i<length; i++){
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }
}
