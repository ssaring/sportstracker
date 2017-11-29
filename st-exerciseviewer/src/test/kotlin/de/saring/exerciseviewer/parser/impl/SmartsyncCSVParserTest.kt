package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser

import org.junit.Assert.*
import org.junit.Test

import java.time.LocalDateTime

/**
 * This class contains all unit tests for the SmartsyncCSVParser class.
 *
 * @author Stefan Saring (Kotlin port)
 * @author Kai Pastor
 */
class SmartsyncCSVParserTest {

    /** Instance to be tested. */
    private val parser: ExerciseParser = SmartsyncCSVParser()

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test(expected = EVException::class)
    fun testParseExerciseMissingFile() {
        parser.parseExercise("misc/testdata/sample-123.csv")
    }

    /**
     * This test parses a valid Smartsync CSV file which contains heartrate data only.
     */
    @Test
    fun testParseSuccess() {

        val exercise = parser.parseExercise("misc/testdata/smartsync-sample.csv")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.SSCSV, exercise.fileType)
        assertEquals("Oregon Scientific Smartsync", exercise.deviceName)

        assertTrue(exercise.recordingMode.isHeartRate)
        assertFalse(exercise.recordingMode.isAltitude)
        assertFalse(exercise.recordingMode.isSpeed)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isPower)
        assertEquals(LocalDateTime.of(2008, 4, 19, 12, 45, 19), exercise.dateTime);
        assertEquals((294 - 1) * 2 * 10, exercise.duration)
        assertEquals(2.toShort(), exercise.recordingInterval)

        assertEquals(147.toShort(), exercise.heartRateAVG)
        assertEquals(180.toShort(), exercise.heartRateMax)

        // check lap data (first, one from middle and last lap only)
        assertEquals(0, exercise.lapList.size)

        // check sample data (first, two from middle and last only)
        assertEquals(294, exercise.sampleList.size)
        assertEquals(0L, exercise.sampleList[0].timestamp)
        assertEquals(71.toShort(), exercise.sampleList[0].heartRate)

        assertEquals(100 * 2 * 1000L, exercise.sampleList[100].timestamp)
        assertEquals(152.toShort(), exercise.sampleList[100].heartRate)

        assertEquals(200 * 2 * 1000L, exercise.sampleList[200].timestamp)
        assertEquals(156.toShort(), exercise.sampleList[200].heartRate)

        assertEquals(293 * 2 * 1000L, exercise.sampleList[293].timestamp)
        assertEquals(163.toShort(), exercise.sampleList[293].heartRate)
    }
}
