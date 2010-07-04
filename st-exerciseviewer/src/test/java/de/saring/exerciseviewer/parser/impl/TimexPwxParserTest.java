package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.parser.*;
import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains all unit tests for the TimexPwxParser class.
 * 
 * This file is based on PolarHsrRawParser.java by Remco den Breeje
 * which is based PolarSRawParser.java by Stefan Saring
 * 
 * @author  Robert C. Schultz
 */
public class TimexPwxParserTest {
    
    /** Instance to be tested. */
    private AbstractExerciseParser parser;
    
    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp () throws Exception {
        parser = new TimexPwxParser ();
    }
    
    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    @Test
    public void testParseExerciseMissingFile () 
    {
        try {
            parser.parseExercise ("missing-file.srd");
            fail ("Parse of the missing file must fail ...");
        } 
        catch (EVException e) {}
    }
    
    /**
     * This method tests the parser with an Timex Pwx Chrono Workout file
     * recorded in metric units.?? Need to verify units
     */
    @Test
    public void testParseTimexPwxChronoWorkoutFile () throws EVException
    {
        // parse exercise file
        EVExercise exercise = parser.parseExercise ("misc/testdata/timex-racetrainer-pwx/Timex20100618201200_1.pwx");

        // check exercise data
        assertEquals (exercise.getFileType (), EVExercise.ExerciseFileType.TIMEX_PWX);
        assertEquals (exercise.getUserID (), (byte) 0);
        Calendar date = Calendar.getInstance ();
        date.setTime (exercise.getDate ());
        assertEquals (18, date.get (Calendar.DAY_OF_MONTH));
        assertEquals (5, date.get (Calendar.MONTH));
        assertEquals (2010, date.get (Calendar.YEAR));
        assertEquals (20, date.get (Calendar.HOUR_OF_DAY));
        assertEquals (12, date.get (Calendar.MINUTE));
        assertEquals (00, date.get (Calendar.SECOND));
        assertEquals ((byte) 0, exercise.getType () );
        assertEquals ("Run",exercise.getTypeLabel ());
        assertEquals (false,exercise.getRecordingMode ().isAltitude ());
        assertEquals (false,exercise.getRecordingMode ().isSpeed ());
        assertEquals (false,exercise.getRecordingMode ().isCadence ());
        assertEquals (false,exercise.getRecordingMode ().isPower ());
        assertEquals ((byte)0,exercise.getRecordingMode ().getBikeNumber ());
        assertEquals ( 10*((0*60*60) + (36*60) + 11) + 0,exercise.getDuration ());
        assertEquals ((short)2, exercise.getRecordingInterval ());
        assertEquals ((short)141,exercise.getHeartRateAVG ());
        assertEquals ((short)160,exercise.getHeartRateMax ());
        assertEquals (null,exercise.getSpeed());
        assertEquals (null,exercise.getCadence ());
        assertEquals (null,exercise.getAltitude ());
        assertEquals (null,exercise.getTemperature ());
        assertEquals (512,exercise.getEnergy ());     // This is calculated from Work
        assertEquals (513,exercise.getEnergyTotal ()); // This is per the device
        assertEquals ( 36  ,exercise.getSumExerciseTime ());
        assertEquals ( 36,exercise.getSumRideTime ());
        assertEquals (  0,exercise.getOdometer ());

        // check heart rate limits
        assertEquals (6, exercise.getHeartRateLimits ().length);
        assertTrue (exercise.getHeartRateLimits ()[0].isAbsoluteRange ());
        assertEquals ((short)191,exercise.getHeartRateLimits ()[0].getUpperHeartRate ());
        assertEquals ((short)172,exercise.getHeartRateLimits ()[0].getLowerHeartRate ());
        assertEquals ((36*60) + 11,exercise.getHeartRateLimits ()[0].getTimeBelow ());
        assertEquals ((0*60) + 0,exercise.getHeartRateLimits ()[0].getTimeWithin ());
        assertEquals ((0*60) + 0,exercise.getHeartRateLimits ()[0].getTimeAbove ());

        assertTrue (exercise.getHeartRateLimits ()[1].isAbsoluteRange ());
        assertEquals ((short)171,exercise.getHeartRateLimits ()[1].getUpperHeartRate ());
        assertEquals ((short)153,exercise.getHeartRateLimits ()[1].getLowerHeartRate ());
        assertEquals ( (26*60) + 55,exercise.getHeartRateLimits ()[1].getTimeBelow ());
        assertEquals ( (9*60) + 16,exercise.getHeartRateLimits ()[1].getTimeWithin ());
        assertEquals ( (0*60) + 0,exercise.getHeartRateLimits ()[1].getTimeAbove ());

        assertTrue (exercise.getHeartRateLimits ()[2].isAbsoluteRange ());
        assertEquals ((short)152,exercise.getHeartRateLimits ()[2].getUpperHeartRate ());
        assertEquals ((short)134,exercise.getHeartRateLimits ()[2].getLowerHeartRate ());
        assertEquals ( (8*60) + 33,exercise.getHeartRateLimits ()[2].getTimeBelow ());
        assertEquals ((18*60) + 22,exercise.getHeartRateLimits ()[2].getTimeWithin ());
        assertEquals ((9*60) + 16,exercise.getHeartRateLimits ()[2].getTimeAbove ());

        assertTrue (exercise.getHeartRateLimits ()[3].isAbsoluteRange ());
        assertEquals ((short)133,exercise.getHeartRateLimits ()[3].getUpperHeartRate ());
        assertEquals ((short)115,exercise.getHeartRateLimits ()[3].getLowerHeartRate ());
        assertEquals ((4*60) +00,exercise.getHeartRateLimits ()[3].getTimeBelow ());
        assertEquals ((4*60) +33,exercise.getHeartRateLimits ()[3].getTimeWithin ());
        assertEquals ((27*60) +38,exercise.getHeartRateLimits ()[3].getTimeAbove ());

        assertTrue (exercise.getHeartRateLimits ()[4].isAbsoluteRange ());
        assertEquals ((short)114,exercise.getHeartRateLimits ()[4].getUpperHeartRate ());
        assertEquals ((short)96,exercise.getHeartRateLimits ()[4].getLowerHeartRate ());
        assertEquals ((0*60) +32,exercise.getHeartRateLimits ()[4].getTimeBelow ());
        assertEquals ((3*60) +28,exercise.getHeartRateLimits ()[4].getTimeWithin ());
        assertEquals ((32*60) +11,exercise.getHeartRateLimits ()[4].getTimeAbove ());

        assertTrue (exercise.getHeartRateLimits ()[5].isAbsoluteRange ());
        assertEquals ((short)159,exercise.getHeartRateLimits ()[5].getUpperHeartRate ());
        assertEquals ((short)150,exercise.getHeartRateLimits ()[5].getLowerHeartRate ());
        assertEquals ((21*60) +15,exercise.getHeartRateLimits ()[5].getTimeBelow ());
        assertEquals ((14*60) +44,exercise.getHeartRateLimits ()[5].getTimeWithin ());
        assertEquals ((0*60) +12,exercise.getHeartRateLimits ()[5].getTimeAbove ());

        // check lap data (first, two from middle and last only)
        float delta=0;
        assertEquals (exercise.getLapList ().length, 12);
        assertEquals (exercise.getLapList ()[0].getTimeSplit (), 10*((0*60*60) + (3*60) + 45) + 4);
        assertEquals (exercise.getLapList ()[0].getHeartRateSplit (), (short) 0);
        assertEquals (exercise.getLapList ()[0].getHeartRateAVG (), (short) 98);
        assertEquals (exercise.getLapList ()[0].getHeartRateMax (), (short) 0);
        assertEquals ((float) (36 * 402.336 / (10*((0*60*60) + (3*60) + 45) + 4.9)),exercise.getLapList()[0].getSpeed().getSpeedAVG(),delta);
        assertEquals ((short)402, exercise.getLapList()[0].getSpeed().getDistance());
        assertEquals (0,exercise.getLapList ()[0].getAltitude ().getAltitude());
        assertEquals (0,exercise.getLapList ()[0].getAltitude ().getAscent());
        assertEquals (25,exercise.getLapList ()[0].getTemperature ().getTemperature());

        assertEquals (exercise.getLapList ()[2].getTimeSplit (), 10*((0*60*60) + (9*60) + 2) + 1);
        assertEquals (exercise.getLapList ()[2].getHeartRateSplit (), (short) 0);
        assertEquals (exercise.getLapList ()[2].getHeartRateAVG (), (short) 151);
        assertEquals (exercise.getLapList ()[2].getHeartRateMax (), (short) 0);
        assertEquals ((float) (36 * 402.336 / (1653.8)),exercise.getLapList()[2].getSpeed().getSpeedAVG(),delta);
        assertEquals ((short)402, exercise.getLapList()[2].getSpeed().getDistance());
        assertEquals (0,exercise.getLapList ()[2].getAltitude ().getAltitude());
        assertEquals (0,exercise.getLapList ()[2].getAltitude ().getAscent());
        assertEquals (25,exercise.getLapList ()[2].getTemperature ().getTemperature());

        assertEquals (exercise.getLapList ()[4].getTimeSplit (), 10*((0*60*60) + (15*60) + 3) + 3);
        assertEquals (exercise.getLapList ()[4].getHeartRateSplit (), (short) 0);
        assertEquals (exercise.getLapList ()[4].getHeartRateAVG (), (short) 148);
        assertEquals (exercise.getLapList ()[4].getHeartRateMax (), (short) 0);
        assertEquals ((float) (36 * 402.336 / (1801.5)),exercise.getLapList()[4].getSpeed().getSpeedAVG(),delta);
        assertEquals ((short)402, exercise.getLapList()[4].getSpeed().getDistance());
        assertEquals (0,exercise.getLapList ()[4].getAltitude ().getAltitude());
        assertEquals (0,exercise.getLapList ()[4].getAltitude ().getAscent());
        assertEquals (25,exercise.getLapList ()[4].getTemperature ().getTemperature());

        assertEquals (exercise.getLapList ()[11].getTimeSplit (), 10*((0*60*60) + (36*60) + 11) + 8);
        assertEquals (exercise.getLapList ()[11].getHeartRateSplit (), (short) 0);
        assertEquals (exercise.getLapList ()[11].getHeartRateAVG (), (short) 138);
        assertEquals (exercise.getLapList ()[11].getHeartRateMax (), (short) 0);
        assertEquals ((float) (36 * 402.336 / (1985.9)),exercise.getLapList()[11].getSpeed().getSpeedAVG(),delta);
        assertEquals ((short)402, exercise.getLapList()[11].getSpeed().getDistance());
        assertEquals (0,exercise.getLapList ()[11].getAltitude ().getAltitude());
        assertEquals (0,exercise.getLapList ()[11].getAltitude ().getAscent());
        assertEquals (25,exercise.getLapList ()[11].getTemperature ().getTemperature());

        // check sample data (first, two from middle and last only)
        assertEquals (exercise.getSampleList ().length, 1099);
        assertEquals (exercise.getSampleList ()[0].getHeartRate (), (short) 66);
        assertEquals (exercise.getSampleList ()[0].getAltitude (), (short) 0);
        assertEquals (exercise.getSampleList ()[0].getSpeed(), 0f, 0f);
        assertEquals (exercise.getSampleList ()[0].getCadence (), (short) 0);
        assertEquals (exercise.getSampleList ()[0].getDistance (), 0);

        assertEquals (exercise.getSampleList ()[333].getHeartRate (), (short) 149);
        assertEquals (exercise.getSampleList ()[333].getAltitude (), (short) 0);
        assertEquals (exercise.getSampleList ()[333].getSpeed(), 0f, 0f);
        assertEquals (exercise.getSampleList ()[333].getCadence (), (short) 0);
        assertEquals (exercise.getSampleList ()[333].getDistance (), 0);

        assertEquals (exercise.getSampleList ()[555].getHeartRate (), (short) 147);
        assertEquals (exercise.getSampleList ()[555].getAltitude (), (short) 0);
        assertEquals (exercise.getSampleList ()[555].getSpeed(), 0f, 0f);
        assertEquals (exercise.getSampleList ()[555].getCadence (), (short) 0);
        assertEquals (exercise.getSampleList ()[555].getDistance (), 0);

        assertEquals (exercise.getSampleList ()[1098].getHeartRate (), (short) 130);
        assertEquals (exercise.getSampleList ()[1098].getAltitude (), (short) 0);
        assertEquals (exercise.getSampleList ()[1098].getSpeed(), 0f, 0f);
        assertEquals (exercise.getSampleList ()[1098].getCadence (), (short) 0);
        assertEquals (exercise.getSampleList ()[1098].getDistance (), 0);
    }
}
