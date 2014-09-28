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
     * Tests the method getTrimmedTextOrNull() for success conditions.
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
     * Tests the method getTextOrEmptyString() for failure conditions.
     */
    @Test
    public void testGetTextOrEmptyString() {
        assertEquals("", StringUtils.getTextOrEmptyString(""));
        assertEquals("", StringUtils.getTextOrEmptyString(null));
        assertEquals(" Foo Bar ", StringUtils.getTextOrEmptyString(" Foo Bar "));
    }
}
