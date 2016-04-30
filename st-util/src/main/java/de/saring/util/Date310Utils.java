package de.saring.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
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

    /**
     * Returns the week number of the specified date.
     *
     * @param date date
     * @param weekStartsSunday flag whether the week starts on sunday or monday
     * @return week number (1 to 53)
     */
    public static int getWeekNumber(final LocalDate date, final boolean weekStartsSunday) {
        final WeekFields weekField = weekStartsSunday ? WeekFields.SUNDAY_START : WeekFields.ISO;
        return date.get(weekField.weekOfWeekBasedYear());
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by the passed LocalDateTime object.
     *
     * @return  the number of milliseconds
     */
    public static long getMilliseconds(final LocalDateTime dateTime) {
        return localDateTimeToDate(dateTime).getTime();
    }

    /**
     * Returns a LocalDateTime instance for the specified date with time set to 12:00:00.
     * When no date is specified then the current date will be used.
     *
     * @param date contains the date to be used (optional)
     * @return the created LocalDateTime
     */
    public static LocalDateTime getNoonDateTimeForDate(final LocalDate date) {
        final LocalDate tempDate = date == null ? LocalDate.now() : date;
        return LocalDateTime.of(tempDate, LocalTime.of(12, 0));
    }

    /**
     * Converts the specified LocalDateTime to Unix time (the number of seconds since 1970-01-01 00:00:00 UTC).
     *
     * @param dateTime LocalDateTime object containing the date and time (JSR 310)
     * @return Unix time
     */
    public static long localDateTimeToUnixTime(final LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).getEpochSecond();
    }
}
