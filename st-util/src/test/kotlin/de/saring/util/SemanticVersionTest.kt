package de.saring.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test


/**
 * Unit tests for class [SemanticVersion].
 *
 * @author Stefan Saring
 */
class SemanticVersionTest {

    /**
     * Tests of method compareTo(): both versions are equal.
     */
    @Test
    fun testCompareToEquals() {

        assertEquals(0,
                SemanticVersion(0, 0, 0).compareTo(SemanticVersion(0, 0, 0)))
        assertEquals(0,
                SemanticVersion(1, 0, 0).compareTo(SemanticVersion(1, 0, 0)))
        assertEquals(0,
                SemanticVersion(12, 13, 14).compareTo(SemanticVersion(12, 13, 14)))
    }

    /**
     * Tests of method compareTo(): the version is smaller than the passed other one.
     */
    @Test
    fun testCompareToSmaller() {

        assertEquals(-1,
                SemanticVersion(0, 0, 0).compareTo(SemanticVersion(0, 0, 1)))
        assertEquals(-1,
                SemanticVersion(0, 0, 9).compareTo(SemanticVersion(0, 1, 0)))
        assertEquals(-1,
                SemanticVersion(0, 9, 9).compareTo(SemanticVersion(1, 0, 0)))
        assertEquals(-1,
                SemanticVersion(12, 13, 14).compareTo(SemanticVersion(14, 13, 12)))
    }

    /**
     * Tests of method compareTo(): the version is bigger than the passed other one.
     */
    @Test
    fun testCompareToBigger() {

        assertEquals(1,
                SemanticVersion(0, 0, 1).compareTo(SemanticVersion(0, 0, 0)))
        assertEquals(1,
                SemanticVersion(0, 1, 0).compareTo(SemanticVersion(0, 0, 9)))
        assertEquals(1,
                SemanticVersion(1, 0, 0).compareTo(SemanticVersion(0, 9, 9)))
        assertEquals(1,
                SemanticVersion(14, 13, 12).compareTo(SemanticVersion(12, 13, 14)))
    }

    /**
     * Tests of method toString().
     */
    @Test
    fun testToString() {

        assertEquals("0.0.1", SemanticVersion(0, 0, 1).toString())
        assertEquals("123.456.789", SemanticVersion(123, 456, 789).toString())
    }

    /**
     * Tests of method parse(): all passed version are valid semantic versions.
     */
    @Test
    fun testParseSuccess() {

        assertEquals(SemanticVersion(0, 0, 0), SemanticVersion.parse("0.0.0"))
        assertEquals(SemanticVersion(1, 2, 3), SemanticVersion.parse("1.2.3"))
        assertEquals(SemanticVersion(321, 456, 987), SemanticVersion.parse("321.456.987"))
    }

    /**
     * Tests of method parse(): all passed version are invalid semantic versions and need to throw an
     * [IllegalArgumentException].
     */
    @Test
    fun testParseInvalid() {

        assertThrows(IllegalArgumentException::class.java) { SemanticVersion.parse("") }
        assertThrows(IllegalArgumentException::class.java) { SemanticVersion.parse("1") }
        assertThrows(IllegalArgumentException::class.java) { SemanticVersion.parse("1.0") }
        assertThrows(IllegalArgumentException::class.java) { SemanticVersion.parse("1,0,0") }
        assertThrows(IllegalArgumentException::class.java) { SemanticVersion.parse("1.2.3.4") }

        assertThrows(IllegalArgumentException::class.java) { SemanticVersion.parse("1.2.a") }
        assertThrows(IllegalArgumentException::class.java) { SemanticVersion.parse("1.2.3pre") }
        assertThrows(IllegalArgumentException::class.java) { SemanticVersion.parse("1.2.3pre") }
    }
}
