package com.bank.common.constants;
public final class RegexPatterns {

    private RegexPatterns() {
    }

    public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_NUMBER = "^[6-9]\\d{9}$"; // Indian mobile format
    public static final String PAN_NUMBER = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";
    public static final String AADHAAR_NUMBER = "^\\d{12}$";
    public static final String IFSC_CODE = "^[A-Z]{4}0[A-Z0-9]{6}$";
    public static final String ACCOUNT_NUMBER = "^AC\\d{10}$";
    public static final String STRONG_PASSWORD =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
}