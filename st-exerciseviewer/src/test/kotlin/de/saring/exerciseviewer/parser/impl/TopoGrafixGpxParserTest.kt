package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser

import org.junit.Assert.*
import org.junit.Test

import java.time.LocalDateTime

/**
 * This class contains all unit tests for the TopoGrafixGpxParser class.
 *
 * @author Stefan Saring
 */
class TopoGrafixGpxParserTest {

    /** Instance to be tested. */
    private val parser: ExerciseParser = TopoGrafixGpxParser()

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test(expected = EVException::class)
    fun testParseExerciseMissingFile() {
        parser.parseExercise("misc/testdata/gpx/unknown-file.gpx")
    }

    /**
     * This test parses a GPX file for a bike tour created by GPSies.com.
     * It contains track (location), time and altitude data.
     */
    @Test
    fun testGpxBikeTour() {

        val exercise = parser.parseExercise("misc/testdata/gpx/bike-tour-gpsies.gpx")

        // check basic exercise data
        assertEquals(EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals("Garmin GPX", exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isSpeed)
        assertFalse(exercise.recordingMode.isHeartRate)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isPower)
        assertFalse(exercise.recordingMode.isTemperature)
        assertTrue(exercise.recordingMode.isLocation)
        assertFalse(exercise.recordingMode.isIntervalExercise)

        // Check exercise time and duration
        assertEquals(LocalDateTime.of(2010, 8, 10, 17, 27, 47), exercise.dateTime);
        assertEquals(((47 * 60) + 6) * 10, exercise.duration)

        // check altitude data
        assertEquals(236.toShort(), exercise.altitude.altitudeMin)
        assertEquals(270.toShort(), exercise.altitude.altitudeAvg)
        assertEquals(315.toShort(), exercise.altitude.altitudeMax)
        assertEquals(245, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(39.38832f, exercise.speed.speedMax)
        assertEquals(16484, exercise.speed.distance)
        assertEquals(20.998724f, exercise.speed.speedAvg)

        // check sample data
        assertEquals(199, exercise.sampleList.size)

        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(51.05423620, exercise.sampleList[0].position!!.latitude, 0.00001)
        assertEquals(13.83243080, exercise.sampleList[0].position!!.longitude, 0.00001)
        assertEquals(236.toShort(), exercise.sampleList[0].altitude)
        assertEquals(0f, exercise.sampleList[0].speed)
        assertNull(exercise.sampleList[0].heartRate)
        assertNull(exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(77000L, exercise.sampleList[10].timestamp)
        assertEquals(51.05160000, exercise.sampleList[10].position!!.latitude, 0.00001)
        assertEquals(13.82978000, exercise.sampleList[10].position!!.longitude, 0.00001)
        assertEquals(255.toShort(), exercise.sampleList[10].altitude)
        assertEquals(22.800179f, exercise.sampleList[10].speed)

        assertEquals(2826000L, exercise.sampleList[198].timestamp)
        assertEquals(51.01730000, exercise.sampleList[198].position!!.latitude, 0.00001)
        assertEquals(13.95372000, exercise.sampleList[198].position!!.longitude, 0.00001)
        assertEquals(243.toShort(), exercise.sampleList[198].altitude)
        assertEquals(21.250275f, exercise.sampleList[198].speed)
    }

    /**
     * This test parses a GPX file for a bike tour created by Garmin Oregon with heart rate monitor.
     * It contains track (location), time, altitude and heart rate data.
     */
    @Test
    fun testGpxGarminOregonHeartRateBikeTour() {
        val exercise = parser.parseExercise("misc/testdata/gpx/bike-tour-garmin-oregon-with-heartrate.gpx")

        // check basic exercise data
        assertEquals(EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals("Garmin GPX", exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isPower)
        assertFalse(exercise.recordingMode.isTemperature)
        assertTrue(exercise.recordingMode.isLocation)
        assertFalse(exercise.recordingMode.isIntervalExercise)

        // Check exercise time and duration
        assertEquals(LocalDateTime.of(2012, 3, 2, 6, 53, 51), exercise.dateTime);
        assertEquals(6090, exercise.duration)

        // check altitude data
        assertEquals(32.toShort(), exercise.altitude.altitudeMin)
        assertEquals(41.toShort(), exercise.altitude.altitudeAvg)
        assertEquals(53.toShort(), exercise.altitude.altitudeMax)
        assertEquals(41, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(37.978153f, exercise.speed.speedMax)
        assertEquals(4494, exercise.speed.distance)
        assertEquals(26.565517f, exercise.speed.speedAvg)

        // check heartRate data
        assertEquals(152.toShort(), exercise.heartRateAVG)
        assertEquals(169.toShort(), exercise.heartRateMax)

        // check sample data
        assertEquals(204, exercise.sampleList.size)

        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(50.823521, exercise.sampleList[0].position!!.latitude, 0.00001)
        assertEquals(4.672067, exercise.sampleList[0].position!!.longitude, 0.00001)
        assertEquals(52.toShort(), exercise.sampleList[0].altitude)
        assertEquals(0f, exercise.sampleList[0].speed)
        assertEquals(158.toShort(), exercise.sampleList[0].heartRate)
        assertNull(exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(303000L, exercise.sampleList[101].timestamp)
        assertEquals(50.816284, exercise.sampleList[101].position!!.latitude, 0.00001)
        assertEquals(4.65173, exercise.sampleList[101].position!!.longitude, 0.00001)
        assertEquals(45.toShort(), exercise.sampleList[101].altitude)
        assertEquals(36.26108f, exercise.sampleList[101].speed)
        assertEquals(156.toShort(), exercise.sampleList[101].heartRate)

        assertEquals(609000L, exercise.sampleList[203].timestamp)
        assertEquals(50.802288, exercise.sampleList[203].position!!.latitude, 0.00001)
        assertEquals(4.639261, exercise.sampleList[203].position!!.longitude, 0.00001)
        assertEquals(33.toShort(), exercise.sampleList[203].altitude)
        assertEquals(32.644943f, exercise.sampleList[203].speed)
        assertEquals(154.toShort(), exercise.sampleList[203].heartRate)
    }

    /**
     * This test parses a GPX file for a bike tour created by Holux FunTrek 130 pro with heart rate monitor.
     * It contains track (location), time, altitude and heart rate data.
     * Note that first version of the test file is derived from a Garmin Oregon track file, which has been
     * manually patched to be aligned with the example shown in feature request 3432983
     */
    @Test
    fun testGpxHoluxFunTrek130ProHeartRateBikeTour() {
        val exercise = parser.parseExercise("misc/testdata/gpx/bike-tour-holux-funtrek-130-pro-with-heartrate.gpx")

        // check basic exercise data
        assertEquals(EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals("Garmin GPX", exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isPower)
        assertFalse(exercise.recordingMode.isTemperature)
        assertTrue(exercise.recordingMode.isLocation)
        assertFalse(exercise.recordingMode.isIntervalExercise)

        // Check exercise time and duration
        assertEquals(LocalDateTime.of(2011, 11, 3, 9, 9, 25), exercise.dateTime);
        assertEquals(1780, exercise.duration)

        // check altitude data
        assertEquals(212.toShort(), exercise.altitude.altitudeMin)
        assertEquals(221.toShort(), exercise.altitude.altitudeAvg)
        assertEquals(226.toShort(), exercise.altitude.altitudeMax)
        assertEquals(22, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(39.3484f, exercise.speed.speedMax)
        assertEquals(1055, exercise.speed.distance)
        assertEquals(21.337078f, exercise.speed.speedAvg)

        // check heartRate data
        assertEquals(140.toShort(), exercise.heartRateAVG)
        assertEquals(166.toShort(), exercise.heartRateMax)

        // check sample data
        assertEquals(179, exercise.sampleList.size)

        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(49.990125, exercise.sampleList[0].position!!.latitude, 0.00001)
        assertEquals(36.34850472, exercise.sampleList[0].position!!.longitude, 0.00001)
        assertEquals(224.toShort(), exercise.sampleList[0].altitude)
        assertEquals(0f, exercise.sampleList[0].speed)
        assertEquals(81.toShort(), exercise.sampleList[0].heartRate)
        assertNull(exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(90000L, exercise.sampleList[90].timestamp)
        assertEquals(49.98855833, exercise.sampleList[90].position!!.latitude, 0.00001)
        assertEquals(36.34222333, exercise.sampleList[90].position!!.longitude, 0.00001)
        assertEquals(220.toShort(), exercise.sampleList[90].altitude)
        assertEquals(25.607729f, exercise.sampleList[90].speed)
        assertEquals(157.toShort(), exercise.sampleList[90].heartRate)

        assertEquals(178000L, exercise.sampleList[178].timestamp)
        assertEquals(49.98728833, exercise.sampleList[178].position!!.latitude, 0.00001)
        assertEquals(36.33563333, exercise.sampleList[178].position!!.longitude, 0.00001)
        assertEquals(212.toShort(), exercise.sampleList[178].altitude)
        assertEquals(38.86589f, exercise.sampleList[178].speed)
        assertEquals(165.toShort(), exercise.sampleList[178].heartRate)
    }

    /**
     * This test parses a GPX file, which contains just the track (location) data.
     */
    @Test
    fun testGpxBikeTourLocationDataOnly() {

        val exercise = parser.parseExercise("misc/testdata/gpx/bike-tour-track_only.gpx")

        // check basic exercise data
        assertEquals(EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals("Garmin GPX", exercise.deviceName)
        assertEquals(EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertFalse(exercise.recordingMode.isAltitude)
        assertFalse(exercise.recordingMode.isHeartRate)
        assertFalse(exercise.recordingMode.isSpeed)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isPower)
        assertFalse(exercise.recordingMode.isTemperature)
        assertTrue(exercise.recordingMode.isLocation)
        assertFalse(exercise.recordingMode.isIntervalExercise)

        // check exercise time and duration
        assertNull(exercise.dateTime)
        assertEquals(0, exercise.duration)
        assertNull(exercise.altitude)

        // check sample data
        assertEquals(142, exercise.sampleList.size)

        assertNull(exercise.sampleList[0].timestamp)
        assertEquals(51.41659034, exercise.sampleList[0].position!!.latitude, 0.00001)
        assertEquals(14.94992411, exercise.sampleList[0].position!!.longitude, 0.00001)
        assertNull(exercise.sampleList[0].heartRate)
        assertNull(exercise.sampleList[0].altitude)
        assertNull(exercise.sampleList[0].speed)
        assertNull(exercise.sampleList[0].cadence)
        assertNull(exercise.sampleList[0].distance)

        assertNull(exercise.sampleList[10].timestamp)
        assertEquals(51.4178715, exercise.sampleList[10].position!!.latitude, 0.00001)
        assertEquals(14.94420463, exercise.sampleList[10].position!!.longitude, 0.00001)
        assertNull(exercise.sampleList[10].altitude)

        assertNull(exercise.sampleList[141].timestamp)
        assertEquals(51.32114, exercise.sampleList[141].position!!.latitude, 0.00001)
        assertEquals(14.99391, exercise.sampleList[141].position!!.longitude, 0.00001)
        assertNull(exercise.sampleList[141].altitude)
    }
}
