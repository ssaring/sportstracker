package de.saring.exerciseviewer.parser.impl

import groovy.transform.TypeChecked;
import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser

/**
 * This class contains all unit tests for the TopoGrafixGpxParser class.
 *
 * @author Stefan Saring
 */
@TypeChecked
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
        assertEquals ((int) (calDate.getTime().getTime() / 1000), (int) (exercise.date.time / 1000))
        assertEquals (((47*60) + 6) *10, exercise.duration)

        // check altitude data
        assertEquals((short) 236, exercise.altitude.altitudeMin)
        assertEquals((short) 270, exercise.altitude.altitudeAVG)
        assertEquals((short) 315, exercise.altitude.altitudeMax)
        assertEquals(245, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(39.38832f, exercise.speed.speedMax)
        assertEquals(16484, exercise.speed.distance)
        assertEquals(20.998724f, exercise.speed.speedAVG)

        // check sample data
        assertEquals(199, exercise.sampleList.size())

        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(51.05423620d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(13.83243080d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals((short) 236, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals((short) 0, exercise.sampleList[0].heartRate)
        assertEquals((short) 0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(77000L, exercise.sampleList[10].timestamp)
        assertEquals(51.05160000d, exercise.sampleList[10].position.latitude, 0.00001d)
        assertEquals(13.82978000d, exercise.sampleList[10].position.longitude, 0.00001d)
        assertEquals((short) 255, exercise.sampleList[10].altitude)
        assertEquals(22.800179f, exercise.sampleList[10].speed)

        assertEquals(2826000L, exercise.sampleList[198].timestamp)
        assertEquals(51.01730000d, exercise.sampleList[198].position.latitude, 0.00001d)
        assertEquals(13.95372000d, exercise.sampleList[198].position.longitude, 0.00001d)
        assertEquals((short) 243, exercise.sampleList[198].altitude)
        assertEquals(21.250275f, exercise.sampleList[198].speed)
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
        calDate.set (2012, 3-1, 2, 6, 53, 51)
        assertEquals ((int) (calDate.getTime().getTime() / 1000), (int) (exercise.date.time / 1000))
        assertEquals (6090, exercise.duration)

        // check altitude data
        assertEquals((short) 32, exercise.altitude.altitudeMin)
        assertEquals((short) 41, exercise.altitude.altitudeAVG)
        assertEquals((short) 53, exercise.altitude.altitudeMax)
        assertEquals(41, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(37.978153f, exercise.speed.speedMax)
        assertEquals(4494, exercise.speed.distance)
        assertEquals(26.565517f, exercise.speed.speedAVG)

        // check heartRate data
        assertEquals((short) 152, exercise.heartRateAVG)
        assertEquals((short) 169, exercise.heartRateMax)

        // check sample data
        assertEquals(204, exercise.sampleList.size())

        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(50.823521d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(4.672067d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals((short) 52, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals((short) 158, exercise.sampleList[0].heartRate)
        assertEquals((short) 0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(303000L, exercise.sampleList[101].timestamp)
        assertEquals(50.816284d, exercise.sampleList[101].position.latitude, 0.00001d)
        assertEquals(4.65173d, exercise.sampleList[101].position.longitude, 0.00001d)
        assertEquals((short) 45, exercise.sampleList[101].altitude)
        assertEquals(36.26108f, exercise.sampleList[101].speed)
        assertEquals((short) 156, exercise.sampleList[101].heartRate)

        assertEquals(609000L, exercise.sampleList[203].timestamp)
        assertEquals(50.802288d, exercise.sampleList[203].position.latitude, 0.00001d)
        assertEquals(4.639261d, exercise.sampleList[203].position.longitude, 0.00001d)
        assertEquals((short) 33, exercise.sampleList[203].altitude)
        assertEquals(32.644943f, exercise.sampleList[203].speed)
        assertEquals((short) 154, exercise.sampleList[203].heartRate)
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
        assertEquals ((int) (calDate.getTime().getTime() / 1000), (int) (exercise.date.time / 1000))
        assertEquals (1780, exercise.duration)

        // check altitude data
        assertEquals((short) 212, exercise.altitude.altitudeMin)
        assertEquals((short) 221, exercise.altitude.altitudeAVG)
        assertEquals((short) 226, exercise.altitude.altitudeMax)
        assertEquals(22, exercise.altitude.ascent)

        // check speed summary data
        assertEquals(39.3484f, exercise.speed.speedMax)
        assertEquals(1055, exercise.speed.distance)
        assertEquals(21.337078f, exercise.speed.speedAVG)

        // check heartRate data
        assertEquals((short) 140, exercise.heartRateAVG)
        assertEquals((short) 166, exercise.heartRateMax)

        // check sample data
        assertEquals(179, exercise.sampleList.size())

        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(49.990125d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(36.34850472d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals((short) 224, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals((short) 81, exercise.sampleList[0].heartRate)
        assertEquals((short) 0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(90000L, exercise.sampleList[90].timestamp)
        assertEquals(49.98855833d, exercise.sampleList[90].position.latitude, 0.00001d)
        assertEquals(36.34222333d, exercise.sampleList[90].position.longitude, 0.00001d)
        assertEquals((short) 220, exercise.sampleList[90].altitude)
        assertEquals(25.607729f, exercise.sampleList[90].speed)
        assertEquals((short) 157, exercise.sampleList[90].heartRate)

        assertEquals(178000L, exercise.sampleList[178].timestamp)
        assertEquals(49.98728833d, exercise.sampleList[178].position.latitude, 0.00001d)
        assertEquals(36.33563333d, exercise.sampleList[178].position.longitude, 0.00001d)
        assertEquals((short) 212, exercise.sampleList[178].altitude)
        assertEquals(38.86589f, exercise.sampleList[178].speed)
        assertEquals((short) 165, exercise.sampleList[178].heartRate)
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

        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(51.41659034d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(14.94992411d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals((short) 0, exercise.sampleList[0].heartRate)
        assertEquals((short) 0, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals((short) 0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)

        assertEquals(0L, exercise.sampleList[10].timestamp)
        assertEquals(51.4178715d, exercise.sampleList[10].position.latitude, 0.00001d)
        assertEquals(14.94420463d, exercise.sampleList[10].position.longitude, 0.00001d)
        assertEquals((short) 0, exercise.sampleList[10].altitude)

        assertEquals(0L, exercise.sampleList[141].timestamp)
        assertEquals(51.32114d, exercise.sampleList[141].position.latitude, 0.00001d)
        assertEquals(14.99391d, exercise.sampleList[141].position.longitude, 0.00001d)
        assertEquals((short) 0, exercise.sampleList[141].altitude)
    }
}
