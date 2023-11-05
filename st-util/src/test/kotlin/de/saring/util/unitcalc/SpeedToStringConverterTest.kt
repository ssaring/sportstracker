package de.saring.util.unitcalc

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.Locale

/**
 * Unit tests of class SpeedToStringConverter.
 *
 * @author Stefan Saring
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpeedToStringConverterTest {

    private lateinit var defaultLocale: Locale

    /**
     * Set default locale to GERMAN, because the number validation tests are locale dependent.
     */
    @BeforeAll
    fun initLocale() {
        print("----------- initLocale executed")
        defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.GERMAN)
    }

    /**
     * Reset default locale to previous value.
     */
    @AfterAll
    fun resetLocale() {
        print("----------- resetLocale executed")
        Locale.setDefault(defaultLocale)
    }

    /**
     * Tests of method doubleSpeedtoString() when using SpeedMode speed.
     */
    @Test
    fun testDoubleSpeedToStringDistancePerHour() {
        print("----------- testDoubleSpeedToStringDistancePerHour executed")

        val converter = SpeedToStringConverter(SpeedMode.SPEED)

        assertEquals(null, converter.doubleSpeedtoString(null))
        assertEquals("0", converter.doubleSpeedtoString(0.0))
        assertEquals("24,3", converter.doubleSpeedtoString(24.3))
        assertEquals("12.345,679", converter.doubleSpeedtoString(12345.6789)) // must round the 3. digit
    }

    /**
     * Tests of method doubleSpeedtoString() when using SpeedMode pace.
     */
    @Test
    fun testDoubleSpeedToStringMinutesPerDistance() {

        val converter = SpeedToStringConverter(SpeedMode.PACE)

        assertEquals(null, converter.doubleSpeedtoString(null))
        assertEquals("00:00", converter.doubleSpeedtoString(0.0))
        assertEquals("03:00", converter.doubleSpeedtoString(20.0))
        assertEquals("02:30", converter.doubleSpeedtoString(24.0))
        assertEquals("00:29", converter.doubleSpeedtoString(123.456))
    }

    /**
     * Tests of method stringSpeedToDouble() when using SpeedMode speed.
     */
    @Test
    fun testStringSpeedToDoubleDistancePerHour() {

        val converter = SpeedToStringConverter(SpeedMode.SPEED)

        assertNull(converter.stringSpeedToDouble(null))
        assertNull(converter.stringSpeedToDouble(""))
        assertNull(converter.stringSpeedToDouble("A1"))

        assertEquals(0.0, converter.stringSpeedToDouble("0"))
        assertEquals(20.0, converter.stringSpeedToDouble("20"))
        assertEquals(12345.678, converter.stringSpeedToDouble("12.345,678"))
    }

    /**
     * Tests of method stringSpeedToDouble() when using SpeedMode pace.
     */
    @Test
    fun testStringSpeedToDoubleMinutesPerDistance() {

        val converter = SpeedToStringConverter(SpeedMode.PACE)

        assertNull(converter.stringSpeedToDouble(null))
        assertNull(converter.stringSpeedToDouble(""))
        assertNull(converter.stringSpeedToDouble("A1"))
        assertNull(converter.stringSpeedToDouble("0:0"))
        assertNull(converter.stringSpeedToDouble("2:2"))

        assertEquals(0.0, converter.stringSpeedToDouble("00:00"))
        assertEquals(30.0, converter.stringSpeedToDouble("02:00"))
        assertEquals(24.0, converter.stringSpeedToDouble("02:30"))
        assertEquals(124.138, converter.stringSpeedToDouble("00:29")!!, 0.0001)
    }
}
