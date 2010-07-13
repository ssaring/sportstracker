package de.saring.util.unitcalc;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains all unit tests for the ConvertUtils class.
 *
 * @author Stefan Saring, Jacob Ilsoe Christensen (parts of C# version)
 */
public class ConvertUtilsTest {
    
    /**
     * Tests the appropriate method.
     */
    @Test
    public void testConvertMiles2Kilometer () {
        // test double version
        assertEquals (ConvertUtils.convertMiles2Kilometer (0.0d), 0.0d, 0d);
        assertEquals (ConvertUtils.convertMiles2Kilometer (145.3d), 233.83768320000004d, 0.000001d);
        
        // test int version
        assertEquals (ConvertUtils.convertMiles2Kilometer (0), 0);
        assertEquals (ConvertUtils.convertMiles2Kilometer (145), 233);
    }
    
    /**
     * Tests the appropriate method.
     */
    @Test
    public void testConvertKilometer2Miles () {
        // test double version
        assertEquals (ConvertUtils.convertKilometer2Miles (0d, false), 0d, 0d);
        assertEquals (ConvertUtils.convertKilometer2Miles (0d, true), 0d, 0d);
        assertEquals (ConvertUtils.convertKilometer2Miles (145.3d, false), 90.2852342320846d, 0.00001d);
        assertEquals (ConvertUtils.convertKilometer2Miles (145.3d, true), 90.285d, 0.00001d);
        
        // test int version
        assertEquals (ConvertUtils.convertKilometer2Miles (0), 0);
        assertEquals (ConvertUtils.convertKilometer2Miles (145), 90);
    }
    
    /**
     * Tests the appropriate method.
     */
    @Test
    public void testConvertFeet2Meter () {
        assertEquals (ConvertUtils.convertFeet2Meter (0), 0);
        assertEquals (ConvertUtils.convertFeet2Meter (2540), 774);
    }
    
    /**
     * Tests the appropriate method.
     */
    @Test
    public void testConvertMeter2Feet () {
        assertEquals (ConvertUtils.convertMeter2Feet (0), 0);
        assertEquals (ConvertUtils.convertMeter2Feet (2540), 8334);
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testConvertMeterPerSecond2KilometerPerHour() {
        assertEquals(0f, ConvertUtils.convertMeterPerSecond2KilometerPerHour(0f), 0f);
        assertEquals(3.6f, ConvertUtils.convertMeterPerSecond2KilometerPerHour(1f), 0.001f);
        assertEquals(44.64f, ConvertUtils.convertMeterPerSecond2KilometerPerHour(12.4f), 0.001f);
    }

     /**
     * Tests the appropriate method.
     */
    @Test
    public void testConvertKilogram2Lbs () {
        assertEquals (0, ConvertUtils.convertKilogram2Lbs (0), 0.0001d);
        assertEquals (2.2046d, ConvertUtils.convertKilogram2Lbs (1), 0.0001d);
        assertEquals (165.852058d, ConvertUtils.convertKilogram2Lbs (75.23d), 0.0001d);
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testConvertLbs2Kilogram () {
        assertEquals (0, ConvertUtils.convertLbs2Kilogram (0), 0.0001d);
        assertEquals (1, ConvertUtils.convertLbs2Kilogram (2.2046d), 0.0001d);
        assertEquals (34.12410d, ConvertUtils.convertLbs2Kilogram (75.23d), 0.0001d);
    }
    
    /**
     * Tests the appropriate method.
     */
    @Test
    public void testConvertSemicircle2Degree() {
        assertEquals(0d, ConvertUtils.convertSemicircle2Degree(0), 0d);
        assertEquals(51.054392d, ConvertUtils.convertSemicircle2Degree(609102623), 0.0001d);
        assertEquals(-51.054392d, ConvertUtils.convertSemicircle2Degree(-609102623), 0.0001d);
    }
}
