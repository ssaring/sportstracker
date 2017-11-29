package de.saring.exerciseviewer.parser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import de.saring.exerciseviewer.core.EVException;
import org.junit.Before;
import org.junit.Test;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;

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
        assertEquals("Polar S6xx/S7xx Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2004, 9, 12, 7, 26, 7), exercise.getDateTime());
        assertEquals(exercise.getType(), "TB2    ");
        assertTrue(exercise.getRecordingMode().isHeartRate());
        assertEquals(exercise.getRecordingMode().isAltitude(), false);
        assertEquals(exercise.getRecordingMode().isSpeed(), false);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertNull(exercise.getRecordingMode().getBikeNumber());
        assertEquals(exercise.getDuration().intValue(), (1 * 60 * 60 * 10) + (36 * 60 * 10) + 50 * 10 + 8);
        assertEquals(exercise.getRecordingInterval().intValue(), 5);
        assertEquals(exercise.getHeartRateAVG().intValue(), 158);
        assertEquals(exercise.getHeartRateMax().intValue(), 176);
        assertEquals(exercise.getSpeed(), null);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude(), null);
        assertEquals(exercise.getTemperature(), null);
        assertEquals(exercise.getEnergy().intValue(), 1214);
        assertEquals(exercise.getEnergyTotal().intValue(), 22552);
        assertEquals(exercise.getSumExerciseTime().intValue(), (25 * 60) + 58);
        assertNull(exercise.getSumRideTime());
        assertNull(exercise.getOdometer());

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().size(), 3);
        assertTrue(exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(0).getLowerHeartRate(), (short) 143);
        assertEquals(exercise.getHeartRateLimits().get(0).getUpperHeartRate(), (short) 162);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeBelow().intValue(), (0 * 60 * 60) + (2 * 60) + 11);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeWithin(), (1 * 60 * 60) + (17 * 60) + 34);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeAbove().intValue(), (0 * 60 * 60) + (17 * 60) + 5);

        assertTrue(exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(1).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(1).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(2).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(2).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data (first and last lap only)
        assertEquals(exercise.getLapList().size(), 3);
        assertEquals(exercise.getLapList().get(0).getTimeSplit(), (0 * 60 * 60 * 10) + (50 * 60 * 10) + (17 * 10) + 2);
        assertEquals(exercise.getLapList().get(0).getHeartRateSplit().intValue(), 165);
        assertEquals(exercise.getLapList().get(0).getHeartRateAVG().intValue(), 157);
        assertEquals(exercise.getLapList().get(0).getHeartRateMax().intValue(), 176);
        assertEquals(exercise.getLapList().get(0).getSpeed(), null);
        assertEquals(exercise.getLapList().get(0).getAltitude(), null);
        assertEquals(exercise.getLapList().get(0).getTemperature(), null);

        assertEquals(exercise.getLapList().get(2).getTimeSplit(), (1 * 60 * 60 * 10) + (36 * 60 * 10) + (50 * 10) + 8);
        assertEquals(exercise.getLapList().get(2).getHeartRateSplit().intValue(), 159);
        assertEquals(exercise.getLapList().get(2).getHeartRateAVG().intValue(), 160);
        assertEquals(exercise.getLapList().get(2).getHeartRateMax().intValue(), 171);
        assertEquals(exercise.getLapList().get(2).getSpeed(), null);
        assertEquals(exercise.getLapList().get(2).getAltitude(), null);
        assertEquals(exercise.getLapList().get(2).getTemperature(), null);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().size(), 1163);
        assertEquals(0L, exercise.getSampleList().get(0).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(0).getHeartRate().intValue(), 109);
        assertNull(exercise.getSampleList().get(0).getAltitude());
        assertNull(exercise.getSampleList().get(0).getSpeed());
        assertNull(exercise.getSampleList().get(0).getCadence());
        assertNull(exercise.getSampleList().get(0).getDistance());

        assertEquals(240 * 5 * 1000L, exercise.getSampleList().get(240).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(240).getHeartRate().intValue(), 160);
        assertNull(exercise.getSampleList().get(240).getAltitude());
        assertNull(exercise.getSampleList().get(240).getSpeed());
        assertNull(exercise.getSampleList().get(240).getCadence());
        assertNull(exercise.getSampleList().get(240).getDistance());

        assertEquals(480 * 5 * 1000L, exercise.getSampleList().get(480).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(480).getHeartRate().intValue(), 161);
        assertNull(exercise.getSampleList().get(480).getAltitude());
        assertNull(exercise.getSampleList().get(480).getSpeed());
        assertNull(exercise.getSampleList().get(480).getCadence());
        assertNull(exercise.getSampleList().get(480).getDistance());

        assertEquals(1162 * 5 * 1000L, exercise.getSampleList().get(1162).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(1162).getHeartRate().intValue(), 159);
        assertNull(exercise.getSampleList().get(1162).getAltitude());
        assertNull(exercise.getSampleList().get(1162).getSpeed());
        assertNull(exercise.getSampleList().get(1162).getCadence());
        assertNull(exercise.getSampleList().get(1162).getDistance());
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
        assertEquals("Polar S6xx/S7xx Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2002, 11, 20, 14, 7, 44), exercise.getDateTime());
        assertEquals("ExeSet1", exercise.getType());
        assertEquals((1 * 60 * 60 * 10) + (13 * 60 * 10) + 34 * 10 + 3, exercise.getDuration().intValue());
        assertTrue(exercise.getRecordingMode().isHeartRate());
        assertTrue(exercise.getRecordingMode().isAltitude());
        assertTrue(exercise.getRecordingMode().isSpeed());
        assertFalse(exercise.getRecordingMode().isCadence());
        assertFalse(exercise.getRecordingMode().isPower());
        assertEquals(2, exercise.getRecordingMode().getBikeNumber().intValue());
        assertEquals(15, exercise.getRecordingInterval().intValue());
        assertEquals(135, exercise.getHeartRateAVG().intValue());
        assertEquals(232, exercise.getHeartRateMax().intValue());
        assertEquals(251, Math.round(exercise.getSpeed().getSpeedAvg() * 10));
        assertEquals(1093, Math.round(exercise.getSpeed().getSpeedMax() * 10));
        assertEquals(29900, exercise.getSpeed().getDistance());
        assertNull(exercise.getCadence());
        assertEquals((short) 215, exercise.getAltitude().getAltitudeMin(), 215);
        assertEquals((short) 253, exercise.getAltitude().getAltitudeAvg(), 253);
        assertEquals((short) 300, exercise.getAltitude().getAltitudeMax(), 300);
        assertEquals((short) 3, exercise.getTemperature().getTemperatureMin(), 3);
        assertEquals((short) 3, exercise.getTemperature().getTemperatureAvg(), 3);
        assertEquals((short) 5, exercise.getTemperature().getTemperatureMax(), 5);
        assertEquals(591, exercise.getEnergy().intValue());
        assertEquals(24099, exercise.getEnergyTotal().intValue());
        assertEquals((56 * 60) + 34, exercise.getSumExerciseTime().intValue());
        assertEquals((42 * 60) + 56, exercise.getSumRideTime().intValue());
        assertEquals(1024, exercise.getOdometer().intValue());

        // check heart rate limits
        assertEquals(3, exercise.getHeartRateLimits().size(), 3);
        assertTrue(exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertEquals((short) 120, exercise.getHeartRateLimits().get(0).getLowerHeartRate(), 120);
        assertEquals((short) 155, exercise.getHeartRateLimits().get(0).getUpperHeartRate(), 155);
        assertEquals((0 * 60 * 60) + (5 * 60) + 32, exercise.getHeartRateLimits().get(0).getTimeBelow().intValue());
        assertEquals((1 * 60 * 60) + (3 * 60) + 19, exercise.getHeartRateLimits().get(0).getTimeWithin());
        assertEquals((0 * 60 * 60) + (4 * 60) + 43, exercise.getHeartRateLimits().get(0).getTimeAbove().intValue());

        assertTrue(exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertEquals((short) 80, exercise.getHeartRateLimits().get(1).getLowerHeartRate(), 80);
        assertEquals((short) 160, exercise.getHeartRateLimits().get(1).getUpperHeartRate(), 160);
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.getHeartRateLimits().get(1).getTimeBelow().intValue());
        assertEquals((1 * 60 * 60) + (10 * 60) + 55, exercise.getHeartRateLimits().get(1).getTimeWithin());
        assertEquals((0 * 60 * 60) + (2 * 60) + 39, exercise.getHeartRateLimits().get(1).getTimeAbove().intValue());

        assertTrue(exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertEquals((short) 80, exercise.getHeartRateLimits().get(2).getLowerHeartRate(), 80);
        assertEquals((short) 160, exercise.getHeartRateLimits().get(2).getUpperHeartRate(), 160);
        assertEquals((0 * 60 * 60) + (0 * 60) + 0, exercise.getHeartRateLimits().get(2).getTimeBelow().intValue());
        assertEquals((1 * 60 * 60) + (10 * 60) + 55, exercise.getHeartRateLimits().get(2).getTimeWithin());
        assertEquals((0 * 60 * 60) + (2 * 60) + 39, exercise.getHeartRateLimits().get(2).getTimeAbove().intValue());

        // check lap data (first, one from middle and last lap only)
        assertEquals(5, exercise.getLapList().size());
        assertEquals((0 * 60 * 60 * 10) + (06 * 60 * 10) + (59 * 10) + 2, exercise.getLapList().get(0).getTimeSplit());
        assertEquals(136, exercise.getLapList().get(0).getHeartRateSplit().intValue());
        assertEquals(128, exercise.getLapList().get(0).getHeartRateAVG().intValue());
        assertEquals(152, exercise.getLapList().get(0).getHeartRateMax().intValue());
        assertEquals(141, Math.round(exercise.getLapList().get(0).getSpeed().getSpeedEnd() * 10));
        assertEquals(258, Math.round(exercise.getLapList().get(0).getSpeed().getSpeedAVG() * 10));
        assertEquals(3 * 1000, exercise.getLapList().get(0).getSpeed().getDistance());
        assertNull(exercise.getLapList().get(0).getSpeed().getCadence());
        assertEquals((short) 231, exercise.getLapList().get(0).getAltitude().getAltitude());
        assertEquals(25, exercise.getLapList().get(0).getAltitude().getAscent());
        assertEquals((short) 4, exercise.getLapList().get(0).getTemperature().getTemperature());

        assertEquals((0 * 60 * 60 * 10) + (40 * 60 * 10) + (18 * 10) + 8, exercise.getLapList().get(2).getTimeSplit());
        assertEquals(136, exercise.getLapList().get(2).getHeartRateSplit().intValue());
        assertEquals(134, exercise.getLapList().get(2).getHeartRateAVG().intValue());
        assertEquals(168, exercise.getLapList().get(2).getHeartRateMax().intValue());
        assertEquals(193, Math.round(exercise.getLapList().get(2).getSpeed().getSpeedEnd() * 10));
        assertEquals(242, Math.round(exercise.getLapList().get(2).getSpeed().getSpeedAVG() * 10));
        assertEquals(15700, exercise.getLapList().get(2).getSpeed().getDistance());
        assertNull(exercise.getLapList().get(2).getSpeed().getCadence());
        assertEquals((short) 247, exercise.getLapList().get(2).getAltitude().getAltitude());
        assertEquals(135, exercise.getLapList().get(2).getAltitude().getAscent());
        assertEquals((short) 4, exercise.getLapList().get(2).getTemperature().getTemperature());

        assertEquals((1 * 60 * 60 * 10) + (13 * 60 * 10) + (34 * 10) + 3, exercise.getLapList().get(4).getTimeSplit());
        assertEquals(123, exercise.getLapList().get(4).getHeartRateSplit().intValue());
        assertEquals(121, exercise.getLapList().get(4).getHeartRateAVG().intValue());
        assertEquals(123, exercise.getLapList().get(4).getHeartRateMax().intValue());
        assertEquals(0 * 10, Math.round(exercise.getLapList().get(4).getSpeed().getSpeedEnd() * 10));
        assertEquals(0 * 10, Math.round(exercise.getLapList().get(4).getSpeed().getSpeedAVG() * 10));
        assertEquals(29900, exercise.getLapList().get(4).getSpeed().getDistance());
        assertNull(exercise.getLapList().get(4).getSpeed().getCadence());
        assertEquals((short) 229, exercise.getLapList().get(4).getAltitude().getAltitude());
        assertEquals(240, exercise.getLapList().get(4).getAltitude().getAscent());
        assertEquals((short) 4, exercise.getLapList().get(4).getTemperature().getTemperature());

        // check sample data (first, two from middle and last only)
        assertEquals(295, exercise.getSampleList().size());
        assertEquals(0L, exercise.getSampleList().get(0).getTimestamp().longValue());
        assertEquals(101, exercise.getSampleList().get(0).getHeartRate().intValue());
        assertEquals(240, exercise.getSampleList().get(0).getAltitude().intValue());
        assertEquals(42, Math.round(exercise.getSampleList().get(0).getSpeed() * 10));
        assertNull(exercise.getSampleList().get(0).getCadence());
        assertEquals(0, exercise.getSampleList().get(0).getDistance().intValue());

        assertEquals(100 * 15 * 1000L, exercise.getSampleList().get(100).getTimestamp().longValue());
        assertEquals(147, exercise.getSampleList().get(100).getHeartRate().intValue());
        assertEquals(278, exercise.getSampleList().get(100).getAltitude().intValue());
        assertEquals(171, Math.round(exercise.getSampleList().get(100).getSpeed() * 10));
        assertNull(exercise.getSampleList().get(100).getCadence());
        assertEquals(9479, exercise.getSampleList().get(100).getDistance().intValue());

        assertEquals(200 * 15 * 1000L, exercise.getSampleList().get(200).getTimestamp().longValue());
        assertEquals(166, exercise.getSampleList().get(200).getHeartRate().intValue());
        assertEquals(275, exercise.getSampleList().get(200).getAltitude().intValue());
        assertEquals(141, Math.round(exercise.getSampleList().get(200).getSpeed() * 10));
        assertNull(exercise.getSampleList().get(200).getCadence());
        assertEquals(19256, exercise.getSampleList().get(200).getDistance().intValue());

        assertEquals(294 * 15 * 1000L, exercise.getSampleList().get(294).getTimestamp().longValue());
        assertEquals(123, exercise.getSampleList().get(294).getHeartRate().intValue());
        assertEquals(229, exercise.getSampleList().get(294).getAltitude().intValue());
        assertEquals(0, Math.round(exercise.getSampleList().get(294).getSpeed() * 10));
        assertNull(exercise.getSampleList().get(294).getCadence());
        assertEquals(29900, exercise.getSampleList().get(294).getDistance().intValue());
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
        assertEquals("Polar S6xx/S7xx Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2002, 11, 20, 13, 10, 42), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet1");
        assertTrue(exercise.getRecordingMode().isHeartRate());
        assertEquals(exercise.getRecordingMode().isAltitude(), true);
        assertEquals(exercise.getRecordingMode().isSpeed(), true);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertEquals(2, exercise.getRecordingMode().getBikeNumber().intValue());
        assertEquals(exercise.getDuration().intValue(), (0 * 60 * 60 * 10) + (51 * 60 * 10) + 22 * 10 + 6);
        assertEquals(exercise.getRecordingInterval().intValue(), 15);
        assertEquals(exercise.getHeartRateAVG().intValue(), 137);
        assertEquals(exercise.getHeartRateMax().intValue(), 232);
        assertEquals(Math.round(exercise.getSpeed().getSpeedAvg() * 10), 247);
        assertEquals(Math.round(exercise.getSpeed().getSpeedMax() * 10), 1076);
        assertEquals(exercise.getSpeed().getDistance(), 20921);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), (short) 221);
        assertEquals(exercise.getAltitude().getAltitudeAvg(), (short) 245);
        assertEquals(exercise.getAltitude().getAltitudeMax(), (short) 277);
        assertEquals(exercise.getTemperature().getTemperatureMin(), (short) 3);
        assertEquals(exercise.getTemperature().getTemperatureAvg(), (short) 4);
        assertEquals(exercise.getTemperature().getTemperatureMax(), (short) 15);
        assertEquals(exercise.getEnergy().intValue(), 418);
        assertEquals(exercise.getEnergyTotal().intValue(), 23508);
        assertEquals(exercise.getSumExerciseTime().intValue(), (55 * 60) + 21);
        assertEquals(exercise.getSumRideTime().intValue(), (41 * 60) + 45);
        assertEquals(exercise.getOdometer().intValue(), 993);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().size(), 3);
        assertTrue(exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(0).getLowerHeartRate(), (short) 120);
        assertEquals(exercise.getHeartRateLimits().get(0).getUpperHeartRate(), (short) 155);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeBelow().intValue(), (0 * 60 * 60) + (2 * 60) + 4);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeWithin(), (0 * 60 * 60) + (45 * 60) + 21);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeAbove().intValue(), (0 * 60 * 60) + (3 * 60) + 57);

        assertTrue(exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(1).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(1).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 1);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeWithin(), (0 * 60 * 60) + (48 * 60) + 51);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeAbove().intValue(), (0 * 60 * 60) + (2 * 60) + 30);

        assertTrue(exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(2).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(2).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 1);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeWithin(), (0 * 60 * 60) + (48 * 60) + 51);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeAbove().intValue(), (0 * 60 * 60) + (2 * 60) + 30);

        // check lap data (first, one from middle and last lap only)
        assertEquals(exercise.getLapList().size(), 4);
        assertEquals(exercise.getLapList().get(0).getTimeSplit(), (0 * 60 * 60 * 10) + (20 * 60 * 10) + (34 * 10) + 6);
        assertEquals(exercise.getLapList().get(0).getHeartRateSplit().intValue(), 143);
        assertEquals(exercise.getLapList().get(0).getHeartRateAVG().intValue(), 141);
        assertEquals(exercise.getLapList().get(0).getHeartRateMax().intValue(), 232);
        assertEquals(Math.round(exercise.getLapList().get(0).getSpeed().getSpeedEnd() * 10), 206);
        assertEquals(Math.round(exercise.getLapList().get(0).getSpeed().getSpeedAVG() * 10), 230);
        assertEquals(exercise.getLapList().get(0).getSpeed().getDistance(), 7886);
        assertNull(exercise.getLapList().get(0).getSpeed().getCadence());
        assertEquals(exercise.getLapList().get(0).getAltitude().getAltitude(), (short) 273);
        assertEquals(exercise.getLapList().get(0).getAltitude().getAscent(), 73);
        assertEquals(exercise.getLapList().get(0).getTemperature().getTemperature(), (short) 3);

        assertEquals(exercise.getLapList().get(1).getTimeSplit(), (0 * 60 * 60 * 10) + (46 * 60 * 10) + (51 * 10) + 2);
        assertEquals(exercise.getLapList().get(1).getHeartRateSplit().intValue(), 129);
        assertEquals(exercise.getLapList().get(1).getHeartRateAVG().intValue(), 133);
        assertEquals(exercise.getLapList().get(1).getHeartRateMax().intValue(), 160);
        assertEquals(Math.round(exercise.getLapList().get(1).getSpeed().getSpeedEnd() * 10), 353);
        assertEquals(Math.round(exercise.getLapList().get(1).getSpeed().getSpeedAVG() * 10), 253);
        assertEquals(exercise.getLapList().get(1).getSpeed().getDistance(), 18990);
        assertNull(exercise.getLapList().get(1).getSpeed().getCadence());
        assertEquals(exercise.getLapList().get(1).getAltitude().getAltitude(), (short) 248);
        assertEquals(exercise.getLapList().get(1).getAltitude().getAscent(), 146);
        assertEquals(exercise.getLapList().get(1).getTemperature().getTemperature(), (short) 3);

        assertEquals(exercise.getLapList().get(3).getTimeSplit(), (0 * 60 * 60 * 10) + (51 * 60 * 10) + (22 * 10) + 6);
        assertEquals(exercise.getLapList().get(3).getHeartRateSplit().intValue(), 116);
        assertEquals(exercise.getLapList().get(3).getHeartRateAVG().intValue(), 119);
        assertEquals(exercise.getLapList().get(3).getHeartRateMax().intValue(), 125);
        assertEquals(Math.round(exercise.getLapList().get(3).getSpeed().getSpeedEnd() * 10), 0);
        assertEquals(Math.round(exercise.getLapList().get(3).getSpeed().getSpeedAVG() * 10), 0);
        assertEquals(exercise.getLapList().get(3).getSpeed().getDistance(), 20921);
        assertNull(exercise.getLapList().get(3).getSpeed().getCadence());
        assertEquals(exercise.getLapList().get(3).getAltitude().getAltitude(), (short) 239);
        assertEquals(exercise.getLapList().get(3).getAltitude().getAscent(), 152);
        assertEquals(exercise.getLapList().get(3).getTemperature().getTemperature(), (short) 4);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().size(), 206);
        assertEquals(0L, exercise.getSampleList().get(0).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(0).getHeartRate().intValue(), 83);
        assertEquals(exercise.getSampleList().get(0).getAltitude().intValue(), 221);
        assertEquals(Math.round(exercise.getSampleList().get(0).getSpeed() * 10), 0 * 10);
        assertNull(exercise.getSampleList().get(0).getCadence());
        assertEquals(exercise.getSampleList().get(0).getDistance().intValue(), 0);

        assertEquals(100 * 15 * 1000L, exercise.getSampleList().get(100).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(100).getHeartRate().intValue(), 124);
        assertEquals(exercise.getSampleList().get(100).getAltitude().intValue(), 270);
        assertEquals(Math.round(exercise.getSampleList().get(100).getSpeed() * 10), 350);
        assertNull(exercise.getSampleList().get(100).getCadence());
        assertEquals(exercise.getSampleList().get(100).getDistance().intValue(), 9972);

        assertEquals(200 * 15 * 1000L, exercise.getSampleList().get(200).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(200).getHeartRate().intValue(), 138);
        assertEquals(exercise.getSampleList().get(200).getAltitude().intValue(), 242);
        assertEquals(Math.round(exercise.getSampleList().get(200).getSpeed() * 10), 291);
        assertNull(exercise.getSampleList().get(200).getCadence());
        assertEquals(exercise.getSampleList().get(200).getDistance().intValue(), 20451);

        assertEquals(205 * 15 * 1000L, exercise.getSampleList().get(205).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(205).getHeartRate().intValue(), 113);
        assertEquals(exercise.getSampleList().get(205).getAltitude().intValue(), 239);
        assertEquals(Math.round(exercise.getSampleList().get(205).getSpeed() * 10), 0);
        assertNull(exercise.getSampleList().get(205).getCadence());
        assertEquals(exercise.getSampleList().get(205).getDistance().intValue(), 20921);
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
        assertEquals("Polar S6xx/S7xx Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2002, 12, 25, 10, 21, 4), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet2");
        assertTrue(exercise.getRecordingMode().isHeartRate());
        assertEquals(exercise.getRecordingMode().isAltitude(), true);
        assertEquals(exercise.getRecordingMode().isSpeed(), false);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertNull(exercise.getRecordingMode().getBikeNumber());
        assertEquals(exercise.getDuration().intValue(), (0 * 60 * 60 * 10) + (42 * 60 * 10) + 24 * 10 + 7);
        assertEquals(exercise.getRecordingInterval().intValue(), 15);
        assertEquals(exercise.getHeartRateAVG().intValue(), 148);
        assertEquals(exercise.getHeartRateMax().intValue(), 159);
        assertEquals(exercise.getSpeed(), null);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), (short) 86);
        assertEquals(exercise.getAltitude().getAltitudeAvg(), (short) 93);
        assertEquals(exercise.getAltitude().getAltitudeMax(), (short) 101);
        assertEquals(exercise.getTemperature().getTemperatureMin(), (short) 18);
        assertEquals(exercise.getTemperature().getTemperatureAvg(), (short) 21);
        assertEquals(exercise.getTemperature().getTemperatureMax(), (short) 27);
        assertEquals(exercise.getEnergy().intValue(), 399);
        assertEquals(exercise.getEnergyTotal().intValue(), 30058);
        assertEquals(exercise.getSumExerciseTime().intValue(), (72 * 60) + 7);
        assertEquals(exercise.getSumRideTime().intValue(), (51 * 60) + 54);
        assertEquals(exercise.getOdometer().intValue(), 1200);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().size(), 3);
        assertTrue(exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(0).getLowerHeartRate(), (short) 130);
        assertEquals(exercise.getHeartRateLimits().get(0).getUpperHeartRate(), (short) 150);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 54);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeWithin(), (0 * 60 * 60) + (30 * 60) + 36);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeAbove().intValue(), (0 * 60 * 60) + (10 * 60) + 54);

        assertTrue(exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(1).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(1).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 4);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeWithin(), (0 * 60 * 60) + (42 * 60) + 20);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(2).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(2).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 4);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeWithin(), (0 * 60 * 60) + (42 * 60) + 20);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data (one lap only)
        assertEquals(exercise.getLapList().size(), 1);
        assertEquals(exercise.getLapList().get(0).getTimeSplit(), (0 * 60 * 60 * 10) + (42 * 60 * 10) + (24 * 10) + 7);
        assertEquals(exercise.getLapList().get(0).getHeartRateSplit().intValue(), 146);
        assertEquals(exercise.getLapList().get(0).getHeartRateAVG().intValue(), 148);
        assertEquals(exercise.getLapList().get(0).getHeartRateMax().intValue(), 159);
        assertEquals(exercise.getLapList().get(0).getSpeed(), null);
        assertEquals(exercise.getLapList().get(0).getAltitude().getAltitude(), (short) 88);
        assertEquals(exercise.getLapList().get(0).getAltitude().getAscent(), 20);
        assertEquals(exercise.getLapList().get(0).getTemperature().getTemperature(), (short) 19);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().size(), 170);
        assertEquals(0 * 15 * 1000L, exercise.getSampleList().get(0).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(0).getHeartRate().intValue(), 0);
        assertEquals(exercise.getSampleList().get(0).getAltitude().intValue(), 91);
        assertNull(exercise.getSampleList().get(0).getSpeed());
        assertNull(exercise.getSampleList().get(0).getCadence());
        assertNull(exercise.getSampleList().get(0).getDistance());

        assertEquals(100 * 15 * 1000L, exercise.getSampleList().get(100).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(100).getHeartRate().intValue(), 149);
        assertEquals(exercise.getSampleList().get(100).getAltitude().intValue(), 98);
        assertNull(exercise.getSampleList().get(100).getSpeed());
        assertNull(exercise.getSampleList().get(100).getCadence());
        assertNull(exercise.getSampleList().get(100).getDistance());

        assertEquals(150 * 15 * 1000L, exercise.getSampleList().get(150).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(150).getHeartRate().intValue(), 142);
        assertEquals(exercise.getSampleList().get(150).getAltitude().intValue(), 89);
        assertNull(exercise.getSampleList().get(150).getSpeed());
        assertNull(exercise.getSampleList().get(150).getCadence());
        assertNull(exercise.getSampleList().get(150).getDistance());

        assertEquals(169 * 15 * 1000L, exercise.getSampleList().get(169).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(169).getHeartRate().intValue(), 147);
        assertEquals(exercise.getSampleList().get(169).getAltitude().intValue(), 88);
        assertNull(exercise.getSampleList().get(169).getSpeed());
        assertNull(exercise.getSampleList().get(169).getCadence());
        assertNull(exercise.getSampleList().get(169).getDistance());
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
        assertEquals("Polar S6xx/S7xx Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2005, 4, 16, 9, 56, 32), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet1");
        assertTrue(exercise.getRecordingMode().isHeartRate());
        assertEquals(exercise.getRecordingMode().isAltitude(), true);
        assertEquals(exercise.getRecordingMode().isSpeed(), true);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertEquals(1, exercise.getRecordingMode().getBikeNumber().intValue());
        assertEquals(exercise.getDuration().intValue(), (5 * 60 * 60 * 10) + (9 * 60 * 10) + 58 * 10 + 5);
        assertEquals(exercise.getRecordingInterval().intValue(), 5);
        assertEquals(exercise.getHeartRateAVG().intValue(), 134);
        assertEquals(exercise.getHeartRateMax().intValue(), 232);                      // recording error due to electric tram
        assertEquals(Math.round(exercise.getSpeed().getSpeedAvg() * 10), 246);
        assertEquals(Math.round(exercise.getSpeed().getSpeedMax() * 10), 1103); // recording error due to electric tram
        assertEquals(exercise.getSpeed().getDistance(), 111700);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), (short) 174);
        assertEquals(exercise.getAltitude().getAltitudeAvg(), (short) 276);
        assertEquals(exercise.getAltitude().getAltitudeMax(), (short) 403);
        assertEquals(exercise.getTemperature().getTemperatureMin(), (short) 19);
        assertEquals(exercise.getTemperature().getTemperatureAvg(), (short) 22);
        assertEquals(exercise.getTemperature().getTemperatureMax(), (short) 33);
        assertEquals(exercise.getEnergy().intValue(), 2344);
        assertEquals(exercise.getEnergyTotal().intValue(), 2344);  // first recorded exercise, so the sums are low
        assertEquals(exercise.getSumExerciseTime().intValue(), (5 * 60) + 9);
        assertEquals(exercise.getSumRideTime().intValue(), (4 * 60) + 32);
        assertEquals(exercise.getOdometer().intValue(), 111);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().size(), 3);
        assertTrue(exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(0).getLowerHeartRate(), (short) 120);
        assertEquals(exercise.getHeartRateLimits().get(0).getUpperHeartRate(), (short) 155);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeBelow().intValue(), (1 * 60 * 60) + (0 * 60) + 55);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeWithin(), (3 * 60 * 60) + (59 * 60) + 21);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeAbove().intValue(), (0 * 60 * 60) + (9 * 60) + 42);

        assertTrue(exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(1).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(1).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(2).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(2).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data (just 2 laps)
        assertEquals(exercise.getLapList().size(), 2);
        assertEquals(exercise.getLapList().get(0).getTimeSplit(), (2 * 60 * 60 * 10) + (24 * 60 * 10) + (19 * 10) + 9);
        assertEquals(exercise.getLapList().get(0).getHeartRateSplit().intValue(), 122);
        assertEquals(exercise.getLapList().get(0).getHeartRateAVG().intValue(), 133);
        assertEquals(exercise.getLapList().get(0).getHeartRateMax().intValue(), 229);
        assertEquals(Math.round(exercise.getLapList().get(0).getSpeed().getSpeedEnd() * 10), 0 * 10);
        assertEquals(Math.round(exercise.getLapList().get(0).getSpeed().getSpeedAVG() * 10), 202);
        assertEquals(exercise.getLapList().get(0).getSpeed().getDistance(), 48600);
        assertNull(exercise.getLapList().get(0).getSpeed().getCadence());
        assertEquals(exercise.getLapList().get(0).getAltitude().getAltitude(), (short) 392);
        assertEquals(exercise.getLapList().get(0).getAltitude().getAscent(), 675);
        assertEquals(exercise.getLapList().get(0).getTemperature().getTemperature(), (short) 20);

        assertEquals(exercise.getLapList().get(1).getTimeSplit(), (5 * 60 * 60 * 10) + (9 * 60 * 10) + (58 * 10) + 5);
        assertEquals(exercise.getLapList().get(1).getHeartRateSplit().intValue(), 123);
        assertEquals(exercise.getLapList().get(1).getHeartRateAVG().intValue(), 135);
        assertEquals(exercise.getLapList().get(1).getHeartRateMax().intValue(), 232);
        assertEquals(Math.round(exercise.getLapList().get(1).getSpeed().getSpeedEnd() * 10), 38);
        assertEquals(Math.round(exercise.getLapList().get(1).getSpeed().getSpeedAVG() * 10), 229);
        assertEquals(exercise.getLapList().get(1).getSpeed().getDistance(), 111700);
        assertNull(exercise.getLapList().get(1).getSpeed().getCadence());
        assertEquals(exercise.getLapList().get(1).getAltitude().getAltitude(), (short) 244);
        assertEquals(exercise.getLapList().get(1).getAltitude().getAscent(), 1255);
        assertEquals(exercise.getLapList().get(1).getTemperature().getTemperature(), (short) 25);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().size(), 3720);
        assertEquals(0L, exercise.getSampleList().get(0).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(0).getHeartRate().intValue(), 81);
        assertEquals(exercise.getSampleList().get(0).getAltitude().intValue(), 219);
        assertEquals(Math.round(exercise.getSampleList().get(0).getSpeed() * 10), 0 * 10);
        assertNull(exercise.getSampleList().get(0).getCadence());
        assertEquals(exercise.getSampleList().get(0).getDistance().intValue(), 0);

        assertEquals(1020 * 5 * 1000L, exercise.getSampleList().get(1020).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(1020).getHeartRate().intValue(), 134);
        assertEquals(exercise.getSampleList().get(1020).getAltitude().intValue(), 190);
        assertEquals(Math.round(exercise.getSampleList().get(1020).getSpeed() * 10), 246);
        assertNull(exercise.getSampleList().get(1020).getCadence());
        assertEquals(exercise.getSampleList().get(1020).getDistance().intValue(), 30927);

        assertEquals(2880 * 5 * 1000L, exercise.getSampleList().get(2880).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(2880).getHeartRate().intValue(), 131);
        assertEquals(exercise.getSampleList().get(2880).getAltitude().intValue(), 276);
        assertEquals(Math.round(exercise.getSampleList().get(2880).getSpeed() * 10), 383);
        assertNull(exercise.getSampleList().get(2880).getCadence());
        assertEquals(exercise.getSampleList().get(2880).getDistance().intValue(), 79820);

        assertEquals(3719 * 5 * 1000L, exercise.getSampleList().get(3719).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(3719).getHeartRate().intValue(), 123);
        assertEquals(exercise.getSampleList().get(3719).getAltitude().intValue(), 243);
        assertEquals(Math.round(exercise.getSampleList().get(3719).getSpeed() * 10), 54);
        assertNull(exercise.getSampleList().get(3719).getCadence());
        assertEquals(exercise.getSampleList().get(3719).getDistance().intValue(), 111700);
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
        assertEquals("Polar S6xx/S7xx Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2005, 4, 17, 8, 59, 3), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet1");
        assertTrue(exercise.getRecordingMode().isHeartRate());
        assertEquals(exercise.getRecordingMode().isAltitude(), true);
        assertEquals(exercise.getRecordingMode().isSpeed(), false);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertNull(exercise.getRecordingMode().getBikeNumber());
        assertEquals(exercise.getDuration().intValue(), (2 * 60 * 60 * 10) + (29 * 60 * 10) + 1 * 10 + 9);
        assertEquals(exercise.getRecordingInterval().intValue(), 5);
        assertEquals(exercise.getHeartRateAVG().intValue(), 112);
        assertEquals(exercise.getHeartRateMax().intValue(), 147);
        assertEquals(exercise.getSpeed(), null);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude().getAltitudeMin(), (short) 142);
        assertEquals(exercise.getAltitude().getAltitudeAvg(), (short) 186);
        assertEquals(exercise.getAltitude().getAltitudeMax(), (short) 284);
        assertEquals(exercise.getTemperature().getTemperatureMin(), (short) 16);
        assertEquals(exercise.getTemperature().getTemperatureAvg(), (short) 18);
        assertEquals(exercise.getTemperature().getTemperatureMax(), (short) 27);
        assertEquals(exercise.getEnergy().intValue(), 806);
        assertEquals(exercise.getEnergyTotal().intValue(), 3150);
        assertEquals(exercise.getSumExerciseTime().intValue(), (7 * 60) + 38);
        assertEquals(exercise.getSumRideTime().intValue(), (4 * 60) + 32);
        assertEquals(exercise.getOdometer().intValue(), 111);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().size(), 3);
        assertTrue(exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(0).getLowerHeartRate(), (short) 120);
        assertEquals(exercise.getHeartRateLimits().get(0).getUpperHeartRate(), (short) 155);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeBelow().intValue(), (1 * 60 * 60) + (43 * 60) + 51);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeWithin(), (0 * 60 * 60) + (44 * 60) + 59);
        assertEquals(exercise.getHeartRateLimits().get(0).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(1).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(1).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 7); // ==> shoudn't this be 0 ?
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(1).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(2).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(2).getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeBelow().intValue(), (0 * 60 * 60) + (0 * 60) + 4); // ==> shoudn't this be 0 ?
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits().get(2).getTimeAbove().intValue(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data (first and last lap only)
        assertEquals(exercise.getLapList().size(), 3);
        assertEquals(exercise.getLapList().get(0).getTimeSplit(), (1 * 60 * 60 * 10) + (31 * 60 * 10) + (8 * 10) + 8);
        assertEquals(exercise.getLapList().get(0).getHeartRateSplit().intValue(), 70);
        assertEquals(exercise.getLapList().get(0).getHeartRateAVG().intValue(), 112);
        assertEquals(exercise.getLapList().get(0).getHeartRateMax().intValue(), 147);
        assertEquals(exercise.getLapList().get(0).getSpeed(), null);
        assertEquals(exercise.getLapList().get(0).getAltitude().getAltitude(), (short) 174);
        assertEquals(exercise.getLapList().get(0).getAltitude().getAscent(), 160);
        assertEquals(exercise.getLapList().get(0).getTemperature().getTemperature(), (short) 17);

        assertEquals(exercise.getLapList().get(2).getTimeSplit(), (2 * 60 * 60 * 10) + (29 * 60 * 10) + (1 * 10) + 9);
        assertEquals(exercise.getLapList().get(2).getHeartRateSplit().intValue(), 86);
        assertEquals(exercise.getLapList().get(2).getHeartRateAVG().intValue(), 117);
        assertEquals(exercise.getLapList().get(2).getHeartRateMax().intValue(), 142);
        assertEquals(exercise.getLapList().get(2).getSpeed(), null);
        assertEquals(exercise.getLapList().get(2).getAltitude().getAltitude(), (short) 281);
        assertEquals(exercise.getLapList().get(2).getAltitude().getAscent(), 315);
        assertEquals(exercise.getLapList().get(2).getTemperature().getTemperature(), (short) 21);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().size(), 1789);
        assertEquals(0L, exercise.getSampleList().get(0).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(0).getHeartRate().intValue(), 76);
        assertEquals(exercise.getSampleList().get(0).getAltitude().intValue(), 274);
        assertNull(exercise.getSampleList().get(0).getSpeed());
        assertNull(exercise.getSampleList().get(0).getCadence());
        assertNull(exercise.getSampleList().get(0).getDistance());

        assertEquals(720 * 5 * 1000L, exercise.getSampleList().get(720).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(720).getHeartRate().intValue(), 125);
        assertEquals(exercise.getSampleList().get(720).getAltitude().intValue(), 202);
        assertNull(exercise.getSampleList().get(720).getSpeed());
        assertNull(exercise.getSampleList().get(720).getCadence());
        assertNull(exercise.getSampleList().get(720).getDistance());

        assertEquals(1440 * 5 * 1000L, exercise.getSampleList().get(1440).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(1440).getAltitude().intValue(), 143);
        assertNull(exercise.getSampleList().get(1440).getSpeed());
        assertNull(exercise.getSampleList().get(1440).getCadence());
        assertNull(exercise.getSampleList().get(1440).getDistance());

        assertEquals(1788 * 5 * 1000L, exercise.getSampleList().get(1788).getTimestamp().longValue());
        assertEquals(exercise.getSampleList().get(1788).getHeartRate().intValue(), 86);
        assertEquals(exercise.getSampleList().get(1788).getAltitude().intValue(), 281);
        assertNull(exercise.getSampleList().get(1788).getSpeed());
        assertNull(exercise.getSampleList().get(1788).getCadence());
        assertNull(exercise.getSampleList().get(1788).getDistance());
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
        assertEquals("Polar S6xx/S7xx Series", exercise.getDeviceName());

        // check exercise data (only the relevant heartrate range values)
        assertEquals(exercise.getHeartRateAVG().intValue(), 146);
        assertEquals(exercise.getHeartRateMax().intValue(), 177);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().size(), 3);
        assertFalse(exercise.getHeartRateLimits().get(0).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(0).getLowerHeartRate(), (short) 70);
        assertEquals(exercise.getHeartRateLimits().get(0).getUpperHeartRate(), (short) 80);

        assertFalse(exercise.getHeartRateLimits().get(1).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(1).getLowerHeartRate(), (short) 80);
        assertEquals(exercise.getHeartRateLimits().get(1).getUpperHeartRate(), (short) 90);

        assertFalse(exercise.getHeartRateLimits().get(2).isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits().get(2).getLowerHeartRate(), (short) 90);
        assertEquals(exercise.getHeartRateLimits().get(2).getUpperHeartRate(), (short) 100);

        // check some lap data 
        assertEquals(exercise.getLapList().size(), 12);
        assertEquals(exercise.getLapList().get(0).getHeartRateSplit().intValue(), 141);
        assertEquals(exercise.getLapList().get(10).getHeartRateSplit().intValue(), 130);

        // check some sample data 
        assertEquals(exercise.getSampleList().get(0).getHeartRate().intValue(), 116);
        assertEquals(exercise.getSampleList().get(10).getHeartRate().intValue(), 135);
    }
}
