package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser
import groovy.transform.TypeChecked

import java.time.LocalDateTime

/**
 * This class contains all unit tests for the GarminTcxParser class.
 *
 * @author Stefan Saring
 */
@TypeChecked
class GarminTcxParserTest extends GroovyTestCase {

    /** Instance to be tested. */
    private ExerciseParser parser

    /**
     * This method initializes the environment for testing.
     */
    void setUp() {
        parser = new GarminTcxParser()
    }

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    void testParseExerciseMissingFile() {
        shouldFail(EVException) {
            parser.parseExercise('misc/testdata/garmin-tcx/unknown-file.tcx')
        }
    }

    /**
     * This test parses a TCX file from a Garmin Forerunner 305 (Running, no heartrate data, 1 lap).
     */
    void testForerunner305_Running_NoHeartrate_1Lap() {

        def exercise = parser.parseExercise('misc/testdata/garmin-tcx/Forerunner305-Running-NoHeartrate-1Lap.tcx')
        assertEquals(EVExercise.ExerciseFileType.GARMIN_TCX, exercise.fileType)
        assertEquals('Garmin Forerunner305', exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.altitude)
        assertTrue(exercise.recordingMode.speed)
        assertFalse(exercise.recordingMode.cadence)
        assertTrue(exercise.recordingMode.location)
        assertEquals(LocalDateTime.of(2007, 8, 7, 2, 42, 41), exercise.dateTime);
        assertEquals((39 * 60 + 5) * 10, exercise.duration)

        // heart rates
        assertEquals((short) 0, exercise.heartRateAVG)
        assertEquals((short) 0, exercise.heartRateMax)
        assertEquals(285, exercise.energy)

        // heartrate limits not available in TCX files
        assertNull(exercise.heartRateLimits)

        // distance & speed & odometer
        assertEquals(8349, exercise.speed.distance)
        assertEquals(72.284d, exercise.speed.speedMax, 0.001d)
        assertEquals(12.817d, exercise.speed.speedAVG, 0.001d)

        // altitude
        assertEquals(-4, exercise.altitude.altitudeMin)
        assertEquals((short) 8, exercise.altitude.altitudeAVG)
        assertEquals((short) 21, exercise.altitude.altitudeMax)
        assertEquals(149, exercise.altitude.ascent)

        // cadence
        assertNull(exercise.cadence)

        // lap data
        assertEquals(1, exercise.lapList.size())

        assertEquals((39 * 60 + 5) * 10, exercise.lapList[0].timeSplit)
        assertEquals((short) 0, exercise.lapList[0].heartRateSplit)
        assertEquals((short) 0, exercise.lapList[0].heartRateAVG)
        assertEquals((short) 0, exercise.lapList[0].heartRateMax)
        assertEquals(8349, exercise.lapList[0].speed.distance)
        assertEquals(12.8164d, exercise.lapList[0].speed.speedAVG, 0.001d)
        assertEquals(0, exercise.lapList[0].speed.speedEnd)
        assertEquals((short) 0, exercise.lapList[0].speed.cadence)
        assertEquals((short) 10, exercise.lapList[0].altitude.altitude)
        assertEquals(149, exercise.lapList[0].altitude.ascent)
        assertEquals(37.8765614d, exercise.lapList[0].positionSplit.latitude, 0.001d)
        assertEquals(-122.4601646d, exercise.lapList[0].positionSplit.longitude, 0.001d)

        // sample data
        assertEquals(379, exercise.sampleList.size())

        assertEquals(0 * 1000L, exercise.sampleList[0].timestamp)
        assertEquals(37.8959665d, exercise.sampleList[0].position.latitude, 0.001d)
        assertEquals(-122.4896709d, exercise.sampleList[0].position.longitude, 0.001d)
        assertEquals((short) 0, exercise.sampleList[0].heartRate)
        assertEquals((short) 4, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals((short) 0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(129 * 1000L, exercise.sampleList[20].timestamp)
        assertEquals(37.8943428d, exercise.sampleList[20].position.latitude, 0.001d)
        assertEquals(-122.4870681d, exercise.sampleList[20].position.longitude, 0.001d)
        assertEquals((short) 0, exercise.sampleList[20].heartRate)
        assertEquals((short) 6, exercise.sampleList[20].altitude)
        assertEquals(5.9811f, exercise.sampleList[20].speed, 0.001f)
        assertEquals((short) 0, exercise.sampleList[20].cadence)
        assertEquals(301, exercise.sampleList[20].distance)

        assertEquals(((39 * 60) + 5) * 1000L, exercise.sampleList[378].timestamp)
        assertEquals(37.8765614d, exercise.sampleList[378].position.latitude, 0.001d)
        assertEquals(-122.4601646d, exercise.sampleList[378].position.longitude, 0.001d)
        assertEquals((short) 0, exercise.sampleList[378].heartRate)
        assertEquals((short) 10, exercise.sampleList[378].altitude)
        // speed must be 0 (corrected, because distance is decreased in the last sample)
        assertEquals(0, exercise.sampleList[378].speed, 0.001f)
        assertEquals((short) 0, exercise.sampleList[378].cadence)
        assertEquals(8349, exercise.sampleList[378].distance)
    }

    /**
     * This test parses a TCX file from a Garmin Edge 705 (Running, heartrate data, 2 laps).
     */
    void testEdge705_Running_Heartrate_2Laps() {

        def exercise = parser.parseExercise('misc/testdata/garmin-tcx/Edge705-Running-Heartrate-2Laps.tcx')
        assertEquals(EVExercise.ExerciseFileType.GARMIN_TCX, exercise.fileType)
        assertEquals('Garmin EDGE705', exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.altitude)
        assertTrue(exercise.recordingMode.speed)
        assertTrue(exercise.recordingMode.cadence)
        assertTrue(exercise.recordingMode.location)
        assertEquals(LocalDateTime.of(2009, 12, 9, 6, 54, 25), exercise.dateTime);
        assertEquals(6086 * 10, exercise.duration)

        // heart rates
        assertEquals((short) 157, exercise.heartRateAVG)
        assertEquals((short) 173, exercise.heartRateMax)
        assertEquals(2251, exercise.energy)

        // heartrate limits not available in TCX files
        assertNull(exercise.heartRateLimits)

        // distance & speed & odometer
        assertEquals(18990, exercise.speed.distance)
        assertEquals(164.425d, exercise.speed.speedMax, 0.001d)
        assertEquals(11.232d, exercise.speed.speedAVG, 0.001d)

        // altitude
        assertEquals((short) 94, exercise.altitude.altitudeMin)
        assertEquals((short) 115, exercise.altitude.altitudeAVG)
        assertEquals((short) 153, exercise.altitude.altitudeMax)
        assertEquals(388, exercise.altitude.ascent)

        // cadence
        assertEquals((short) 88, exercise.cadence.cadenceAVG)
        assertEquals((short) 90, exercise.cadence.cadenceMax)

        // lap data
        assertEquals(2, exercise.lapList.size())

        assertEquals((56 * 60 + 20) * 10, exercise.lapList[0].timeSplit)
        assertEquals((short) 168, exercise.lapList[0].heartRateSplit)
        assertEquals((short) 158, exercise.lapList[0].heartRateAVG)
        assertEquals((short) 173, exercise.lapList[0].heartRateMax)
        assertEquals(10618, exercise.lapList[0].speed.distance)
        assertEquals(11.310d, exercise.lapList[0].speed.speedAVG, 0.001d)
        assertEquals((short) 135, exercise.lapList[0].altitude.altitude)
        assertEquals(213, exercise.lapList[0].altitude.ascent)
        assertEquals(9.009, exercise.lapList[0].speed.speedEnd, 0.001d)
        assertEquals((short) 86, exercise.lapList[0].speed.cadence)
        assertEquals(51.030515d, exercise.lapList[0].positionSplit.latitude, 0.001d)
        assertEquals(13.730152d, exercise.lapList[0].positionSplit.longitude, 0.001d)

        assertEquals(((56 * 60 + 20) + (45 * 60 + 6)) * 10, exercise.lapList[1].timeSplit)
        assertEquals((short) 160, exercise.lapList[1].heartRateSplit)
        assertEquals((short) 155, exercise.lapList[1].heartRateAVG)
        assertEquals((short) 166, exercise.lapList[1].heartRateMax)
        assertEquals(10618 + 8372, exercise.lapList[1].speed.distance)
        assertEquals(11.138d, exercise.lapList[1].speed.speedAVG, 0.001d)
        assertEquals(12.568, exercise.lapList[1].speed.speedEnd, 0.01)
        assertEquals((short) 112, exercise.lapList[1].altitude.altitude)
        assertEquals(175, exercise.lapList[1].altitude.ascent)
        assertEquals((short) 0, exercise.lapList[1].speed.cadence)
        assertEquals(51.045960d, exercise.lapList[1].positionSplit.latitude, 0.001d)
        assertEquals(13.809391d, exercise.lapList[1].positionSplit.longitude, 0.001d)

        // sample data
        assertEquals(1254, exercise.sampleList.size())

        assertEquals(1 * 1000L, exercise.sampleList[0].timestamp)
        assertEquals(51.045824d, exercise.sampleList[0].position.latitude, 0.001d)
        assertEquals(13.809552d, exercise.sampleList[0].position.longitude, 0.001d)
        assertEquals((short) 81, exercise.sampleList[0].heartRate)
        assertEquals((short) 110, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals((short) 90, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(72 * 1000L, exercise.sampleList[20].timestamp)
        assertEquals(51.044963d, exercise.sampleList[20].position.latitude, 0.001d)
        assertEquals(13.807715d, exercise.sampleList[20].position.longitude, 0.001d)
        assertEquals((short) 121, exercise.sampleList[20].heartRate)
        assertEquals((short) 108, exercise.sampleList[20].altitude)
        assertEquals(13.739f, exercise.sampleList[20].speed, 0.001f)
        assertEquals((short) 88, exercise.sampleList[20].cadence)
        assertEquals(168, exercise.sampleList[20].distance)

        // last sample of last lap
        assertEquals(6086 * 1000L, exercise.sampleList[1253].timestamp)
        assertEquals(51.045960d, exercise.sampleList[1253].position.latitude, 0.001d)
        assertEquals(13.809391d, exercise.sampleList[1253].position.longitude, 0.001d)
        assertEquals((short) 160, exercise.sampleList[1253].heartRate)
        assertEquals((short) 112, exercise.sampleList[1253].altitude)
        assertEquals(12.5676f, exercise.sampleList[1253].speed, 0.001f)
        assertEquals((short) 0, exercise.sampleList[1253].cadence)
        assertEquals(18990, exercise.sampleList[1253].distance)
    }

    /**
     * This test parses a TCX file from a Garmin Forerunner 405 (Running, 6 laps, with heartrate and run cadence data).
     */
    void testForerunner405_Running_Heartrate_RunCadence() {

        def exercise = parser.parseExercise('misc/testdata/garmin-tcx/Forerunner405-Running_CadenceSensor.tcx')
        assertEquals(EVExercise.ExerciseFileType.GARMIN_TCX, exercise.fileType)
        assertEquals('Garmin Forerunner 405 Software Version 2.80', exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertFalse(exercise.recordingMode.altitude)
        assertTrue(exercise.recordingMode.speed)
        assertTrue(exercise.recordingMode.cadence)
        assertFalse(exercise.recordingMode.location)
        assertEquals(LocalDateTime.of(2017, 1, 16, 19, 02, 0), exercise.dateTime);
        assertEquals(1542 * 10, exercise.duration)

        // heart rates
        assertEquals((short) 173, exercise.heartRateAVG)
        assertEquals((short) 191, exercise.heartRateMax)
        assertEquals(446, exercise.energy)

        // heartrate limits not available in TCX files
        assertNull(exercise.heartRateLimits)

        // distance & speed & odometer
        assertEquals(5599, exercise.speed.distance)
        assertEquals(16.5737d, exercise.speed.speedMax, 0.001d)
        assertEquals(13.0716d, exercise.speed.speedAVG, 0.001d)

        // altitude
        assertNull(exercise.altitude)

        // (run) cadence
        assertEquals((short) 80, exercise.cadence.cadenceAVG)
        assertEquals((short) 90, exercise.cadence.cadenceMax)

        // lap data (check the first only)
        assertEquals(6, exercise.lapList.size())

        assertEquals((4 * 60 + 56) * 10, exercise.lapList[0].timeSplit)
        assertEquals((short) 164, exercise.lapList[0].heartRateSplit)
        assertEquals((short) 150, exercise.lapList[0].heartRateAVG)
        assertEquals((short) 164, exercise.lapList[0].heartRateMax)
        assertEquals(1000, exercise.lapList[0].speed.distance)
        assertEquals(12.1622d, exercise.lapList[0].speed.speedAVG, 0.001d)
        assertEquals(12.3965d, exercise.lapList[0].speed.speedEnd, 0.001d)
        assertNull(exercise.lapList[0].altitude)
        assertEquals((short) 80, exercise.lapList[0].speed.cadence)
        assertNull(exercise.lapList[0].positionSplit)

        // sample data
        assertEquals(270, exercise.sampleList.size())

        assertEquals(1 * 1000L, exercise.sampleList[0].timestamp)
        assertEquals((short) 94, exercise.sampleList[0].heartRate)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals((short) 50, exercise.sampleList[0].cadence)
        assertEquals(2, exercise.sampleList[0].distance)
        assertEquals(0, exercise.sampleList[0].altitude)
        assertNull(exercise.sampleList[0].position)

        assertEquals(31 * 1000L, exercise.sampleList[10].timestamp)
        assertEquals((short) 125, exercise.sampleList[10].heartRate)
        assertEquals(11.5263d, exercise.sampleList[10].speed, 0.001d)
        assertEquals((short) 90, exercise.sampleList[10].cadence)
        assertEquals(92, exercise.sampleList[10].distance)
        assertEquals(0, exercise.sampleList[10].altitude)
        assertNull(exercise.sampleList[10].position)

        assertEquals(86 * 1000L, exercise.sampleList[20].timestamp)
        assertEquals((short) 150, exercise.sampleList[20].heartRate)
        assertEquals(12.0612d, exercise.sampleList[20].speed, 0.001d)
        assertEquals((short) 79, exercise.sampleList[20].cadence)
        assertEquals(278, exercise.sampleList[20].distance)
        assertEquals(0, exercise.sampleList[20].altitude)
        assertNull(exercise.sampleList[20].position)
    }
}