package de.saring.polarviewer.parser.impl

import de.saring.polarviewer.core.PVException
import de.saring.polarviewer.data.PVExercise
import de.saring.polarviewer.parser.ExerciseParser

/**
 * This class contains all unit tests for the SmartsyncCSVParser class.
 *
 * @author Kai Pastor
 */
class SmartsyncCSVParserTest extends GroovyTestCase {
    
    /** Instance to be tested. */
    private ExerciseParser parser
    
    /**
     * This method initializes the environment for testing.
     */
    void setUp () {
        parser = new SmartsyncCSVParser ()
    }
    
    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    void testParseExerciseMissingFile () {
        shouldFail (PVException) {
            parser.parseExercise ('misc/testdata/sample-123.csv')
        }
    }
    
    /**
     * This method tests the parser by with an cycling exercise file
     * recorded in metric units.
     */
    void testParseExercise () {
        
        // parse exercise file
        def exercise = parser.parseExercise ('misc/testdata/smartsync-sample.csv')
        
        // check exercise data
        assertEquals (PVExercise.ExerciseFileType.SSCSV, exercise.fileType)
        assertEquals (0, exercise.userID)
        assertFalse (exercise.recordingMode.altitude)
        assertFalse (exercise.recordingMode.speed)
        assertFalse (exercise.recordingMode.cadence)
        assertFalse (exercise.recordingMode.power)        
        
        def calDate = Calendar.getInstance ()
        calDate.set (2008, 4-1, 19, 12, 45, 19)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals ((294 - 1) * 2 * 10, exercise.duration)
        assertEquals (2, exercise.recordingInterval)

        assertEquals (147, exercise.heartRateAVG)
        assertEquals (180, exercise.heartRateMax)

        // check lap data (first, one from middle and last lap only)
        assertEquals (0, exercise.lapList.size ())
        
        // check sample data (first, two from middle and last only)
        assertEquals (294, exercise.sampleList.size ())
        assertEquals (71, exercise.sampleList[0].heartRate)
        assertEquals (152, exercise.sampleList[100].heartRate)
        assertEquals (156, exercise.sampleList[200].heartRate)
        assertEquals (163, exercise.sampleList[293].heartRate)
    }
}
