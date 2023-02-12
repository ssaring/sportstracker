package de.saring.util.data

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Unit tests of class IdObject.
 *
 * @author Stefan Saring
 */
class IdObjectTest {

    /**
     * Test of equals method, of class IdObject.
     */
    @Test
    fun equals() {
        val subClass1_1_1 = SubClass1(1)
        val subClass1_1_2 = SubClass1(1)
        val subClass1_3_1 = SubClass1(3)
        val subClass2_1_1 = SubClass2(1)

        assertTrue(subClass1_1_1.equals(subClass1_1_2))
        assertTrue(subClass1_1_1.equals(subClass1_1_1))
        assertFalse(subClass1_1_1.equals(null))
        assertFalse(subClass1_1_1.equals(subClass1_3_1))
        assertFalse(subClass1_1_1.equals(subClass2_1_1))

        // verify instance comparison when ID is null in both objects
        val subClass1_noId_1 = SubClass1(null)
        val subClass1_noId_2 = SubClass1(null)
        assertFalse(subClass1_noId_1.equals(subClass1_noId_2))
        assertTrue(subClass1_noId_1.equals(subClass1_noId_1))
    }

    // Subclasses of abstract class IdObject for testing.
    internal class SubClass1(id: Long?) : IdObject(id)

    internal class SubClass2(id: Long?) : IdObject(id)
}
