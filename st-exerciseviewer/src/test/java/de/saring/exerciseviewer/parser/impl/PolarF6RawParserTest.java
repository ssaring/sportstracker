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
     * This method tests the parser with a Polar F6 raw exercise file.
     */
    @Test
    public void testParseF6() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/f6-test.frd");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.F6RAW);
        assertEquals("Polar F Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2007, 10, 28, 11, 7, 34), exercise.getDateTime());
        assertEquals("0", exercise.getSportType());
        assertTrue(exercise.getRecordingMode().isHeartRate());
        assertEquals(false, exercise.getRecordingMode().isAltitude());
        assertEquals(false, exercise.getRecordingMode().isSpeed());
        assertEquals(false, exercise.getRecordingMode().isCadence());
        assertEquals(false, exercise.getRecordingMode().isPower());
        assertNull(exercise.getRecordingMode().getBikeNumber());
        assertEquals((40 * 60 * 10) + 26 * 10, exercise.getDuration().intValue());
        assertNull(exercise.getRecordingInterval());
        assertEquals(169, exercise.getHeartRateAVG().intValue());
        assertEquals(178, exercise.getHeartRateMax().intValue());
        assertEquals(null, exercise.getSpeed());
        assertEquals(null, exercise.getCadence());
        assertEquals(null, exercise.getAltitude());
        assertEquals(null, exercise.getTemperature());
        assertEquals(595, exercise.getEnergy().intValue());
        assertEquals(54377, exercise.getEnergyTotal().intValue());
        assertEquals((65 * 60) + 9, exercise.getSumExerciseTime().intValue());
        assertNull(exercise.getSumRideTime());
        assertNull(exercise.getOdometer());

        // check heart rate limits
        assertEquals(4, exercise.getHeartRateLimits().size());
        assertEquals((short) 158, exercise.getHeartRateLimits().get(0).getLowerHeartRate());
        assertEquals((short) 176, exercise.getHeartRateLimits().get(0).getUpperHeartRate());
        assertEquals(true, exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertNull(exercise.getHeartRateLimits().get(0).getTimeBelow());
        assertEquals((36 * 60) + 13, exercise.getHeartRateLimits().get(0).getTimeWithin());
        assertNull(exercise.getHeartRateLimits().get(0).getTimeAbove());

        assertEquals((short) 60, exercise.getHeartRateLimits().get(1).getLowerHeartRate());
        assertEquals((short) 70, exercise.getHeartRateLimits().get(1).getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertNull(exercise.getHeartRateLimits().get(1).getTimeBelow());
        assertEquals(40, exercise.getHeartRateLimits().get(1).getTimeWithin());
        assertNull(exercise.getHeartRateLimits().get(1).getTimeAbove());

        assertEquals((short) 71, exercise.getHeartRateLimits().get(2).getLowerHeartRate());
        assertEquals((short) 80, exercise.getHeartRateLimits().get(2).getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertNull(exercise.getHeartRateLimits().get(2).getTimeBelow());
        assertEquals((1 * 60) + 49, exercise.getHeartRateLimits().get(2).getTimeWithin());
        assertNull(exercise.getHeartRateLimits().get(2).getTimeAbove());

        assertEquals((short) 81, exercise.getHeartRateLimits().get(3).getLowerHeartRate());
        assertEquals((short) 90, exercise.getHeartRateLimits().get(3).getUpperHeartRate());
        assertEquals(false, exercise.getHeartRateLimits().get(3).isAbsoluteRange());
        assertNull(exercise.getHeartRateLimits().get(3).getTimeBelow());
        assertEquals((37 * 60) + 57, exercise.getHeartRateLimits().get(3).getTimeWithin());
        assertNull(exercise.getHeartRateLimits().get(3).getTimeAbove());

        // check lap data
        assertTrue(exercise.getLapList().isEmpty());

        // check sample data
        assertTrue(exercise.getSampleList().isEmpty());
    }
}
