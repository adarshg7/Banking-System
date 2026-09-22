package com.bank.common.util;

/**
 * Masks sensitive data before logging or displaying partially.
 * PCI-DSS / RBI compliance requirement — full account numbers,
 * PAN, card numbers must NEVER appear in plain text in logs.
 */
public final class MaskingUtils {

    private MaskingUtils() {
    }

    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        int visibleDigits = 4;
        String lastDigits = accountNumber.substring(accountNumber.length() - visibleDigits);
        return "X".repeat(accountNumber.length() - visibleDigits) + lastDigits;
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****";
        }
        String[] parts = email.split("@");
        String namePart = parts[0];
        String maskedName = namePart.length() <= 2
                ? "*".repeat(namePart.length())
                : namePart.charAt(0) + "*".repeat(namePart.length() - 2) + namePart.charAt(namePart.length() - 1);
        return maskedName + "@" + parts[1];
    }

    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "X".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }

    public static String maskPan(String pan) {
        if (pan == null || pan.length() != 10) {
            return "****";
        }
        return pan.substring(0, 2) + "XXXXXX" + pan.substring(8);
    }
}