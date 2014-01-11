package de.saring.util;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private Date createDate(int year, int month, int day, int hour, int minute, int second) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }
}
