package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser
import org.junit.Assert.*
import org.junit.Test

import java.time.LocalDateTime

/**
 * This class contains all unit tests for the PolarRS200SDParser class.
 *
 * @author Stefan Saring
 */
class PolarRS200SDParserTest {

    /** Instance to be tested. */
    private val parser: ExerciseParser = PolarRS200SDParser()

    /**
     * This method must fail on parsing an exercise file which doesn't exists.
     */
    @Test(expected = EVException::class)
    fun testParseExerciseMissingFile() {
        parser.parseExercise("misc/testdata/rs200sd-sample-123.xml")
    }

    /**
     * This test method parses an RS200SD exercise file with speed data.
     */
    @Test
    fun testParseExerciseWithSpeedData() {

        // parse exercise file
        val exercise = parser.parseExercise("misc/testdata/rs200sd-sample.xml")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.RS200SDRAW, exercise.fileType)
        assertEquals("Polar RS200", exercise.deviceName)

        assertEquals(LocalDateTime.of(2006, 2, 20, 16, 54, 11), exercise.dateTime);
        assertEquals(28 * 60 * 10 + 51 * 10 + 6, exercise.duration)

        // heart rates
        assertEquals(161.toShort(), exercise.heartRateAVG)
        assertEquals(181.toShort(), exercise.heartRateMax)

        // energy
        assertEquals(404, exercise.energy)

        // heartrate limits
        assertEquals(5, exercise.heartRateLimits.size)
        assertEquals(100.toShort(), exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals(118.toShort(), exercise.heartRateLimits[0].upperHeartRate)
        assertEquals(25, exercise.heartRateLimits[0].timeWithin)
        assertEquals(140.toShort(), exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals(158.toShort(), exercise.heartRateLimits[2].upperHeartRate)
        assertEquals(485, exercise.heartRateLimits[2].timeWithin)

        // distance & speed & odometer
        assertEquals(5680, exercise.speed.distance)
        assertEquals(13745, (exercise.speed.speedMax * 1000).toInt())
        assertEquals(11811, (exercise.speed.speedAVG * 1000).toInt())

        // lap data
        assertEquals(6, exercise.lapList.size)

        assertEquals(173.toShort(), exercise.lapList[0].heartRateSplit)
        assertEquals(151.toShort(), exercise.lapList[0].heartRateAVG)
        assertEquals(173.toShort(), exercise.lapList[0].heartRateMax)
        assertEquals(1000, exercise.lapList[0].speed!!.distance)
        assertEquals(11428, (exercise.lapList[0].speed!!.speedAVG * 1000).toInt())
        assertEquals(12756, (exercise.lapList[0].speed!!.speedEnd * 1000).toInt())

        assertEquals(165.toShort(), exercise.lapList[2].heartRateSplit)
        assertEquals(165.toShort(), exercise.lapList[2].heartRateAVG)
        assertEquals(174.toShort(), exercise.lapList[2].heartRateMax)
        assertEquals(3000, exercise.lapList[2].speed!!.distance)
        assertEquals(11842, (exercise.lapList[2].speed!!.speedAVG * 1000).toInt())
        assertEquals(13249, (exercise.lapList[2].speed!!.speedEnd * 1000).toInt())

        assertEquals(181.toShort(), exercise.lapList[5].heartRateSplit)
        assertEquals(172.toShort(), exercise.lapList[5].heartRateAVG)
        assertEquals(181.toShort(), exercise.lapList[5].heartRateMax)
        assertEquals(5680, exercise.lapList[5].speed!!.distance)
        assertEquals(11780, (exercise.lapList[5].speed!!.speedAVG * 1000).toInt())
        assertEquals(12829, (exercise.lapList[5].speed!!.speedEnd * 1000).toInt())
    }
}
