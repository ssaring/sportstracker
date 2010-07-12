package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import java.util.Calendar;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains all unit tests for the GarminFitParser class.
 *
 * @author Stefan Saring
 */
public class GarminFitParserTest {
    
    /** Instance to be tested. */
    private AbstractExerciseParser parser;
    
    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp() throws Exception {
        parser = new GarminFitParser();
    }
    
    /**
     * This method must fail on parsing an exercise file which doesn't exists.
     */
    @Test(expected=EVException.class)
    public void testParseExerciseMissingFile() throws EVException {
        parser.parseExercise("missing-file.fit");
    }

    /**
     * This method tests the parser with an exercise file with cycling data
     * (contains speed, heartrate, altitude and cadence data).
     */
    @Test
    public void testParseExercise() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise ("misc/testdata/garmin-fit/2010-07-04-06-07-36.fit");
        
        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.getFileType());
        assertTrue(exercise.getRecordingMode().isSpeed());
        assertTrue(exercise.getRecordingMode().isAltitude());
        assertTrue(exercise.getRecordingMode().isCadence());

        // TODO: check, that test values are OK (speed!)!
        assertDate(exercise.getDate(), 2010, 7-1, 4, 6, 7, 36);
        assertEquals(146499, exercise.getDuration());

        assertEquals(121, exercise.getHeartRateAVG());
        assertEquals(180, exercise.getHeartRateMax());
        assertEquals(1567, exercise.getEnergy());

        assertEquals(101710, exercise.getSpeed().getDistance());
        assertEquals(24.9948, exercise.getSpeed().getSpeedAVG(), 0.001d);
        assertEquals(68.4648, exercise.getSpeed().getSpeedMax(), 0.001d);

        // TODO: min, max, avg are missing
        assertEquals(1115, exercise.getAltitude().getAscent());
        
        assertEquals(84, exercise.getCadence().getCadenceAVG());
        assertEquals(119, exercise.getCadence().getCadenceMax());

        // TODO: add tests for record/sample data


