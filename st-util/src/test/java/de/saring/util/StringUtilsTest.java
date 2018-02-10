package de.saring.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * This class contains all unit tests for the StringUtils class.
 *
 * @author Stefan Saring
 */
public class StringUtilsTest {

    /**
     * Tests the method getTrimmedTextOrNull().
     */
    @Test
    public void testGetTrimmedTextOrNull() {
        assertNull(StringUtils.getTrimmedTextOrNull(null));
        assertNull(StringUtils.getTrimmedTextOrNull(""));
        assertNull(StringUtils.getTrimmedTextOrNull("    "));

        assertEquals("foo BAR", StringUtils.getTrimmedTextOrNull("foo BAR"));
        assertEquals("foo BAR", StringUtils.getTrimmedTextOrNull(" foo BAR  "));
    }

    /**
     * Tests the method getTextOrEmptyString().
     */
    @Test
    public void testGetTextOrEmptyString() {
        assertEquals("", StringUtils.getTextOrEmptyString(""));
        assertEquals("", StringUtils.getTextOrEmptyString(null));
        assertEquals(" Foo Bar ", StringUtils.getTextOrEmptyString(" Foo Bar "));
    }

    /**
     * Tests the method getFirstLineOfText().
     */
    @Test
    public void testGetFirstLineOfText() {
        assertNull(StringUtils.getFirstLineOfText(null));
        assertEquals("", StringUtils.getFirstLineOfText(""));
        assertEquals("Foo Bar", StringUtils.getFirstLineOfText(" Foo Bar "));
        assertEquals("Foo Bar", StringUtils.getFirstLineOfText(" Foo Bar \n Bar Foo \n Foo Bar "));
    }

    /**
     * Tests the method isNullOrEmpty().
     */
    @Test
    public void testIsNullOrEmpty() {
        assertTrue(StringUtils.isNullOrEmpty(null));
        assertTrue(StringUtils.isNullOrEmpty(""));
        assertTrue(StringUtils.isNullOrEmpty("   "));
        assertFalse(StringUtils.isNullOrEmpty("a"));
    }
}
