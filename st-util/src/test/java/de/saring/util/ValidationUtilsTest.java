package de.saring.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains all unit tests for the ValidationUtilsTest class.
 *
 * @author Stefan Saring
 */
public class ValidationUtilsTest {

    /**
     * Tests the method isValueIntegerBetween() for success conditions.
     */
    @Test
    public void testIsValueIntegerBetweenSuccess() {
        assertTrue(ValidationUtils.isValueIntegerBetween("0", 0, 23));
        assertTrue(ValidationUtils.isValueIntegerBetween("10", 0, 23));
        assertTrue(ValidationUtils.isValueIntegerBetween("23", 0, 23));
    }

    /**
     * Tests the method isValueIntegerBetween() for failure conditions.
     */
    @Test
    public void testIsValueIntegerBetweenFailed() {
        assertFalse(ValidationUtils.isValueIntegerBetween(null, 0, 23));
        assertFalse(ValidationUtils.isValueIntegerBetween("", 0, 23));
        assertFalse(ValidationUtils.isValueIntegerBetween("foo", 0, 23));
        assertFalse(ValidationUtils.isValueIntegerBetween("-1", 0, 23));
        assertFalse(ValidationUtils.isValueIntegerBetween("24", 0, 23));
    }
}
