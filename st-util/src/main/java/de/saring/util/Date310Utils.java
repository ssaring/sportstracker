package de.saring.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * This util class contains several methods for date and time conversion related to new JSR 310 DateTime API.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class Date310Utils {

    private Date310Utils() {
    }

    /**
     * Converts the specified Date object to LocalDateTime.
     *
     * @param date Date object containing the date and time
     * @return the created LocalDateTime (JSR 310)
     */
    public static LocalDateTime dateToLocalDateTime(final Date date) {
        final Instant instant = Instant.ofEpochMilli(date.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Converts the specified LocalDateTime object to Date.
     *
     * @param dateTime LocalDateTime object containing the date and time (JSR 310)
     * @return the created Date
     */
    public static Date localDateTimeToDate(final LocalDateTime dateTime) {
        final Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Converts the specified Date object to LocalDate.
     *
     * @param date Date object containing the date
     * @return the created LocalDate (JSR 310)
     */
    public static LocalDate dateToLocalDate(final Date date) {
        return dateToLocalDateTime(date).toLocalDate();
    }

    /**
     * Converts the specified LocalDate object to Date.
     *
     * @param date LocalDate object containing the date (JSR 310)
     * @return the created Date with time fraction of 00:00:00
     */
    public static Date localDateToDate(final LocalDate date) {
        return localDateTimeToDate(date.atStartOfDay());
    }
}
