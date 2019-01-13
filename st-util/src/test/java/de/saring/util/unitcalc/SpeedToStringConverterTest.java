package de.saring.util.unitcalc;

import de.saring.util.unitcalc.FormatUtils.SpeedMode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
     * Tests of method floatSpeedtoString() when using SpeedMode speed.
     */
    @Test
    public void testFloatSpeedtoStringDistancePerHour() {

        final SpeedToStringConverter CONVERTER = new SpeedToStringConverter(SpeedMode.SPEED);

        assertEquals(null, CONVERTER.floatSpeedtoString(null));
        assertEquals("0", CONVERTER.floatSpeedtoString(0f));
        assertEquals("24,3", CONVERTER.floatSpeedtoString(24.3f));
        assertEquals("12.345,679", CONVERTER.floatSpeedtoString(12345.6789f)); // must round the 3. digit
    }

    /**
     * Tests of method floatSpeedtoString() when using SpeedMode pace.
     */
    @Test
    public void testFloatSpeedtoStringMinutesPerDistance() {

        final SpeedToStringConverter CONVERTER = new SpeedToStringConverter(SpeedMode.PACE);

        assertEquals(null, CONVERTER.floatSpeedtoString(null));
        assertEquals("00:00", CONVERTER.floatSpeedtoString(0f));
        assertEquals("03:00", CONVERTER.floatSpeedtoString(20f));
        assertEquals("02:30", CONVERTER.floatSpeedtoString(24f));
        assertEquals("00:29", CONVERTER.floatSpeedtoString(123.456f));
    }

    /**
     * Tests of method stringSpeedToFloat() when using SpeedMode speed.
     */
    @Test
    public void testStringSpeedToFloatDistancePerHour() {

        final SpeedToStringConverter CONVERTER = new SpeedToStringConverter(SpeedMode.SPEED);

        assertNull(CONVERTER.stringSpeedToFloat(null));
        assertNull(CONVERTER.stringSpeedToFloat(""));
        assertNull(CONVERTER.stringSpeedToFloat("A1"));

        assertEquals(0f, CONVERTER.stringSpeedToFloat("0").floatValue(), 0.0001f);
        assertEquals(20f, CONVERTER.stringSpeedToFloat("20").floatValue(), 0.0001f);
        assertEquals(12345.678f, CONVERTER.stringSpeedToFloat("12.345,678").floatValue(), 0.0001f);
    }

    /**
     * Tests of method stringSpeedToFloat() when using SpeedMode pace.
     */
    @Test
    public void testStringSpeedToFloatMinutesPerDistance() {

        final SpeedToStringConverter CONVERTER = new SpeedToStringConverter(SpeedMode.PACE);

        assertNull(CONVERTER.stringSpeedToFloat(null));
        assertNull(CONVERTER.stringSpeedToFloat(""));
        assertNull(CONVERTER.stringSpeedToFloat("A1"));
        assertNull(CONVERTER.stringSpeedToFloat("0:0"));
        assertNull(CONVERTER.stringSpeedToFloat("2:2"));

        assertEquals(0f, CONVERTER.stringSpeedToFloat("00:00").floatValue(), 0.0001f);
        assertEquals(30f, CONVERTER.stringSpeedToFloat("02:00").floatValue(), 0.0001f);
        assertEquals(24f, CONVERTER.stringSpeedToFloat("02:30").floatValue(), 0.0001f);
        assertEquals(124.138f, CONVERTER.stringSpeedToFloat("00:29").floatValue(), 0.001f);
    }
}
