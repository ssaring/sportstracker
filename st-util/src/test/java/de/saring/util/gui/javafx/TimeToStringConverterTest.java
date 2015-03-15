package de.saring.util.gui.javafx;

import static org.junit.Assert.assertEquals;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.Test;

/**
 * Unit tests of class TimeToStringConverter.
 *
 * @author Stefan Saring
 */
public class TimeToStringConverterTest {

    private static final TimeToStringConverter CONVERTER = new TimeToStringConverter();

    /**
     * Tests of method toString().
     */
    @Test
    public void testToString() {
        assertEquals("", CONVERTER.toString(null));
        assertEquals("00:00", CONVERTER.toString(LocalTime.of(0, 0)));
        assertEquals("00:59", CONVERTER.toString(LocalTime.of(0, 59)));
        assertEquals("23:59", CONVERTER.toString(LocalTime.of(23, 59)));
    }

    /**
     * Tests of method fromString(): all conversions are successful.
     */
    @Test
    public void testFromStringSuccess() {
        assertEquals(LocalTime.of(0, 0), CONVERTER.fromString("0:0"));
        assertEquals(LocalTime.of(0, 0), CONVERTER.fromString("00:00"));
        assertEquals(LocalTime.of(0, 0), CONVERTER.fromString("24:0"));
        assertEquals(LocalTime.of(5, 5), CONVERTER.fromString("5:5"));
        assertEquals(LocalTime.of(23, 59), CONVERTER.fromString("23:59"));
    }

    /**
     * Tests of method fromString(): conversion must fail for invalid time values.
     */
    @Test(expected = DateTimeParseException.class)
    public void testFromStringFailedInvalidTime() {
        CONVERTER.fromString("24:62");
    }

    /**
     * Tests of method fromString(): conversion must fail for no time values.
     */
    @Test(expected = DateTimeParseException.class)
    public void testFromStringFailedNoTime() {
        CONVERTER.fromString("foo:bar");
    }

    /**
     * Tests of method fromString(): conversion must fail when minutes are missing.
     */
    @Test(expected = DateTimeParseException.class)
    public void testFromStringFailedNoMinutes() {
        CONVERTER.fromString("10:");
    }
}
