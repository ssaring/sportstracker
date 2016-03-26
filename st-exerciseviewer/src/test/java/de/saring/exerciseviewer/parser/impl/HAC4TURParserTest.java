package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * This class contains all unit tests for the HAC4TURParser class.
 *
 * @author Stefan Saring (the C# version was done by Ilja Booij)
 */
public class HAC4TURParserTest {

    /**
     * Instance to be tested.
     */
    private AbstractExerciseParser parser;

    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp() throws Exception {
        parser = new HAC4TURParser();
    }

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test
    public void testParseExerciseMissingFile() {
        try {
            parser.parseExercise("missing-file.tur");
            fail("Parse of the missing file must fail ...");
        } catch (EVException e) {
        }
    }

    /**
     * This method test the parser by using a HAC4 cycling exercise file.
     */
    @Test
    public void testParseCyclingExercise() throws EVException {

        // parse exercise
        EVExercise exercise = parser.parseExercise("misc/testdata/hac4/pailheres.tur");

        assertEquals(EVExercise.ExerciseFileType.HAC4TUR, exercise.getFileType());
        assertEquals("HACtronic - Tour", exercise.getDeviceName());
        assertEquals((short) 20, exercise.getRecordingInterval());
        // check recording mode
        assertTrue(exercise.getRecordingMode().isSpeed());
        assertFalse(exercise.getRecordingMode().isCadence());
        assertTrue(exercise.getRecordingMode().isAltitude());
        assertFalse(exercise.getRecordingMode().isPower());
        assertTrue(exercise.getRecordingMode().isTemperature());

        // time information
        assertEquals(LocalDateTime.of(2005, 10, 10, 14, 5, 0), exercise.getDateTime());
        assertEquals(9037 * 10, exercise.getDuration());
        assertEquals(183 * 60 + 14, exercise.getSumExerciseTime());

        // heart rates
        assertEquals((short) 159, exercise.getHeartRateAVG());
        assertEquals((short) 184, exercise.getHeartRateMax());

        // distance & speed & odometer
        assertEquals(40.1f, exercise.getSpeed().getDistance() / 1000f, 0.01f);
        assertEquals(72f, exercise.getSpeed().getSpeedMax(), 0.01f);
        assertEquals(22.21f, exercise.getSpeed().getSpeedAVG(), 0.01f);
        assertEquals(4817, exercise.getOdometer());

        // altitudes
        assertEquals((short) 1901, exercise.getAltitude().getAltitudeMax());
        assertEquals((short) 624, exercise.getAltitude().getAltitudeMin());
        assertEquals((short) 1352, exercise.getAltitude().getAltitudeAVG());
        assertEquals(1281, exercise.getAltitude().getAscent());

        // temperatures
        assertEquals((short) 15, exercise.getTemperature().getTemperatureMin());
        assertEquals((short) 17, exercise.getTemperature().getTemperatureAVG());
        assertEquals((short) 23, exercise.getTemperature().getTemperatureMax());

        // heart rate limits
        assertEquals(1, exercise.getHeartRateLimits().length);
        assertEquals((short) 140, exercise.getHeartRateLimits()[0].getLowerHeartRate());
        assertEquals((short) 150, exercise.getHeartRateLimits()[0].getUpperHeartRate());
        assertEquals(61 * 60, exercise.getHeartRateLimits()[0].getTimeBelow());
        assertEquals(6 * 60 + 20, exercise.getHeartRateLimits()[0].getTimeWithin());
        assertEquals((1 * 60 + 23) * 60 + 40, exercise.getHeartRateLimits()[0].getTimeAbove());

        // we don't have any lap data yet:
        assertEquals(1, exercise.getLapList().length);

        // too lazy to check samples for now..
        assertEquals(0, exercise.getSampleList()[0].getTimestamp());
        assertEquals(50 * 20 * 1000, exercise.getSampleList()[50].getTimestamp());
        assertEquals(100 * 20 * 1000, exercise.getSampleList()[100].getTimestamp());
    }

    /**
     * This method test the parser by using a HAC5 cycling exercise file.
     */
    @Test
    public void testParseHAC5CyclingExercise() throws EVException {

        EVExercise exercise = parser.parseExercise("misc/testdata/hac4/hac5.tur");

        assertEquals(EVExercise.ExerciseFileType.HAC4TUR, exercise.getFileType());
        assertEquals("HACtronic - Tour", exercise.getDeviceName());
        assertEquals((short) 5, exercise.getRecordingInterval());
        // check recording mode
        assertTrue(exercise.getRecordingMode().isSpeed());
        assertTrue(exercise.getRecordingMode().isCadence());
        assertTrue(exercise.getRecordingMode().isAltitude());
        assertFalse(exercise.getRecordingMode().isPower());
        assertTrue(exercise.getRecordingMode().isTemperature());

        // time information
        assertEquals(LocalDateTime.of(2006, 2, 4, 10, 34, 0), exercise.getDateTime());
        assertEquals(16675 * 10, exercise.getDuration());
        assertEquals(13965 / 60, exercise.getSumExerciseTime());

        // heart rates
        assertEquals((short) 128, exercise.getHeartRateAVG());
        assertEquals((short) 157, exercise.getHeartRateMax());

        // distance & speed & odometer
        assertEquals(105.56f, exercise.getSpeed().getDistance() / 1000f, 0.01f);
        assertEquals(43.2f, exercise.getSpeed().getSpeedMax(), 0.01f);
        assertEquals(27.21f, exercise.getSpeed().getSpeedAVG(), 0.01f);
        assertEquals(105, exercise.getOdometer());

        // Cadence values
        assertEquals((short) 58, exercise.getCadence().getCadenceAVG());
        assertEquals((short) 110, exercise.getCadence().getCadenceMax());
        // altitudes
        assertEquals((short) 5, exercise.getAltitude().getAltitudeMax());
        assertEquals((short) 2, exercise.getAltitude().getAltitudeMin());
        assertEquals((short) 2, exercise.getAltitude().getAltitudeAVG());
        assertEquals(10, exercise.getAltitude().getAscent());

        // temperatures
        assertEquals((short) 6, exercise.getTemperature().getTemperatureMin());
        assertEquals((short) 7, exercise.getTemperature().getTemperatureAVG());
        assertEquals((short) 16, exercise.getTemperature().getTemperatureMax());

        // heart rate limits
        assertEquals(1, exercise.getHeartRateLimits().length);
        assertEquals((short) 42, exercise.getHeartRateLimits()[0].getLowerHeartRate());
        assertEquals((short) 195, exercise.getHeartRateLimits()[0].getUpperHeartRate());
        assertEquals(0, exercise.getHeartRateLimits()[0].getTimeBelow());
        assertEquals(16680, exercise.getHeartRateLimits()[0].getTimeWithin());
        assertEquals(0, exercise.getHeartRateLimits()[0].getTimeAbove());

        // we don't have any lap data yet:
        assertEquals(1, exercise.getLapList().length);

        // too lazy to check samples for now..
        assertEquals(0, exercise.getSampleList()[0].getTimestamp());
        assertEquals(50 * 5 * 1000, exercise.getSampleList()[50].getTimestamp());
        assertEquals(100 * 5 * 1000, exercise.getSampleList()[100].getTimestamp());
    }
}
