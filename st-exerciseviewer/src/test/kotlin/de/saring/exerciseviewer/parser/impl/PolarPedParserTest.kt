package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise

import org.junit.Assert.*
import org.junit.Test

import java.time.LocalDateTime

/**
 * This class contains all unit tests for the PolarPedParser class.
 *
 * @author Stefan Saring (Kotlin port)
 * @author Philippe Marzouk
 */
class PolarPedParserTest {

    /** Instance to be tested. */
    private val parser = PolarPedParser()

    private val testExerciseFilename = "misc/testdata/polarpersonaltrainer/polar-ped-sample.ped"

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test(expected = EVException::class)
    fun testParseExerciseMissingFile() {
        parser.parseExercise("misc/testdata/missing-file.ped")
    }

    /**
     * This test parses a valid PED exercise file and returns the first exercise.
     */
    @Test
    fun testParseExercise() {
        val exercise = parser.parseExercise(testExerciseFilename)

        assertEquals(EVExercise.ExerciseFileType.PED, exercise.fileType)
        assertEquals("Polar PED", exercise.deviceName)
        assertEquals(LocalDateTime.of(2010, 4, 2, 19, 19, 0), exercise.dateTime)
        assertEquals(31560, exercise.duration)
        assertEquals(12800, exercise.speed.distance)
        assertEquals(14600, (exercise.speed.speedAvg * 1000).toInt())

        assertEquals(367, exercise.energy)

        assertEquals(127.toShort(), exercise.heartRateAVG)
        assertEquals(141.toShort(), exercise.heartRateMax)
    }
}
