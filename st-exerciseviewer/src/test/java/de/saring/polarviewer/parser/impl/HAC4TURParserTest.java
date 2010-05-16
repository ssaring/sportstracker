package de.saring.polarviewer.parser.impl;

import de.saring.polarviewer.parser.*;
import de.saring.polarviewer.core.PVException;
import de.saring.polarviewer.data.PVExercise;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains all unit tests for the HAC4TURParser class.
 *
 * @author Stefan Saring (the C# version was done by Ilja Booij)
 */
public class HAC4TURParserTest {
    
    /** Instance to be tested. */
    private AbstractExerciseParser parser;
    
    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp () throws Exception {
        parser = new HAC4TURParser ();
    }
    
    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test
    public void testParseExerciseMissingFile () 
    {
        try {
            parser.parseExercise ("missing-file.tur");
            fail ("Parse of the missing file must fail ...");
        } 
        catch (PVException e) {}
    }
    
    /**
     * This method test the parser by using a HAC4 cycling exercise file.
     */
    @Test
    public void testParseCyclingExercise () throws PVException {

        // parse exercise
        PVExercise exercise = parser.parseExercise ("misc/testdata/hac4/pailheres.tur");
        
        assertEquals (PVExercise.ExerciseFileType.HAC4TUR, exercise.getFileType ());
        assertEquals ((short) 20, exercise.getRecordingInterval ());
        // check recording mode
        assertTrue (exercise.getRecordingMode ().isSpeed ());
        assertFalse (exercise.getRecordingMode ().isCadence ());
        assertTrue (exercise.getRecordingMode ().isAltitude ());
        assertFalse (exercise.getRecordingMode ().isPower ());
        assertTrue (exercise.getRecordingMode ().isTemperature ());
        
        // time information
        Calendar date = Calendar.getInstance ();
        date.setTime (exercise.getDate ());
        assertEquals (10, date.get (Calendar.DAY_OF_MONTH));
        assertEquals (10-1, date.get (Calendar.MONTH));
        assertEquals (2005, date.get (Calendar.YEAR));
        assertEquals (14, date.get (Calendar.HOUR_OF_DAY));
        assertEquals (05, date.get (Calendar.MINUTE));
        assertEquals (9037 * 10, exercise.getDuration ());
        assertEquals (183 * 60 + 14, exercise.getSumExerciseTime ());
        
        // heart rates
        assertEquals ((short) 159, exercise.getHeartRateAVG ());
        assertEquals ((short) 184, exercise.getHeartRateMax ());
        
        // distance & speed & odometer
        assertEquals (40.1f, exercise.getSpeed ().getDistance  () / 1000f, 0.01f);
        assertEquals (72f, exercise.getSpeed ().getSpeedMax (), 0.01f);
        assertEquals (22.21f, exercise.getSpeed ().getSpeedAVG  (), 0.01f);
        assertEquals (4817, exercise.getOdometer ());
        
        // altitudes
        assertEquals ((short) 1901, exercise.getAltitude ().getAltitudeMax ());
        assertEquals ((short) 624, exercise.getAltitude ().getAltitudeMin ());
        assertEquals ((short) 1352, exercise.getAltitude ().getAltitudeAVG ());
        assertEquals (1281, exercise.getAltitude ().getAscent ());
        
        // temperatures
        assertEquals ((short) 15, exercise.getTemperature ().getTemperatureMin ());
        assertEquals ((short) 17, exercise.getTemperature ().getTemperatureAVG ());
        assertEquals ((short) 23, exercise.getTemperature ().getTemperatureMax ());
        
        // heart rate limits
        assertEquals (1, exercise.getHeartRateLimits ().length);
        assertEquals ((short) 140, exercise.getHeartRateLimits ()[0].getLowerHeartRate ());
        assertEquals ((short) 150, exercise.getHeartRateLimits ()[0].getUpperHeartRate ());
        assertEquals (61 * 60, exercise.getHeartRateLimits ()[0].getTimeBelow ());
        assertEquals (6 * 60 + 20, exercise.getHeartRateLimits ()[0].getTimeWithin ());
        assertEquals ((1 * 60 + 23) * 60 + 40, exercise.getHeartRateLimits ()[0].getTimeAbove ());
        
        // we don't have any lap data yet:
        assertEquals (1, exercise.getLapList ().length);
        
        // too lazy to check samples for now..
    }
	
    /**
     * This method test the parser by using a HAC5 cycling exercise file.
     */
    @Test
    public void testParseHAC5CyclingExercise () throws PVException {
        
        PVExercise exercise = parser.parseExercise ("misc/testdata/hac4/hac5.tur");
        
        assertEquals (PVExercise.ExerciseFileType.HAC4TUR, exercise.getFileType ());
        assertEquals ((short) 5, exercise.getRecordingInterval ());
        // check recording mode
        assertTrue (exercise.getRecordingMode ().isSpeed ());
        assertTrue (exercise.getRecordingMode ().isCadence ());
        assertTrue (exercise.getRecordingMode ().isAltitude ());
        assertFalse (exercise.getRecordingMode ().isPower ());
        assertTrue (exercise.getRecordingMode ().isTemperature ());
        
        // time information
        Calendar date = Calendar.getInstance ();
        date.setTime (exercise.getDate ());
        assertEquals (4, date.get (Calendar.DAY_OF_MONTH));
        assertEquals (2-1, date.get (Calendar.MONTH));
        assertEquals (2006, date.get (Calendar.YEAR));
        assertEquals (10, date.get (Calendar.HOUR_OF_DAY));
        assertEquals (34, date.get (Calendar.MINUTE));
        assertEquals (16675 * 10, exercise.getDuration ());
        assertEquals (13965 / 60, exercise.getSumExerciseTime ());
        
        // heart rates
        assertEquals ((short) 128, exercise.getHeartRateAVG ());
        assertEquals ((short) 157, exercise.getHeartRateMax ());
        
        // distance & speed & odometer
        assertEquals (105.56f, exercise.getSpeed ().getDistance () / 1000f, 0.01f);
        assertEquals (43.2f, exercise.getSpeed ().getSpeedMax (), 0.01f);
        assertEquals (27.21f, exercise.getSpeed ().getSpeedAVG (), 0.01f);
        assertEquals (105, exercise.getOdometer ());
        
        // Cadence values
        assertEquals ((short) 58, exercise.getCadence ().getCadenceAVG ());
        assertEquals ((short) 110, exercise.getCadence ().getCadenceMax ());
        // altitudes
        assertEquals ((short) 5, exercise.getAltitude ().getAltitudeMax ());
        assertEquals ((short) 2, exercise.getAltitude ().getAltitudeMin ());
        assertEquals ((short) 2, exercise.getAltitude ().getAltitudeAVG ());
        assertEquals (10, exercise.getAltitude ().getAscent ());
        
        // temperatures
        assertEquals ((short) 6, exercise.getTemperature ().getTemperatureMin ());
        assertEquals ((short) 7, exercise.getTemperature ().getTemperatureAVG ());
        assertEquals ((short) 16, exercise.getTemperature ().getTemperatureMax ());
        
        // heart rate limits
        assertEquals (1, exercise.getHeartRateLimits ().length);
        assertEquals ((short) 42, exercise.getHeartRateLimits ()[0].getLowerHeartRate ());
        assertEquals ((short) 195, exercise.getHeartRateLimits ()[0].getUpperHeartRate ());
        assertEquals (0, exercise.getHeartRateLimits ()[0].getTimeBelow ());
        assertEquals (16680, exercise.getHeartRateLimits ()[0].getTimeWithin ());
        assertEquals (0, exercise.getHeartRateLimits ()[0].getTimeAbove ());
        
        // we don't have any lap data yet:
        assertEquals (1, exercise.getLapList ().length);
        
        // too lazy to check samples for now..
    }
}
