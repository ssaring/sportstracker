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
 * This class contains all unit tests for the PolarSRawParser class.
 *
 * @author Stefan Saring
 */
public class PolarSRawParserTest {

    /**
     * Instance to be tested.
     */
    private AbstractExerciseParser parser;

    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp() throws Exception {
        parser = new PolarSRawParser();
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
     * This method tests the parser with an Polar S610 raw exercise file
     * recorded in metric units.
     * This test is taken from the C# test class so the code could be better :-)
     */
    @Test
    public void testParseS610ExerciseWithMetricUnits() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s610/ma_br_20040912T072607.srd");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.S610RAW);
        assertEquals(exercise.getUserID(), (byte) 1);
        assertEquals(LocalDateTime.of(2004, 9, 12, 7, 26, 7), exercise.getDateTime());
        assertEquals(exercise.getType(), "TB2    ");
        assertEquals(exercise.getRecordingMode().isAltitude(), false);
        assertEquals(exercise.getRecordingMode().isSpeed(), false);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertEquals(exercise.getRecordingMode().getBikeNumber(), (byte) 0);
        assertEquals(exercise.getDuration(), (1 * 60 * 60 * 10) + (36 * 60 * 10) + 50 * 10 + 8);
        assertEquals(exercise.getRecordingInterval(), (short) 5);
        assertEquals(exercise.getHeartRateAVG(), (short) 158);
        assertEquals(exercise.getHeartRateMax(), (short) 176);
        assertEquals(exercise.getSpeed(), null);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude(), null);
        assertEquals(exercise.getTemperature(), null);
        assertEquals(exercise.getEnergy(), 1214);
        assertEquals(exercise.getEnergyTotal(), 22552);
        assertEquals(exercise.getSumExerciseTime(), (25 * 60) + 58);
        assertEquals(exercise.getSumRideTime(), 0);
        assertEquals(exercise.getOdometer(), 0);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().length, 3);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[0].getLowerHeartRate(), (short) 143);
        assertEquals(exercise.getHeartRateLimits()[0].getUpperHeartRate(), (short) 162);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeBelow(), (0 * 60 * 60) + (2 * 60) + 11);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeWithin(), (1 * 60 * 60) + (17 * 60) + 34);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeAbove(), (0 * 60 * 60) + (17 * 60) + 5);

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[1].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[1].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[2].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[2].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data (first and last lap only)
        assertEquals(exercise.getLapList().length, 3);
        assertEquals(exercise.getLapList()[0].getTimeSplit(), (0 * 60 * 60 * 10) + (50 * 60 * 10) + (17 * 10) + 2);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit(), (short) 165);
        assertEquals(exercise.getLapList()[0].getHeartRateAVG(), (short) 157);
        assertEquals(exercise.getLapList()[0].getHeartRateMax(), (short) 176);
        assertEquals(exercise.getLapList()[0].getSpeed(), null);
        assertEquals(exercise.getLapList()[0].getAltitude(), null);
        assertEquals(exercise.getLapList()[0].getTemperature(), null);

        assertEquals(exercise.getLapList()[2].getTimeSplit(), (1 * 60 * 60 * 10) + (36 * 60 * 10) + (50 * 10) + 8);
        assertEquals(exercise.getLapList()[2].getHeartRateSplit(), (short) 159);
        assertEquals(exercise.getLapList()[2].getHeartRateAVG(), (short) 160);
        assertEquals(exercise.getLapList()[2].getHeartRateMax(), (short) 171);
        assertEquals(exercise.getLapList()[2].getSpeed(), null);
        assertEquals(exercise.getLapList()[2].getAltitude(), null);
        assertEquals(exercise.getLapList()[2].getTemperature(), null);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 1163);
        assertEquals(0, exercise.getSampleList()[0].getTimestamp());
        assertEquals(exercise.getSampleList()[0].getHeartRate(), (short) 109);
        assertEquals(exercise.getSampleList()[0].getAltitude(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[0].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getDistance(), 0);

        assertEquals(240 * 5 * 1000, exercise.getSampleList()[240].getTimestamp());
        assertEquals(exercise.getSampleList()[240].getHeartRate(), (short) 160);
        assertEquals(exercise.getSampleList()[240].getAltitude(), (short) 0);
        assertEquals(exercise.getSampleList()[240].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[240].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[240].getDistance(), 0);

        assertEquals(480 * 5 * 1000, exercise.getSampleList()[480].getTimestamp());
        assertEquals(exercise.getSampleList()[480].getHeartRate(), (short) 161);
        assertEquals(exercise.getSampleList()[480].getAltitude(), (short) 0);
        assertEquals(exercise.getSampleList()[480].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[480].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[480].getDistance(), 0);

        assertEquals(1162 * 5 * 1000, exercise.getSampleList()[1162].getTimestamp());
        assertEquals(exercise.getSampleList()[1162].getHeartRate(), (short) 159);
        assertEquals(exercise.getSampleList()[1162].getAltitude(), (short) 0);
        assertEquals(exercise.getSampleList()[1162].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[1162].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[1162].getDistance(), 0);
    }

    /**
     * This method tests the parser with an cycling exercise file
     * recorded in metric units from Polar S710.
     */
    @Test
    public void testParseS710CyclingExerciseWithMetricUnits() throws EVException {

        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s710/cycling-metric.srd");

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.S710RAW, exercise.getFileType());
        assertEquals((byte) 0, exercise.getUserID());
        assertEquals(LocalDateTime.of(2002, 11, 20, 14, 7, 44), exercise.getDateTime());
        assertEquals("ExeSet1", exercise.getType());
        assertEquals((1 * 60 * 60 * 10) + (13 * 60 * 10) + 34 * 10 + 3, exercise.getDuration());
        assertTrue(exercise.getRecordingMode().isAltitude());
        assertTrue(exercise.getRecordingMode().isSpeed());
        assertFalse(exercise.getRecordingMode().isCadence());
        assertFalse(exercise.getRecordingMode().isPower());
        assertEquals((byte) 2, exercise.getRecordingMode().getBikeNumber());
        assertEquals((short) 15, exercise.getRecordingInterval());
        assertEquals((short) 135, exercise.getHeartRateAVG());
        assertEquals((short) 232, exercise.getHeartRateMax());
        assertEquals(251, Math.round(exercise.getSpeed().getSpeedAVG() * 10));
        assertEquals(1093, Math.round(exercise.getSpeed().getSpeedMax() * 10));
        assertEquals(29900, exercise.getSpeed().getDistance());
        assertNull(exercise.getCadence());
        assertEquals((short) 215, exercise.getAltitude().getAltitudeMin(), 215);
        assertEquals((short) 253, exercise.getAltitude().getAltitudeAVG(), 253);
        assertEquals((short) 300, exercise.getAltitude().getAltitudeMax(), 300);
        assertEquals((short) 3, exercise.getTemperature().getTemperatureMin(), 3);
        assertEquals((short) 3, exercise.getTemperature().getTemperatureAVG(), 3);
        assertEquals((short) 5, exercise.getTemperature().getTemperatureMax(), 5);
        assertEquals(591, exercise.getEnergy());
        assertEquals(24099, exercise.getEnergyTotal());
        assertEquals((56 * 60) + 34, exercise.getSumExerciseTime());
        assertEquals((42 * 60) + 56, exercise.getSumRideTime());
        assertEquals(1024, exercise.getOdometer());

        // check heart rate limits
        assertEquals(3, exercise.getHeartRateLimits().length, 3);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals((short) 120, exercise.getHeartRateLimits()[0].getLowerHeartRate(), 120);
        assertEquals((short) 155, exercise.getHeartRateLimits()[0].getUpperHeartRate(), 155);
        assertEquals((0 * 60 * 60) + (5 * 60) + 32, exercise.getHeartRateLimits()[0].getTimeBelow());
        assertEquals((1 * 60 * 60) + (3 * 60) + 19, exercise.getHeartRateLimits()[0].getTimeWithin());
        assertEquals((0 * 60 * 60) + (4 * 60) + 43, exercise.getHeartRateLimits()[0].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals((short) 80, exercise.getHeartRateLimits()[1].getLowerHeartRate(), 80);
        assertEquals((short) 160, exercise.getHeartRateLimits()[1].getUpperHeartRate(), 160);
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.getHeartRateLimits()[1].getTimeBelow());
        assertEquals((1 * 60 * 60) + (10 * 60) + 55, exercise.getHeartRateLimits()[1].getTimeWithin());
        assertEquals((0 * 60 * 60) + (2 * 60) + 39, exercise.getHeartRateLimits()[1].getTimeAbove());

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals((short) 80, exercise.getHeartRateLimits()[2].getLowerHeartRate(), 80);
        assertEquals((short) 160, exercise.getHeartRateLimits()[2].getUpperHeartRate(), 160);
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.getHeartRateLimits()[2].getTimeBelow());
        assertEquals((1 * 60 * 60) + (10 * 60) + 55, exercise.getHeartRateLimits()[2].getTimeWithin());
        assertEquals((0 * 60 * 60) + (2 * 60) + 39, exercise.getHeartRateLimits()[2].getTimeAbove());

        // check lap data (first, one from middle and last lap only)
        assertEquals(5, exercise.getLapList().length);
        assertEquals((0 * 60 * 60 * 10) + (06 * 60 * 10) + (59 * 10) + 2, exercise.getLapList()[0].getTimeSplit());
        assertEquals((short) 136, exercise.getLapList()[0].getHeartRateSplit());
        assertEquals((short) 128, exercise.getLapList()[0].getHeartRateAVG());
        assertEquals((short) 152, exercise.getLapList()[0].getHeartRateMax());
        assertEquals(141, Math.round(exercise.getLapList()[0].getSpeed().getSpeedEnd() * 10));
        assertEquals(258, Math.round(exercise.getLapList()[0].getSpeed().getSpeedAVG() * 10));
        assertEquals(3 * 1000, exercise.getLapList()[0].getSpeed().getDistance());
        assertEquals((short) 0, exercise.getLapList()[0].getSpeed().getCadence());
        assertEquals((short) 231, exercise.getLapList()[0].getAltitude().getAltitude());
        assertEquals(25, exercise.getLapList()[0].getAltitude().getAscent());
        assertEquals((short) 4, exercise.getLapList()[0].getTemperature().getTemperature());

        assertEquals((0 * 60 * 60 * 10) + (40 * 60 * 10) + (18 * 10) + 8, exercise.getLapList()[2].getTimeSplit());
        assertEquals((short) 136, exercise.getLapList()[2].getHeartRateSplit());
        assertEquals((short) 134, exercise.getLapList()[2].getHeartRateAVG());
        assertEquals((short) 168, exercise.getLapList()[2].getHeartRateMax());
        assertEquals(193, Math.round(exercise.getLapList()[2].getSpeed().getSpeedEnd() * 10));
        assertEquals(242, Math.round(exercise.getLapList()[2].getSpeed().getSpeedAVG() * 10));
        assertEquals(15700, exercise.getLapList()[2].getSpeed().getDistance());
        assertEquals((short) 0, exercise.getLapList()[2].getSpeed().getCadence());
        assertEquals((short) 247, exercise.getLapList()[2].getAltitude().getAltitude());
        assertEquals(135, exercise.getLapList()[2].getAltitude().getAscent());
        assertEquals((short) 4, exercise.getLapList()[2].getTemperature().getTemperature());

        assertEquals((1 * 60 * 60 * 10) + (13 * 60 * 10) + (34 * 10) + 3, exercise.getLapList()[4].getTimeSplit());
        assertEquals((short) 123, exercise.getLapList()[4].getHeartRateSplit());
        assertEquals((short) 121, exercise.getLapList()[4].getHeartRateAVG());
        assertEquals((short) 123, exercise.getLapList()[4].getHeartRateMax());
        assertEquals(0 * 10, Math.round(exercise.getLapList()[4].getSpeed().getSpeedEnd() * 10));
        assertEquals(0 * 10, Math.round(exercise.getLapList()[4].getSpeed().getSpeedAVG() * 10));
        assertEquals(29900, exercise.getLapList()[4].getSpeed().getDistance());
        assertEquals((short) 0, exercise.getLapList()[4].getSpeed().getCadence());
        assertEquals((short) 229, exercise.getLapList()[4].getAltitude().getAltitude());
        assertEquals(240, exercise.getLapList()[4].getAltitude().getAscent());
        assertEquals((short) 4, exercise.getLapList()[4].getTemperature().getTemperature());

        // check sample data (first, two from middle and last only)
        assertEquals(295, exercise.getSampleList().length);
        assertEquals(0, exercise.getSampleList()[0].getTimestamp());
        assertEquals((short) 101, exercise.getSampleList()[0].getHeartRate());
        assertEquals((short) 240, exercise.getSampleList()[0].getAltitude());
        assertEquals(42, Math.round(exercise.getSampleList()[0].getSpeed() * 10));
        assertEquals((short) 0, exercise.getSampleList()[0].getCadence());
        assertEquals(0, exercise.getSampleList()[0].getDistance());

        assertEquals(100 * 15 * 1000, exercise.getSampleList()[100].getTimestamp());
        assertEquals((short) 147, exercise.getSampleList()[100].getHeartRate());
        assertEquals((short) 278, exercise.getSampleList()[100].getAltitude());
        assertEquals(171, Math.round(exercise.getSampleList()[100].getSpeed() * 10));
        assertEquals((short) 0, exercise.getSampleList()[100].getCadence());
        assertEquals(9479, exercise.getSampleList()[100].getDistance());

        assertEquals(200 * 15 * 1000, exercise.getSampleList()[200].getTimestamp());
        assertEquals((short) 166, exercise.getSampleList()[200].getHeartRate());
        assertEquals((short) 275, exercise.getSampleList()[200].getAltitude());
        assertEquals(141, Math.round(exercise.getSampleList()[200].getSpeed() * 10));
        assertEquals((short) 0, exercise.getSampleList()[200].getCadence());
        assertEquals(19256, exercise.getSampleList()[200].getDistance());

        assertEquals(294 * 15 * 1000, exercise.getSampleList()[294].getTimestamp());
        assertEquals((short) 123, exercise.getSampleList()[294].getHeartRate());
        assertEquals((short) 229, exercise.getSampleList()[294].getAltitude());
        assertEquals(0, Math.round(exercise.getSampleList()[294].getSpeed() * 10));
        assertEquals((short) 0, exercise.getSampleList()[294].getCadence());
        assertEquals(29900, exercise.getSampleList()[294].getDistance());
    }

    /**
     * This method tests the parser with an cycling exercise file
     * recorded in english units from Polar S710.
     * This test is taken from the C# test class so the code could be better :-)
     */
    @Test
    public void testParseS710CyclingExerciseWithEnglishUnits() throws EVException {

        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s710/cycling-english.srd");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.S710RAW);
        assertEquals(exercise.getUserID(), (byte) 0);
        assertEquals(LocalDateTime.of(2002, 11, 20, 13, 10, 42), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet1");
        assertEquals(exercise.getRecordingMode().isAltitude(), true);
        assertEquals(exercise.getRecordingMode().isSpeed(), true);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertEquals(exercise.getRecordingMode().getBikeNumber(), (byte) 2);
        assertEquals(exercise.getDuration(), (0 * 60 * 60 * 10) + (51 * 60 * 10) + 22 * 10 + 6);
        assertEquals(exercise.getRecordingInterval(), (short) 15);
        assertEquals(exercise.getHeartRateAVG(), (short) 137);
        assertEquals(exercise.getHeartRateMax(), (short) 232);
        assertEquals(Math.round(exercise.getSpeed().getSpeedAVG() * 10), 247);
        assertEquals(Math.round(exercise.getSpeed().getSpeedMax() * 10), 1076);
        assertEquals(exercise.getSpeed().getDistance(), 20921);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), (short) 221);
        assertEquals(exercise.getAltitude().getAltitudeAVG(), (short) 245);
        assertEquals(exercise.getAltitude().getAltitudeMax(), (short) 277);
        assertEquals(exercise.getTemperature().getTemperatureMin(), (short) 3);
        assertEquals(exercise.getTemperature().getTemperatureAVG(), (short) 4);
        assertEquals(exercise.getTemperature().getTemperatureMax(), (short) 15);
        assertEquals(exercise.getEnergy(), 418);
        assertEquals(exercise.getEnergyTotal(), 23508);
        assertEquals(exercise.getSumExerciseTime(), (55 * 60) + 21);
        assertEquals(exercise.getSumRideTime(), (41 * 60) + 45);
        assertEquals(exercise.getOdometer(), 993);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().length, 3);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[0].getLowerHeartRate(), (short) 120);
        assertEquals(exercise.getHeartRateLimits()[0].getUpperHeartRate(), (short) 155);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeBelow(), (0 * 60 * 60) + (2 * 60) + 4);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeWithin(), (0 * 60 * 60) + (45 * 60) + 21);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeAbove(), (0 * 60 * 60) + (3 * 60) + 57);

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[1].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[1].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 1);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeWithin(), (0 * 60 * 60) + (48 * 60) + 51);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeAbove(), (0 * 60 * 60) + (2 * 60) + 30);

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[2].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[2].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 1);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeWithin(), (0 * 60 * 60) + (48 * 60) + 51);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeAbove(), (0 * 60 * 60) + (2 * 60) + 30);

        // check lap data (first, one from middle and last lap only)
        assertEquals(exercise.getLapList().length, 4);
        assertEquals(exercise.getLapList()[0].getTimeSplit(), (0 * 60 * 60 * 10) + (20 * 60 * 10) + (34 * 10) + 6);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit(), (short) 143);
        assertEquals(exercise.getLapList()[0].getHeartRateAVG(), (short) 141);
        assertEquals(exercise.getLapList()[0].getHeartRateMax(), (short) 232);
        assertEquals(Math.round(exercise.getLapList()[0].getSpeed().getSpeedEnd() * 10), 206);
        assertEquals(Math.round(exercise.getLapList()[0].getSpeed().getSpeedAVG() * 10), 230);
        assertEquals(exercise.getLapList()[0].getSpeed().getDistance(), 7886);
        assertEquals(exercise.getLapList()[0].getSpeed().getCadence(), (short) 0);
        assertEquals(exercise.getLapList()[0].getAltitude().getAltitude(), (short) 273);
        assertEquals(exercise.getLapList()[0].getAltitude().getAscent(), 73);
        assertEquals(exercise.getLapList()[0].getTemperature().getTemperature(), (short) 3);

        assertEquals(exercise.getLapList()[1].getTimeSplit(), (0 * 60 * 60 * 10) + (46 * 60 * 10) + (51 * 10) + 2);
        assertEquals(exercise.getLapList()[1].getHeartRateSplit(), (short) 129);
        assertEquals(exercise.getLapList()[1].getHeartRateAVG(), (short) 133);
        assertEquals(exercise.getLapList()[1].getHeartRateMax(), (short) 160);
        assertEquals(Math.round(exercise.getLapList()[1].getSpeed().getSpeedEnd() * 10), 353);
        assertEquals(Math.round(exercise.getLapList()[1].getSpeed().getSpeedAVG() * 10), 253);
        assertEquals(exercise.getLapList()[1].getSpeed().getDistance(), 18990);
        assertEquals(exercise.getLapList()[1].getSpeed().getCadence(), (short) 0);
        assertEquals(exercise.getLapList()[1].getAltitude().getAltitude(), (short) 248);
        assertEquals(exercise.getLapList()[1].getAltitude().getAscent(), 146);
        assertEquals(exercise.getLapList()[1].getTemperature().getTemperature(), (short) 3);

        assertEquals(exercise.getLapList()[3].getTimeSplit(), (0 * 60 * 60 * 10) + (51 * 60 * 10) + (22 * 10) + 6);
        assertEquals(exercise.getLapList()[3].getHeartRateSplit(), (short) 116);
        assertEquals(exercise.getLapList()[3].getHeartRateAVG(), (short) 119);
        assertEquals(exercise.getLapList()[3].getHeartRateMax(), (short) 125);
        assertEquals(Math.round(exercise.getLapList()[3].getSpeed().getSpeedEnd() * 10), 0);
        assertEquals(Math.round(exercise.getLapList()[3].getSpeed().getSpeedAVG() * 10), 0);
        assertEquals(exercise.getLapList()[3].getSpeed().getDistance(), 20921);
        assertEquals(exercise.getLapList()[3].getSpeed().getCadence(), (short) 0);
        assertEquals(exercise.getLapList()[3].getAltitude().getAltitude(), (short) 239);
        assertEquals(exercise.getLapList()[3].getAltitude().getAscent(), 152);
        assertEquals(exercise.getLapList()[3].getTemperature().getTemperature(), (short) 4);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 206);
        assertEquals(0, exercise.getSampleList()[0].getTimestamp());
        assertEquals(exercise.getSampleList()[0].getHeartRate(), (short) 83);
        assertEquals(exercise.getSampleList()[0].getAltitude(), (short) 221);
        assertEquals(Math.round(exercise.getSampleList()[0].getSpeed() * 10), 0 * 10);
        assertEquals(exercise.getSampleList()[0].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getDistance(), 0);

        assertEquals(100 * 15 * 1000, exercise.getSampleList()[100].getTimestamp());
        assertEquals(exercise.getSampleList()[100].getHeartRate(), (short) 124);
        assertEquals(exercise.getSampleList()[100].getAltitude(), (short) 270);
        assertEquals(Math.round(exercise.getSampleList()[100].getSpeed() * 10), 350);
        assertEquals(exercise.getSampleList()[100].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[100].getDistance(), 9972);

        assertEquals(200 * 15 * 1000, exercise.getSampleList()[200].getTimestamp());
        assertEquals(exercise.getSampleList()[200].getHeartRate(), (short) 138);
        assertEquals(exercise.getSampleList()[200].getAltitude(), (short) 242);
        assertEquals(Math.round(exercise.getSampleList()[200].getSpeed() * 10), 291);
        assertEquals(exercise.getSampleList()[200].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[200].getDistance(), 20451);

        assertEquals(205 * 15 * 1000, exercise.getSampleList()[205].getTimestamp());
        assertEquals(exercise.getSampleList()[205].getHeartRate(), (short) 113);
        assertEquals(exercise.getSampleList()[205].getAltitude(), (short) 239);
        assertEquals(Math.round(exercise.getSampleList()[205].getSpeed() * 10), 0);
        assertEquals(exercise.getSampleList()[205].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[205].getDistance(), 20921);
    }

    /**
     * This method tests the parser with an running exercise file
     * recorded in metric units from Polar S710.
     * This test is taken from the C# test class so the code could be better :-)
     */
    @Test
    public void testParseS710RunningExerciseWithMetricUnits() throws EVException {

        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s710/running-metric.srd");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.S710RAW);
        assertEquals(exercise.getUserID(), (byte) 0);
        assertEquals(LocalDateTime.of(2002, 12, 25, 10, 21, 4), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet2");
        assertEquals(exercise.getRecordingMode().isAltitude(), true);
        assertEquals(exercise.getRecordingMode().isSpeed(), false);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertEquals(exercise.getRecordingMode().getBikeNumber(), (byte) 0);
        assertEquals(exercise.getDuration(), (0 * 60 * 60 * 10) + (42 * 60 * 10) + 24 * 10 + 7);
        assertEquals(exercise.getRecordingInterval(), (short) 15);
        assertEquals(exercise.getHeartRateAVG(), (short) 148);
        assertEquals(exercise.getHeartRateMax(), (short) 159);
        assertEquals(exercise.getSpeed(), null);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), (short) 86);
        assertEquals(exercise.getAltitude().getAltitudeAVG(), (short) 93);
        assertEquals(exercise.getAltitude().getAltitudeMax(), (short) 101);
        assertEquals(exercise.getTemperature().getTemperatureMin(), (short) 18);
        assertEquals(exercise.getTemperature().getTemperatureAVG(), (short) 21);
        assertEquals(exercise.getTemperature().getTemperatureMax(), (short) 27);
        assertEquals(exercise.getEnergy(), 399);
        assertEquals(exercise.getEnergyTotal(), 30058);
        assertEquals(exercise.getSumExerciseTime(), (72 * 60) + 7);
        assertEquals(exercise.getSumRideTime(), (51 * 60) + 54);
        assertEquals(exercise.getOdometer(), 1200);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().length, 3);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[0].getLowerHeartRate(), (short) 130);
        assertEquals(exercise.getHeartRateLimits()[0].getUpperHeartRate(), (short) 150);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 54);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeWithin(), (0 * 60 * 60) + (30 * 60) + 36);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeAbove(), (0 * 60 * 60) + (10 * 60) + 54);

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[1].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[1].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 4);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeWithin(), (0 * 60 * 60) + (42 * 60) + 20);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[2].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[2].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 4);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeWithin(), (0 * 60 * 60) + (42 * 60) + 20);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data (one lap only)
        assertEquals(exercise.getLapList().length, 1);
        assertEquals(exercise.getLapList()[0].getTimeSplit(), (0 * 60 * 60 * 10) + (42 * 60 * 10) + (24 * 10) + 7);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit(), (short) 146);
        assertEquals(exercise.getLapList()[0].getHeartRateAVG(), (short) 148);
        assertEquals(exercise.getLapList()[0].getHeartRateMax(), (short) 159);
        assertEquals(exercise.getLapList()[0].getSpeed(), null);
        assertEquals(exercise.getLapList()[0].getAltitude().getAltitude(), (short) 88);
        assertEquals(exercise.getLapList()[0].getAltitude().getAscent(), 20);
        assertEquals(exercise.getLapList()[0].getTemperature().getTemperature(), (short) 19);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 170);
        assertEquals(0 * 15 * 1000, exercise.getSampleList()[0].getTimestamp());
        assertEquals(exercise.getSampleList()[0].getHeartRate(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getAltitude(), (short) 91);
        assertEquals(exercise.getSampleList()[0].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[0].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getDistance(), 0);

        assertEquals(100 * 15 * 1000, exercise.getSampleList()[100].getTimestamp());
        assertEquals(exercise.getSampleList()[100].getHeartRate(), (short) 149);
        assertEquals(exercise.getSampleList()[100].getAltitude(), (short) 98);
        assertEquals(exercise.getSampleList()[100].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[100].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[100].getDistance(), 0);

        assertEquals(150 * 15 * 1000, exercise.getSampleList()[150].getTimestamp());
        assertEquals(exercise.getSampleList()[150].getHeartRate(), (short) 142);
        assertEquals(exercise.getSampleList()[150].getAltitude(), (short) 89);
        assertEquals(exercise.getSampleList()[150].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[150].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[150].getDistance(), 0);

        assertEquals(169 * 15 * 1000, exercise.getSampleList()[169].getTimestamp());
        assertEquals(exercise.getSampleList()[169].getHeartRate(), (short) 147);
        assertEquals(exercise.getSampleList()[169].getAltitude(), (short) 88);
        assertEquals(exercise.getSampleList()[169].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[169].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[169].getDistance(), 0);
    }

    /**
     * This method tests the parser with an cycling exercise file
     * recorded in metric units from Polar S725.
     * This test is taken from the C# test class so the code could be better :-)
     */
    @Test
    public void testParseS725CyclingExerciseWithMetricUnits() throws EVException {

        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s725/cycling-metric.srd");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.S710RAW);
        assertEquals(exercise.getUserID(), (byte) 1);
        assertEquals(LocalDateTime.of(2005, 4, 16, 9, 56, 32), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet1");
        assertEquals(exercise.getRecordingMode().isAltitude(), true);
        assertEquals(exercise.getRecordingMode().isSpeed(), true);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertEquals(exercise.getRecordingMode().getBikeNumber(), (byte) 1);
        assertEquals(exercise.getDuration(), (5 * 60 * 60 * 10) + (9 * 60 * 10) + 58 * 10 + 5);
        assertEquals(exercise.getRecordingInterval(), (short) 5);
        assertEquals(exercise.getHeartRateAVG(), (short) 134);
        assertEquals(exercise.getHeartRateMax(), (short) 232);                      // recording error due to electric tram
        assertEquals(Math.round(exercise.getSpeed().getSpeedAVG() * 10), 246);
        assertEquals(Math.round(exercise.getSpeed().getSpeedMax() * 10), 1103); // recording error due to electric tram
        assertEquals(exercise.getSpeed().getDistance(), 111700);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), (short) 174);
        assertEquals(exercise.getAltitude().getAltitudeAVG(), (short) 276);
        assertEquals(exercise.getAltitude().getAltitudeMax(), (short) 403);
        assertEquals(exercise.getTemperature().getTemperatureMin(), (short) 19);
        assertEquals(exercise.getTemperature().getTemperatureAVG(), (short) 22);
        assertEquals(exercise.getTemperature().getTemperatureMax(), (short) 33);
        assertEquals(exercise.getEnergy(), 2344);
        assertEquals(exercise.getEnergyTotal(), 2344);  // first recorded exercise, so the sums are low
        assertEquals(exercise.getSumExerciseTime(), (5 * 60) + 9);
        assertEquals(exercise.getSumRideTime(), (4 * 60) + 32);
        assertEquals(exercise.getOdometer(), 111);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().length, 3);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[0].getLowerHeartRate(), (short) 120);
        assertEquals(exercise.getHeartRateLimits()[0].getUpperHeartRate(), (short) 155);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeBelow(), (1 * 60 * 60) + (0 * 60) + 55);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeWithin(), (3 * 60 * 60) + (59 * 60) + 21);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeAbove(), (0 * 60 * 60) + (9 * 60) + 42);

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[1].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[1].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[2].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[2].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data (just 2 laps)
        assertEquals(exercise.getLapList().length, 2);
        assertEquals(exercise.getLapList()[0].getTimeSplit(), (2 * 60 * 60 * 10) + (24 * 60 * 10) + (19 * 10) + 9);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit(), (short) 122);
        assertEquals(exercise.getLapList()[0].getHeartRateAVG(), (short) 133);
        assertEquals(exercise.getLapList()[0].getHeartRateMax(), (short) 229);
        assertEquals(Math.round(exercise.getLapList()[0].getSpeed().getSpeedEnd() * 10), 0 * 10);
        assertEquals(Math.round(exercise.getLapList()[0].getSpeed().getSpeedAVG() * 10), 202);
        assertEquals(exercise.getLapList()[0].getSpeed().getDistance(), 48600);
        assertEquals(exercise.getLapList()[0].getSpeed().getCadence(), (short) 0);
        assertEquals(exercise.getLapList()[0].getAltitude().getAltitude(), (short) 392);
        assertEquals(exercise.getLapList()[0].getAltitude().getAscent(), 675);
        assertEquals(exercise.getLapList()[0].getTemperature().getTemperature(), (short) 20);

        assertEquals(exercise.getLapList()[1].getTimeSplit(), (5 * 60 * 60 * 10) + (9 * 60 * 10) + (58 * 10) + 5);
        assertEquals(exercise.getLapList()[1].getHeartRateSplit(), (short) 123);
        assertEquals(exercise.getLapList()[1].getHeartRateAVG(), (short) 135);
        assertEquals(exercise.getLapList()[1].getHeartRateMax(), (short) 232);
        assertEquals(Math.round(exercise.getLapList()[1].getSpeed().getSpeedEnd() * 10), 38);
        assertEquals(Math.round(exercise.getLapList()[1].getSpeed().getSpeedAVG() * 10), 229);
        assertEquals(exercise.getLapList()[1].getSpeed().getDistance(), 111700);
        assertEquals(exercise.getLapList()[1].getSpeed().getCadence(), (short) 0);
        assertEquals(exercise.getLapList()[1].getAltitude().getAltitude(), (short) 244);
        assertEquals(exercise.getLapList()[1].getAltitude().getAscent(), 1255);
        assertEquals(exercise.getLapList()[1].getTemperature().getTemperature(), (short) 25);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 3720);
        assertEquals(0, exercise.getSampleList()[0].getTimestamp());
        assertEquals(exercise.getSampleList()[0].getHeartRate(), (short) 81);
        assertEquals(exercise.getSampleList()[0].getAltitude(), (short) 219);
        assertEquals(Math.round(exercise.getSampleList()[0].getSpeed() * 10), 0 * 10);
        assertEquals(exercise.getSampleList()[0].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getDistance(), 0);

        assertEquals(1020 * 5 * 1000, exercise.getSampleList()[1020].getTimestamp());
        assertEquals(exercise.getSampleList()[1020].getHeartRate(), (short) 134);
        assertEquals(exercise.getSampleList()[1020].getAltitude(), (short) 190);
        assertEquals(Math.round(exercise.getSampleList()[1020].getSpeed() * 10), 246);
        assertEquals(exercise.getSampleList()[1020].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[1020].getDistance(), 30927);

        assertEquals(2880 * 5 * 1000, exercise.getSampleList()[2880].getTimestamp());
        assertEquals(exercise.getSampleList()[2880].getHeartRate(), (short) 131);
        assertEquals(exercise.getSampleList()[2880].getAltitude(), (short) 276);
        assertEquals(Math.round(exercise.getSampleList()[2880].getSpeed() * 10), 383);
        assertEquals(exercise.getSampleList()[2880].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[2880].getDistance(), 79820);

        assertEquals(3719 * 5 * 1000, exercise.getSampleList()[3719].getTimestamp());
        assertEquals(exercise.getSampleList()[3719].getHeartRate(), (short) 123);
        assertEquals(exercise.getSampleList()[3719].getAltitude(), (short) 243);
        assertEquals(Math.round(exercise.getSampleList()[3719].getSpeed() * 10), 54);
        assertEquals(exercise.getSampleList()[3719].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[3719].getDistance(), 111700);
    }

    /**
     * This method tests the parser with an no-speed (same as running) exercise
     * file recorded in metric units from Polar S725.
     * This test is taken from the C# test class so the code could be better :-)
     */
    @Test
    public void testParseS725NoSpeedExerciseWithMetricUnits() throws EVException {

        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s725/nospeed-metric.srd");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.S710RAW);
        assertEquals(exercise.getUserID(), (byte) 1);
        assertEquals(LocalDateTime.of(2005, 4, 17, 8, 59, 3), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet1");
        assertEquals(exercise.getRecordingMode().isAltitude(), true);
        assertEquals(exercise.getRecordingMode().isSpeed(), false);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertEquals(exercise.getRecordingMode().getBikeNumber(), (byte) 0);
        assertEquals(exercise.getDuration(), (2 * 60 * 60 * 10) + (29 * 60 * 10) + 1 * 10 + 9);
        assertEquals(exercise.getRecordingInterval(), (short) 5);
        assertEquals(exercise.getHeartRateAVG(), (short) 112);
        assertEquals(exercise.getHeartRateMax(), (short) 147);
        assertEquals(exercise.getSpeed(), null);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), (short) 142);
        assertEquals(exercise.getAltitude().getAltitudeAVG(), (short) 186);
        assertEquals(exercise.getAltitude().getAltitudeMax(), (short) 284);
        assertEquals(exercise.getTemperature().getTemperatureMin(), (short) 16);
        assertEquals(exercise.getTemperature().getTemperatureAVG(), (short) 18);
        assertEquals(exercise.getTemperature().getTemperatureMax(), (short) 27);
        assertEquals(exercise.getEnergy(), 806);
        assertEquals(exercise.getEnergyTotal(), 3150);
        assertEquals(exercise.getSumExerciseTime(), (7 * 60) + 38);
        assertEquals(exercise.getSumRideTime(), (4 * 60) + 32);
        assertEquals(exercise.getOdometer(), 111);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().length, 3);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[0].getLowerHeartRate(), (short) 120);
        assertEquals(exercise.getHeartRateLimits()[0].getUpperHeartRate(), (short) 155);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeBelow(), (1 * 60 * 60) + (43 * 60) + 51);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeWithin(), (0 * 60 * 60) + (44 * 60) + 59);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[1].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[1].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 7); // ==> shoudn't this be 0 ?
        assertEquals(exercise.getHeartRateLimits()[1].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[2].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[2].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 4); // ==> shoudn't this be 0 ?
        assertEquals(exercise.getHeartRateLimits()[2].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data (first and last lap only)
        assertEquals(exercise.getLapList().length, 3);
        assertEquals(exercise.getLapList()[0].getTimeSplit(), (1 * 60 * 60 * 10) + (31 * 60 * 10) + (8 * 10) + 8);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit(), (short) 70);
        assertEquals(exercise.getLapList()[0].getHeartRateAVG(), (short) 112);
        assertEquals(exercise.getLapList()[0].getHeartRateMax(), (short) 147);
        assertEquals(exercise.getLapList()[0].getSpeed(), null);
        assertEquals(exercise.getLapList()[0].getAltitude().getAltitude(), (short) 174);
        assertEquals(exercise.getLapList()[0].getAltitude().getAscent(), 160);
        assertEquals(exercise.getLapList()[0].getTemperature().getTemperature(), (short) 17);

        assertEquals(exercise.getLapList()[2].getTimeSplit(), (2 * 60 * 60 * 10) + (29 * 60 * 10) + (1 * 10) + 9);
        assertEquals(exercise.getLapList()[2].getHeartRateSplit(), (short) 86);
        assertEquals(exercise.getLapList()[2].getHeartRateAVG(), (short) 117);
        assertEquals(exercise.getLapList()[2].getHeartRateMax(), (short) 142);
        assertEquals(exercise.getLapList()[2].getSpeed(), null);
        assertEquals(exercise.getLapList()[2].getAltitude().getAltitude(), (short) 281);
        assertEquals(exercise.getLapList()[2].getAltitude().getAscent(), 315);
        assertEquals(exercise.getLapList()[2].getTemperature().getTemperature(), (short) 21);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 1789);
        assertEquals(0, exercise.getSampleList()[0].getTimestamp());
        assertEquals(exercise.getSampleList()[0].getHeartRate(), (short) 76);
        assertEquals(exercise.getSampleList()[0].getAltitude(), (short) 274);
        assertEquals(exercise.getSampleList()[0].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[0].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[0].getDistance(), 0);

        assertEquals(720 * 5 * 1000, exercise.getSampleList()[720].getTimestamp());
        assertEquals(exercise.getSampleList()[720].getHeartRate(), (short) 125);
        assertEquals(exercise.getSampleList()[720].getAltitude(), (short) 202);
        assertEquals(exercise.getSampleList()[720].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[720].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[720].getDistance(), 0);

        assertEquals(1440 * 5 * 1000, exercise.getSampleList()[1440].getTimestamp());
        assertEquals(exercise.getSampleList()[1440].getAltitude(), (short) 143);
        assertEquals(exercise.getSampleList()[1440].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[1440].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[1440].getDistance(), 0);

        assertEquals(1788 * 5 * 1000, exercise.getSampleList()[1788].getTimestamp());
        assertEquals(exercise.getSampleList()[1788].getHeartRate(), (short) 86);
        assertEquals(exercise.getSampleList()[1788].getAltitude(), (short) 281);
        assertEquals(exercise.getSampleList()[1788].getSpeed(), 0f, 0f);
        assertEquals(exercise.getSampleList()[1788].getCadence(), (short) 0);
        assertEquals(exercise.getSampleList()[1788].getDistance(), 0);
    }

    /**
     * This method tests the parser with an no-speed (same as running) exercise
     * file recorded in metric units from Polar S725.
     * This test is taken from the C# test class so the code could be better :-)
     */
    @Test
    public void testParseS625PercentualHeartRateRanges() throws EVException {

        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s625x/20080224T113030-percentual_ranges.srd");

        // check exercise data (only the relevant heartrate range values)
        assertEquals(exercise.getHeartRateAVG(), (short) 146);
        assertEquals(exercise.getHeartRateMax(), (short) 177);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().length, 3);
        assertFalse(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[0].getLowerHeartRate(), (short) 70);
        assertEquals(exercise.getHeartRateLimits()[0].getUpperHeartRate(), (short) 80);

        assertFalse(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[1].getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits()[1].getUpperHeartRate(), (short) 90);

        assertFalse(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[2].getLowerHeartRate(), (short) 90);
        assertEquals(exercise.getHeartRateLimits()[2].getUpperHeartRate(), (short) 100);

        // check some lap data 
        assertEquals(exercise.getLapList().length, 12);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit(), (short) 141);
        assertEquals(exercise.getLapList()[10].getHeartRateSplit(), (short) 130);

        // check some sample data 
        assertEquals(exercise.getSampleList()[0].getHeartRate(), (short) 116);
        assertEquals(exercise.getSampleList()[10].getHeartRate(), (short) 135);
    }
}