//        assertEquals((byte) 0, exercise.getUserID());
//        assertEquals ((byte) 1, exercise.getType ());
//        assertEquals ("ExeSet1", exercise.getTypeLabel ());
//        assertEquals ((1*60*60*10) + (13*60*10) + 34*10 + 3, exercise.getDuration ());
//        assertTrue (exercise.getRecordingMode ().isAltitude ());
//        assertTrue (exercise.getRecordingMode ().isSpeed ());
//        assertFalse (exercise.getRecordingMode ().isCadence ());
//        assertFalse (exercise.getRecordingMode ().isPower ());
//        assertEquals ((byte) 2, exercise.getRecordingMode ().getBikeNumber ());
//        assertEquals ((short) 15, exercise.getRecordingInterval ());
//        assertEquals ((short) 135, exercise.getHeartRateAVG ());
//        assertEquals ((short) 232, exercise.getHeartRateMax ());
//        assertEquals (251, Math.round (exercise.getSpeed ().getSpeedAVG () * 10));
//        assertEquals (1093, Math.round (exercise.getSpeed ().getSpeedMax () * 10));
//        assertEquals (29900, exercise.getSpeed ().getDistance ());
//        assertNull (exercise.getCadence ());
//        assertEquals ((short) 215, exercise.getAltitude ().getAltitudeMin (), 215);
//        assertEquals ((short) 253, exercise.getAltitude ().getAltitudeAVG (), 253);
//        assertEquals ((short) 300, exercise.getAltitude ().getAltitudeMax (), 300);
//        assertEquals ((short) 3, exercise.getTemperature ().getTemperatureMin (), 3);
//        assertEquals ((short) 3, exercise.getTemperature ().getTemperatureAVG (), 3);
//        assertEquals ((short) 5, exercise.getTemperature ().getTemperatureMax (), 5);
//        assertEquals (591, exercise.getEnergy ());
//        assertEquals (24099, exercise.getEnergyTotal ());
//        assertEquals ((56*60) + 34, exercise.getSumExerciseTime ());
//        assertEquals ((42*60) + 56, exercise.getSumRideTime ());
//        assertEquals (1024, exercise.getOdometer ());
//
//        // check heart rate limits
//        assertEquals (3, exercise.getHeartRateLimits ().length, 3);
//        assertTrue (exercise.getHeartRateLimits ()[0].isAbsoluteRange ());
//        assertEquals ((short) 120, exercise.getHeartRateLimits ()[0].getLowerHeartRate (), 120);
//        assertEquals ((short) 155, exercise.getHeartRateLimits ()[0].getUpperHeartRate (), 155);
//        assertEquals ((0*60*60) + (5*60) + 32, exercise.getHeartRateLimits ()[0].getTimeBelow ());
//        assertEquals ((1*60*60) + (3*60) + 19, exercise.getHeartRateLimits ()[0].getTimeWithin ());
//        assertEquals ((0*60*60) + (4*60) + 43, exercise.getHeartRateLimits ()[0].getTimeAbove ());
//
//        assertTrue (exercise.getHeartRateLimits ()[1].isAbsoluteRange ());
//        assertEquals ((short) 80, exercise.getHeartRateLimits ()[1].getLowerHeartRate (), 80);
//        assertEquals ((short) 160, exercise.getHeartRateLimits ()[1].getUpperHeartRate (), 160);
//        assertEquals ((0*60*60) + (0*60) + 0, exercise.getHeartRateLimits ()[1].getTimeBelow ());
//        assertEquals ((1*60*60) + (10*60) + 55, exercise.getHeartRateLimits ()[1].getTimeWithin ());
//        assertEquals ((0*60*60) + (2*60) + 39, exercise.getHeartRateLimits ()[1].getTimeAbove ());
//
//        assertTrue (exercise.getHeartRateLimits ()[2].isAbsoluteRange ());
//        assertEquals ((short) 80, exercise.getHeartRateLimits ()[2].getLowerHeartRate (), 80);
//        assertEquals ((short) 160, exercise.getHeartRateLimits ()[2].getUpperHeartRate (), 160);
//        assertEquals ((0*60*60) + (0*60) + 0, exercise.getHeartRateLimits ()[2].getTimeBelow ());
//        assertEquals ((1*60*60) + (10*60) + 55, exercise.getHeartRateLimits ()[2].getTimeWithin ());
//        assertEquals ((0*60*60) + (2*60) + 39, exercise.getHeartRateLimits ()[2].getTimeAbove ());
//
//        // check lap data (first, one from middle and last lap only)
//        assertEquals (5, exercise.getLapList ().length);
//        assertEquals ((0*60*60*10) + (06*60*10) + (59*10) + 2, exercise.getLapList ()[0].getTimeSplit ());
//        assertEquals ((short) 136, exercise.getLapList ()[0].getHeartRateSplit ());
//        assertEquals ((short) 128, exercise.getLapList ()[0].getHeartRateAVG ());
//        assertEquals ((short) 152, exercise.getLapList ()[0].getHeartRateMax ());
//        assertEquals (141, Math.round (exercise.getLapList ()[0].getSpeed ().getSpeedEnd () * 10));
//        assertEquals (258, Math.round (exercise.getLapList ()[0].getSpeed ().getSpeedAVG () * 10));
//        assertEquals (3*1000, exercise.getLapList ()[0].getSpeed ().getDistance ());
//        assertEquals ((short) 0, exercise.getLapList ()[0].getSpeed ().getCadence ());
//        assertEquals ((short) 231, exercise.getLapList ()[0].getAltitude ().getAltitude ());
//        assertEquals (25, exercise.getLapList ()[0].getAltitude ().getAscent ());
//        assertEquals ((short) 4, exercise.getLapList ()[0].getTemperature ().getTemperature ());
//
//        assertEquals ((0*60*60*10) + (40*60*10) + (18*10) + 8, exercise.getLapList ()[2].getTimeSplit ());
//        assertEquals ((short) 136, exercise.getLapList ()[2].getHeartRateSplit ());
//        assertEquals ((short) 134, exercise.getLapList ()[2].getHeartRateAVG ());
//        assertEquals ((short) 168, exercise.getLapList ()[2].getHeartRateMax ());
//        assertEquals (193, Math.round (exercise.getLapList ()[2].getSpeed ().getSpeedEnd () * 10));
//        assertEquals (242, Math.round (exercise.getLapList ()[2].getSpeed ().getSpeedAVG () * 10));
//        assertEquals (15700, exercise.getLapList ()[2].getSpeed ().getDistance ());
//        assertEquals ((short) 0, exercise.getLapList ()[2].getSpeed ().getCadence ());
//        assertEquals ((short) 247, exercise.getLapList ()[2].getAltitude ().getAltitude ());
//        assertEquals (135, exercise.getLapList ()[2].getAltitude ().getAscent ());
//        assertEquals ((short) 4, exercise.getLapList ()[2].getTemperature ().getTemperature ());
//
//        assertEquals ((1*60*60*10) + (13*60*10) + (34*10) + 3, exercise.getLapList ()[4].getTimeSplit ());
//        assertEquals ((short) 123, exercise.getLapList ()[4].getHeartRateSplit ());
//        assertEquals ((short) 121, exercise.getLapList ()[4].getHeartRateAVG ());
//        assertEquals ((short) 123, exercise.getLapList ()[4].getHeartRateMax ());
//        assertEquals (0*10, Math.round (exercise.getLapList ()[4].getSpeed ().getSpeedEnd () * 10));
//        assertEquals (0*10, Math.round (exercise.getLapList ()[4].getSpeed ().getSpeedAVG () * 10));
//        assertEquals (29900, exercise.getLapList ()[4].getSpeed ().getDistance ());
//        assertEquals ((short) 0, exercise.getLapList ()[4].getSpeed ().getCadence ());
//        assertEquals ((short) 229, exercise.getLapList ()[4].getAltitude ().getAltitude ());
//        assertEquals (240, exercise.getLapList ()[4].getAltitude ().getAscent ());
//        assertEquals ((short) 4, exercise.getLapList ()[4].getTemperature ().getTemperature ());
//
//        // check sample data (first, two from middle and last only)
//        assertEquals (295, exercise.getSampleList ().length);
//        assertEquals ((short) 101, exercise.getSampleList ()[0].getHeartRate ());
//        assertEquals ((short) 240, exercise.getSampleList ()[0].getAltitude ());
//        assertEquals (42, Math.round (exercise.getSampleList ()[0].getSpeed () * 10));
//        assertEquals ((short) 0, exercise.getSampleList ()[0].getCadence ());
//        assertEquals (0, exercise.getSampleList ()[0].getDistance ());
//
//        assertEquals ((short) 147, exercise.getSampleList ()[100].getHeartRate ());
//        assertEquals ((short) 278, exercise.getSampleList ()[100].getAltitude ());
//        assertEquals (171, Math.round (exercise.getSampleList ()[100].getSpeed () * 10));
//        assertEquals ((short) 0, exercise.getSampleList ()[100].getCadence ());
//        assertEquals (9479, exercise.getSampleList ()[100].getDistance ());
//
//        assertEquals ((short) 166, exercise.getSampleList ()[200].getHeartRate ());
//        assertEquals ((short) 275, exercise.getSampleList ()[200].getAltitude ());
//        assertEquals (141, Math.round (exercise.getSampleList ()[200].getSpeed () * 10));
//        assertEquals ((short) 0, exercise.getSampleList ()[200].getCadence ());
//        assertEquals (19256, exercise.getSampleList ()[200].getDistance ());
//
//        assertEquals ((short) 123, exercise.getSampleList ()[294].getHeartRate ());
//        assertEquals ((short) 229, exercise.getSampleList ()[294].getAltitude ());
//        assertEquals (0, Math.round (exercise.getSampleList ()[294].getSpeed () * 10));
//        assertEquals ((short) 0, exercise.getSampleList ()[294].getCadence ());
//        assertEquals (29900, exercise.getSampleList ()[294].getDistance ());
    }
    
    private void assertDate(Date date, int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        assertEquals(year, cal.get(Calendar.YEAR));
        assertEquals(month, cal.get(Calendar.MONTH));
        assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(hour, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, cal.get(Calendar.MINUTE));
        assertEquals(second, cal.get(Calendar.SECOND));
    }
}
