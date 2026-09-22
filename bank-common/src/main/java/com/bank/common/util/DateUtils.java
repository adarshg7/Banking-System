package com.bank.common.util;

import com.bank.common.constants.AppConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private DateUtils(){
    }

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(AppConstants.DATE_TIME_FORMAT);

    public static String formatDate(LocalDate date){
        return date == null ? null : date.format((DATE_FORMATTER));
    }

    public static String formatDateTime(LocalDateTime dateTime){
        return dateTime == null ? null : dateTime.format(DATE_TIME_FORMATTER);
    }

    public static int calculateAge(LocalDate dateOfBirth){
        if(dateOfBirth == null){
            throw new IllegalArgumentException("Date of birth cannot be null");
        }

        return Period.between(dateOfBirth,LocalDate.now()).getYears();
    }

    public static boolean isMinor(LocalDate dateOfBirth){
        return calculateAge(dateOfBirth) < 18;
    }
}
