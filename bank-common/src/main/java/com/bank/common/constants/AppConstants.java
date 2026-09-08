package com.bank.common.constants;

public final class AppConstants {
    private AppConstants(){

    }

    //DATE/TIME FORMATS
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMEZONE = "Asia/Kolkata";

    //PAGINATION DEFAULTS
    public static  final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    //ACCOUNT RULES
    public static final double MIN_SAVING_BALANCE = 1000.00;
    public static final double MIN_CURRENT_BALANCE = 5000.00;
    public static final double DAILY_TRANSFER_LIMIT_INDIVIDUAL = 100000.00;
    public static final double DAILY_TRANSFER_LIMIT_BUSINESS = 10000000.00;

    //OTP
    public static final int OTP_LENGTH = 6;
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int MAX_OTP_ATTEMPTS = 3;

    //SECURITY
    public static final int JWT_EXPIRATION_MS = 3600000;
    public static final int REFRESH_TOKEN_EXPIRATION_MS = 8640000;

    //ACCOUNT NUMBER GENERATION
    public static final String ACCOUNT_NUMBER_PREFIX = "AC";
    public static final int ACCOUNT_NUMBER_LENGTH = 12;

    //TRANSACTION REFERENCE
    public static final String TXN_REFERENCE_PREFIX = "TXN";
}
