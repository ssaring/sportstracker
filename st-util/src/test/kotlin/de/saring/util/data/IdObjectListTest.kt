package de.saring.util.data

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.ArrayList

/**
 * Unit tests of class IdObjectList.
 *
 * @author Stefan Saring
 */
class IdObjectListTest {

    /** The instance to test. */
    private lateinit var list: IdObjectList<NameObject>

    @BeforeEach
    fun setUp() {

        // create a new list with some content
        list = IdObjectList()
        list.set(NameObject(1, "one"))
        list.set(NameObject(2, "two"))
        list.set(NameObject(3, "three"))
    }

    /**
     * Test of getByID method, of class IdObjectList.
     */
    @Test
    fun getByID() {
        assertNull(list.getByID(0))
        assertNull(list.getByID(1000))
        assertEquals("one", list.getByID(1)?.name)
        assertEquals("three", list.getByID(3)?.name)
    }

    /**
     * Test of getAt method, of class IdObjectList.
     */
    @Test
    fun getAt() {
        assertEquals("one", list.getAt(0).name)
        assertEquals("three", list.getAt(2).name)

        assertThrows(IndexOutOfBoundsException::class.java) { list.getAt(3) }
    }

    /**
     * Test of indexOf method, of class IdObjectList.
     */
    @Test
    fun indexOf() {
        assertEquals(1, list.indexOf(NameObject(2, "two")))
    }

    /**
     * Test of contains method, of class IdObjectList.
     */
    @Test
    fun contains() {
        assertTrue(list.contains(NameObject(2, "two")))
        assertFalse(list.contains(NameObject(4, "four")))
    }

    /**
     * Test of set method set() of class IdObjectList.
     */
    @Test
    fun set() {

        // this should add an element to list
        list.set(NameObject(4, "four"))
        assertEquals(4, list.size())
        assertEquals("four", list.getAt(3).name)

        // this should replace a list-element
        list.set(NameObject(2, "zwei"))
        assertEquals(4, list.size())
        assertEquals("zwei", list.getByID(2)?.name)
    }

    /**
     * Test of method clearAndAddAll(). The previous list content must be removed, the
     * list must contain only the new entries.
     */
    @Test
    fun clearAndAddAll() {

        val tempEntries = ArrayList<NameObject>()
        tempEntries.add(NameObject(5, "five"))
        tempEntries.add(NameObject(6, "six"))
        list.clearAndAddAll(tempEntries)

        assertEquals(2, list.size())
        assertEquals("five", list.getAt(0).name)
        assertEquals("six", list.getAt(1).name)
    }

    /**
     * Test of removeByID method, of class IdObjectList.
     */
    @Test
    fun removeByID() {

        // this delete must work
        assertTrue(list.removeByID(3))
        assertEquals(2, list.size())
        assertNull(list.getByID(3))

        // this delete can't work
        assertFalse(list.removeByID(3))
        assertEquals(2, list.size())
    }

    /**
     * Test of getNewID method, of class IdObjectList.
     */
    @Test
    fun getNewID() {

        // ID's 1-3 in use => next needs to be 4
        assertEquals(list.getNewId(), 4)

        // add ID's 4 and 6 to list => next needs to be 5
        list.set(NameObject(4, "four"))
        list.set(NameObject(6, "six"))
        assertEquals(5, list.getNewId())

        // remove ID 2 from list => next needs to be 2
        list.removeByID(2)
        assertEquals(2, list.getNewId())
    }

    /**
     * Subclass of abstract class IdObject for testing.
     */
    internal class NameObject(id: Long, val name: String) : IdObject(id)
}
