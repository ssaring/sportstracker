package de.saring.exerciseviewer.parser.impl;

import groovy.transform.TypeChecked;
import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.ExerciseParser

/**
 * Unit tests of class PolarPedParser.
 *
 * @author Philippe Marzouk
 */
@TypeChecked
public class PolarPedParserTest extends GroovyTestCase {

    /** Instance to be tested. */
    private PolarPedParser parser;

    public void setUp() {
        parser = new PolarPedParser();
    }

     /**
     * This method must fail on parsing an exercise file which doesn't exists.
     */
    public void testParsePedExerciseMissingFile() throws Exception {
        shouldFail (EVException) {
            parser.parseExercise("missing-file.ped")
        }
    }

    /**
     * This method must fail if parseExercise is too many times for the number of exercises in the file
     */
    public void testPedtooManyCalls() throws Exception {
        def filename = 'misc/testdata/polarpersonaltrainer/polar-ped-sample.ped'
        def exercise = parser.parseExercise(filename)
        def exerciseCount = parser.exerciseCount;
        shouldFail (EVException) {
            exercise = parser.parseExercise(filename, exerciseCount)
        }
    }

    /**
     * Test of readPedFile method, of class PolarPedParser.
     * @throws Exception
     */
    public void testReadPedFile() throws Exception {
        def filename = 'misc/testdata/polarpersonaltrainer/polar-ped-sample.ped'
        def exercise = parser.parseExercise(filename)

        //assertEquals(2, parser.getExerciseCount())

        assertEquals(EVExercise.ExerciseFileType.PED, exercise.fileType)

        def calDate = Calendar.getInstance ()
        calDate.set (2010, 4-1, 2, 19, 19, 00)
        assertEquals ((int) (calDate.getTime().getTime() / 1000), (int) (exercise.date.time / 1000))

        assertEquals(31560, exercise.duration)
        assertEquals(12800, exercise.speed.distance)
        assertEquals(14600, (int) (exercise.speed.speedAVG * 1000))

        assertEquals(367, exercise.energy)

        assertEquals((short) 127, exercise.heartRateAVG)
        assertEquals((short) 141, exercise.heartRateMax)

    }
}
