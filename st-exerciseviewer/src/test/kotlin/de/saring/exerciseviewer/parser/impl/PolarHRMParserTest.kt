package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser

import org.junit.Assert.*
import org.junit.Test

import java.time.LocalDateTime

/**
 * This class contains all unit tests for the PolarHRMParser class.
 *
 * @author Stefan Saring
 */
class PolarHRMParserTest {

    /** Instance to be tested. */
    private val parser: ExerciseParser = PolarHRMParser()

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test(expected = EVException::class)
    fun testParseExerciseMissingFile() {
        parser.parseExercise("misc/testdata/sample-123.hrm")
    }

    /**
     * This method tests the parser by with an cycling exercise file recorded in metric units.
     */
    @Test
    fun testParseExerciseWithMetricUnits() {

        // parse exercise file
        val exercise = parser.parseExercise("misc/testdata/s710/cycling-metric.hrm")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.HRM, exercise.fileType)
        assertEquals("Polar HRM", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isSpeed)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isPower)

        assertEquals(LocalDateTime.of(2002, 11, 20, 14, 7, 44), exercise.dateTime);
        assertEquals((1 * 60 * 60 * 10) + (13 * 60 * 10) + 15 * 10, exercise.duration)
        assertEquals(15.toShort(), exercise.recordingInterval)

        assertEquals(135.toShort(), exercise.heartRateAVG)
        assertEquals(218.toShort(), exercise.heartRateMax)
        assertEquals(251, Math.round(exercise.speed!!.speedAvg * 10))
        assertEquals(969, Math.round(exercise.speed!!.speedMax * 10))
        assertEquals(29900, exercise.speed!!.distance)

        assertNull(exercise.cadence)
        assertEquals(215.toShort(), exercise.altitude!!.altitudeMin)
        assertEquals(253.toShort(), exercise.altitude!!.altitudeAvg)
        assertEquals(300.toShort(), exercise.altitude!!.altitudeMax)
        assertEquals(1024, exercise.odometer)

        // check heart rate limits
        assertEquals(3, exercise.heartRateLimits.size)
        assertEquals(120.toShort(), exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals(155.toShort(), exercise.heartRateLimits[0].upperHeartRate)
        assertEquals((0 * 60 * 60) + (5 * 60) + 0, exercise.heartRateLimits[0].timeBelow)
        assertEquals((1 * 60 * 60) + (3 * 60) + 45, exercise.heartRateLimits[0].timeWithin)
        assertEquals((0 * 60 * 60) + (4 * 60) + 15, exercise.heartRateLimits[0].timeAbove)

        assertEquals(80.toShort(), exercise.heartRateLimits[1].lowerHeartRate)
        assertEquals(160.toShort(), exercise.heartRateLimits[1].upperHeartRate)
        assertEquals((0 * 60 * 60) + (5 * 60) + 0, exercise.heartRateLimits[1].timeBelow)   // same as for first range, but they should be different ???
        assertEquals((1 * 60 * 60) + (3 * 60) + 45, exercise.heartRateLimits[1].timeWithin) // same as for first range, but they should be different ???
        assertEquals((0 * 60 * 60) + (4 * 60) + 15, exercise.heartRateLimits[1].timeAbove)  // same as for first range, but they should be different ???

        assertEquals(80.toShort(), exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals(160.toShort(), exercise.heartRateLimits[2].upperHeartRate)
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeBelow)  // 0 for some reason, although should be same as for second range ???
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeWithin) // 0 for some reason, although should be same as for second range ???
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeAbove)  // 0 for some reason, although should be same as for second range ???

        // check lap data (first, one from middle and last lap only)
        assertEquals(5, exercise.lapList.size)
        assertEquals((0 * 60 * 60 * 10) + (6 * 60 * 10) + (59 * 10) + 2, exercise.lapList[0].timeSplit)
        assertEquals(136.toShort(), exercise.lapList[0].heartRateSplit)
        assertEquals(128.toShort(), exercise.lapList[0].heartRateAVG)
        assertEquals(151.toShort(), exercise.lapList[0].heartRateMax)
        assertEquals(141, Math.round(exercise.lapList[0].speed!!.speedEnd * 10))
        assertEquals(258, Math.round(exercise.lapList[0].speed!!.speedAVG * 10))
        assertEquals(2998, exercise.lapList[0].speed!!.distance)
        assertNull(exercise.lapList[0].speed!!.cadence)
        assertEquals(231.toShort(), exercise.lapList[0].altitude!!.altitude)
        assertEquals(4.toShort(), exercise.lapList[0].temperature!!.temperature)

        assertEquals((0 * 60 * 60 * 10) + (40 * 60 * 10) + (18 * 10) + 8, exercise.lapList[2].timeSplit)
        assertEquals(136.toShort(), exercise.lapList[2].heartRateSplit)
        assertEquals(133.toShort(), exercise.lapList[2].heartRateAVG)
        assertEquals(168.toShort(), exercise.lapList[2].heartRateMax)
        assertEquals(193, Math.round(exercise.lapList[2].speed!!.speedEnd * 10))
        assertEquals(247, Math.round(exercise.lapList[2].speed!!.speedAVG * 10))
        assertEquals(15874, exercise.lapList[2].speed!!.distance)
        assertNull(exercise.lapList[2].speed!!.cadence)
        assertEquals(247.toShort(), exercise.lapList[2].altitude!!.altitude)
        assertEquals(4.toShort(), exercise.lapList[2].temperature!!.temperature)

        assertEquals((1 * 60 * 60 * 10) + (13 * 60 * 10) + (34 * 10) + 3, exercise.lapList[4].timeSplit)
        assertEquals(123.toShort(), exercise.lapList[4].heartRateSplit)
        assertEquals(60.toShort(), exercise.lapList[4].heartRateAVG) // this value is impossible ???
        assertEquals(121.toShort(), exercise.lapList[4].heartRateMax) // hoe can this be, the split HR is higher ???
        assertEquals(0, Math.round(exercise.lapList[4].speed!!.speedEnd * 10))
        assertEquals(0, Math.round(exercise.lapList[4].speed!!.speedAVG * 10))
        assertEquals(29900, exercise.lapList[4].speed!!.distance)
        assertNull(exercise.lapList[4].speed!!.cadence)
        assertEquals(229.toShort(), exercise.lapList[4].altitude!!.altitude)
        assertEquals(4.toShort(), exercise.lapList[4].temperature!!.temperature)

        // check sample data (first, two from middle and last only)
        assertEquals(294, exercise.sampleList.size)
        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(101.toShort(), exercise.sampleList[0].heartRate)
        assertEquals(240.toShort(), exercise.sampleList[0].altitude)
        assertEquals(42, Math.round(exercise.sampleList[0].speed!! * 10))
        assertNull(exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(100 * 15 * 1000L, exercise.sampleList[100].timestamp)
        assertEquals(147.toShort(), exercise.sampleList[100].heartRate)
        assertEquals(278.toShort(), exercise.sampleList[100].altitude)
        assertEquals(171, Math.round(exercise.sampleList[100].speed!! * 10))
        assertNull(exercise.sampleList[100].cadence)
        assertEquals(9499, exercise.sampleList[100].distance)

        assertEquals(200 * 15 * 1000L, exercise.sampleList[200].timestamp)
        assertEquals(166.toShort(), exercise.sampleList[200].heartRate)
        assertEquals(275.toShort(), exercise.sampleList[200].altitude)
        assertEquals(141, Math.round(exercise.sampleList[200].speed!! * 10))
        assertNull(exercise.sampleList[200].cadence)
        assertEquals(19296, exercise.sampleList[200].distance)

        assertEquals(293 * 15 * 1000L, exercise.sampleList[293].timestamp)
        assertEquals(121.toShort(), exercise.sampleList[293].heartRate)
        assertEquals(228.toShort(), exercise.sampleList[293].altitude)
        assertEquals(153, Math.round(exercise.sampleList[293].speed!! * 10))
        assertNull(exercise.sampleList[293].cadence)
        assertEquals(29900, exercise.sampleList[293].distance)
    }

    /**
     * This method tests the parser by with an cycling exercise file recorded in english units.
     */
    @Test
    fun testParseCyclingExerciseWithEnglishUnits() {
        // parse exercise file
        val exercise = parser.parseExercise("misc/testdata/s710/cycling-english.hrm")

        // check exercise data
        assertEquals(exercise.fileType, EVExercise.ExerciseFileType.HRM)
        assertEquals("Polar HRM", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isSpeed)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isPower)

        assertEquals(LocalDateTime.of(2002, 11, 20, 13, 10, 42), exercise.dateTime);
        assertEquals((0 * 60 * 60 * 10) + (51 * 60 * 10) + 0 * 10, exercise.duration)
        assertEquals(15.toShort(), exercise.recordingInterval)

        assertEquals(136.toShort(), exercise.heartRateAVG)
        assertEquals(232.toShort(), exercise.heartRateMax)
        assertEquals(247, Math.round(exercise.speed!!.speedAvg * 10))
        assertEquals(1077, Math.round(exercise.speed!!.speedMax * 10))
        assertEquals(20921, exercise.speed!!.distance)
        assertNull(exercise.cadence)
        assertEquals(221.toShort(), exercise.altitude!!.altitudeMin)
        assertEquals(245.toShort(), exercise.altitude!!.altitudeAvg)
        assertEquals(277.toShort(), exercise.altitude!!.altitudeMax)
        assertEquals(exercise.odometer, 993)

        // check heart rate limits
        assertEquals(3, exercise.heartRateLimits.size)
        assertEquals(120.toShort(), exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals(155.toShort(), exercise.heartRateLimits[0].upperHeartRate)
        assertEquals((0 * 60 * 60) + (1 * 60) + 45, exercise.heartRateLimits[0].timeBelow)
        assertEquals((0 * 60 * 60) + (45 * 60) + 15, exercise.heartRateLimits[0].timeWithin)
        assertEquals((0 * 60 * 60) + (2 * 60) + 0, exercise.heartRateLimits[0].timeAbove)

        assertEquals(exercise.heartRateLimits[1].lowerHeartRate, 80.toShort())
        assertEquals(exercise.heartRateLimits[1].upperHeartRate, 160.toShort())
        assertEquals((0 * 60 * 60) + (1 * 60) + 45, exercise.heartRateLimits[1].timeBelow) // same as for first range, but they should be different ???
        assertEquals((0 * 60 * 60) + (45 * 60) + 15, exercise.heartRateLimits[1].timeWithin) // same as for first range, but they should be different ???
        assertEquals((0 * 60 * 60) + (2 * 60) + 0, exercise.heartRateLimits[1].timeAbove) // same as for first range, but they should be different ???

        assertEquals(80.toShort(), exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals(160.toShort(), exercise.heartRateLimits[2].upperHeartRate)
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeBelow) // 0 for some reason, although should be same as for second range ???
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeWithin) // 0 for some reason, although should be same as for second range ???
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeAbove) // 0 for some reason, although should be same as for second range ???

        // check lap data (first, one from middle and last lap only)
        assertEquals(4, exercise.lapList.size)
        assertEquals((0 * 60 * 60 * 10) + (20 * 60 * 10) + (34 * 10) + 6, exercise.lapList[0].timeSplit)
        assertEquals(143.toShort(), exercise.lapList[0].heartRateSplit)
        assertEquals(140.toShort(), exercise.lapList[0].heartRateAVG)
        assertEquals(232.toShort(), exercise.lapList[0].heartRateMax)
        assertEquals(206, Math.round(exercise.lapList[0].speed!!.speedEnd * 10))
        assertEquals(243, Math.round(exercise.lapList[0].speed!!.speedAVG * 10))
        assertEquals(8332, exercise.lapList[0].speed!!.distance)
        assertNull(exercise.lapList[0].speed!!.cadence)
        assertEquals(273.toShort(), exercise.lapList[0].altitude!!.altitude)
        assertEquals(3.toShort(), exercise.lapList[0].temperature!!.temperature)

        assertEquals((0 * 60 * 60 * 10) + (46 * 60 * 10) + (51 * 10) + 2, exercise.lapList[1].timeSplit)
        assertEquals(129.toShort(), exercise.lapList[1].heartRateSplit)
        assertEquals(133.toShort(), exercise.lapList[1].heartRateAVG)
        assertEquals(159.toShort(), exercise.lapList[1].heartRateMax)
        assertEquals(352, Math.round(exercise.lapList[1].speed!!.speedEnd * 10))
        assertEquals(254, Math.round(exercise.lapList[1].speed!!.speedAVG * 10))
        assertEquals(19437, exercise.lapList[1].speed!!.distance)
        assertNull(exercise.lapList[1].speed!!.cadence)
        assertEquals(248.toShort(), exercise.lapList[1].altitude!!.altitude)
        assertEquals(3.toShort(), exercise.lapList[1].temperature!!.temperature)

        assertEquals((0 * 60 * 60 * 10) + (51 * 60 * 10) + (22 * 10) + 6, exercise.lapList[3].timeSplit)
        assertEquals(116.toShort(), exercise.lapList[3].heartRateSplit)
        assertEquals(60.toShort(), exercise.lapList[3].heartRateAVG) // this value is impossible ???
        assertEquals(121.toShort(), exercise.lapList[3].heartRateMax)
        assertEquals(0 * 10, Math.round(exercise.lapList[3].speed!!.speedEnd * 10))
        assertEquals(0 * 10, Math.round(exercise.lapList[3].speed!!.speedAVG * 10))
        assertEquals(20921, exercise.lapList[3].speed!!.distance)
        assertNull(exercise.lapList[3].speed!!.cadence)
        assertEquals(239.toShort(), exercise.lapList[3].altitude!!.altitude)
        assertEquals(4.toShort(), exercise.lapList[3].temperature!!.temperature)

        // check sample data (first, two from middle and last only)
        assertEquals(205, exercise.sampleList.size)
        assertEquals(83.toShort(), exercise.sampleList[0].heartRate)
        assertEquals(221.toShort(), exercise.sampleList[0].altitude)
        assertEquals(0 * 10, Math.round(exercise.sampleList[0].speed!! * 10))
        assertNull(exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(100 * 15 * 1000L, exercise.sampleList[100].timestamp)
        assertEquals(124.toShort(), exercise.sampleList[100].heartRate)
        assertEquals(270.toShort(), exercise.sampleList[100].altitude)
        assertEquals(351, Math.round(exercise.sampleList[100].speed!! * 10))
        assertNull(exercise.sampleList[100].cadence)
        assertEquals(9970, exercise.sampleList[100].distance)

        assertEquals(200 * 15 * 1000L, exercise.sampleList[200].timestamp)
        assertEquals(138.toShort(), exercise.sampleList[200].heartRate)
        assertEquals(242.toShort(), exercise.sampleList[200].altitude)
        assertEquals(291, Math.round(exercise.sampleList[200].speed!! * 10))
        assertNull(exercise.sampleList[200].cadence)
        assertEquals(20452, exercise.sampleList[200].distance)

        assertEquals(204 * 15 * 1000L, exercise.sampleList[204].timestamp)
        assertEquals(121.toShort(), exercise.sampleList[204].heartRate)
        assertEquals(241.toShort(), exercise.sampleList[204].altitude)
        assertEquals(0, Math.round(exercise.sampleList[204].speed!! * 10))
        assertNull(exercise.sampleList[204].cadence)
        assertEquals(20921, exercise.sampleList[204].distance)
    }

    /**
     * This method tests the parser by with an running exercise file recorded in metric units.
     */
    @Test
    fun testParseRunningExerciseWithMetricUnits() {
        val exercise = parser.parseExercise("misc/testdata/s710/running-metric.hrm")

        // check exercise data
        assertEquals(exercise.fileType, EVExercise.ExerciseFileType.HRM)
        assertEquals("Polar HRM", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isAltitude)
        assertFalse(exercise.recordingMode.isSpeed)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isPower)

        assertEquals(LocalDateTime.of(2002, 12, 25, 10, 21, 4), exercise.dateTime);
        assertEquals((0 * 60 * 60 * 10) + (42 * 60 * 10) + 24 * 10 + 7, exercise.duration)
        assertEquals(15.toShort(), exercise.recordingInterval)

        assertEquals(147.toShort(), exercise.heartRateAVG)
        assertEquals(158.toShort(), exercise.heartRateMax)
        assertNull(exercise.speed)
        assertNull(exercise.cadence)
        assertEquals(86.toShort(), exercise.altitude!!.altitudeMin)
        assertEquals(93.toShort(), exercise.altitude!!.altitudeAvg)
        assertEquals(101.toShort(), exercise.altitude!!.altitudeMax)
        assertEquals(1200, exercise.odometer)

        // check heart rate limits
        assertEquals(3, exercise.heartRateLimits.size)
        assertEquals(130.toShort(), exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals(150.toShort(), exercise.heartRateLimits[0].upperHeartRate)
        assertEquals((0 * 60 * 60) + (0 * 60) + 45, exercise.heartRateLimits[0].timeBelow)
        assertEquals((0 * 60 * 60) + (31 * 60) + 0, exercise.heartRateLimits[0].timeWithin)
        assertEquals((0 * 60 * 60) + (10 * 60) + 15, exercise.heartRateLimits[0].timeAbove)

        assertEquals(80.toShort(), exercise.heartRateLimits[1].lowerHeartRate)
        assertEquals(160.toShort(), exercise.heartRateLimits[1].upperHeartRate)
        assertEquals((0 * 60 * 60) + (0 * 60) + 45, exercise.heartRateLimits[1].timeBelow) // same as for first range, but they should be different ???
        assertEquals((0 * 60 * 60) + (31 * 60) + 0, exercise.heartRateLimits[1].timeWithin) // same as for first range, but they should be different ???
        assertEquals((0 * 60 * 60) + (10 * 60) + 15, exercise.heartRateLimits[1].timeAbove) // same as for first range, but they should be different ???

        assertEquals(80.toShort(), exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals(160.toShort(), exercise.heartRateLimits[2].upperHeartRate)
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeBelow) // 0 for some reason, although should be same as for second range ???
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeWithin) // 0 for some reason, although should be same as for second range ???
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.heartRateLimits[2].timeAbove) // 0 for some reason, although should be same as for second range ???

        // check lap data (one lap only)
        assertEquals(1, exercise.lapList.size)
        assertEquals((0 * 60 * 60 * 10) + (42 * 60 * 10) + (24 * 10) + 7, exercise.lapList[0].timeSplit)
        assertEquals(146.toShort(), exercise.lapList[0].heartRateSplit)
        assertEquals(146.toShort(), exercise.lapList[0].heartRateAVG)
        assertEquals(158.toShort(), exercise.lapList[0].heartRateMax)
        assertNull(exercise.lapList[0].speed)
        assertEquals(88.toShort(), exercise.lapList[0].altitude!!.altitude)
        assertEquals(19.toShort(), exercise.lapList[0].temperature!!.temperature)

        // check sample data (first, two from middle and last only)
        assertEquals(170, exercise.sampleList.size)
        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(0.toShort(), exercise.sampleList[0].heartRate)
        assertEquals(91.toShort(), exercise.sampleList[0].altitude)
        assertNull(exercise.sampleList[0].speed)
        assertNull(exercise.sampleList[0].cadence)
        assertNull(exercise.sampleList[0].distance)

        assertEquals(100 * 15 * 1000L, exercise.sampleList[100].timestamp)
        assertEquals(149.toShort(), exercise.sampleList[100].heartRate)
        assertEquals(98.toShort(), exercise.sampleList[100].altitude)
        assertNull(exercise.sampleList[100].speed)
        assertNull(exercise.sampleList[100].cadence)
        assertNull(exercise.sampleList[100].distance)

        assertEquals(150 * 15 * 1000L, exercise.sampleList[150].timestamp)
        assertEquals(142.toShort(), exercise.sampleList[150].heartRate)
        assertEquals(89.toShort(), exercise.sampleList[150].altitude)
        assertNull(exercise.sampleList[150].speed)
        assertNull(exercise.sampleList[150].cadence)
        assertNull(exercise.sampleList[150].distance)

        assertEquals(169 * 15 * 1000L, exercise.sampleList[169].timestamp)
        assertEquals(147.toShort(), exercise.sampleList[169].heartRate)
        assertEquals(88.toShort(), exercise.sampleList[169].altitude)
        assertNull(exercise.sampleList[169].speed)
        assertNull(exercise.sampleList[169].cadence)
        assertNull(exercise.sampleList[169].distance)
    }
}
