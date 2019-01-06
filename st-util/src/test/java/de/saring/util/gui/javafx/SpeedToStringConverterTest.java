package de.saring.util.gui.javafx;

import de.saring.util.unitcalc.FormatUtils;
import de.saring.util.unitcalc.FormatUtils.SpeedMode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests of class SpeedToStringConverter.
 *
 * @author Stefan Saring
 */
public class SpeedToStringConverterTest {

    private static Locale defaultLocale;

    /**
     * Set default locale to GERMAN, because the number validation tests are locale dependent.
     */
    @BeforeAll
    public static void initLocale() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.GERMAN);
    }

    /**
     * Reset default locale to previous value.
     */
    @AfterAll
    public static void resetLocale() {
        Locale.setDefault(defaultLocale);
    }

    /**
     * Tests of method toString() when using SpeedView DistancePerHour.
     */
    @Test
    public void testToStringDistancePerHour() {

        final SpeedToStringConverter CONVERTER =
                new SpeedToStringConverter(new FormatUtils(null), SpeedMode.SPEED);

        assertEquals("", CONVERTER.toString(null));
        assertEquals("0", CONVERTER.toString(0));
        assertEquals("24,3", CONVERTER.toString(24.3f));
        assertEquals("12.345,679", CONVERTER.toString(12345.6789f)); // must round the 3. digit
    }

    /**
     * Tests of method toString() when using SpeedView MinutesPerDistance.
     */
    @Test
    public void testToStringMinutesPerDistance() {

        final SpeedToStringConverter CONVERTER =
                new SpeedToStringConverter(new FormatUtils(null), SpeedMode.PACE);

        assertEquals("", CONVERTER.toString(null));
        assertEquals("00:00", CONVERTER.toString(0));
        assertEquals("03:00", CONVERTER.toString(20f));
        assertEquals("02:30", CONVERTER.toString(24f));
        assertEquals("00:29", CONVERTER.toString(123.456f));
    }

    /**
     * Tests of method fromString() when using SpeedView DistancePerHour.
     */
    @Test
    public void testFromStringDistancePerHour() {

        final SpeedToStringConverter CONVERTER =
                new SpeedToStringConverter(new FormatUtils(null), SpeedMode.SPEED);

        assertEquals(-1f, CONVERTER.fromString(null).floatValue(), 0.0001f);
        assertEquals(-1f, CONVERTER.fromString("").floatValue(), 0.0001f);
        assertEquals(-1f, CONVERTER.fromString("A1").floatValue(), 0.0001f);

        assertEquals(0f, CONVERTER.fromString("0").floatValue(), 0.0001f);
        assertEquals(20f, CONVERTER.fromString("20").floatValue(), 0.0001f);
        assertEquals(12345.678f, CONVERTER.fromString("12.345,678").floatValue(), 0.0001f);
    }

    /**
     * Tests of method fromString() when using SpeedView MinutesPerDistance.
     */
    @Test
    public void testFromStringMinutesPerDistance() {

        final SpeedToStringConverter CONVERTER =
                new SpeedToStringConverter(new FormatUtils(null), SpeedMode.PACE);

        assertEquals(-1f, CONVERTER.fromString(null).floatValue(), 0.0001f);
        assertEquals(-1f, CONVERTER.fromString("").floatValue(), 0.0001f);
        assertEquals(-1f, CONVERTER.fromString("A1").floatValue(), 0.0001f);
        assertEquals(-1f, CONVERTER.fromString("0:0").floatValue(), 0.0001f);
        assertEquals(-1f, CONVERTER.fromString("2:2").floatValue(), 0.0001f);

        assertEquals(0f, CONVERTER.fromString("00:00").floatValue(), 0.0001f);
        assertEquals(30f, CONVERTER.fromString("02:00").floatValue(), 0.0001f);
        assertEquals(24f, CONVERTER.fromString("02:30").floatValue(), 0.0001f);
        assertEquals(124.138f, CONVERTER.fromString("00:29").floatValue(), 0.001f);
    }
}
