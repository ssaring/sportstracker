package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class contains all unit tests for the PolarF6RawParser class.
 * <p/>
 * Based on the PolarSRawParserTest by Stefan Saring.
 *
 * @author Roland Hostettler
 */
public class PolarF6RawParserTest {

    /**
     * Instance to be tested.
     */
    private AbstractExerciseParser parser;

    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp() throws Exception {
        parser = new PolarF6RawParser();
    }

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test
    public void testParseExerciseMissingFile() {
        try {
            parser.parseExercise("missing-file.frd");
            fail("Parse of the missing file must fail ...");
        } catch (EVException e) {
        }
    }

    /**
     * This method tests the parser with a Polar F6 raw exercise file.
     */
    @Test
    public void testParseF6() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/f6-test.frd");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.F6RAW);
        assertEquals(LocalDateTime.of(2007, 10, 28, 11, 7, 34), exercise.getDateTime());
        assertEquals("0", exercise.getType());
        assertEquals(false, exercise.getRecordingMode().isAltitude());
        assertEquals(false, exercise.getRecordingMode().isSpeed());
        assertEquals(false, exercise.getRecordingMode().isCadence());
        assertEquals(false, exercise.getRecordingMode().isPower());
        assertEquals((byte) 0, exercise.getRecordingMode().getBikeNumber());
        assertEquals((40 * 60 * 10) + 26 * 10, exercise.getDuration());
        assertEquals((short) 0, exercise.getRecordingInterval());
        assertEquals((short) 169, exercise.getHeartRateAVG());
        assertEquals((short) 178, exercise.getHeartRateMax());
        assertEquals(null, exercise.getSpeed());
        assertEquals(null, exercise.getCadence());
        assertEquals(null, exercise.getAltitude());
        assertEquals(null, exercise.getTemperature());
        assertEquals(595, exercise.getEnergy());
        assertEquals(54377, exercise.getEnergyTotal());
        assertEquals((65 * 60) + 9, exercise.getSumExerciseTime());
        assertEquals(0, exercise.getSumRideTime());
        assertEquals(0, exercise.getOdometer());

        // check heart rate limits
        assertEquals(4, exercise.getHeartRateLimits().length);//--
        assertEquals((short) 158, exercise.getHeartRateLimits()[0].getLowerHeartRate());
        assertEquals((short) 176, exercise.getHeartRateLimits()[0].getUpperHeartRate());
        assertEquals(true, exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(0, exercise.getHeartRateLimits()[0].getTimeBelow());
        assertEquals((36 * 60) + 13, exercise.getHeartRateLimits()[0].getTimeWithin());
        assertEquals(0, exercise.getHeartRateLimits()[0].getTimeAbove());

        assertEquals((short) 60, exercise.getHeartRateLimits()[1].getLowerHeartRate());
        assertEquals((short) 70, exercise.getHeartRateLimits()[1].getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(0, exercise.getHeartRateLimits()[1].getTimeBelow());
        assertEquals(40, exercise.getHeartRateLimits()[1].getTimeWithin());
        assertEquals(0, exercise.getHeartRateLimits()[1].getTimeAbove());

        assertEquals((short) 71, exercise.getHeartRateLimits()[2].getLowerHeartRate());
        assertEquals((short) 80, exercise.getHeartRateLimits()[2].getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(0, exercise.getHeartRateLimits()[2].getTimeBelow());
        assertEquals((1 * 60) + 49, exercise.getHeartRateLimits()[2].getTimeWithin());
        assertEquals(0, exercise.getHeartRateLimits()[2].getTimeAbove());

        assertEquals((short) 81, exercise.getHeartRateLimits()[3].getLowerHeartRate());
        assertEquals((short) 90, exercise.getHeartRateLimits()[3].getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits()[3].isAbsoluteRange());
        assertEquals(0, exercise.getHeartRateLimits()[3].getTimeBelow());
        assertEquals((37 * 60) + 57, exercise.getHeartRateLimits()[3].getTimeWithin());
        assertEquals(0, exercise.getHeartRateLimits()[3].getTimeAbove());

        // check lap data
        assertEquals(exercise.getLapList().length, 0);

        // check sample data
        assertEquals(exercise.getSampleList().length, 0);
    }
}
