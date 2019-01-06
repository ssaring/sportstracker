package de.saring.util.unitcalc;

import de.saring.util.unitcalc.FormatUtils.SpeedMode;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class contains all unit tests for the ConvertUtils class.
 *
 * @author Stefan Saring, Jacob Ilsoe Christensen (parts of C# version)
 */
public class FormatUtilsTest {

    /**
     * Thes tests needs to use the English locale, results look different for other.
     */
    @BeforeAll
    static void setUp() {
        Locale.setDefault(Locale.ENGLISH);
    }

    /**
     * Restore default Locale afterwards.
     */
    @AfterAll
    static void tearDown() {
        Locale.setDefault(Locale.getDefault());
    }

    /**
     * Tests the getter of current unit system.
     */
    @Test
    public void testGetSettings() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.English);
        assertEquals(UnitSystem.English, formatUtils.getUnitSystem());
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testMinutes2TimeString() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals(formatUtils.minutes2TimeString(0), "00:00");
        assertEquals(formatUtils.minutes2TimeString(9), "00:09");
        assertEquals(formatUtils.minutes2TimeString(70), "01:10");
        assertEquals(formatUtils.minutes2TimeString(3600), "60:00");
        assertEquals(formatUtils.minutes2TimeString(7199), "119:59");
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testSeconds2TimeString() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals(formatUtils.seconds2TimeString(0), "00:00:00");
        assertEquals(formatUtils.seconds2TimeString(3599), "00:59:59");
        assertEquals(formatUtils.seconds2TimeString(3601), "01:00:01");
        assertEquals(formatUtils.seconds2TimeString(365145), "101:25:45");
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testSeconds2MinuteTimeString() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals(formatUtils.seconds2MinuteTimeString(0), "00:00");
        assertEquals(formatUtils.seconds2MinuteTimeString(3599), "59:59");
        assertEquals(formatUtils.seconds2MinuteTimeString(3601), "60:01");
        assertEquals(formatUtils.seconds2MinuteTimeString(365145), "6085:45");
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testTimeString2TotalSeconds() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals(formatUtils.timeString2TotalSeconds(null), -1);
        assertEquals(formatUtils.timeString2TotalSeconds(""), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("affe"), -1);

        assertEquals(formatUtils.timeString2TotalSeconds(":"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("::"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("12:12:12:12"), -1);

        assertEquals(formatUtils.timeString2TotalSeconds("12:12:60"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("12:60:12"), -1);

        assertEquals(formatUtils.timeString2TotalSeconds("-12:12:12"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("12:-12:12"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("12:12:-12"), -1);

        assertEquals(formatUtils.timeString2TotalSeconds("a:b:c"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("12a:12:12"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("12:12b:12"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("12:12:12c"), -1);

        assertEquals(formatUtils.timeString2TotalSeconds(":12:12"), -1);
        assertEquals(formatUtils.timeString2TotalSeconds("12::12"), -1);

        assertEquals(formatUtils.timeString2TotalSeconds("0"), 0);
        assertEquals(formatUtils.timeString2TotalSeconds("5"), 5);
        assertEquals(formatUtils.timeString2TotalSeconds("37"), 37);

        assertEquals(formatUtils.timeString2TotalSeconds("3:0"), (3 * 60) + 0);
        assertEquals(formatUtils.timeString2TotalSeconds("3:5"), (3 * 60) + 5);
        assertEquals(formatUtils.timeString2TotalSeconds("32:48"), (32 * 60) + 48);

        assertEquals(formatUtils.timeString2TotalSeconds("3:0:0"), (3 * 60 * 60) + (0 * 60) + 0);
        assertEquals(formatUtils.timeString2TotalSeconds("13:5:34"), (13 * 60 * 60) + (5 * 60) + 34);
        assertEquals(formatUtils.timeString2TotalSeconds("114:32:48"), (114 * 60 * 60) + (32 * 60) + 48);
    }

    /**
     * Tests that temperatureToString works as expected.
     */
    @Test
    public void testTemperatureToString() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals("-5 C", formatUtils.temperatureToString((short) -5));
        assertEquals("100 C", formatUtils.temperatureToString((short) 100));
        assertEquals("1,234 C", formatUtils.temperatureToString((short) 1234));

        formatUtils = new FormatUtils(UnitSystem.English);
        assertEquals("212 F", formatUtils.temperatureToString((short) 100));
    }

    /**
     * Tests that distanceToString works as expected.
     */
    @Test
    public void testDistanceToString() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals("0 km", formatUtils.distanceToString(0f, 0));
        assertEquals("100 km", formatUtils.distanceToString(100f, 0));
        assertEquals("100 km", formatUtils.distanceToString(100.0f, 2));
        assertEquals("100.55 km", formatUtils.distanceToString(100.55f, 2));
        assertEquals("100.56 km", formatUtils.distanceToString(100.555f, 2));
        assertEquals("100,234.55 km", formatUtils.distanceToString(100234.55f, 2));

        formatUtils = new FormatUtils(UnitSystem.English);
        assertEquals("0 m", formatUtils.distanceToString(0f, 0));
        assertEquals("62 m", formatUtils.distanceToString(100f, 0));
        assertEquals("62.45 m", formatUtils.distanceToString(100.50f, 2));
    }

    /**
     * Tests that distanceToStringWithoutUnitName works as expected (most cases
     * are allready tested in testDistanceToString()).
     */
    @Test
    public void testDistanceToStringWithoutUnitName() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals("100", formatUtils.distanceToStringWithoutUnitName(100f, 0));
        assertEquals("100.56", formatUtils.distanceToStringWithoutUnitName(100.555f, 2));

        formatUtils = new FormatUtils(UnitSystem.English);
        assertEquals("62", formatUtils.distanceToStringWithoutUnitName(100f, 0));
        assertEquals("62.45", formatUtils.distanceToStringWithoutUnitName(100.50f, 2));
    }

    /**
     * Tests that speedToString() works as expected.
     */
    @Test
    public void testSpeedToString() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals("0", formatUtils.speedToString(0f, 0, SpeedMode.SPEED));
        assertEquals("100 km/h", formatUtils.speedToString(100f, 0, SpeedMode.SPEED));
        assertEquals("100 km/h", formatUtils.speedToString(100.0f, 2, SpeedMode.SPEED));
        assertEquals("100.55 km/h", formatUtils.speedToString(100.55f, 2, SpeedMode.SPEED));
        assertEquals("100.56 km/h", formatUtils.speedToString(100.555f, 2, SpeedMode.SPEED));
        assertEquals("100,234.55 km/h", formatUtils.speedToString(100234.55f, 2, SpeedMode.SPEED));

        formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals("05:00 min/km", formatUtils.speedToString(12f, 0, SpeedMode.PACE));
        assertEquals("N/A", formatUtils.speedToString(0f, 0, SpeedMode.PACE));

        formatUtils = new FormatUtils(UnitSystem.English);
        assertEquals("0", formatUtils.speedToString(0f, 0, SpeedMode.SPEED));
        assertEquals("62 mph", formatUtils.speedToString(100f, 0, SpeedMode.SPEED));
        assertEquals("62.45 mph", formatUtils.speedToString(100.50f, 2, SpeedMode.SPEED));

        formatUtils = new FormatUtils(UnitSystem.English);
        assertEquals("08:02 min/m", formatUtils.speedToString(12f, 0, SpeedMode.PACE));
        assertEquals("N/A", formatUtils.speedToString(0f, 0, SpeedMode.PACE));
    }

    /**
     * Tests that speedToStringWithoutUnitName() works as expected (most cases are already tested in
     * testSpeedToString()).
     */
    @Test
    public void testSpeedToStringWithoutUnitName() {
        FormatUtils formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals("0", formatUtils.speedToString(0f, 0, SpeedMode.SPEED));
        assertEquals("100", formatUtils.speedToStringWithoutUnitName(100f, 0, SpeedMode.SPEED));
        assertEquals("100.56", formatUtils.speedToStringWithoutUnitName(100.555f, 2, SpeedMode.SPEED));

        formatUtils = new FormatUtils(UnitSystem.Metric);
        assertEquals("05:00", formatUtils.speedToStringWithoutUnitName(12f, 0, SpeedMode.PACE));
        assertEquals("N/A", formatUtils.speedToStringWithoutUnitName(0f, 0, SpeedMode.PACE));

        formatUtils = new FormatUtils(UnitSystem.English);
        assertEquals("0", formatUtils.speedToString(0f, 0, SpeedMode.SPEED));
        assertEquals("62.45", formatUtils.speedToStringWithoutUnitName(100.50f, 2, SpeedMode.SPEED));

        formatUtils = new FormatUtils(UnitSystem.English);
        assertEquals("08:02", formatUtils.speedToStringWithoutUnitName(12f, 0, SpeedMode.PACE));
        assertEquals("N/A", formatUtils.speedToStringWithoutUnitName(0f, 0, SpeedMode.PACE));
    }

    /**
     * Tests that getDistanceUnitName works as expected.
     */
    @Test
    public void testGetDistanceUnitName() {
        assertEquals("km", new FormatUtils(UnitSystem.Metric).getDistanceUnitName());
        assertEquals("m", new FormatUtils(UnitSystem.English).getDistanceUnitName());
    }

    /**
     * Tests that getSpeedUnitName() works as expected.
     */
    @Test
    public void testGetSpeedUnitName() {
        assertEquals("km/h", new FormatUtils(UnitSystem.Metric).getSpeedUnitName(SpeedMode.SPEED));
        assertEquals("mph", new FormatUtils(UnitSystem.English).getSpeedUnitName(SpeedMode.SPEED));
        assertEquals("min/km", new FormatUtils(UnitSystem.Metric).getSpeedUnitName(SpeedMode.PACE));
        assertEquals("min/m", new FormatUtils(UnitSystem.English).getSpeedUnitName(SpeedMode.PACE));
    }

    /**
     * Tests that getTemperatureUnitName works as expected.
     */
    @Test
    public void testGetTemperatureUnitName() {
        assertEquals("C", new FormatUtils(UnitSystem.Metric).getTemperatureUnitName());
        assertEquals("F", new FormatUtils(UnitSystem.English).getTemperatureUnitName());
    }

    /**
     * Tests that getAltitudeUnitName() works as expected.
     */
    @Test
    public void testGetAltitudeUnitName() {
        assertEquals("m", new FormatUtils(UnitSystem.Metric).getAltitudeUnitName());
        assertEquals("ft", new FormatUtils(UnitSystem.English).getAltitudeUnitName());
    }

    /**
     * Tests that heartRateToString works as expected.
     */
    @Test
    public void testHeartRateToString() {
        assertEquals("0 bpm", new FormatUtils(UnitSystem.Metric).heartRateToString(0));
        assertEquals("100 bpm", new FormatUtils(UnitSystem.Metric).heartRateToString(100));
        assertEquals("1,234 bpm", new FormatUtils(UnitSystem.Metric).heartRateToString(1234));
    }

    /**
     * Tests that heightToString works as expected.
     */
    @Test
    public void testHeightToString() {
        assertEquals("0 m", new FormatUtils(UnitSystem.Metric).heightToString(0));
        assertEquals("100 m", new FormatUtils(UnitSystem.Metric).heightToString(100));
        assertEquals("10,023 m", new FormatUtils(UnitSystem.Metric).heightToString(10023));
        assertEquals("0 ft", new FormatUtils(UnitSystem.English).heightToString(0));
        assertEquals("328 ft", new FormatUtils(UnitSystem.English).heightToString(100));
    }

    /**
     * Tests that heightToStringWithoutUnitName works as expected (most cases
     * are allready tested in testHeightToString()).
     */
    @Test
    public void testHeightToStringWithoutUnitName() {
        assertEquals("100", new FormatUtils(UnitSystem.Metric).heightToStringWithoutUnitName(100));
        assertEquals("328", new FormatUtils(UnitSystem.English).heightToStringWithoutUnitName(100));
    }

    /**
     * Tests that cadenceToString works as expected.
     */
    @Test
    public void testCadenceToString() {
        assertEquals("0 rpm / spm", new FormatUtils(UnitSystem.Metric).cadenceToString(0));
        assertEquals("90 rpm / spm", new FormatUtils(UnitSystem.Metric).cadenceToString(90));
        assertEquals("1,234 rpm / spm", new FormatUtils(UnitSystem.Metric).cadenceToString(1234));
    }

    /**
     * Tests that totcyclesToString works as expected.
     */
    @Test
    public void totcyclesToString() {
        assertEquals("0 rotations / steps", new FormatUtils(UnitSystem.Metric).cyclesToString(0));
        assertEquals("90 rotations / steps", new FormatUtils(UnitSystem.Metric).cyclesToString(90));
        assertEquals("1,234 rotations / steps", new FormatUtils(UnitSystem.Metric).cyclesToString(1234));
    }

    /**
     * Tests that caloriesToString works as expected.
     */
    @Test
    public void testCaloriesToString() {
        assertEquals("0 kCal", new FormatUtils(UnitSystem.Metric).caloriesToString(0));
        assertEquals("90 kCal", new FormatUtils(UnitSystem.Metric).caloriesToString(90));
        assertEquals("1,234 kCal", new FormatUtils(UnitSystem.Metric).caloriesToString(1234));
        assertEquals("1,234,567 kCal", new FormatUtils(UnitSystem.Metric).caloriesToString(1234567));
    }

    /**
     * Tests that weightToString works as expected.
     */
    @Test
    public void testweightToString() {
        assertEquals("0 kg", new FormatUtils(UnitSystem.Metric).weightToString(0, 0));
        assertEquals("100 kg", new FormatUtils(UnitSystem.Metric).weightToString(100, 2));
        assertEquals("100.24 kg", new FormatUtils(UnitSystem.Metric).weightToString(100.2373f, 2));
        assertEquals("0 lbs", new FormatUtils(UnitSystem.English).weightToString(0, 2));
        assertEquals("220.46 lbs", new FormatUtils(UnitSystem.English).weightToString(100, 2));
        assertEquals("220 lbs", new FormatUtils(UnitSystem.English).weightToString(100, 0));
    }

    /**
     * Tests that weightToStringWithoutUnitName works as expected (most cases
     * are allready tested in testweightToString()).
     */
    @Test
    public void testWeightToStringWithoutUnitName() {
        assertEquals("100", new FormatUtils(UnitSystem.Metric).weightToStringWithoutUnitName(100, 2));
        assertEquals("100.24", new FormatUtils(UnitSystem.Metric).weightToStringWithoutUnitName(100.2373f, 2));
        assertEquals("220.46", new FormatUtils(UnitSystem.English).weightToStringWithoutUnitName(100, 2));
    }
}
