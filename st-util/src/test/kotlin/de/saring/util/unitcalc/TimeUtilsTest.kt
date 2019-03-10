package de.saring.util.unitcalc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * This class contains all unit tests for the [TimeUtils] class.
 *
 * @author Stefan Saring
 */
class TimeUtilsTest {

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testMinutes2TimeString() {
        assertEquals(TimeUtils.minutes2TimeString(0), "00:00")
        assertEquals(TimeUtils.minutes2TimeString(9), "00:09")
        assertEquals(TimeUtils.minutes2TimeString(70), "01:10")
        assertEquals(TimeUtils.minutes2TimeString(3600), "60:00")
        assertEquals(TimeUtils.minutes2TimeString(7199), "119:59")
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testSeconds2TimeString() {
        assertEquals(TimeUtils.seconds2TimeString(0), "00:00:00")
        assertEquals(TimeUtils.seconds2TimeString(3599), "00:59:59")
        assertEquals(TimeUtils.seconds2TimeString(3601), "01:00:01")
        assertEquals(TimeUtils.seconds2TimeString(365145), "101:25:45")
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testSeconds2MinuteTimeString() {
        assertEquals(TimeUtils.seconds2MinuteTimeString(0), "00:00")
        assertEquals(TimeUtils.seconds2MinuteTimeString(3599), "59:59")
        assertEquals(TimeUtils.seconds2MinuteTimeString(3601), "60:01")
        assertEquals(TimeUtils.seconds2MinuteTimeString(365145), "6085:45")
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    fun testTimeString2TotalSeconds() {
        assertEquals(TimeUtils.timeString2TotalSeconds(null), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds(""), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("affe"), -1)

        assertEquals(TimeUtils.timeString2TotalSeconds(":"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("::"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("12:12:12:12"), -1)

        assertEquals(TimeUtils.timeString2TotalSeconds("12:12:60"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("12:60:12"), -1)

        assertEquals(TimeUtils.timeString2TotalSeconds("-12:12:12"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("12:-12:12"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("12:12:-12"), -1)

        assertEquals(TimeUtils.timeString2TotalSeconds("a:b:c"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("12a:12:12"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("12:12b:12"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("12:12:12c"), -1)

        assertEquals(TimeUtils.timeString2TotalSeconds(":12:12"), -1)
        assertEquals(TimeUtils.timeString2TotalSeconds("12::12"), -1)

        assertEquals(TimeUtils.timeString2TotalSeconds("0"), 0)
        assertEquals(TimeUtils.timeString2TotalSeconds("5"), 5)
        assertEquals(TimeUtils.timeString2TotalSeconds("37"), 37)

        assertEquals(TimeUtils.timeString2TotalSeconds("3:0"), 3 * 60 + 0)
        assertEquals(TimeUtils.timeString2TotalSeconds("3:5"), 3 * 60 + 5)
        assertEquals(TimeUtils.timeString2TotalSeconds("32:48"), 32 * 60 + 48)

        assertEquals(TimeUtils.timeString2TotalSeconds("3:0:0"), 3 * 60 * 60 + 0 * 60 + 0)
        assertEquals(TimeUtils.timeString2TotalSeconds("13:5:34"), 13 * 60 * 60 + 5 * 60 + 34)
        assertEquals(TimeUtils.timeString2TotalSeconds("114:32:48"), 114 * 60 * 60 + 32 * 60 + 48)
    }
}
