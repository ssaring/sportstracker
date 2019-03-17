package de.saring.util.unitcalc

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Locale

/**
 * This class contains all unit tests for the ConvertUtils class.
 *
 * @author Stefan Saring, Jacob Ilsoe Christensen (parts of C# version)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FormatUtilsTest {

    /**
     * Thes tests needs to use the English locale, results look different for other.
     */
    @BeforeAll
    fun initLocale() {
        Locale.setDefault(Locale.ENGLISH)
    }

    /**
     * Restore default Locale afterwards.
     */
    @AfterAll
    fun resetLocale() {
        Locale.setDefault(Locale.getDefault())
    }

    /**
     * Tests the property of the current unit system.
     */
    @Test
    fun testGetSettings() {
        val formatUtils = FormatUtils(UnitSystem.ENGLISH)
        assertEquals(UnitSystem.ENGLISH, formatUtils.unitSystem)
    }

    /**
     * Tests that temperatureToString works as expected.
     */
    @Test
    fun testTemperatureToString() {
        var formatUtils = FormatUtils(UnitSystem.METRIC)
        assertEquals("-5 C", formatUtils.temperatureToString((-5).toShort()))
        assertEquals("100 C", formatUtils.temperatureToString(100.toShort()))
        assertEquals("1,234 C", formatUtils.temperatureToString(1234.toShort()))

        formatUtils = FormatUtils(UnitSystem.ENGLISH)
        assertEquals("212 F", formatUtils.temperatureToString(100.toShort()))
    }

    /**
     * Tests that distanceToString works as expected.
     */
    @Test
    fun testDistanceToString() {
        var formatUtils = FormatUtils(UnitSystem.METRIC)
        assertEquals("0 km", formatUtils.distanceToString(0.0, 0))
        assertEquals("100 km", formatUtils.distanceToString(100.0, 0))
        assertEquals("100 km", formatUtils.distanceToString(100.0, 2))
        assertEquals("100.55 km", formatUtils.distanceToString(100.55, 2))
        assertEquals("100.56 km", formatUtils.distanceToString(100.555, 2))
        assertEquals("100,234.55 km", formatUtils.distanceToString(100234.55, 2))

        formatUtils = FormatUtils(UnitSystem.ENGLISH)
        assertEquals("0 m", formatUtils.distanceToString(0.0, 0))
        assertEquals("62 m", formatUtils.distanceToString(100.0, 0))
        assertEquals("62.45 m", formatUtils.distanceToString(100.50, 2))
    }

    /**
     * Tests that distanceToStringWithoutUnitName works as expected (most cases
     * are allready tested in testDistanceToString()).
     */
    @Test
    fun testDistanceToStringWithoutUnitName() {
        var formatUtils = FormatUtils(UnitSystem.METRIC)
        assertEquals("100", formatUtils.distanceToStringWithoutUnitName(100.0, 0))
        assertEquals("100.56", formatUtils.distanceToStringWithoutUnitName(100.555, 2))

        formatUtils = FormatUtils(UnitSystem.ENGLISH)
        assertEquals("62", formatUtils.distanceToStringWithoutUnitName(100.0, 0))
        assertEquals("62.45", formatUtils.distanceToStringWithoutUnitName(100.50, 2))
    }

    /**
     * Tests that speedToString() works as expected.
     */
    @Test
    fun testSpeedToString() {
        var formatUtils = FormatUtils(UnitSystem.METRIC)
        assertEquals("0", formatUtils.speedToString(0f, 0, SpeedMode.SPEED))
        assertEquals("100 km/h", formatUtils.speedToString(100f, 0, SpeedMode.SPEED))
        assertEquals("100 km/h", formatUtils.speedToString(100.0f, 2, SpeedMode.SPEED))
        assertEquals("100.55 km/h", formatUtils.speedToString(100.55f, 2, SpeedMode.SPEED))
        assertEquals("100.56 km/h", formatUtils.speedToString(100.555f, 2, SpeedMode.SPEED))
        assertEquals("100,234.55 km/h", formatUtils.speedToString(100234.55f, 2, SpeedMode.SPEED))

        formatUtils = FormatUtils(UnitSystem.METRIC)
        assertEquals("05:00 min/km", formatUtils.speedToString(12f, 0, SpeedMode.PACE))
        assertEquals("N/A", formatUtils.speedToString(0f, 0, SpeedMode.PACE))

        formatUtils = FormatUtils(UnitSystem.ENGLISH)
        assertEquals("0", formatUtils.speedToString(0f, 0, SpeedMode.SPEED))
        assertEquals("62 mph", formatUtils.speedToString(100f, 0, SpeedMode.SPEED))
        assertEquals("62.45 mph", formatUtils.speedToString(100.50f, 2, SpeedMode.SPEED))

        formatUtils = FormatUtils(UnitSystem.ENGLISH)
        assertEquals("08:02 min/m", formatUtils.speedToString(12f, 0, SpeedMode.PACE))
        assertEquals("N/A", formatUtils.speedToString(0f, 0, SpeedMode.PACE))
    }

    /**
     * Tests that speedToStringWithoutUnitName() works as expected (most cases are already tested in
     * testSpeedToString()).
     */
    @Test
    fun testSpeedToStringWithoutUnitName() {
        var formatUtils = FormatUtils(UnitSystem.METRIC)
        assertEquals("0", formatUtils.speedToString(0f, 0, SpeedMode.SPEED))
        assertEquals("100", formatUtils.speedToStringWithoutUnitName(100f, 0, SpeedMode.SPEED))
        assertEquals("100.56", formatUtils.speedToStringWithoutUnitName(100.555f, 2, SpeedMode.SPEED))

        formatUtils = FormatUtils(UnitSystem.METRIC)
        assertEquals("05:00", formatUtils.speedToStringWithoutUnitName(12f, 0, SpeedMode.PACE))
        assertEquals("N/A", formatUtils.speedToStringWithoutUnitName(0f, 0, SpeedMode.PACE))

        formatUtils = FormatUtils(UnitSystem.ENGLISH)
        assertEquals("0", formatUtils.speedToString(0f, 0, SpeedMode.SPEED))
        assertEquals("62.45", formatUtils.speedToStringWithoutUnitName(100.50f, 2, SpeedMode.SPEED))

        formatUtils = FormatUtils(UnitSystem.ENGLISH)
        assertEquals("08:02", formatUtils.speedToStringWithoutUnitName(12f, 0, SpeedMode.PACE))
        assertEquals("N/A", formatUtils.speedToStringWithoutUnitName(0f, 0, SpeedMode.PACE))
    }

    /**
     * Tests that getDistanceUnitName works as expected.
     */
    @Test
    fun testGetDistanceUnitName() {
        assertEquals("km", FormatUtils(UnitSystem.METRIC).getDistanceUnitName())
        assertEquals("m", FormatUtils(UnitSystem.ENGLISH).getDistanceUnitName())
    }

    /**
     * Tests that getSpeedUnitName() works as expected.
     */
    @Test
    fun testGetSpeedUnitName() {
        assertEquals("km/h", FormatUtils(UnitSystem.METRIC).getSpeedUnitName(SpeedMode.SPEED))
        assertEquals("mph", FormatUtils(UnitSystem.ENGLISH).getSpeedUnitName(SpeedMode.SPEED))
        assertEquals("min/km", FormatUtils(UnitSystem.METRIC).getSpeedUnitName(SpeedMode.PACE))
        assertEquals("min/m", FormatUtils(UnitSystem.ENGLISH).getSpeedUnitName(SpeedMode.PACE))
    }

    /**
     * Tests that getTemperatureUnitName works as expected.
     */
    @Test
    fun testGetTemperatureUnitName() {
        assertEquals("C", FormatUtils(UnitSystem.METRIC).getTemperatureUnitName())
        assertEquals("F", FormatUtils(UnitSystem.ENGLISH).getTemperatureUnitName())
    }

    /**
     * Tests that getAltitudeUnitName() works as expected.
     */
    @Test
    fun testGetAltitudeUnitName() {
        assertEquals("m", FormatUtils(UnitSystem.METRIC).getAltitudeUnitName())
        assertEquals("ft", FormatUtils(UnitSystem.ENGLISH).getAltitudeUnitName())
    }

    /**
     * Tests that heartRateToString works as expected.
     */
    @Test
    fun testHeartRateToString() {
        assertEquals("0 bpm", FormatUtils(UnitSystem.METRIC).heartRateToString(0))
        assertEquals("100 bpm", FormatUtils(UnitSystem.METRIC).heartRateToString(100))
        assertEquals("1,234 bpm", FormatUtils(UnitSystem.METRIC).heartRateToString(1234))
    }

    /**
     * Tests that heightToString works as expected.
     */
    @Test
    fun testHeightToString() {
        assertEquals("0 m", FormatUtils(UnitSystem.METRIC).heightToString(0))
        assertEquals("100 m", FormatUtils(UnitSystem.METRIC).heightToString(100))
        assertEquals("10,023 m", FormatUtils(UnitSystem.METRIC).heightToString(10023))
        assertEquals("0 ft", FormatUtils(UnitSystem.ENGLISH).heightToString(0))
        assertEquals("328 ft", FormatUtils(UnitSystem.ENGLISH).heightToString(100))
    }

    /**
     * Tests that heightToStringWithoutUnitName works as expected (most cases
     * are allready tested in testHeightToString()).
     */
    @Test
    fun testHeightToStringWithoutUnitName() {
        assertEquals("100", FormatUtils(UnitSystem.METRIC).heightToStringWithoutUnitName(100))
        assertEquals("328", FormatUtils(UnitSystem.ENGLISH).heightToStringWithoutUnitName(100))
    }

    /**
     * Tests that cadenceToString works as expected.
     */
    @Test
    fun testCadenceToString() {
        assertEquals("0 rpm / spm", FormatUtils(UnitSystem.METRIC).cadenceToString(0))
        assertEquals("90 rpm / spm", FormatUtils(UnitSystem.METRIC).cadenceToString(90))
        assertEquals("1,234 rpm / spm", FormatUtils(UnitSystem.METRIC).cadenceToString(1234))
    }

    /**
     * Tests that totcyclesToString works as expected.
     */
    @Test
    fun totcyclesToString() {
        assertEquals("0 rotations / steps", FormatUtils(UnitSystem.METRIC).cyclesToString(0))
        assertEquals("90 rotations / steps", FormatUtils(UnitSystem.METRIC).cyclesToString(90))
        assertEquals("1,234 rotations / steps", FormatUtils(UnitSystem.METRIC).cyclesToString(1234))
    }

    /**
     * Tests that caloriesToString works as expected.
     */
    @Test
    fun testCaloriesToString() {
        assertEquals("0 kCal", FormatUtils(UnitSystem.METRIC).caloriesToString(0))
        assertEquals("90 kCal", FormatUtils(UnitSystem.METRIC).caloriesToString(90))
        assertEquals("1,234 kCal", FormatUtils(UnitSystem.METRIC).caloriesToString(1234))
        assertEquals("1,234,567 kCal", FormatUtils(UnitSystem.METRIC).caloriesToString(1234567))
    }

    /**
     * Tests that weightToString works as expected.
     */
    @Test
    fun testweightToString() {
        assertEquals("0 kg", FormatUtils(UnitSystem.METRIC).weightToString(0f, 0))
        assertEquals("100 kg", FormatUtils(UnitSystem.METRIC).weightToString(100f, 2))
        assertEquals("100.24 kg", FormatUtils(UnitSystem.METRIC).weightToString(100.2373f, 2))
        assertEquals("0 lbs", FormatUtils(UnitSystem.ENGLISH).weightToString(0f, 2))
        assertEquals("220.46 lbs", FormatUtils(UnitSystem.ENGLISH).weightToString(100f, 2))
        assertEquals("220 lbs", FormatUtils(UnitSystem.ENGLISH).weightToString(100f, 0))
    }

    /**
     * Tests that weightToStringWithoutUnitName works as expected (most cases
     * are allready tested in testweightToString()).
     */
    @Test
    fun testWeightToStringWithoutUnitName() {
        assertEquals("100", FormatUtils(UnitSystem.METRIC).weightToStringWithoutUnitName(100f, 2))
        assertEquals("100.24", FormatUtils(UnitSystem.METRIC).weightToStringWithoutUnitName(100.2373f, 2))
        assertEquals("220.46", FormatUtils(UnitSystem.ENGLISH).weightToStringWithoutUnitName(100f, 2))
    }
}
