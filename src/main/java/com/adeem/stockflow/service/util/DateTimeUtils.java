package com.adeem.stockflow.service.util;

import com.adeem.stockflow.config.Constants;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public final class DateTimeUtils {

    /**
     * Gets the current date and time in Algeria time zone.
     * @return ZonedDateTime representing current time in Algeria
     */
    public static ZonedDateTime nowAlgeria() {
        return ZonedDateTime.now(Constants.ALGERIA_ZONE);
    }

    /**
     * Converts an Instant to ZonedDateTime in Algeria time zone.
     * @param instant The instant to convert
     * @return ZonedDateTime in Algeria time zone
     */
    public static ZonedDateTime toAlgeriaZone(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(Constants.ALGERIA_ZONE);
    }

    /**
     * Converts a LocalDateTime to ZonedDateTime in Algeria time zone.
     * @param localDateTime The LocalDateTime to convert
     * @return ZonedDateTime in Algeria time zone
     */
    public static ZonedDateTime toAlgeriaZone(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(Constants.ALGERIA_ZONE);
    }

    /**
     * Converts a LocalDate to ZonedDateTime at start of day in Algeria time zone.
     * @param localDate The LocalDate to convert
     * @return ZonedDateTime at start of day in Algeria time zone
     */
    public static ZonedDateTime startOfDayAlgeria(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay(Constants.ALGERIA_ZONE);
    }

    /**
     * Formats ZonedDateTime to string in a consistent format.
     * @param zonedDateTime The ZonedDateTime to format
     * @return String representation of date/time
     */
    public static String formatDateTime(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        // Define your preferred format here
        return zonedDateTime.toString();
    }

    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }
}
