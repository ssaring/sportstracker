package de.saring.util.gui.javafx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.saring.util.unitcalc.FormatUtils;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of class TimeInSecondsToStringConverter.
 *
 * @author Stefan Saring
 */
public class TimeInSecondsToStringConverterTest {

    private static final TimeInSecondsToStringConverter CONVERTER =
            new TimeInSecondsToStringConverter(new FormatUtils(null));

    /**
     * Tests of method toString().
     */
    @Test
    public void testToString() {
        assertEquals("", CONVERTER.toString(null));
        assertEquals("00:00:00", CONVERTER.toString(0));
        assertEquals("00:59:59", CONVERTER.toString(3599));
        assertEquals("01:00:00", CONVERTER.toString(3600));
        assertEquals("100:00:00", CONVERTER.toString(3600*100));
    }

    /**
     * Tests of method fromString().
     */
    @Test
    public void testFromString() {
        assertEquals(-1, CONVERTER.fromString(null));
        assertEquals(-1, CONVERTER.fromString(""));

        assertEquals(3600, CONVERTER.fromString("1:0:0"));
        assertEquals(3600, CONVERTER.fromString("0001:00:00"));
        assertEquals(0, CONVERTER.fromString("0"));

        assertEquals(-1, CONVERTER.fromString("0::0"));
        assertEquals(-1, CONVERTER.fromString("1:x:0"));
        assertEquals(-1, CONVERTER.fromString("1: 34:02"));
    }
}
