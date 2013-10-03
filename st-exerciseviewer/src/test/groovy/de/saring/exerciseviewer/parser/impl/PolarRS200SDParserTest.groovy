package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser
import groovy.transform.TypeChecked

/**
 * This class contains all unit tests for the PolarRS200SDParser class.
 *
 * @author Stefan Saring
 */
@TypeChecked
class PolarRS200SDParserTest extends GroovyTestCase {

    /** Instance to be tested. */
    private ExerciseParser parser

    /**
     * This method initializes the environment for testing.
     */
    void setUp() {
        parser = new PolarRS200SDParser()
    }

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    void testParseExerciseMissingFile() {
        shouldFail(EVException) {
            parser.parseExercise('misc/testdata/rs200sd-sample-123.xml')
        }
    }

    /**
     * This test method parses an RS200SD exercise file with speed data.
     */
    void testParseExerciseWithSpeedData() {

        // parse exercise file
        def exercise = parser.parseExercise('misc/testdata/rs200sd-sample.xml')

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.RS200SDRAW, exercise.fileType)

        def calDate = Calendar.getInstance()
        calDate.set(2006, 2 - 1, 20, 16, 54, 11)
        assertEquals((int) (calDate.getTime().getTime() / 1000), (int) (exercise.date.time / 1000))
        assertEquals(28 * 60 * 10 + 51 * 10 + 5, exercise.duration)

        // heart rates
        assertEquals((short) 161, exercise.heartRateAVG)
        assertEquals((short) 181, exercise.heartRateMax)

        // energy
        assertEquals(404, exercise.energy)

        // heartrate limits
        assertEquals(5, exercise.heartRateLimits.size())
        assertEquals((short) 100, exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals((short) 118, exercise.heartRateLimits[0].upperHeartRate)
        assertEquals(25, exercise.heartRateLimits[0].timeWithin)
        assertEquals((short) 140, exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals((short) 158, exercise.heartRateLimits[2].upperHeartRate)
        assertEquals(485, exercise.heartRateLimits[2].timeWithin)

        // distance & speed & odometer
        assertEquals(5680, exercise.speed.distance)
        assertEquals(13745, (int) (exercise.speed.speedMax * 1000))
        assertEquals(11811, (int) (exercise.speed.speedAVG * 1000))

        // lap data
        assertEquals(6, exercise.lapList.size())

        assertEquals((short) 173, exercise.lapList[0].heartRateSplit)
        assertEquals((short) 151, exercise.lapList[0].heartRateAVG)
        assertEquals((short) 173, exercise.lapList[0].heartRateMax)
        assertEquals(1000, exercise.lapList[0].speed.distance)
        assertEquals(11428, (int) (exercise.lapList[0].speed.speedAVG * 1000))
        assertEquals(12756, (int) (exercise.lapList[0].speed.speedEnd * 1000))

        assertEquals((short) 165, exercise.lapList[2].heartRateSplit)
        assertEquals((short) 165, exercise.lapList[2].heartRateAVG)
        assertEquals((short) 174, exercise.lapList[2].heartRateMax)
        assertEquals(3000, exercise.lapList[2].speed.distance)
        assertEquals(11842, (int) (exercise.lapList[2].speed.speedAVG * 1000))
        assertEquals(13249, (int) (exercise.lapList[2].speed.speedEnd * 1000))

        assertEquals((short) 181, exercise.lapList[5].heartRateSplit)
        assertEquals((short) 172, exercise.lapList[5].heartRateAVG)
        assertEquals((short) 181, exercise.lapList[5].heartRateMax)
        assertEquals(5680, exercise.lapList[5].speed.distance)
        assertEquals(11780, (int) (exercise.lapList[5].speed.speedAVG * 1000))
        assertEquals(12829, (int) (exercise.lapList[5].speed.speedEnd * 1000))
    }
}
