package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser

/**
 * This class contains all unit tests for the TopoGrafixGpxParser class.
 *
 * @author Stefan Saring
 */
class TopoGrafixGpxParserTest extends GroovyTestCase {

    /** Instance to be tested. */
    private ExerciseParser parser

    /**
     * This method initializes the environment for testing.
     */
    void setUp () {
        parser = new TopoGrafixGpxParser ()
    }

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    void testParseExerciseMissingFile () {
        shouldFail (EVException) {
            parser.parseExercise ('misc/testdata/gpx/unknown-file.gpx')
        }
    }

    /**
     * This test parses a GPX file for a bike tour created by GPSies.com.
     * It contains track (location), time and altitude data.
     */
    void testGpxBikeTour () {

        def exercise = parser.parseExercise ('misc/testdata/gpx/bike-tour-gpsies.gpx')

        // check basic exercise data
        assertEquals (EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals (EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.altitude)
        assertTrue(exercise.recordingMode.speed)
        assertFalse(exercise.recordingMode.heartRate)
        assertFalse(exercise.recordingMode.cadence)
        assertFalse(exercise.recordingMode.power)
        assertFalse(exercise.recordingMode.temperature)
        assertTrue(exercise.recordingMode.location)
        assertFalse(exercise.recordingMode.intervalExercise)

        // Check exercise time and duration
        def calDate = Calendar.getInstance ()
        calDate.set (2010, 8-1, 10, 17, 27, 47)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals (((47*60) + 6) *10, exercise.duration)

        // check altitude data
        assertEquals(236, exercise.altitude.altitudeMin)
        assertEquals(269, exercise.altitude.altitudeAVG)
        assertEquals(315, exercise.altitude.altitudeMax)
        assertEquals(245, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(39.39968f, exercise.speed.speedMax)
        assertEquals(16489, exercise.speed.distance)
        assertEquals(21.005095f, exercise.speed.speedAVG)

        // check sample data
        assertEquals(199, exercise.sampleList.size())

        assertEquals(0, exercise.sampleList[0].timestamp)
        assertEquals(51.05423620d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(13.83243080d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals(236, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals(0, exercise.sampleList[0].heartRate)
        assertEquals(0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(77000, exercise.sampleList[10].timestamp)
        assertEquals(51.05160000d, exercise.sampleList[10].position.latitude, 0.00001d)
        assertEquals(13.82978000d, exercise.sampleList[10].position.longitude, 0.00001d)
        assertEquals(255, exercise.sampleList[10].altitude)
        assertEquals(22.806398f, exercise.sampleList[10].speed)

        assertEquals(2826000, exercise.sampleList[198].timestamp)
        assertEquals(51.01730000d, exercise.sampleList[198].position.latitude, 0.00001d)
        assertEquals(13.95372000d, exercise.sampleList[198].position.longitude, 0.00001d)
        assertEquals(243, exercise.sampleList[198].altitude)
        assertEquals(21.255596f, exercise.sampleList[198].speed)
    }

    /**
     * This test parses a GPX file for a bike tour created by Garmin Oregon with heart rate monitor.
     * It contains track (location), time, altitude and heart rate data.
     */
    void testGpxGarminOregonHeartRateBikeTour () {
        def exercise = parser.parseExercise ('misc/testdata/gpx/bike-tour-garmin-oregon-with-heartrate.gpx')

        // check basic exercise data
        assertEquals (EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals (EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.altitude)
        assertTrue(exercise.recordingMode.speed)
        assertTrue(exercise.recordingMode.heartRate)
        assertFalse(exercise.recordingMode.cadence)
        assertFalse(exercise.recordingMode.power)
        assertFalse(exercise.recordingMode.temperature)
        assertTrue(exercise.recordingMode.location)
        assertFalse(exercise.recordingMode.intervalExercise)

        // Check exercise time and duration
        def calDate = Calendar.getInstance ()
        calDate.set (2012, 2-1, 16, 06, 37, 07)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals (610, exercise.duration)

        // check altitude data
        assertEquals(41, exercise.altitude.altitudeMin)
        assertEquals(48, exercise.altitude.altitudeAVG)
        assertEquals(52, exercise.altitude.altitudeMax)
        assertEquals(17, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(31.854105f, exercise.speed.speedMax)
        assertEquals(377, exercise.speed.distance)
        assertEquals(22.249182f, exercise.speed.speedAVG)

        // check heartRate data
        assertEquals(136, exercise.heartRateAVG)
        assertEquals(144, exercise.heartRateMax)

        // check sample data
        assertEquals(18, exercise.sampleList.size())

        assertEquals(0, exercise.sampleList[0].timestamp)
        assertEquals(48.174979d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(8.124356d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals(41, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals(103, exercise.sampleList[0].heartRate)
        assertEquals(0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(48000, exercise.sampleList[10].timestamp)
        assertEquals(48.176585d, exercise.sampleList[10].position.latitude, 0.00001d)
        assertEquals(8.120153d, exercise.sampleList[10].position.longitude, 0.00001d)
        assertEquals(50, exercise.sampleList[10].altitude)
        assertEquals(9.1799345f, exercise.sampleList[10].speed)
        assertEquals(144, exercise.sampleList[10].heartRate)

        assertEquals(61000, exercise.sampleList[17].timestamp)
        assertEquals(48.176650d, exercise.sampleList[17].position.latitude, 0.00001d)
        assertEquals(8.120041d, exercise.sampleList[17].position.longitude, 0.00001d)
        assertEquals(52, exercise.sampleList[17].altitude)
        assertEquals(7.833878f, exercise.sampleList[17].speed)
        assertEquals(142, exercise.sampleList[17].heartRate)
    }

    /**
     * This test parses a GPX file for a bike tour created by Holux FunTrek 130 pro with heart rate monitor.
     * It contains track (location), time, altitude and heart rate data.
     * Note that first version of the test file is derived from a Garmin Oregon track file, which has been
     * manually patched to be aligned with the example shown in feature request 3432983
     */
    void testGpxHoluxFunTrek130ProHeartRateBikeTour () {
        def exercise = parser.parseExercise ('misc/testdata/gpx/bike-tour-holux-funtrek-130-pro-with-heartrate.gpx')

        // check basic exercise data
        assertEquals (EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals (EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.altitude)
        assertTrue(exercise.recordingMode.speed)
        assertTrue(exercise.recordingMode.heartRate)
        assertFalse(exercise.recordingMode.cadence)
        assertFalse(exercise.recordingMode.power)
        assertFalse(exercise.recordingMode.temperature)
        assertTrue(exercise.recordingMode.location)
        assertFalse(exercise.recordingMode.intervalExercise)

        // Check exercise time and duration
        def calDate = Calendar.getInstance ()
        calDate.set (2011, 11-1, 3, 9, 9, 25)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals (23440, exercise.duration)

        // check altitude data
        assertEquals(99, exercise.altitude.altitudeMin)
        assertEquals(142, exercise.altitude.altitudeAVG)
        assertEquals(226, exercise.altitude.altitudeMax)
        assertEquals(382, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(52.30528f, exercise.speed.speedMax)
        assertEquals(13990, exercise.speed.distance)
        assertEquals(21.486347f, exercise.speed.speedAVG)

        // check heartRate data
        assertEquals(153, exercise.heartRateAVG)
        assertEquals(184, exercise.heartRateMax)

        // check sample data
        assertEquals(2345, exercise.sampleList.size())

        assertEquals(0, exercise.sampleList[0].timestamp)
        assertEquals(49.990125d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(36.34850472d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals(224, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals(81, exercise.sampleList[0].heartRate)
        assertEquals(0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(1000000, exercise.sampleList[1000].timestamp)
        assertEquals(50.00348667d, exercise.sampleList[1000].position.latitude, 0.00001d)
        assertEquals(36.26712500d, exercise.sampleList[1000].position.longitude, 0.00001d)
        assertEquals(100, exercise.sampleList[1000].altitude)
        assertEquals(25.760117f, exercise.sampleList[1000].speed)
        assertEquals(168, exercise.sampleList[1000].heartRate)

        assertEquals(2344000, exercise.sampleList[2344].timestamp)
        assertEquals(50.03874833d, exercise.sampleList[2344].position.latitude, 0.00001d)
        assertEquals(36.22104139d, exercise.sampleList[2344].position.longitude, 0.00001d)
        assertEquals(149, exercise.sampleList[2344].altitude)
        assertEquals(1.2139143f, exercise.sampleList[2344].speed)
        assertEquals(114, exercise.sampleList[2344].heartRate)
    }

    /**
     * This test parses a GPX file, which contains just the track (location) data.
     */
    void testGpxBikeTourLocationDataOnly () {

        def exercise = parser.parseExercise ('misc/testdata/gpx/bike-tour-track_only.gpx')

        // check basic exercise data
        assertEquals (EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals (EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertFalse(exercise.recordingMode.altitude)
        assertFalse(exercise.recordingMode.heartRate)
        assertFalse(exercise.recordingMode.speed)
        assertFalse(exercise.recordingMode.cadence)
        assertFalse(exercise.recordingMode.power)
        assertFalse(exercise.recordingMode.temperature)
        assertTrue(exercise.recordingMode.location)
        assertFalse(exercise.recordingMode.intervalExercise)

        // check exercise time and duration
        assertNull (exercise.date)
        assertEquals (0, exercise.duration)
        assertNull(exercise.altitude)
        
        // check sample data
        assertEquals(142, exercise.sampleList.size())

        assertEquals(0, exercise.sampleList[0].timestamp)
        assertEquals(51.41659034d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(14.94992411d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals(0, exercise.sampleList[0].heartRate)
        assertEquals(0, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals(0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(0, exercise.sampleList[10].timestamp)
        assertEquals(51.4178715d, exercise.sampleList[10].position.latitude, 0.00001d)
        assertEquals(14.94420463d, exercise.sampleList[10].position.longitude, 0.00001d)
        assertEquals(0, exercise.sampleList[10].altitude)

        assertEquals(0, exercise.sampleList[141].timestamp)
        assertEquals(51.32114d, exercise.sampleList[141].position.latitude, 0.00001d)
        assertEquals(14.99391d, exercise.sampleList[141].position.longitude, 0.00001d)
        assertEquals(0, exercise.sampleList[141].altitude)
    }
}
