package de.saring.util.unitcalc;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains all unit tests for the CalculationUtils class.
 *
 * @author Stefan Saring
 */
public class CalculationUtilsTest {
    
    /**
     * Tests the appropriate method.
     */
    @Test
    public void testCalculateDistance () {
        assertEquals (0f, CalculationUtils.calculateDistance (0, 0), 0f);
        assertEquals (20f, CalculationUtils.calculateDistance (20, 3600), 0.01f);
        assertEquals (49.165054f, CalculationUtils.calculateDistance ((float) 35.3, 5014), 0.0001f);
    }
    
    /**
     * Tests the appropriate method.
     */
    @Test
    public void testCalculateAvgSpeed () {
        assertEquals (Float.NaN, CalculationUtils.calculateAvgSpeed (0, 0), 0f);
        assertEquals (20f, CalculationUtils.calculateAvgSpeed (20, 3600), 0.01f);
        assertEquals (35.3f, CalculationUtils.calculateAvgSpeed ((float) 49.165054, 5014), 0.00001f);
    }
    
    /**
     * Tests the appropriate method.
     */
    @Test
    public void testCalculateDuration () {
        assertEquals (0, CalculationUtils.calculateDuration (0, 0));
        assertEquals (3600, CalculationUtils.calculateDuration (20, 20));
        assertEquals (5017, CalculationUtils.calculateDuration ((float) 49.2, (float) 35.3));
    }
}
