package de.saring.util;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * This class contains all unit tests for the Date310Utils class.
 *
 * @author Stefan Saring
 */
public class Date310UtilsTest {

    /**
     * Tests the method dateToLocalDateTime().
     */
    @Test
    public void testDateToLocalDateTime() {
        assertEquals(Date310Utils.dateToLocalDateTime(createDate(2014, 01, 15, 0, 0, 0)),
                LocalDateTime.of(2014, 01, 15, 0, 0, 0));
        assertEquals(Date310Utils.dateToLocalDateTime(createDate(2014, 07, 15, 23, 59, 59)),
                LocalDateTime.of(2014, 07, 15, 23, 59, 59));
    }

    /**
     * Tests the method localDateTimeToDate().
     */
    @Test
    public void testLocalDateTimeToDate() {
        assertEquals(Date310Utils.localDateTimeToDate(LocalDateTime.of(2014, 01, 15, 0, 0, 0)),
                createDate(2014, 01, 15, 0, 0, 0));
        assertEquals(Date310Utils.localDateTimeToDate(LocalDateTime.of(2014, 07, 15, 23, 59, 59)),
                createDate(2014, 07, 15, 23, 59, 59));
    }

    /**
     * Tests the method dateToLocalDate().
     */
    @Test
    public void testDateToLocalDate() {
        assertEquals(Date310Utils.dateToLocalDate(createDate(2014, 01, 15, 0, 0, 0)),
                LocalDate.of(2014, 01, 15));
        assertEquals(Date310Utils.dateToLocalDate(createDate(2014, 07, 15, 23, 59, 59)),
                LocalDate.of(2014, 07, 15));
    }

    /**
     * Tests the method localDateToDate().
     */
    @Test
    public void testLocalDateToDate() {
        assertEquals(Date310Utils.localDateToDate(LocalDate.of(2014, 01, 15)),
                createDate(2014, 01, 15, 0, 0, 0));
    }

    /**
     * Tests the method getWeekNumber() when the week starts on Monday.
     */
    @Test
    public void testGetWeekNumberWithStartMonday() {
        assertEquals(52, Date310Utils.getWeekNumber(LocalDate.of(2013, 12, 29), false));
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 1), false));
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 6), false));

        assertEquals(53, Date310Utils.getWeekNumber(LocalDate.of(2015, 12, 28), false));
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2016, 1, 4), false));
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2016, 1, 11), false));
    }

    /**
     * Tests the method getWeekNumber() when the week starts on Sunday.
     */
    @Test
    public void testGetWeekNumberWithStartSunday() {
        assertEquals(52, Date310Utils.getWeekNumber(LocalDate.of(2013, 12, 28), true));
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 1), true));
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 4), true));
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2014, 1, 5), true));

        assertEquals(53, Date310Utils.getWeekNumber(LocalDate.of(2014, 12, 28), true));
        assertEquals(1, Date310Utils.getWeekNumber(LocalDate.of(2015, 1, 2), true));
        assertEquals(2, Date310Utils.getWeekNumber(LocalDate.of(2015, 1, 6), true));
    }

    private Date createDate(int year, int month, int day, int hour, int minute, int second) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }
}
