package com.adeem.stockflow.service.util;

import java.time.LocalDate;

public class GlobalUtils {

    public static String generateReference(String reference) {
        Long currentYear = (long) LocalDate.now().getYear();
        if (reference != null && reference.length() == 9 && reference.charAt(4) == '/') {
            Long year = Long.parseLong(reference.substring(0, 4));
            Long number = Long.parseLong(reference.substring(5));
            return year.equals(currentYear) ? String.format("%04d/%04d", year, number + 1L) : String.format("%04d/%04d", currentYear, 1);
        } else {
            return String.format("%04d/%04d", currentYear, 1);
        }
    }
}
