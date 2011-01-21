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
     */
    void testGpxBikeTour () {        
        
        def exercise = parser.parseExercise ('misc/testdata/gpx/bike-tour-gpsies.gpx')
		
		// check basic exercise data
        assertEquals (EVExercise.ExerciseFileType.GPX, exercise.fileType)
        assertEquals (EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.altitude)
        assertFalse(exercise.recordingMode.speed)
        assertFalse(exercise.recordingMode.cadence)
        assertFalse(exercise.recordingMode.power)
        assertFalse(exercise.recordingMode.temperature)
        assertTrue(exercise.recordingMode.location)
		assertFalse(exercise.recordingMode.intervalExercise)
		
        def calDate = Calendar.getInstance ()        
        calDate.set (2010, 8-1, 10, 17, 27, 47)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals (0, exercise.duration)            

		// check altitude data
		assertEquals(236, exercise.altitude.altitudeMin)
		assertEquals(269, exercise.altitude.altitudeAVG)
		assertEquals(315, exercise.altitude.altitudeMax)
		assertEquals(245, exercise.altitude.ascent)
		
        // check sample data (contains only location data)
        assertEquals(199, exercise.sampleList.size())

        assertEquals(0, exercise.sampleList[0].timestamp)
        assertEquals(51.05423620d, exercise.sampleList[0].position.latitude, 0.00001d)
        assertEquals(13.83243080d, exercise.sampleList[0].position.longitude, 0.00001d)
        assertEquals(0, exercise.sampleList[0].heartRate)
        assertEquals(236, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals(0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)
		
		assertEquals(0, exercise.sampleList[10].timestamp)
		assertEquals(51.05160000d, exercise.sampleList[10].position.latitude, 0.00001d)
		assertEquals(13.82978000d, exercise.sampleList[10].position.longitude, 0.00001d)
		assertEquals(255, exercise.sampleList[10].altitude)
		
		assertEquals(0, exercise.sampleList[198].timestamp)
		assertEquals(51.01730000d, exercise.sampleList[198].position.latitude, 0.00001d)
		assertEquals(13.95372000d, exercise.sampleList[198].position.longitude, 0.00001d)
        assertEquals(243, exercise.sampleList[198].altitude)
    }
}