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
        assertEquals(0.0, CalculationUtils.calculateDistance(0.0, 0))
        assertEquals(20.0, CalculationUtils.calculateDistance(20.0, 3600), 0.01)
        assertEquals(49.165054, CalculationUtils.calculateDistance(35.3, 5014), 0.0001)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testCalculateAvgSpeed() {
        assertEquals(java.lang.Double.NaN, CalculationUtils.calculateAvgSpeed(0.0, 0))
        assertEquals(20.0, CalculationUtils.calculateAvgSpeed(20.0, 3600), 0.01)
        assertEquals(35.3, CalculationUtils.calculateAvgSpeed(49.165054, 5014), 0.00001)
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testCalculateDuration() {
        assertEquals(0, CalculationUtils.calculateDuration(0.0, 0.0))
        assertEquals(3600, CalculationUtils.calculateDuration(20.0, 20.0))
        assertEquals(5017, CalculationUtils.calculateDuration(49.2, 35.3))
    }
}
