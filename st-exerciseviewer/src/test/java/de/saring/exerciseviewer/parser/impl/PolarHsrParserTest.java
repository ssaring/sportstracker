package de.saring.exerciseviewer.parser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;

/**
 * This class contains all unit tests for the PolarHsrRawParser class.
 * <p/>
 * This file is based on PolarSRawParser.java by Stefan Saring
 *
 * @author Remco den Breeje
 */
public class PolarHsrParserTest {

    /**
     * Instance to be tested.
     */
    private AbstractExerciseParser parser;

    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp() throws Exception {
        parser = new PolarHsrRawParser();
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
     * This method tests the parser with an Polar S510 raw exercise file
     * recorded in metric units.
     * This test is taken from the C# test class so the code could be better :-)
     */
    @Test
    public void testParseS510ExerciseCyclingWithMetricUnits() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s510/cycling-metric.hsr");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.S510RAW);
        assertEquals("Polar S4xx/S5xx Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2007, 11, 3, 11, 03, 53), exercise.getDateTime());
        assertEquals(exercise.getType(), "BasicUse");
        assertEquals(exercise.getRecordingMode().isAltitude(), false);
        assertEquals(exercise.getRecordingMode().isSpeed(), true);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertEquals(2, exercise.getRecordingMode().getBikeNumber().intValue());
        assertEquals(exercise.getDuration(), 10 * ((2 * 60 * 60) + (14 * 60) + 51) + 5);
        assertEquals(exercise.getRecordingInterval(), (short) 120);
        assertEquals(exercise.getHeartRateAVG(), (short) 171);
        assertEquals(exercise.getHeartRateMax(), (short) 194);
        assertEquals(exercise.getSpeed().getDistance(), 45800);

        assertEquals(Math.round(exercise.getSpeed().getSpeedAVG() * 10), 206);
        assertEquals(Math.round(exercise.getSpeed().getSpeedMax() * 10), 494);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude(), null);
        assertEquals(exercise.getTemperature(), null);
        assertEquals(exercise.getEnergy(), 1616);
        assertEquals(exercise.getEnergyTotal(), 160947);
        assertEquals(exercise.getSumExerciseTime(), (644 * 60) + 41);
        assertEquals(exercise.getSumRideTime(), 8173);
        assertEquals(exercise.getOdometer(), 3007);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().length, 3);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[0].getLowerHeartRate(), (short) 150);
        assertEquals(exercise.getHeartRateLimits()[0].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[1].getLowerHeartRate(), (short) 183);
        assertEquals(exercise.getHeartRateLimits()[1].getUpperHeartRate(), (short) 187);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[2].getLowerHeartRate(), (short) 90);
        assertEquals(exercise.getHeartRateLimits()[2].getUpperHeartRate(), (short) 130);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 0);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 0);

        // check lap data
        assertEquals(exercise.getLapList().length, 2);
        assertEquals(exercise.getLapList()[0].getTimeSplit(), (1 * 60 * 60 * 10) + (31 * 60 * 10) + (11 * 10) + 7);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit().intValue(), 156);
        assertEquals(exercise.getLapList()[0].getHeartRateAVG().intValue(), 173);
        assertEquals(exercise.getLapList()[0].getHeartRateMax().intValue(), 194);
        assertEquals(Math.round(exercise.getLapList()[0].getSpeed().getSpeedEnd() * 10), 164);
        assertEquals(Math.round(exercise.getLapList()[0].getSpeed().getDistance() / 100f), 310);
        assertEquals(exercise.getLapList()[0].getAltitude(), null);
        assertEquals(exercise.getLapList()[0].getTemperature(), null);

        assertEquals(exercise.getLapList()[1].getTimeSplit(), (2 * 60 * 60 * 10) + (14 * 60 * 10) + (51 * 10) + 5);
        assertEquals(exercise.getLapList()[1].getHeartRateSplit().intValue(), 155);
        assertEquals(exercise.getLapList()[1].getHeartRateAVG().intValue(), 169);
        assertEquals(exercise.getLapList()[1].getHeartRateMax().intValue(), 189);
        assertEquals(Math.round(exercise.getLapList()[1].getSpeed().getSpeedEnd() * 10), 3);
        assertEquals(Math.round(exercise.getLapList()[1].getSpeed().getDistance() / 100), 458);
        assertEquals(exercise.getLapList()[1].getAltitude(), null);
        assertEquals(exercise.getLapList()[1].getTemperature(), null);

        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 67);
        assertEquals(0, exercise.getSampleList()[0].getTimestamp().intValue());
        assertEquals(exercise.getSampleList()[0].getHeartRate().intValue(), 152);
        assertNull(exercise.getSampleList()[0].getAltitude());
        assertEquals(Math.round(exercise.getSampleList()[0].getSpeed() * 10), 230);
        assertNull(exercise.getSampleList()[0].getCadence());
        assertEquals(exercise.getSampleList()[0].getDistance().intValue(), 0);

        assertEquals(24 * 120 * 1000, exercise.getSampleList()[24].getTimestamp().intValue());
        assertEquals(exercise.getSampleList()[24].getHeartRate().intValue(), 165);
        assertNull(exercise.getSampleList()[24].getAltitude());
        assertEquals(Math.round(exercise.getSampleList()[24].getSpeed() * 10), 270);
        assertNull(exercise.getSampleList()[24].getCadence());
        assertEquals(exercise.getSampleList()[24].getDistance().intValue(), 16500);

        assertEquals(48 * 120 * 1000, exercise.getSampleList()[48].getTimestamp().intValue());
        assertEquals(exercise.getSampleList()[48].getHeartRate().intValue(), 164);
        assertNull(exercise.getSampleList()[48].getAltitude());
        assertEquals(Math.round(exercise.getSampleList()[48].getSpeed() * 10), 190);
        assertNull(exercise.getSampleList()[48].getCadence());
        assertEquals(exercise.getSampleList()[48].getDistance().intValue(), 32933);

        assertEquals(66 * 120 * 1000, exercise.getSampleList()[66].getTimestamp().intValue());
        assertEquals(exercise.getSampleList()[66].getHeartRate().intValue(), 161);
        assertNull(exercise.getSampleList()[66].getAltitude());
        assertEquals(Math.round(exercise.getSampleList()[66].getSpeed() * 10), 210);
        assertNull(exercise.getSampleList()[66].getCadence());
        assertEquals(exercise.getSampleList()[66].getDistance().intValue(), 45800);
    }

    /**
     * This method tests the parser with an Polar S510 raw exercise file
     * recorded in metric units.
     * This test is taken from the C# test class so the code could be better :-)
     */
    @Test
    public void testParseS510ExerciseRunningIntervaltrainingWithMetricUnits() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/s510/running-interval-metric.hsr");

        // check exercise data
        assertEquals(exercise.getFileType(), EVExercise.ExerciseFileType.S510RAW);
        assertEquals("Polar S4xx/S5xx Series", exercise.getDeviceName());
        assertEquals(LocalDateTime.of(2007, 8, 20, 21, 10, 11), exercise.getDateTime());
        assertEquals(exercise.getType(), "ExeSet5");
        assertEquals(exercise.getRecordingMode().isAltitude(), false);
        assertEquals(exercise.getRecordingMode().isSpeed(), false);
        assertEquals(exercise.getRecordingMode().isCadence(), false);
        assertEquals(exercise.getRecordingMode().isPower(), false);
        assertNull(exercise.getRecordingMode().getBikeNumber());
        assertEquals(exercise.getDuration(), 10 * ((0 * 60 * 60) + (59 * 60) + 31) + 7);
        assertEquals(exercise.getRecordingInterval(), (short) 30);
        assertEquals(exercise.getHeartRateAVG(), (short) 161);
        assertEquals(exercise.getHeartRateMax(), (short) 190);
        assertEquals(exercise.getSpeed(), null);
        assertEquals(exercise.getCadence(), null);
        assertEquals(exercise.getAltitude(), null);
        assertEquals(exercise.getTemperature(), null);
        assertEquals(exercise.getEnergy(), 645);
        assertEquals(exercise.getEnergyTotal(), 142456);
        assertEquals(exercise.getSumExerciseTime(), (617 * 60) + 29);
        assertEquals(exercise.getSumRideTime(), 7516);
        assertEquals(exercise.getOdometer(), 2793);

        // check heart rate limits
        assertEquals(exercise.getHeartRateLimits().length, 3);
        assertTrue(exercise.getHeartRateLimits()[0].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[0].getLowerHeartRate(), (short) 150);
        assertEquals(exercise.getHeartRateLimits()[0].getUpperHeartRate(), (short) 160);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeBelow(), (0 * 60 * 60) + (13 * 60) + 42);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeWithin(), (0 * 60 * 60) + (23 * 60) + 30);
        assertEquals(exercise.getHeartRateLimits()[0].getTimeAbove(), (0 * 60 * 60) + (22 * 60) + 19);

        assertTrue(exercise.getHeartRateLimits()[1].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[1].getLowerHeartRate(), (short) 183);
        assertEquals(exercise.getHeartRateLimits()[1].getUpperHeartRate(), (short) 187);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeBelow(), (0 * 60 * 60) + (46 * 60) + 10);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeWithin(), (0 * 60 * 60) + (12 * 60) + 46);
        assertEquals(exercise.getHeartRateLimits()[1].getTimeAbove(), (0 * 60 * 60) + (0 * 60) + 35);

        assertTrue(exercise.getHeartRateLimits()[2].isAbsoluteRange());
        assertEquals(exercise.getHeartRateLimits()[2].getLowerHeartRate(), (short) 90);
        assertEquals(exercise.getHeartRateLimits()[2].getUpperHeartRate(), (short) 130);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeBelow(), (0 * 60 * 60) + (0 * 60) + 9);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeWithin(), (0 * 60 * 60) + (0 * 60) + 17);
        assertEquals(exercise.getHeartRateLimits()[2].getTimeAbove(), (0 * 60 * 60) + (59 * 60) + 5);

        // check lap data (first, two from middle and last only)
        assertEquals(exercise.getLapList().length, 7);
        assertEquals(exercise.getLapList()[0].getTimeSplit(), 10 * ((0 * 60 * 60) + (15 * 60) + 0) + 0);
        assertEquals(exercise.getLapList()[0].getHeartRateSplit().intValue(), 154);
        assertEquals(exercise.getLapList()[0].getHeartRateAVG().intValue(), 155);
        assertEquals(exercise.getLapList()[0].getHeartRateMax().intValue(), 168);
        assertEquals(exercise.getLapList()[0].getSpeed(), null);
        assertEquals(exercise.getLapList()[0].getAltitude(), null);
        assertEquals(exercise.getLapList()[0].getTemperature(), null);

        assertEquals(exercise.getLapList()[2].getTimeSplit(), 10 * ((0 * 60 * 60) + (27 * 60) + 20) + 0);
        assertEquals(exercise.getLapList()[2].getHeartRateSplit().intValue(), 187);
        assertEquals(exercise.getLapList()[2].getHeartRateAVG().intValue(), 182);
        assertEquals(exercise.getLapList()[2].getHeartRateMax().intValue(), 188);

        assertEquals(exercise.getLapList()[4].getTimeSplit(), 10 * ((0 * 60 * 60) + (39 * 60) + 30) + 0);
        assertEquals(exercise.getLapList()[4].getHeartRateSplit().intValue(), 147);
        assertEquals(exercise.getLapList()[4].getHeartRateAVG().intValue(), 182);
        assertEquals(exercise.getLapList()[4].getHeartRateMax().intValue(), 147);

        assertEquals(exercise.getLapList()[6].getTimeSplit(), 10 * ((0 * 60 * 60) + (59 * 60) + 31) + 7);
        assertEquals(exercise.getLapList()[6].getHeartRateSplit().intValue(), 126);
        assertEquals(exercise.getLapList()[6].getHeartRateAVG().intValue(), 161);
        assertEquals(exercise.getLapList()[6].getHeartRateMax().intValue(), 190);


        // check sample data (first, two from middle and last only)
        assertEquals(exercise.getSampleList().length, 119);
        assertEquals(0, exercise.getSampleList()[0].getTimestamp().intValue());
        assertEquals(exercise.getSampleList()[0].getHeartRate().intValue(), 143);
        assertNull(exercise.getSampleList()[0].getAltitude());
        assertNull(exercise.getSampleList()[0].getSpeed());
        assertNull(exercise.getSampleList()[0].getCadence());
        assertNull(exercise.getSampleList()[0].getDistance());

        assertEquals(44 * 30 * 1000, exercise.getSampleList()[44].getTimestamp().intValue());
        assertEquals(exercise.getSampleList()[44].getHeartRate().intValue(), 151);
        assertNull(exercise.getSampleList()[44].getAltitude());
        assertNull(exercise.getSampleList()[44].getSpeed());
        assertNull(exercise.getSampleList()[44].getCadence());
        assertNull(exercise.getSampleList()[44].getDistance());

        assertEquals(88 * 30 * 1000, exercise.getSampleList()[88].getTimestamp().intValue());
        assertEquals(exercise.getSampleList()[88].getHeartRate().intValue(), 153);
        assertNull(exercise.getSampleList()[88].getAltitude());
        assertNull(exercise.getSampleList()[88].getSpeed());
        assertNull(exercise.getSampleList()[88].getCadence());
        assertNull(exercise.getSampleList()[88].getDistance());

        assertEquals(118 * 30 * 1000, exercise.getSampleList()[118].getTimestamp().intValue());
        assertEquals(exercise.getSampleList()[118].getHeartRate().intValue(), 140);
        assertNull(exercise.getSampleList()[118].getAltitude());
        assertNull(exercise.getSampleList()[118].getSpeed());
        assertNull(exercise.getSampleList()[118].getCadence());
        assertNull(exercise.getSampleList()[118].getDistance());
    }
}
