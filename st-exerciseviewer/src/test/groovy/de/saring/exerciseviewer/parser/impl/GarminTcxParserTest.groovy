package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser

/**
 * This class contains all unit tests for the GarminTcxParser class.
 *
 * @author Stefan Saring
 */
class GarminTcxParserTest extends GroovyTestCase {
    
    /** Instance to be tested. */
    private ExerciseParser parser
    
    /**
     * This method initializes the environment for testing.
     */
    void setUp () {
        parser = new GarminTcxParser ()
    }
    
    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    void testParseExerciseMissingFile () {
        shouldFail (EVException) {
            parser.parseExercise ('misc/testdata/garmin-tcx/unknown-file.tcx')
        }
    }
    
    /**
     * This test parses a TCX file from a Garmin Forerunner 305 (Running, no heartrate data, 1 lap).
     */
    void testForerunner305_Running_NoHeartrate_1Lap () {        
        
        def exercise = parser.parseExercise ('misc/testdata/garmin-tcx/Forerunner305-Running-NoHeartrate-1Lap.tcx')
        assertEquals (EVExercise.ExerciseFileType.GARMIN_TCX, exercise.fileType)
        assertEquals (EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.altitude)
        assertTrue(exercise.recordingMode.speed)
        assertFalse(exercise.recordingMode.cadence)
        assertTrue(exercise.recordingMode.location)
        
        def calDate = Calendar.getInstance ()        
        calDate.set (2007, 8-1, 7, 2, 42, 41)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals ((39*60 + 5) * 10, exercise.duration)            

        // heart rates
        assertEquals (0, exercise.heartRateAVG)
        assertEquals (0, exercise.heartRateMax)
        assertEquals (285, exercise.energy)

        // heartrate limits not available in TCX files
        assertNull (exercise.heartRateLimits)

        // distance & speed & odometer
        assertEquals (8349, exercise.speed.distance)
	    assertEquals (72.284d, exercise.speed.speedMax, 0.001d)
	    assertEquals (12.817d, exercise.speed.speedAVG, 0.001d)

	    // altitude
        assertEquals (-4, exercise.altitude.altitudeMin)
        assertEquals (8, exercise.altitude.altitudeAVG)
        assertEquals (21, exercise.altitude.altitudeMax)
        assertEquals (149, exercise.altitude.ascent)
        
        // cadence
        assertNull (exercise.cadence)
        
        // lap data
        assertEquals  (1, exercise.lapList.size())

	    assertEquals ((39*60 + 5) * 10, exercise.lapList[0].timeSplit)        
        assertEquals (0, exercise.lapList[0].heartRateSplit)        
        assertEquals (0, exercise.lapList[0].heartRateAVG)
        assertEquals (0, exercise.lapList[0].heartRateMax)
        assertEquals (8349, exercise.lapList[0].speed.distance)
	    assertEquals (12.8164d, exercise.lapList[0].speed.speedAVG, 0.001d)
        assertEquals (0, exercise.lapList[0].speed.speedEnd)        
        assertEquals (0, exercise.lapList[0].speed.cadence)        
        assertEquals (10, exercise.lapList[0].altitude.altitude)
        assertEquals (149, exercise.lapList[0].altitude.ascent)
		assertEquals (37.8765614d, exercise.lapList[0].positionSplit.latitude, 0.001d)
		assertEquals (-122.4601646d, exercise.lapList[0].positionSplit.longitude, 0.001d)
		
        // sample data
        assertEquals(379, exercise.sampleList.size())
        
        assertEquals(0*1000, exercise.sampleList[0].timestamp)
        assertEquals(37.8959665d, exercise.sampleList[0].position.latitude, 0.001d)
        assertEquals(-122.4896709d, exercise.sampleList[0].position.longitude, 0.001d)
        assertEquals(0, exercise.sampleList[0].heartRate)
        assertEquals(4, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals(0, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)
        
        assertEquals(129*1000, exercise.sampleList[20].timestamp)
        assertEquals(37.8943428d, exercise.sampleList[20].position.latitude, 0.001d)
        assertEquals(-122.4870681d, exercise.sampleList[20].position.longitude, 0.001d)
        assertEquals(0, exercise.sampleList[20].heartRate)
        assertEquals(6, exercise.sampleList[20].altitude)
        assertEquals(5.9811f, exercise.sampleList[20].speed, 0.001f)
        assertEquals(0, exercise.sampleList[20].cadence)
        assertEquals(301, exercise.sampleList[20].distance)

        assertEquals(((39*60) + 5) * 1000, exercise.sampleList[378].timestamp)
        assertEquals(37.8765614d, exercise.sampleList[378].position.latitude, 0.001d)
        assertEquals(-122.4601646d, exercise.sampleList[378].position.longitude, 0.001d)
        assertEquals(0, exercise.sampleList[378].heartRate)
        assertEquals(10, exercise.sampleList[378].altitude)
        // speed must be 0 (corrected, because distance is decreased in the last sample)
        assertEquals(0, exercise.sampleList[378].speed, 0.001f)
        assertEquals(0, exercise.sampleList[378].cadence)
        assertEquals(8349, exercise.sampleList[378].distance)        
    }
    
    /**
     * This test parses a TCX file from a Garmin Edge 705 (Running, heartrate data, 2 laps).
     */
    void testEdge705_Running_Heartrate_2Laps () {
        
        def exercise = parser.parseExercise ('misc/testdata/garmin-tcx/Edge705-Running-Heartrate-2Laps.tcx')        
        assertEquals (EVExercise.ExerciseFileType.GARMIN_TCX, exercise.fileType)
        assertEquals (EVExercise.DYNAMIC_RECORDING_INTERVAL, exercise.recordingInterval)
        assertTrue(exercise.recordingMode.altitude)
        assertTrue(exercise.recordingMode.speed)
        assertTrue(exercise.recordingMode.cadence)
        assertTrue(exercise.recordingMode.location)
        
        def calDate = Calendar.getInstance ()        
        calDate.set (2009, 12-1, 9, 6, 54, 25)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals (6086 * 10, exercise.duration)            
        
        // heart rates
        assertEquals (157, exercise.heartRateAVG) 
        assertEquals (173, exercise.heartRateMax)
        assertEquals (2251, exercise.energy)
        
        // heartrate limits not available in TCX files
        assertNull (exercise.heartRateLimits)
        
        // distance & speed & odometer
        assertEquals (18990, exercise.speed.distance)
        assertEquals (164.425d, exercise.speed.speedMax, 0.001d)
        assertEquals (11.232d, exercise.speed.speedAVG, 0.001d)
        
        // altitude
        assertEquals (94, exercise.altitude.altitudeMin)
        assertEquals (115, exercise.altitude.altitudeAVG)
        assertEquals (153, exercise.altitude.altitudeMax)
        assertEquals (388, exercise.altitude.ascent)

        // cadence
        assertEquals(88, exercise.cadence.cadenceAVG)
        assertEquals(90, exercise.cadence.cadenceMax)

        // lap data
        assertEquals  (2, exercise.lapList.size())
        
        assertEquals ((56*60 + 20) * 10, exercise.lapList[0].timeSplit)        
        assertEquals (168, exercise.lapList[0].heartRateSplit)        
        assertEquals (158, exercise.lapList[0].heartRateAVG)
        assertEquals (173, exercise.lapList[0].heartRateMax)
        assertEquals (10618, exercise.lapList[0].speed.distance)
        assertEquals (11.310d, exercise.lapList[0].speed.speedAVG, 0.001d)
        assertEquals (135, exercise.lapList[0].altitude.altitude)
        assertEquals (213, exercise.lapList[0].altitude.ascent)
        assertEquals (9.009, exercise.lapList[0].speed.speedEnd, 0.001d)  
        assertEquals (86, exercise.lapList[0].speed.cadence)  
		assertEquals (51.030515d, exercise.lapList[0].positionSplit.latitude, 0.001d)
		assertEquals (13.730152d, exercise.lapList[0].positionSplit.longitude, 0.001d)

        assertEquals (((56*60 + 20) + (45*60 + 6)) * 10, exercise.lapList[1].timeSplit)        
        assertEquals (160, exercise.lapList[1].heartRateSplit)        
        assertEquals (155, exercise.lapList[1].heartRateAVG)
        assertEquals (166, exercise.lapList[1].heartRateMax)
        assertEquals (10618 + 8372, exercise.lapList[1].speed.distance)
        assertEquals (11.138d, exercise.lapList[1].speed.speedAVG, 0.001d)
        assertEquals (12.568, exercise.lapList[1].speed.speedEnd, 0.01)  
        assertEquals (112, exercise.lapList[1].altitude.altitude)
        assertEquals (175, exercise.lapList[1].altitude.ascent)
        assertEquals (0, exercise.lapList[1].speed.cadence)  
		assertEquals (51.045960d, exercise.lapList[1].positionSplit.latitude, 0.001d)
		assertEquals (13.809391d, exercise.lapList[1].positionSplit.longitude, 0.001d)

        // sample data
        assertEquals(1254, exercise.sampleList.size())
        
        assertEquals(1*1000, exercise.sampleList[0].timestamp)
        assertEquals(51.045824d, exercise.sampleList[0].position.latitude, 0.001d)
        assertEquals(13.809552d, exercise.sampleList[0].position.longitude, 0.001d)        
        assertEquals(81, exercise.sampleList[0].heartRate)
        assertEquals(110, exercise.sampleList[0].altitude)
        assertEquals(0, exercise.sampleList[0].speed)
        assertEquals(90, exercise.sampleList[0].cadence)
        assertEquals(0, exercise.sampleList[0].distance)
        
        assertEquals(72*1000, exercise.sampleList[20].timestamp)
        assertEquals(51.044963d, exercise.sampleList[20].position.latitude, 0.001d)
        assertEquals(13.807715d, exercise.sampleList[20].position.longitude, 0.001d)        
        assertEquals(121, exercise.sampleList[20].heartRate)
        assertEquals(108, exercise.sampleList[20].altitude)
        assertEquals(13.739f, exercise.sampleList[20].speed, 0.001f)
        assertEquals(88, exercise.sampleList[20].cadence)
        assertEquals(168, exercise.sampleList[20].distance)

        // last sample of last lap
        assertEquals(6086*1000, exercise.sampleList[1253].timestamp) 
        assertEquals(51.045960d, exercise.sampleList[1253].position.latitude, 0.001d)
        assertEquals(13.809391d, exercise.sampleList[1253].position.longitude, 0.001d)        
        assertEquals(160, exercise.sampleList[1253].heartRate)
        assertEquals(112, exercise.sampleList[1253].altitude)
        assertEquals(12.5676f, exercise.sampleList[1253].speed, 0.001f)
        assertEquals(0, exercise.sampleList[1253].cadence)
        assertEquals(18990, exercise.sampleList[1253].distance)
    }
}