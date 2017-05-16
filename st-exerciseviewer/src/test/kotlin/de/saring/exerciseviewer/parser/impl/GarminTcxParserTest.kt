package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

/**
 * This class contains all unit tests for the GarminTcxParser class.
 *
 * @author Stefan Saring
 */
class GarminTcxParserTest {

    /** Instance to be tested. */
    private val parser: ExerciseParser = GarminTcxParser()

    /**
     * This method must fail on parsing an exercise file which doesn't exists.
     */
    @Test(expected = EVException::class)
    fun testParseExerciseMissingFile() {
        parser.parseExercise("misc/testdata/garmin-tcx/unknown-file.tcx")
    }

    /**
     * This test parses a TCX file from a Garmin Forerunner 305 (Running, no heartrate data, 1 lap).
     */
    @Test
    fun testForerunner305_Running_NoHeartrate_1Lap() {

        val exercise = parser.parseExercise("misc/testdata/garmin-tcx/Forerunner305-Running-NoHeartrate-1Lap.tcx")
        assertEquals(EVExercise.ExerciseFileType.GARMIN_TCX, exercise.fileType)
        assertEquals("Garmin Forerunner305", exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isSpeed)
        assertFalse(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isLocation)
        assertEquals(LocalDateTime.of(2007, 8, 7, 2, 42, 41), exercise.dateTime);
        assertEquals((39 * 60 + 5) * 10, exercise.duration)

        // heart rates
        assertEquals(0.toShort(), exercise.heartRateAVG)
        assertEquals(0.toShort(), exercise.heartRateMax)
        assertEquals(285, exercise.energy)

        // heartrate limits not available in TCX files
        assertNull(exercise.heartRateLimits)

        // distance & speed & odometer
        assertEquals(8349, exercise.speed.distance)
        assertEquals(72.284f, exercise.speed.speedMax, 0.001f)
        assertEquals(12.817f, exercise.speed.speedAVG, 0.001f)

        // altitude
        assertEquals((-4).toShort(), exercise.altitude.altitudeMin)
        assertEquals(8.toShort(), exercise.altitude.altitudeAVG)
        assertEquals(21.toShort(), exercise.altitude.altitudeMax)
        assertEquals(149, exercise.altitude.ascent)

        // cadence
        assertNull(exercise.cadence)

        // lap data
        assertEquals(1, exercise.lapList.size)

        assertEquals((39 * 60 + 5) * 10, exercise.lapList[0].timeSplit)
        assertEquals(0.toShort(), exercise.lapList[0].heartRateSplit)
        assertEquals(0.toShort(), exercise.lapList[0].heartRateAVG)
        assertEquals(0.toShort(), exercise.lapList[0].heartRateMax)
        assertEquals(8349, exercise.lapList[0].speed.distance)
        assertEquals(12.8164f, exercise.lapList[0].speed.speedAVG, 0.001f)
        assertEquals(0.0f, exercise.lapList[0].speed.speedEnd)
        assertEquals(0.toShort(), exercise.lapList[0].speed.cadence)
        assertEquals(10.toShort(), exercise.lapList[0].altitude.altitude)
        assertEquals(149, exercise.lapList[0].altitude.ascent)
        assertEquals(37.8765614, exercise.lapList[0].positionSplit.latitude, 0.001)
        assertEquals(-122.4601646, exercise.lapList[0].positionSplit.longitude, 0.001)

        // sample data
        assertEquals(379, exercise.sampleList.size)

        assertEquals(0 * 1000L, exercise.sampleList[0].timestamp)
        assertEquals(37.8959665, exercise.sampleList[0].position.latitude, 0.001)
        assertEquals(-122.4896709, exercise.sampleList[0].position.longitude, 0.001)
        assertEquals(0.toShort(), exercise.sampleList[0].heartRate)
        assertEquals(4.toShort(), exercise.sampleList[0].altitude)
        assertEquals(0.0f, exercise.sampleList[0].speed)
        assertEquals(0.toShort(), exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(129 * 1000L, exercise.sampleList[20].timestamp)
        assertEquals(37.8943428, exercise.sampleList[20].position.latitude, 0.001)
        assertEquals(-122.4870681, exercise.sampleList[20].position.longitude, 0.001)
        assertEquals(0.toShort(), exercise.sampleList[20].heartRate)
        assertEquals(6.toShort(), exercise.sampleList[20].altitude)
        assertEquals(5.9811f, exercise.sampleList[20].speed, 0.001f)
        assertEquals(0.toShort(), exercise.sampleList[20].cadence)
        assertEquals(301, exercise.sampleList[20].distance)

        assertEquals(((39 * 60) + 5) * 1000L, exercise.sampleList[378].timestamp)
        assertEquals(37.8765614, exercise.sampleList[378].position.latitude, 0.001)
        assertEquals(-122.4601646, exercise.sampleList[378].position.longitude, 0.001)
        assertEquals(0.toShort(), exercise.sampleList[378].heartRate)
        assertEquals(10.toShort(), exercise.sampleList[378].altitude)
        // speed must be 0 (corrected, because distance is decreased in the last sample)
        assertEquals(0f, exercise.sampleList[378].speed, 0.001f)
        assertEquals(0.toShort(), exercise.sampleList[378].cadence)
        assertEquals(8349, exercise.sampleList[378].distance)
    }

    /**
     * This test parses a TCX file from a Garmin Edge 705 (Running, heartrate data, 2 laps).
     */
    @Test
    fun testEdge705_Running_Heartrate_2Laps() {

        val exercise = parser.parseExercise("misc/testdata/garmin-tcx/Edge705-Running-Heartrate-2Laps.tcx")
        assertEquals(EVExercise.ExerciseFileType.GARMIN_TCX, exercise.fileType)
        assertEquals("Garmin EDGE705", exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isLocation)
        assertEquals(LocalDateTime.of(2009, 12, 9, 6, 54, 25), exercise.dateTime);
        assertEquals(6086 * 10, exercise.duration)

        // heart rates
        assertEquals(157.toShort(), exercise.heartRateAVG)
        assertEquals(173.toShort(), exercise.heartRateMax)
        assertEquals(2251, exercise.energy)

        // heartrate limits not available in TCX files
        assertNull(exercise.heartRateLimits)

        // distance & speed & odometer
        assertEquals(18990, exercise.speed.distance)
        assertEquals(164.425f, exercise.speed.speedMax, 0.001f)
        assertEquals(11.232f, exercise.speed.speedAVG, 0.001f)

        // altitude
        assertEquals(94.toShort(), exercise.altitude.altitudeMin)
        assertEquals(115.toShort(), exercise.altitude.altitudeAVG)
        assertEquals(153.toShort(), exercise.altitude.altitudeMax)
        assertEquals(388, exercise.altitude.ascent)

        // cadence
        assertEquals(88.toShort(), exercise.cadence.cadenceAVG)
        assertEquals(90.toShort(), exercise.cadence.cadenceMax)

        // lap data
        assertEquals(2, exercise.lapList.size)

        assertEquals((56 * 60 + 20) * 10, exercise.lapList[0].timeSplit)
        assertEquals(168.toShort(), exercise.lapList[0].heartRateSplit)
        assertEquals(158.toShort(), exercise.lapList[0].heartRateAVG)
        assertEquals(173.toShort(), exercise.lapList[0].heartRateMax)
        assertEquals(10618, exercise.lapList[0].speed.distance)
        assertEquals(11.310f, exercise.lapList[0].speed.speedAVG, 0.001f)
        assertEquals(135.toShort(), exercise.lapList[0].altitude.altitude)
        assertEquals(213, exercise.lapList[0].altitude.ascent)
        assertEquals(9.009f, exercise.lapList[0].speed.speedEnd, 0.001f)
        assertEquals(86.toShort(), exercise.lapList[0].speed.cadence)
        assertEquals(51.030515, exercise.lapList[0].positionSplit.latitude, 0.001)
        assertEquals(13.730152, exercise.lapList[0].positionSplit.longitude, 0.001)

        assertEquals(((56 * 60 + 20) + (45 * 60 + 6)) * 10, exercise.lapList[1].timeSplit)
        assertEquals(160.toShort(), exercise.lapList[1].heartRateSplit)
        assertEquals(155.toShort(), exercise.lapList[1].heartRateAVG)
        assertEquals(166.toShort(), exercise.lapList[1].heartRateMax)
        assertEquals(10618 + 8372, exercise.lapList[1].speed.distance)
        assertEquals(11.138f, exercise.lapList[1].speed.speedAVG, 0.001f)
        assertEquals(12.568f, exercise.lapList[1].speed.speedEnd, 0.01f)
        assertEquals(112.toShort(), exercise.lapList[1].altitude.altitude)
        assertEquals(175, exercise.lapList[1].altitude.ascent)
        assertEquals(0.toShort(), exercise.lapList[1].speed.cadence)
        assertEquals(51.045960, exercise.lapList[1].positionSplit.latitude, 0.001)
        assertEquals(13.809391, exercise.lapList[1].positionSplit.longitude, 0.001)

        // sample data
        assertEquals(1254, exercise.sampleList.size)

        assertEquals(1 * 1000L, exercise.sampleList[0].timestamp)
        assertEquals(51.045824, exercise.sampleList[0].position.latitude, 0.001)
        assertEquals(13.809552, exercise.sampleList[0].position.longitude, 0.001)
        assertEquals(81.toShort(), exercise.sampleList[0].heartRate)
        assertEquals(110.toShort(), exercise.sampleList[0].altitude)
        assertEquals(0f, exercise.sampleList[0].speed)
        assertEquals(90.toShort(), exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(72 * 1000L, exercise.sampleList[20].timestamp)
        assertEquals(51.044963, exercise.sampleList[20].position.latitude, 0.001)
        assertEquals(13.807715, exercise.sampleList[20].position.longitude, 0.001)
        assertEquals(121.toShort(), exercise.sampleList[20].heartRate)
        assertEquals(108.toShort(), exercise.sampleList[20].altitude)
        assertEquals(13.739f, exercise.sampleList[20].speed, 0.001f)
        assertEquals(88.toShort(), exercise.sampleList[20].cadence)
        assertEquals(168, exercise.sampleList[20].distance)

        // last sample of last lap
        assertEquals(6086 * 1000L, exercise.sampleList[1253].timestamp)
        assertEquals(51.045960, exercise.sampleList[1253].position.latitude, 0.001)
        assertEquals(13.809391, exercise.sampleList[1253].position.longitude, 0.001)
        assertEquals(160.toShort(), exercise.sampleList[1253].heartRate)
        assertEquals(112.toShort(), exercise.sampleList[1253].altitude)
        assertEquals(12.5676f, exercise.sampleList[1253].speed, 0.001f)
        assertEquals(0.toShort(), exercise.sampleList[1253].cadence)
        assertEquals(18990, exercise.sampleList[1253].distance)
    }

    /**
     * This test parses a TCX file from a Garmin Forerunner 405 (Running, 6 laps, with heartrate and run cadence data).
     */
    @Test
    fun testForerunner405_Running_Heartrate_RunCadence() {

        val exercise = parser.parseExercise("misc/testdata/garmin-tcx/Forerunner405-Running_CadenceSensor.tcx")
        assertEquals(EVExercise.ExerciseFileType.GARMIN_TCX, exercise.fileType)
        assertEquals("Garmin Forerunner 405 Software Version 2.80", exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertFalse(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isLocation)
        assertEquals(LocalDateTime.of(2017, 1, 16, 19, 2, 0), exercise.dateTime);
        assertEquals(1542 * 10, exercise.duration)

        // heart rates
        assertEquals(173.toShort(), exercise.heartRateAVG)
        assertEquals(191.toShort(), exercise.heartRateMax)
        assertEquals(446, exercise.energy)

        // heartrate limits not available in TCX files
        assertNull(exercise.heartRateLimits)

        // distance & speed & odometer
        assertEquals(5599, exercise.speed.distance)
        assertEquals(16.5737f, exercise.speed.speedMax, 0.001f)
        assertEquals(13.0716f, exercise.speed.speedAVG, 0.001f)

        // altitude
        assertNull(exercise.altitude)

        // (run) cadence
        assertEquals(80.toShort(), exercise.cadence.cadenceAVG)
        assertEquals(90.toShort(), exercise.cadence.cadenceMax)

        // lap data (check the first only)
        assertEquals(6, exercise.lapList.size)

        assertEquals((4 * 60 + 56) * 10, exercise.lapList[0].timeSplit)
        assertEquals(164.toShort(), exercise.lapList[0].heartRateSplit)
        assertEquals(150.toShort(), exercise.lapList[0].heartRateAVG)
        assertEquals(164.toShort(), exercise.lapList[0].heartRateMax)
        assertEquals(1000, exercise.lapList[0].speed.distance)
        assertEquals(12.1622f, exercise.lapList[0].speed.speedAVG, 0.001f)
        assertEquals(12.3965f, exercise.lapList[0].speed.speedEnd, 0.001f)
        assertNull(exercise.lapList[0].altitude)
        assertEquals(80.toShort(), exercise.lapList[0].speed.cadence)
        assertNull(exercise.lapList[0].positionSplit)

        // sample data
        assertEquals(270, exercise.sampleList.size)

        assertEquals(1 * 1000L, exercise.sampleList[0].timestamp)
        assertEquals(94.toShort(), exercise.sampleList[0].heartRate)
        assertEquals(0f, exercise.sampleList[0].speed)
        assertEquals(50.toShort(), exercise.sampleList[0].cadence)
        assertEquals(2, exercise.sampleList[0].distance)
        assertEquals(0.toShort(), exercise.sampleList[0].altitude)
        assertNull(exercise.sampleList[0].position)

        assertEquals(31 * 1000L, exercise.sampleList[10].timestamp)
        assertEquals(125.toShort(), exercise.sampleList[10].heartRate)
        assertEquals(11.5263f, exercise.sampleList[10].speed, 0.001f)
        assertEquals(90.toShort(), exercise.sampleList[10].cadence)
        assertEquals(92, exercise.sampleList[10].distance)
        assertEquals(0.toShort(), exercise.sampleList[10].altitude)
        assertNull(exercise.sampleList[10].position)

        assertEquals(86 * 1000L, exercise.sampleList[20].timestamp)
        assertEquals(150.toShort(), exercise.sampleList[20].heartRate)
        assertEquals(12.0612f, exercise.sampleList[20].speed, 0.001f)
        assertEquals(79.toShort(), exercise.sampleList[20].cadence)
        assertEquals(278, exercise.sampleList[20].distance)
        assertEquals(0.toShort(), exercise.sampleList[20].altitude)
        assertNull(exercise.sampleList[20].position)
    }
}