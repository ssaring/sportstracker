package de.saring.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.Test

/**
 * This class contains all unit tests for the [StringUtils] class.
 *
 * @author Stefan Saring
 */
class StringUtilsTest {

    /**
     * Tests the method getTrimmedTextOrNull().
     */
    @Test
    fun testGetTrimmedTextOrNull() {
        assertNull(StringUtils.getTrimmedTextOrNull(null))
        assertNull(StringUtils.getTrimmedTextOrNull(""))
        assertNull(StringUtils.getTrimmedTextOrNull("    "))

        assertEquals("foo BAR", StringUtils.getTrimmedTextOrNull("foo BAR"))
        assertEquals("foo BAR", StringUtils.getTrimmedTextOrNull(" foo BAR  "))
    }

    /**
     * Tests the method getTextOrEmptyString().
     */
    @Test
    fun testGetTextOrEmptyString() {
        assertEquals("", StringUtils.getTextOrEmptyString(""))
        assertEquals("", StringUtils.getTextOrEmptyString(null))
        assertEquals(" Foo Bar ", StringUtils.getTextOrEmptyString(" Foo Bar "))
    }

    /**
     * Tests the method getFirstLineOfText().
     */
    @Test
    fun testGetFirstLineOfText() {
        assertNull(StringUtils.getFirstLineOfText(null))
        assertEquals("", StringUtils.getFirstLineOfText(""))
        assertEquals("Foo Bar", StringUtils.getFirstLineOfText(" Foo Bar "))
        assertEquals("Foo Bar", StringUtils.getFirstLineOfText(" Foo Bar \n Bar Foo \n Foo Bar "))
    }

    /**
     * Tests the method isNullOrEmpty().
     */
    @Test
    fun testIsNullOrEmpty() {
        assertTrue(StringUtils.isNullOrEmpty(null))
        assertTrue(StringUtils.isNullOrEmpty(""))
        assertTrue(StringUtils.isNullOrEmpty("   "))
        assertFalse(StringUtils.isNullOrEmpty("a"))
    }
}
