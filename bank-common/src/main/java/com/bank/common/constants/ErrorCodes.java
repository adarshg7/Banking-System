package com.bank.common.constants;

public final class ErrorCodes {
    private ErrorCodes(){

    }

    //GENERIC
    public static final String INTERNAL_SERVER_ERROR = "SYS-500";
    public static final String VALIDATION_FAILED = "SYS-001";
    public static final String FORBIDDEN = "SYS-003";
    public static final String UNAUTHORIZED = "SYS-002";

    //USER MODULE
    public static final String USER_NOT_FOUND = "USR-001";
    public static final String USER_ALREADY_EXISTS = "USR-002";
    public static final String INVALID_CREDENTIALS = "USR-003";
    public static final String KYC_NOT_VERIFIED = "USR-004";
    public static final String INVALID_OTP = "USR-005";

    // Account module
    public static final String ACCOUNT_NOT_FOUND = "ACC-001";
    public static final String ACCOUNT_FROZEN = "ACC-002";
    public static final String ACCOUNT_CLOSED = "ACC-003";
    public static final String INSUFFICIENT_BALANCE = "ACC-004";
    public static final String MINIMUM_BALANCE_VIOLATION = "ACC-005";
    public static final String UNAUTHORIZED_ACCOUNT_ACCESS = "ACC-006";

    // Transaction module
    public static final String TRANSACTION_NOT_FOUND = "TXN-001";
    public static final String DAILY_LIMIT_EXCEEDED = "TXN-002";
    public static final String DUPLICATE_TRANSACTION = "TXN-003";
    public static final String TRANSACTION_ALREADY_PROCESSED = "TXN-004";
    public static final String APPROVAL_PENDING = "TXN-005";
    public static final String SELF_TRANSFER_NOT_ALLOWED = "TXN-006";
}
