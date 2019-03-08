package de.saring.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

/**
 * This class contains all unit tests for the [Date310Utils] class.
 *
 * @author Stefan Saring
 */
class Date310UtilsTest {

    /**
     * Tests the method dateToLocalDateTime().
     */
    @Test
    fun testDateToLocalDateTime() {
        assertEquals(Date310Utils.dateToLocalDateTime(createDate(2014, 1, 15, 0, 0, 0)),
                LocalDateTime.of(2014, 1, 15, 0, 0, 0))
        assertEquals(Date310Utils.dateToLocalDateTime(createDate(2014, 7, 15, 23, 59, 59)),
                LocalDateTime.of(2014, 7, 15, 23, 59, 59))
    }

    /**
     * Tests the method localDateTimeToDate().
     */
    @Test
    fun testLocalDateTimeToDate() {
        assertEquals(Date310Utils.localDateTimeToDate(LocalDateTime.of(2014, 1, 15, 0, 0, 0)),
                createDate(2014, 1, 15, 0, 0, 0))
        assertEquals(Date310Utils.localDateTimeToDate(LocalDateTime.of(2014, 7, 15, 23, 59, 59)),
                createDate(2014, 7, 15, 23, 59, 59))
    }

    /**
     * Tests the method dateToLocalDate().
     */
    @Test
    fun testDateToLocalDate() {
        assertEquals(Date310Utils.dateToLocalDate(createDate(2014, 1, 15, 0, 0, 0)),
                LocalDate.of(2014, 1, 15))
        assertEquals(Date310Utils.dateToLocalDate(createDate(2014, 7, 15, 23, 59, 59)),
                LocalDate.of(2014, 7, 15))
    }

    /**
     * Tests the method localDateToDate().
     */
    @Test
    fun testLocalDateToDate() {
        assertEquals(Date310Utils.localDateToDate(LocalDate.of(2014, 1, 15)),
                createDate(2014, 1, 15, 0, 0, 0))
    }

    /**
     * Tests the method getWeekNumber() when the week starts on Monday.
     */
    @Test
    fun testGetWeekNumberWithStartMonday() {
        assertEquals(52, Date310Utils.getWeekNumber(LocalDate.of(2013, 12, 29), false))
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 1), false))
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 1), false))
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 6), false))

        assertEquals(53, Date310Utils.getWeekNumber(LocalDate.of(2015, 12, 28), false))
        assertEquals(53, Date310Utils.getWeekNumber(LocalDate.of(2016, 1, 3), false))
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2016, 1, 4), false))
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2016, 1, 11), false))
    }

    /**
     * Tests the method getWeekNumber() when the week starts on Sunday.
     */
    @Test
    fun testGetWeekNumberWithStartSunday() {
        assertEquals(53, Date310Utils.getWeekNumber(LocalDate.of(2011, 12, 31), true))
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2012, 1, 1), true))

        assertEquals(52, Date310Utils.getWeekNumber(LocalDate.of(2013, 12, 28), true))
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 1), true))
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 4), true))
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 5), true))

        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2014, 12, 28), true))
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2015, 1, 2), true))
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2015, 1, 4), true))
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2015, 1, 10), true))
        assertEquals(3, Date310Utils.getWeekNumber(LocalDate.of(2015, 1, 11), true))
    }

    /**
     * Tests the method getMilliseconds(), must perform like [java.util.Date.getTime].
     */
    @Test
    fun testGetMilliseconds() {
        assertEquals(createDate(2014, 2, 14, 15, 20, 30).time,
                Date310Utils.getMilliseconds(LocalDateTime.of(2014, 2, 14, 15, 20, 30)))
    }

    /**
     * Tests the method getNoonDateTimeForDate().
     */
    @Test
    fun testGetNoonDateTimeForDate() {

        // assert noon time for the specified date
        val testDate = LocalDate.of(2015, 1, 5)
        assertEquals(LocalDateTime.of(2015, 1, 5, 12, 0, 0), //
                Date310Utils.getNoonDateTimeForDate(testDate))

        // assert noon time for today when no date specified
        assertEquals(LocalDate.now().atTime(12, 0, 0), //
                Date310Utils.getNoonDateTimeForDate(null))
    }

    /**
     * Tests the method localDateTimeToUnixTime().
     */
    @Test
    fun testLocalDateTimeToUnixTime() {

        assertEquals(0L, Date310Utils.localDateTimeToUnixTime( //
                LocalDateTime.of(1970, 1, 1, 0, 0, 0)))
        assertEquals(1461253500L, Date310Utils.localDateTimeToUnixTime( //
                LocalDateTime.of(2016, 4, 21, 15, 45, 0)))
    }

    private fun createDate(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(year, month - 1, day, hour, minute, second)
        return calendar.time
    }
}
