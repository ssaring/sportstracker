package de.saring.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
}
