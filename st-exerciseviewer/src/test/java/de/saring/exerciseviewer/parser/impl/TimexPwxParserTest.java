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
 * This class contains all unit tests for the TimexPwxParser class.
 * <p/>
 * This file is based on PolarHsrRawParser.java by Remco den Breeje
 * which is based PolarSRawParser.java by Stefan Saring
 * <p/>
 * 09/10/2010 - Added test for Timex Global Trainer PWX Parsing
 * 06/01/2013 - Added test for Timex Ironman Run Trainer PWX Parsing
 *
 * @author Robert C. Schultz, Stefan Saring
 */
public class TimexPwxParserTest {

    /**
     * Instance to be tested.
     */
    private AbstractExerciseParser parser;

    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp() throws Exception {
        parser = new TimexPwxParser();
    }

    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test
    public void testParseExerciseMissingFile() {
        try {
            parser.parseExercise("missing-file.srd");
            fail("Parse of the missing file must fail ...");
        } catch (EVException e) {
        }
    }

    /**
     * This method tests the parser with an Timex Pwx Chrono Workout file
     * recorded in metric units.?? Need to verify units
     */
    @Test
    public void testParseTimexPwxChronoWorkoutFile() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/timex-racetrainer-pwx/Timex20100618201200_1.pwx");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.TIMEX_PWX);
        assertEquals("Timex 843", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2010, 6, 18, 20, 12, 0), exercise.getDateTime());
        assertEquals("Run", exercise.getType());
        assertEquals(false, exercise.getRecordingMode().isAltitude());
        assertEquals(false, exercise.getRecordingMode().isSpeed());
        assertEquals(false, exercise.getRecordingMode().isCadence());
        assertEquals(false, exercise.getRecordingMode().isPower());
        assertEquals((byte) 0, exercise.getRecordingMode().getBikeNumber());
        assertEquals(10 * ((0 * 60 * 60) + (36 * 60) + 11) + 0, exercise.getDuration());
        assertEquals((short) 2, exercise.getRecordingInterval());
        assertEquals((short) 141, exercise.getHeartRateAVG());
        assertEquals((short) 160, exercise.getHeartRateMax());
        assertEquals(null, exercise.getSpeed());
        assertEquals(null, exercise.getCadence());
        assertEquals(null, exercise.getAltitude());
        assertEquals(null, exercise.getTemperature());
        assertEquals(512, exercise.getEnergy());     // This is calculated from Work
        assertEquals(513, exercise.getEnergyTotal()); // This is per the device
        assertEquals(36, exercise.getSumExerciseTime());
        assertEquals(36, exercise.getSumRideTime());
        assertEquals(0, exercise.getOdometer());

        // check heart rate limits
        assertEquals(6, exercise.getHeartRateLimits().length);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals((short) 191, exercise.getHeartRateLimits()[0].getUpperHeartRate());
        assertEquals((short) 172, exercise.getHeartRateLimits()[0].getLowerHeartRate());
        assertEquals((36 * 60) + 11, exercise.getHeartRateLimits()[0].getTimeBelow());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[0].getTimeWithin());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[0].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals((short) 171, exercise.getHeartRateLimits()[1].getUpperHeartRate());
        assertEquals((short) 153, exercise.getHeartRateLimits()[1].getLowerHeartRate());
        assertEquals((26 * 60) + 55, exercise.getHeartRateLimits()[1].getTimeBelow());
        assertEquals((9 * 60) + 16, exercise.getHeartRateLimits()[1].getTimeWithin());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[1].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals((short) 152, exercise.getHeartRateLimits()[2].getUpperHeartRate());
        assertEquals((short) 134, exercise.getHeartRateLimits()[2].getLowerHeartRate());
        assertEquals((8 * 60) + 33, exercise.getHeartRateLimits()[2].getTimeBelow());
        assertEquals((18 * 60) + 22, exercise.getHeartRateLimits()[2].getTimeWithin());
        assertEquals((9 * 60) + 16, exercise.getHeartRateLimits()[2].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[3].isAbsoluteRange());
        assertEquals((short) 133, exercise.getHeartRateLimits()[3].getUpperHeartRate());
        assertEquals((short) 115, exercise.getHeartRateLimits()[3].getLowerHeartRate());
        assertEquals((4 * 60) + 00, exercise.getHeartRateLimits()[3].getTimeBelow());
        assertEquals((4 * 60) + 33, exercise.getHeartRateLimits()[3].getTimeWithin());
        assertEquals((27 * 60) + 38, exercise.getHeartRateLimits()[3].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[4].isAbsoluteRange());
        assertEquals((short) 114, exercise.getHeartRateLimits()[4].getUpperHeartRate());
        assertEquals((short) 96, exercise.getHeartRateLimits()[4].getLowerHeartRate());
        assertEquals((0 * 60) + 32, exercise.getHeartRateLimits()[4].getTimeBelow());
        assertEquals((3 * 60) + 28, exercise.getHeartRateLimits()[4].getTimeWithin());
        assertEquals((32 * 60) + 11, exercise.getHeartRateLimits()[4].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[5].isAbsoluteRange());
        assertEquals((short) 159, exercise.getHeartRateLimits()[5].getUpperHeartRate());
        assertEquals((short) 150, exercise.getHeartRateLimits()[5].getLowerHeartRate());
        assertEquals((21 * 60) + 15, exercise.getHeartRateLimits()[5].getTimeBelow());
        assertEquals((14 * 60) + 44, exercise.getHeartRateLimits()[5].getTimeWithin());
        assertEquals((0 * 60) + 12, exercise.getHeartRateLimits()[5].getTimeAbove());

        // check lap data (first, two from middle and last only)
        float delta = 0;
        assertEquals(exercise.getLapList().length, 12);
        assertEquals(exercise.getLapList()[0].getTimeSplit(), 10 * ((0 * 60 * 60) + (3 * 60) + 45) + 4);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit(), (short) 0);
        assertEquals(exercise.getLapList()[0].getHeartRateAVG(), (short) 98);
        assertEquals(exercise.getLapList()[0].getHeartRateMax(), (short) 0);
        assertEquals((float) (36 * 402.336 / (10 * ((0 * 60 * 60) + (3 * 60) + 45) + 4.9)), exercise.getLapList()[0].getSpeed().getSpeedAVG(), delta);
        assertEquals((short) 402, exercise.getLapList()[0].getSpeed().getDistance());
        assertEquals(0, exercise.getLapList()[0].getAltitude().getAltitude());
        assertEquals(0, exercise.getLapList()[0].getAltitude().getAscent());
        assertEquals(25, exercise.getLapList()[0].getTemperature().getTemperature());

        assertEquals(exercise.getLapList()[2].getTimeSplit(), 10 * ((0 * 60 * 60) + (9 * 60) + 2) + 1);
        assertEquals(exercise.getLapList()[2].getHeartRateSplit(), (short) 0);
        assertEquals(exercise.getLapList()[2].getHeartRateAVG(), (short) 151);
        assertEquals(exercise.getLapList()[2].getHeartRateMax(), (short) 0);
        assertEquals((float) (36 * 402.336 / (1653.8)), exercise.getLapList()[2].getSpeed().getSpeedAVG(), delta);
        assertEquals((short) 1207, exercise.getLapList()[2].getSpeed().getDistance());
        assertEquals(0, exercise.getLapList()[2].getAltitude().getAltitude());
        assertEquals(0, exercise.getLapList()[2].getAltitude().getAscent());
        assertEquals(25, exercise.getLapList()[2].getTemperature().getTemperature());

        assertEquals(exercise.getLapList()[4].getTimeSplit(), 10 * ((0 * 60 * 60) + (15 * 60) + 3) + 3);
        assertEquals(exercise.getLapList()[4].getHeartRateSplit(), (short) 0);
        assertEquals(exercise.getLapList()[4].getHeartRateAVG(), (short) 148);
        assertEquals(exercise.getLapList()[4].getHeartRateMax(), (short) 0);
        assertEquals((float) (36 * 402.336 / (1801.5)), exercise.getLapList()[4].getSpeed().getSpeedAVG(), delta);
        assertEquals((short) 2011, exercise.getLapList()[4].getSpeed().getDistance());
        assertEquals(0, exercise.getLapList()[4].getAltitude().getAltitude());
        assertEquals(0, exercise.getLapList()[4].getAltitude().getAscent());
        assertEquals(25, exercise.getLapList()[4].getTemperature().getTemperature());

        assertEquals(exercise.getLapList()[11].getTimeSplit(), 10 * ((0 * 60 * 60) + (36 * 60) + 11) + 8);
        assertEquals(exercise.getLapList()[11].getHeartRateSplit(), (short) 0);
        assertEquals(exercise.getLapList()[11].getHeartRateAVG(), (short) 138);
        assertEquals(exercise.getLapList()[11].getHeartRateMax(), (short) 0);
        assertEquals((float) (36 * 402.336 / (1985.9)), exercise.getLapList()[11].getSpeed().getSpeedAVG(), delta);
        assertEquals((short) 4828, exercise.getLapList()[11].getSpeed().getDistance());
        assertEquals(0, exercise.getLapList()[11].getAltitude().getAltitude());
        assertEquals(0, exercise.getLapList()[11].getAltitude().getAscent());
        assertEquals(25, exercise.getLapList()[11].getTemperature().getTemperature());

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 1099);
        assertEquals(exercise.getSampleList()[0].getHeartRate(), (short) 66);
        assertEquals(exercise.getSampleList()[0].getAltitude(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[0].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getDistance(), 0);

        assertEquals(exercise.getSampleList()[333].getHeartRate(), (short) 149);
        assertEquals(exercise.getSampleList()[333].getAltitude(), (short) 0);
        assertEquals(exercise.getSampleList()[333].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[333].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[333].getDistance(), 0);

        assertEquals(exercise.getSampleList()[555].getHeartRate(), (short) 147);
        assertEquals(exercise.getSampleList()[555].getAltitude(), (short) 0);
        assertEquals(exercise.getSampleList()[555].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[555].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[555].getDistance(), 0);

        assertEquals(exercise.getSampleList()[1098].getHeartRate(), (short) 130);
        assertEquals(exercise.getSampleList()[1098].getAltitude(), (short) 0);
        assertEquals(exercise.getSampleList()[1098].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[1098].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[1098].getDistance(), 0);
    }

    /**
     * This method tests the parser with an Timex Global Trainer Pwx Workout file.
     */
    @Test
    public void testParseTimexGlobalTrainerPwxWorkoutFile() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/timex-globaltrainer-pwx/Timex_Global_Trainer_5Laps.pwx");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.TIMEX_PWX);
        assertEquals("Timex Global Trainer", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2010, 9, 9, 17, 53, 21), exercise.getDateTime());
        assertEquals("Bike", exercise.getType());
        assertEquals(true, exercise.getRecordingMode().isAltitude());
        assertEquals(true, exercise.getRecordingMode().isSpeed());
        assertEquals(false, exercise.getRecordingMode().isCadence());
        assertEquals(true, exercise.getRecordingMode().isPower());
        assertEquals((byte) 0, exercise.getRecordingMode().getBikeNumber());
        assertEquals(10 * ((0 * 60 * 60) + (10 * 60) + 06) + 0, exercise.getDuration());
        assertEquals((short) 2, exercise.getRecordingInterval());
        assertEquals((short) 145, exercise.getHeartRateAVG());
        assertEquals((short) 197, exercise.getHeartRateMax());
        assertEquals(exercise.getSpeed().getDistance(), 5319);
        assertEquals(exercise.getSpeed().getSpeedAVG(), 31.550005, .001);
        assertEquals(exercise.getSpeed().getSpeedMax(), 39.579838, .001);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), 0);
        assertEquals(exercise.getAltitude().getAltitudeMax(), 59);
        assertEquals(exercise.getAltitude().getAltitudeAVG(), 0); // Global Trainer doesn't provide this data.
        assertEquals(exercise.getAltitude().getAscent(), 52);
        assertEquals(exercise.getTemperature(), null);
        assertEquals(108, exercise.getEnergy());     // This is calculated from Work
        assertEquals(0, exercise.getEnergyTotal()); // This is per the device
        assertEquals(10, exercise.getSumExerciseTime());
        assertEquals(10, exercise.getSumRideTime());
        assertEquals(5.319, exercise.getOdometer(), .4);

        // check heart rate limits
        assertEquals(6, exercise.getHeartRateLimits().length);
        assertTrue(exercise.getHeartRateLimits()[5].isAbsoluteRange());
        assertEquals((short) 200, exercise.getHeartRateLimits()[5].getUpperHeartRate());
        assertEquals((short) 175, exercise.getHeartRateLimits()[5].getLowerHeartRate());
        assertEquals((8 * 60) + 27, exercise.getHeartRateLimits()[5].getTimeBelow());
        assertEquals((1 * 60) + 39, exercise.getHeartRateLimits()[5].getTimeWithin());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[5].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[4].isAbsoluteRange());
        assertEquals((short) 175, exercise.getHeartRateLimits()[4].getUpperHeartRate());
        assertEquals((short) 150, exercise.getHeartRateLimits()[4].getLowerHeartRate());
        assertEquals((6 * 60) + 36, exercise.getHeartRateLimits()[4].getTimeBelow());
        assertEquals((1 * 60) + 51, exercise.getHeartRateLimits()[4].getTimeWithin());
        assertEquals((1 * 60) + 39, exercise.getHeartRateLimits()[4].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[3].isAbsoluteRange());
        assertEquals((short) 150, exercise.getHeartRateLimits()[3].getUpperHeartRate());
        assertEquals((short) 125, exercise.getHeartRateLimits()[3].getLowerHeartRate());
        assertEquals((0 * 60) + 59, exercise.getHeartRateLimits()[3].getTimeBelow());
        assertEquals((5 * 60) + 46, exercise.getHeartRateLimits()[3].getTimeWithin());
        assertEquals((3 * 60) + 20, exercise.getHeartRateLimits()[3].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals((short) 125, exercise.getHeartRateLimits()[2].getUpperHeartRate());
        assertEquals((short) 100, exercise.getHeartRateLimits()[2].getLowerHeartRate());
        assertEquals((0 * 60) + 14, exercise.getHeartRateLimits()[2].getTimeBelow());
        assertEquals((0 * 60) + 55, exercise.getHeartRateLimits()[2].getTimeWithin());
        assertEquals((8 * 60) + 57, exercise.getHeartRateLimits()[2].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals((short) 100, exercise.getHeartRateLimits()[1].getUpperHeartRate());
        assertEquals((short) 75, exercise.getHeartRateLimits()[1].getLowerHeartRate());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[1].getTimeBelow());
        assertEquals((0 * 60) + 14, exercise.getHeartRateLimits()[1].getTimeWithin());
        assertEquals((9 * 60) + 52, exercise.getHeartRateLimits()[1].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals((short) 75, exercise.getHeartRateLimits()[0].getUpperHeartRate());
        assertEquals((short) 50, exercise.getHeartRateLimits()[0].getLowerHeartRate());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[0].getTimeBelow());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[0].getTimeWithin());
        assertEquals((10 * 60) + 6, exercise.getHeartRateLimits()[0].getTimeAbove());

        // check last lap data
        assertEquals(exercise.getLapList().length, 5);
        assertEquals(exercise.getLapList()[4].getTimeSplit(), 10 * ((0 * 60 * 60) + (10 * 60) + 6) + 9);
        assertEquals(exercise.getLapList()[4].getHeartRateSplit(), (short) 0);
        assertEquals(exercise.getLapList()[4].getHeartRateAVG(), (short) 128);
        assertEquals(exercise.getLapList()[4].getHeartRateMax(), (short) 135);
        assertEquals((float) (32.86), exercise.getLapList()[4].getSpeed().getSpeedAVG(), .01);
        assertEquals((short) 5316, exercise.getLapList()[4].getSpeed().getDistance());
        assertEquals(26, exercise.getLapList()[4].getAltitude().getAltitude());
        assertEquals(26, exercise.getLapList()[4].getAltitude().getAscent());
        assertEquals(25, exercise.getLapList()[4].getTemperature().getTemperature());

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 300);
        assertEquals(exercise.getSampleList()[0].getHeartRate(), (short) 76);
        assertEquals(exercise.getSampleList()[0].getAltitude(), (short) 38);
        assertEquals(exercise.getSampleList()[0].getSpeed(), 1.56, 0.01f);
        assertEquals(exercise.getSampleList()[0].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getDistance(), 0);

        assertEquals(exercise.getSampleList()[5].getHeartRate(), (short) 90);
        assertEquals(exercise.getSampleList()[5].getAltitude(), (short) 39);
        assertEquals(exercise.getSampleList()[5].getSpeed(), 17.89, .1f);
        assertEquals(exercise.getSampleList()[5].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[5].getDistance(), 28);

        assertEquals(exercise.getSampleList()[299].getHeartRate(), (short) 124);
        assertEquals(exercise.getSampleList()[299].getAltitude(), (short) 13);
        assertEquals(exercise.getSampleList()[299].getSpeed(), 10.17f, 0.01f);
        assertEquals(exercise.getSampleList()[299].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[299].getDistance(), 5310);
    }

    /**
     * This method tests the parser with an Timex Ironman Run Trainer Pwx workout file.
     */
    @Test
    public void testParseTimexRunTrainerPwxFile() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise(
                "misc/testdata/timex-runtrainer-pwx/Timex_Run_Trainer_2013_01_01_08_22_52.pwx");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.TIMEX_PWX);
        assertEquals("Timex Run Trainer", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2013, 1, 1, 8, 22, 52), exercise.getDateTime());
        assertEquals("Run", exercise.getType());
        assertEquals(true, exercise.getRecordingMode().isAltitude());
        assertEquals(true, exercise.getRecordingMode().isSpeed());
        assertEquals(false, exercise.getRecordingMode().isCadence());
        assertEquals(true, exercise.getRecordingMode().isPower());
        assertEquals((byte) 0, exercise.getRecordingMode().getBikeNumber());
        assertEquals(39 * 60 * 10 + 41 * 10 + 0, exercise.getDuration());
        assertEquals((short) 2, exercise.getRecordingInterval());
        assertEquals((short) 141, exercise.getHeartRateAVG());
        assertEquals((short) 159, exercise.getHeartRateMax());
        assertEquals(3869, exercise.getSpeed().getDistance());
        assertEquals(5.85, exercise.getSpeed().getSpeedAVG(), 0.001);
        assertEquals(7.938, exercise.getSpeed().getSpeedMax(), 0.001);
        assertNull(exercise.getCadence());
        assertEquals(12, exercise.getAltitude().getAltitudeMin());
        assertEquals(44, exercise.getAltitude().getAltitudeAVG());
        assertEquals(64, exercise.getAltitude().getAltitudeMax());
        assertEquals(117, exercise.getAltitude().getAscent());
        assertNull(exercise.getTemperature());
        assertEquals(2324, exercise.getEnergy()); // this is probably wrong, proper parsing is unknown yet
        assertEquals(0, exercise.getEnergyTotal()); // This is per the device
        assertEquals(39, exercise.getSumExerciseTime());
        assertEquals(39, exercise.getSumRideTime());
        assertEquals(3, exercise.getOdometer());

        // check heart rate limits
        assertEquals(6, exercise.getHeartRateLimits().length);
        assertTrue(exercise.getHeartRateLimits()[5].isAbsoluteRange());
        assertEquals((short) 200, exercise.getHeartRateLimits()[5].getUpperHeartRate());
        assertEquals((short) 175, exercise.getHeartRateLimits()[5].getLowerHeartRate());
        assertEquals((39 * 60) + 41, exercise.getHeartRateLimits()[5].getTimeBelow());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[5].getTimeWithin());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[5].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[4].isAbsoluteRange());
        assertEquals((short) 175, exercise.getHeartRateLimits()[4].getUpperHeartRate());
        assertEquals((short) 150, exercise.getHeartRateLimits()[4].getLowerHeartRate());
        assertEquals((29 * 60) + 57, exercise.getHeartRateLimits()[4].getTimeBelow());
        assertEquals((9 * 60) + 44, exercise.getHeartRateLimits()[4].getTimeWithin());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[4].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[3].isAbsoluteRange());
        assertEquals((short) 150, exercise.getHeartRateLimits()[3].getUpperHeartRate());
        assertEquals((short) 125, exercise.getHeartRateLimits()[3].getLowerHeartRate());
        assertEquals((3 * 60) + 33, exercise.getHeartRateLimits()[3].getTimeBelow());
        assertEquals((27 * 60) + 58, exercise.getHeartRateLimits()[3].getTimeWithin());
        assertEquals((8 * 60) + 10, exercise.getHeartRateLimits()[3].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals((short) 125, exercise.getHeartRateLimits()[2].getUpperHeartRate());
        assertEquals((short) 100, exercise.getHeartRateLimits()[2].getLowerHeartRate());
        assertEquals((0 * 60) + 9, exercise.getHeartRateLimits()[2].getTimeBelow());
        assertEquals((4 * 60) + 0, exercise.getHeartRateLimits()[2].getTimeWithin());
        assertEquals((35 * 60) + 32, exercise.getHeartRateLimits()[2].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals((short) 100, exercise.getHeartRateLimits()[1].getUpperHeartRate());
        assertEquals((short) 75, exercise.getHeartRateLimits()[1].getLowerHeartRate());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[1].getTimeBelow());
        assertEquals((0 * 60) + 9, exercise.getHeartRateLimits()[1].getTimeWithin());
        assertEquals((39 * 60) + 32, exercise.getHeartRateLimits()[1].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals((short) 75, exercise.getHeartRateLimits()[0].getUpperHeartRate());
        assertEquals((short) 50, exercise.getHeartRateLimits()[0].getLowerHeartRate());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[0].getTimeBelow());
        assertEquals((0 * 60) + 0, exercise.getHeartRateLimits()[0].getTimeWithin());
        assertEquals((39 * 60) + 41, exercise.getHeartRateLimits()[0].getTimeAbove());

        // check last lap data
        assertEquals(1, exercise.getLapList().length);
        assertEquals(39 * 60 * 10 + 41 * 10 + 2, exercise.getLapList()[0].getTimeSplit());
        assertEquals((short) 0, exercise.getLapList()[0].getHeartRateSplit());
        assertEquals((short) 0, exercise.getLapList()[0].getHeartRateAVG());
        assertEquals((short) 0, exercise.getLapList()[0].getHeartRateMax());
        assertEquals(5.85f, exercise.getLapList()[0].getSpeed().getSpeedAVG(), 0.01);
        assertEquals(3868, exercise.getLapList()[0].getSpeed().getDistance());
        assertEquals(0, exercise.getLapList()[0].getAltitude().getAltitude());
        assertEquals(0, exercise.getLapList()[0].getAltitude().getAscent());
        assertEquals(25, exercise.getLapList()[0].getTemperature().getTemperature());

        // check sample data (first, two from middle and last only)
        assertEquals(1191, exercise.getSampleList().length);
        assertEquals(1 * 1000, exercise.getSampleList()[0].getTimestamp());
        assertEquals(93, exercise.getSampleList()[0].getHeartRate());
        assertEquals(16, exercise.getSampleList()[0].getAltitude());
        assertEquals(0f, exercise.getSampleList()[0].getSpeed(), 0.01f);
        assertEquals(0, exercise.getSampleList()[0].getCadence());
        assertEquals(0, exercise.getSampleList()[0].getDistance());

        assertEquals((16 * 60 + 41) * 1000, exercise.getSampleList()[500].getTimestamp());
        assertEquals(136, exercise.getSampleList()[500].getHeartRate());
        assertEquals(51, exercise.getSampleList()[500].getAltitude());
        assertEquals(5.19f, exercise.getSampleList()[500].getSpeed(), 0.01f);
        assertEquals(0, exercise.getSampleList()[500].getCadence());
        assertEquals(1608, exercise.getSampleList()[500].getDistance());

        assertEquals((39 * 60 + 41) * 1000, exercise.getSampleList()[1190].getTimestamp());
        assertEquals(141, exercise.getSampleList()[1190].getHeartRate());
        assertEquals(38, exercise.getSampleList()[1190].getAltitude());
        assertEquals(5.49f, exercise.getSampleList()[1190].getSpeed(), 0.01f);
        assertEquals(0, exercise.getSampleList()[1190].getCadence());
        assertEquals(3869, exercise.getSampleList()[1190].getDistance());
    }
}
