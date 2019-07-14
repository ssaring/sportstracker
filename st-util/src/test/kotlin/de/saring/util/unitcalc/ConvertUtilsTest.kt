package de.saring.util.unitcalc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * This class contains all unit tests for the ConvertUtils class.
 *
 * @author Stefan Saring
 */
class ConvertUtilsTest {

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertMiles2Kilometer() {

        // test double version
        assertEquals(0.0, ConvertUtils.convertMiles2Kilometer(0.0))
        assertEquals(ConvertUtils.convertMiles2Kilometer(145.3), 233.83768320000004, 0.000001)

        // test int version
        assertEquals(ConvertUtils.convertMiles2Kilometer(0), 0)
        assertEquals(ConvertUtils.convertMiles2Kilometer(145), 233)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertKilometer2Miles() {

        // test double version
        assertEquals(ConvertUtils.convertKilometer2Miles(0.0, false), 0.0, 0.001)
        assertEquals(ConvertUtils.convertKilometer2Miles(0.0, true), 0.0, 0.001)
        assertEquals(ConvertUtils.convertKilometer2Miles(145.3, false), 90.2852342320846, 0.00001)
        assertEquals(ConvertUtils.convertKilometer2Miles(145.3, true), 90.285, 0.00001)

        // test int version
        assertEquals(ConvertUtils.convertKilometer2Miles(0), 0)
        assertEquals(ConvertUtils.convertKilometer2Miles(145), 90)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertFeet2MeterInt() {

        assertEquals(ConvertUtils.convertFeet2Meter(0), 0)
        assertEquals(ConvertUtils.convertFeet2Meter(2540), 774)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertFeet2MeterDouble() {

        assertEquals(ConvertUtils.convertFeet2Meter(0.0), 0.0)
        assertEquals(ConvertUtils.convertFeet2Meter(2545.5), 775.84292, 0.001)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertMeter2FeetInt() {

        assertEquals(ConvertUtils.convertMeter2Feet(0), 0)
        assertEquals(ConvertUtils.convertMeter2Feet(2540), 8334)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertMeter2FeetDouble() {

        assertEquals(ConvertUtils.convertMeter2Feet(0.0), 0.0)
        assertEquals(ConvertUtils.convertMeter2Feet(2545.5), 8351.65222, 0.001)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertMeterPerSecond2KilometerPerHour() {

        assertEquals(0f, ConvertUtils.convertMeterPerSecond2KilometerPerHour(0f))
        assertEquals(3.6f, ConvertUtils.convertMeterPerSecond2KilometerPerHour(1f), 0.001f)
        assertEquals(44.64f, ConvertUtils.convertMeterPerSecond2KilometerPerHour(12.4f), 0.001f)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertKilogram2Lbs() {

        assertEquals(0.0, ConvertUtils.convertKilogram2Lbs(0.0))
        assertEquals(2.2046, ConvertUtils.convertKilogram2Lbs(1.0), 0.0001)
        assertEquals(165.852058, ConvertUtils.convertKilogram2Lbs(75.23), 0.0001)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertLbs2Kilogram() {

        assertEquals(0.0, ConvertUtils.convertLbs2Kilogram(0.0))
        assertEquals(1.0, ConvertUtils.convertLbs2Kilogram(2.2046), 0.0001)
        assertEquals(34.12410, ConvertUtils.convertLbs2Kilogram(75.23), 0.0001)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testConvertSemicircle2Degree() {

        assertEquals(0.0, ConvertUtils.convertSemicircle2Degree(0), 0.001)
        assertEquals(51.054392, ConvertUtils.convertSemicircle2Degree(609102623), 0.0001)
        assertEquals(-51.054392, ConvertUtils.convertSemicircle2Degree(-609102623), 0.0001)
    }
}
