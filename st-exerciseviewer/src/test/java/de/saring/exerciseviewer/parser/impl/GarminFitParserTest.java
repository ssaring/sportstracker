package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;

import java.io.File;
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
     * This method tests the parser with an FIT file which contains only settings data,
     * no exercise data. An exception needs to be thrown.
     */
    @Test(expected=EVException.class)
    public void testParseExerciseSettingsFile() throws EVException {
        final String FILENAME = "misc/testdata/garmin-fit/Settings.fit"; 
        assertTrue(new File(FILENAME).exists());
        parser.parseExercise(FILENAME);
    }
    
    /**
     * This method tests the parser with an exercise file with cycling data
     * (contains speed, heartrate, altitude and cadence data).
     */
    @Test
    public void testParseExercise() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/garmin-fit/2010-07-04-06-07-36.fit");
        
        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.getFileType());
        assertTrue(exercise.getRecordingMode().isSpeed());
        assertTrue(exercise.getRecordingMode().isLocation());
        assertTrue(exercise.getRecordingMode().isAltitude());
        assertTrue(exercise.getRecordingMode().isCadence());
        assertTrue(exercise.getRecordingMode().isTemperature());

        assertDate(exercise.getDate(), 2010, 7-1, 4, 6, 7, 36);
        assertEquals(146499, exercise.getDuration());

        assertEquals(121, exercise.getHeartRateAVG());
        assertEquals(180, exercise.getHeartRateMax());
        assertEquals(1567, exercise.getEnergy());

        assertEquals(101710, exercise.getSpeed().getDistance());
        assertEquals(24.9948, exercise.getSpeed().getSpeedAVG(), 0.001d);
        assertEquals(68.4648, exercise.getSpeed().getSpeedMax(), 0.001d);

        assertEquals(1115, exercise.getAltitude().getAscent());
        assertEquals(127, exercise.getAltitude().getAltitudeMin());
        assertEquals(290, exercise.getAltitude().getAltitudeAVG());
        assertEquals(419, exercise.getAltitude().getAltitudeMax());
        
        assertEquals(84, exercise.getCadence().getCadenceAVG());
        assertEquals(119, exercise.getCadence().getCadenceMax());

        assertEquals(19, exercise.getTemperature().getTemperatureMin());
        assertEquals(24, exercise.getTemperature().getTemperatureAVG());
        assertEquals(32, exercise.getTemperature().getTemperatureMax());
        
        // check sample data
        assertEquals(8235, exercise.getSampleList().length);
        assertEquals(1000, exercise.getSampleList()[0].getTimestamp());
        assertEquals(97, exercise.getSampleList()[0].getHeartRate());
        assertEquals(0, exercise.getSampleList()[0].getDistance());
        assertEquals(13.8744d, exercise.getSampleList()[0].getSpeed(), 0.001d);
        assertEquals(232, exercise.getSampleList()[0].getAltitude());
        assertEquals(67, exercise.getSampleList()[0].getCadence());
        assertEquals(51.05350d, exercise.getSampleList()[0].getPosition().getLatitude(), 0.001d);
        assertEquals(13.83309d, exercise.getSampleList()[0].getPosition().getLongitude(), 0.001d);
        assertEquals(20, exercise.getSampleList()[0].getTemperature());

        assertEquals(10*1000, exercise.getSampleList()[5].getTimestamp());
        assertEquals(110, exercise.getSampleList()[5].getHeartRate());
        assertEquals(34, exercise.getSampleList()[5].getDistance());
        assertEquals(12.2364, exercise.getSampleList()[5].getSpeed(), 0.001d);
        assertEquals(233, exercise.getSampleList()[5].getAltitude());
        assertEquals(69, exercise.getSampleList()[5].getCadence());
        assertEquals(51.05323d, exercise.getSampleList()[5].getPosition().getLatitude(), 0.001d);
        assertEquals(13.83324d, exercise.getSampleList()[5].getPosition().getLongitude(), 0.001d);
        assertEquals(20, exercise.getSampleList()[5].getTemperature());
        
        assertEquals(((4*3600) + (28*60) + 15) * 1000, exercise.getSampleList()[8234].getTimestamp());
        assertEquals(94, exercise.getSampleList()[8234].getHeartRate());
        assertEquals(101710, exercise.getSampleList()[8234].getDistance());
        assertEquals(0d, exercise.getSampleList()[8234].getSpeed(), 0.001d);
        assertEquals(237, exercise.getSampleList()[8234].getAltitude());
        assertEquals(0, exercise.getSampleList()[8234].getCadence());
        assertEquals(51.05450d, exercise.getSampleList()[8234].getPosition().getLatitude(), 0.001d);
        assertEquals(13.83227d, exercise.getSampleList()[8234].getPosition().getLongitude(), 0.001d);
        assertEquals(30, exercise.getSampleList()[8234].getTemperature());
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
