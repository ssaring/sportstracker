package de.saring.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Date

/**
 * This util class contains several methods for date and time conversion related to new JSR 310 DateTime API.
 *
 * @author Stefan Saring
 */
object Date310Utils {

    /**
     * Converts the specified Date object to LocalDateTime.
     *
     * @param date Date object containing the date and time
     * @return the created LocalDateTime (JSR 310)
     */
    @JvmStatic
    fun dateToLocalDateTime(date: Date): LocalDateTime {
        val instant = Instant.ofEpochMilli(date.time)
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    /**
     * Converts the specified LocalDateTime object to Date.
     *
     * @param dateTime LocalDateTime object containing the date and time (JSR 310)
     * @return the created Date
     */
    @JvmStatic
    fun localDateTimeToDate(dateTime: LocalDateTime): Date {
        val instant = dateTime.atZone(ZoneId.systemDefault()).toInstant()
        return Date.from(instant)
    }

    /**
     * Converts the specified Date object to LocalDate.
     *
     * @param date Date object containing the date
     * @return the created LocalDate (JSR 310)
     */
    @JvmStatic
    fun dateToLocalDate(date: Date): LocalDate {
        return dateToLocalDateTime(date).toLocalDate()
    }

    /**
     * Converts the specified LocalDate object to Date.
     *
     * @param date LocalDate object containing the date (JSR 310)
     * @return the created Date with time fraction of 00:00:00
     */
    @JvmStatic
    fun localDateToDate(date: LocalDate): Date {
        return localDateTimeToDate(date.atStartOfDay())
    }

    /**
     * Returns the week number of the specified date.
     *
     * @param date date
     * @param weekStartsSunday flag whether the week starts on sunday or monday
     * @return week number (1 to 53)
     */
    @JvmStatic
    fun getWeekNumber(date: LocalDate, weekStartsSunday: Boolean): Int {
        val weekField = if (weekStartsSunday) WeekFields.SUNDAY_START else WeekFields.ISO
        return date.get(weekField.weekOfWeekBasedYear())
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by the passed LocalDateTime object.
     *
     * @return  the number of milliseconds
     */
    @JvmStatic
    fun getMilliseconds(dateTime: LocalDateTime): Long {
        return localDateTimeToDate(dateTime).time
    }

    /**
     * Returns a LocalDateTime instance for the specified date with time set to 12:00:00.
     * When no date is specified then the current date will be used.
     *
     * @param date contains the date to be used (optional)
     * @return the created LocalDateTime
     */
    @JvmStatic
    fun getNoonDateTimeForDate(date: LocalDate?): LocalDateTime {
        val tempDate = date ?: LocalDate.now()
        return LocalDateTime.of(tempDate, LocalTime.of(12, 0))
    }
}
