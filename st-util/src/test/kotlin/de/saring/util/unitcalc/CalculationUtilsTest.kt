package de.saring.util.unitcalc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * This class contains all unit tests for the CalculationUtils class.
 *
 * @author Stefan Saring
 */
class CalculationUtilsTest {

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testCalculateDistance() {
        assertEquals(0f, CalculationUtils.calculateDistance(0f, 0))
        assertEquals(20f, CalculationUtils.calculateDistance(20f, 3600), 0.01f)
        assertEquals(49.165054f, CalculationUtils.calculateDistance(35.3.toFloat(), 5014), 0.0001f)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testCalculateAvgSpeed() {
        assertEquals(java.lang.Float.NaN, CalculationUtils.calculateAvgSpeed(0f, 0))
        assertEquals(20f, CalculationUtils.calculateAvgSpeed(20f, 3600), 0.01f)
        assertEquals(35.3f, CalculationUtils.calculateAvgSpeed(49.165054.toFloat(), 5014), 0.00001f)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testCalculateDuration() {
        assertEquals(0, CalculationUtils.calculateDuration(0f, 0f))
        assertEquals(3600, CalculationUtils.calculateDuration(20f, 20f))
        assertEquals(5017, CalculationUtils.calculateDuration(49.2.toFloat(), 35.3.toFloat()))
    }
}
