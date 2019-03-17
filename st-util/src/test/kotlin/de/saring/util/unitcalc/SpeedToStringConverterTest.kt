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
        defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.GERMAN)
    }

    /**
     * Reset default locale to previous value.
     */
    @AfterAll
    fun resetLocale() {
        Locale.setDefault(defaultLocale)
    }

    /**
     * Tests of method floatSpeedtoString() when using SpeedMode speed.
     */
    @Test
    fun testFloatSpeedtoStringDistancePerHour() {

        val converter = SpeedToStringConverter(SpeedMode.SPEED)

        assertEquals(null, converter.floatSpeedtoString(null))
        assertEquals("0", converter.floatSpeedtoString(0f))
        assertEquals("24,3", converter.floatSpeedtoString(24.3f))
        assertEquals("12.345,679", converter.floatSpeedtoString(12345.6789f)) // must round the 3. digit
    }

    /**
     * Tests of method floatSpeedtoString() when using SpeedMode pace.
     */
    @Test
    fun testFloatSpeedtoStringMinutesPerDistance() {

        val converter = SpeedToStringConverter(SpeedMode.PACE)

        assertEquals(null, converter.floatSpeedtoString(null))
        assertEquals("00:00", converter.floatSpeedtoString(0f))
        assertEquals("03:00", converter.floatSpeedtoString(20f))
        assertEquals("02:30", converter.floatSpeedtoString(24f))
        assertEquals("00:29", converter.floatSpeedtoString(123.456f))
    }

    /**
     * Tests of method stringSpeedToFloat() when using SpeedMode speed.
     */
    @Test
    fun testStringSpeedToFloatDistancePerHour() {

        val converter = SpeedToStringConverter(SpeedMode.SPEED)

        assertNull(converter.stringSpeedToFloat(null))
        assertNull(converter.stringSpeedToFloat(""))
        assertNull(converter.stringSpeedToFloat("A1"))

        assertEquals(0f, converter.stringSpeedToFloat("0")!!.toFloat(), 0.0001f)
        assertEquals(20f, converter.stringSpeedToFloat("20")!!.toFloat(), 0.0001f)
        assertEquals(12345.678f, converter.stringSpeedToFloat("12.345,678")!!.toFloat(), 0.0001f)
    }

    /**
     * Tests of method stringSpeedToFloat() when using SpeedMode pace.
     */
    @Test
    fun testStringSpeedToFloatMinutesPerDistance() {

        val converter = SpeedToStringConverter(SpeedMode.PACE)

        assertNull(converter.stringSpeedToFloat(null))
        assertNull(converter.stringSpeedToFloat(""))
        assertNull(converter.stringSpeedToFloat("A1"))
        assertNull(converter.stringSpeedToFloat("0:0"))
        assertNull(converter.stringSpeedToFloat("2:2"))

        assertEquals(0f, converter.stringSpeedToFloat("00:00")!!.toFloat(), 0.0001f)
        assertEquals(30f, converter.stringSpeedToFloat("02:00")!!.toFloat(), 0.0001f)
        assertEquals(24f, converter.stringSpeedToFloat("02:30")!!.toFloat(), 0.0001f)
        assertEquals(124.138f, converter.stringSpeedToFloat("00:29")!!.toFloat(), 0.001f)
    }
}
