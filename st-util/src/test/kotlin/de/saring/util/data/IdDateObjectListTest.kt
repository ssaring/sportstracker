package de.saring.util.data

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.ArrayList

/**
 * Unit tests of class IdDateObjectList.
 *
 * @author Stefan Saring
 */
class IdDateObjectListTest {

    /** The instance to test. */
    private lateinit var list: IdDateObjectList<DateNameObject>

    @BeforeEach
    fun setUp() {
        list = IdDateObjectList()
        list.set(DateNameObject(1, LocalDateTime.of(2009, 2, 5, 21, 30, 0), "one"))
        list.set(DateNameObject(2, LocalDateTime.of(2008, 12, 11, 20, 30, 0), "two"))
        list.set(DateNameObject(3, LocalDateTime.of(2009, 2, 7, 11, 40, 0), "three"))

        checkDateOrder()
    }

    /**
     * Test of set(): must add a new object to list at position 2.
     */
    @Test
    fun testSetAdd() {
        list.set(DateNameObject(4, LocalDateTime.of(2008, 12, 25, 21, 30, 0), "four"))
        assertEquals(list.size(), 4)
        assertEquals("four", list.getAt(1).name)
        checkDateOrder()
    }

    /**
     * Test of set(): must replace the object in list at position 3 with the new
     * one with the allready used ID.
     */
    @Test
    fun testSetReplaceNew() {
        assertEquals("three", list.getAt(2).name)
        list.set(DateNameObject(3, LocalDateTime.of(2009, 2, 7, 11, 40, 0), "three-new"))
        assertEquals(list.size(), 3)
        assertEquals("three-new", list.getAt(2).name)
        checkDateOrder()
    }

    /**
     * Test of set(): must store the modified object in list from position 3 at
     * the proper list position for the modified date.
     */
    @Test
    fun testSetReplaceModified() {
        val no3 = list.getAt(2)
        assertEquals("three", no3.name)
        no3.dateTime = LocalDateTime.of(2008, 7, 12, 12, 45, 0)
        no3.name = "three-new"

        list.set(no3)
        assertEquals(list.size(), 3)
        assertEquals("three-new", list.getAt(0).name)
        checkDateOrder()
    }

    /**
     * Test of method clearAndAddAll(). The previous list content must be removed, the
     * list must contain only the new entries, sorted by date.
     */
    @Test
    fun clearAndAddAll() {

        val tempEntries = ArrayList<DateNameObject>()
        tempEntries.add(DateNameObject(5, LocalDateTime.of(2009, 2, 5, 21, 30, 0), "five"))
        tempEntries.add(DateNameObject(6, LocalDateTime.of(2009, 2, 1, 21, 30, 0), "six"))
        list.clearAndAddAll(tempEntries)

        assertEquals(2, list.size())
        assertEquals("six", list.getAt(0).name)
        assertEquals("five", list.getAt(1).name)
    }

    /**
     * Test of getEntriesInDateRange(): must find all entries, the date range
     * includes the earliest and latest entry.
     */
    @Test
    fun testGetEntriesInDateRangeFindAll() {
        val lFound = list.getEntriesInDateRange(
                LocalDate.of(2008, 12, 11), LocalDate.of(2009, 2, 7))

        assertEquals(3, lFound.size)
        assertEquals("two", lFound[0].name)
        assertEquals("one", lFound[1].name)
        assertEquals("three", lFound[2].name)
    }

    /**
     * Test of getEntriesInDateRange(): must find only one entries, the date
     * range does not include the earliest and latest entry.
     */
    @Test
    fun testGetEntriesInDateRangeFindOne() {
        val lFound = list.getEntriesInDateRange(
                LocalDate.of(2008, 12, 12), LocalDate.of(2009, 2, 6))

        assertEquals(1, lFound.size)
        assertEquals("one", lFound[0].name)
    }

    /**
     * Test of getEntriesInDateRange(): must fail when the begin date ist after end date.
     */
    @Test
    fun testGetEntriesInDateRangeInvalidRange() {
        assertThrows(IllegalArgumentException::class.java) {
            list.getEntriesInDateRange(
                    LocalDate.of(2009, 12, 11),
                    LocalDate.of(2009, 11, 11))
        }
    }

    /**
     * This helper method makes sure that all IdDateObjects in the list are in
     * correct chronological order (the date of the previous exercise is never
     * bigger then the date of the current exercise).
     */
    private fun checkDateOrder() {
        var previousDate = LocalDateTime.of(1970, 1, 1, 0, 0, 0)

        for (temp in list) {
            assertTrue(previousDate.isBefore(temp.dateTime))
            previousDate = temp.dateTime
        }
    }

    /**
     * Subclass of abstract class IdDateObject for testing.
     */
    internal class DateNameObject(id: Long, dateTime: LocalDateTime, var name: String) : IdDateObject(id) {

        init {
            this.dateTime = dateTime
        }
    }
}
