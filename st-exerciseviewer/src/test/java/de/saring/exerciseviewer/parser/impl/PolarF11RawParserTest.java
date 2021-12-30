package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class contains all unit tests for the PolarF6RawParser class using a F11
 * RAW file.
 * <p/>
 * Based on the PolarF6RawParserTest.
 *
 * @author Roland Hostettler
 */
public class PolarF11RawParserTest {

    /**
     * Instance to be tested.
     */
    private AbstractExerciseParser parser;

    /**
     * This method initializes the environment for testing.
     */
    @BeforeEach
    public void setUp() throws Exception {
        parser = new PolarF6RawParser();
    }

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test
    public void testParseExerciseMissingFile() {
        assertThrows(EVException.class, () ->
                parser.parseExercise("missing-file.frd"));
    }

    /**
     * This method tests the parser with a Polar F11 raw exercise file.
     */
    @Test
    public void testParseF11() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/f11-test.frd");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.F6RAW);
        assertEquals("Polar F Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2008, 9, 1, 19, 27, 48), exercise.getDateTime());
        assertEquals("Normal1", exercise.getSportType());
        assertTrue(exercise.getRecordingMode().isHeartRate());
        assertEquals(false, exercise.getRecordingMode().isAltitude());
        assertEquals(false, exercise.getRecordingMode().isSpeed());
        assertEquals(false, exercise.getRecordingMode().isCadence());
        assertEquals(false, exercise.getRecordingMode().isPower());
        assertNull(exercise.getRecordingMode().getBikeNumber());
        assertEquals((54 * 60 * 10) + 31 * 10, exercise.getDuration().intValue());
        assertNull(exercise.getRecordingInterval());
        assertEquals(156, exercise.getHeartRateAVG().intValue());
        assertEquals(193, exercise.getHeartRateMax().intValue());
        assertEquals(null, exercise.getSpeed());
        assertEquals(null, exercise.getCadence());
        assertEquals(null, exercise.getAltitude());
        assertEquals(null, exercise.getTemperature());
        assertEquals(601, exercise.getEnergy().intValue());
        assertEquals(2776, exercise.getEnergyTotal().intValue());
        assertEquals((4 * 60) + 23, exercise.getSumExerciseTime().intValue());
        assertNull(exercise.getSumRideTime());
        assertNull(exercise.getOdometer());

        // check heart rate limits
        assertEquals(4, exercise.getHeartRateLimits().size());//--
        assertEquals((short) 141, exercise.getHeartRateLimits().get(0).getLowerHeartRate());
        assertEquals((short) 160, exercise.getHeartRateLimits().get(0).getUpperHeartRate());
        assertEquals(true, exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertNull(exercise.getHeartRateLimits().get(0).getTimeBelow());
        assertEquals((31 * 60) + 38, exercise.getHeartRateLimits().get(0).getTimeWithin());
        assertNull(exercise.getHeartRateLimits().get(0).getTimeAbove());

        assertEquals((short) 60, exercise.getHeartRateLimits().get(1).getLowerHeartRate());
        assertEquals((short) 70, exercise.getHeartRateLimits().get(1).getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertNull(exercise.getHeartRateLimits().get(1).getTimeBelow());
        assertEquals((6 * 60) + 3, exercise.getHeartRateLimits().get(1).getTimeWithin());
        assertNull(exercise.getHeartRateLimits().get(1).getTimeAbove());

        assertEquals((short) 71, exercise.getHeartRateLimits().get(2).getLowerHeartRate());
        assertEquals((short) 80, exercise.getHeartRateLimits().get(2).getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertNull(exercise.getHeartRateLimits().get(2).getTimeBelow());
        assertEquals((31 * 60) + 38, exercise.getHeartRateLimits().get(2).getTimeWithin());
        assertNull(exercise.getHeartRateLimits().get(2).getTimeAbove());

        assertEquals((short) 81, exercise.getHeartRateLimits().get(3).getLowerHeartRate());
        assertEquals((short) 90, exercise.getHeartRateLimits().get(3).getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits().get(3).isAbsoluteRange());
        assertNull(exercise.getHeartRateLimits().get(3).getTimeBelow());
        assertEquals((16 * 60) + 50, exercise.getHeartRateLimits().get(3).getTimeWithin());
        assertNull(exercise.getHeartRateLimits().get(3).getTimeAbove());

        // check lap data
        assertTrue(exercise.getLapList().isEmpty());

        // check sample data
        assertTrue(exercise.getSampleList().isEmpty());
    }
}

 	  	 
