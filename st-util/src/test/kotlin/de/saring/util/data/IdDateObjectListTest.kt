package de.saring.util.data

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

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
        assertEquals("one", lFound[0].name)
        assertEquals("two", lFound[1].name)
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
     * Subclass of abstract class IdDateObject for testing.
     */
    internal class DateNameObject(id: Long, dateTime: LocalDateTime, var name: String) : IdDateObject(id) {

        init {
            this.dateTime = dateTime
        }
    }
}
