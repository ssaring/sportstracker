package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.impl.garminfit.GarminFitParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * This class contains all unit tests for the GarminFitParser class.
 *
 * @author Stefan Saring
 */
public class GarminFitParserTest {

    /**
     * Instance to be tested.
     */
    private AbstractExerciseParser parser;

    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp() throws Exception {
        // change locale/timezone to Germany (files recorded there), otherwise the datetime comparision fails
        Locale.setDefault(Locale.GERMANY);
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));

        parser = new GarminFitParser();
    }

    /**
     * This method must fail on parsing an exercise file which doesn't exists.
     */
    @Test(expected = EVException.class)
    public void testParseExerciseMissingFile() throws EVException {
        parser.parseExercise("missing-file.fit");
    }

    /**
     * This method tests the parser with an FIT file which contains only settings data,
     * no exercise data. An exception needs to be thrown.
     */
    @Test(expected = EVException.class)
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
        assertEquals("Garmin EDGE500", exercise.getDeviceName());
        assertTrue(exercise.getRecordingMode().isSpeed());
        assertTrue(exercise.getRecordingMode().isLocation());
        assertTrue(exercise.getRecordingMode().isAltitude());
        assertTrue(exercise.getRecordingMode().isCadence());
        assertTrue(exercise.getRecordingMode().isTemperature());

        assertEquals(LocalDateTime.of(2010, 7, 4, 6, 7, 36), exercise.getDateTime());
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

        // check lap data
        assertEquals(5, exercise.getLapList().length);
        assertEquals(((0 * 3600) + (29 * 60) + 15) * 10, exercise.getLapList()[0].getTimeSplit());
        assertEquals(126, exercise.getLapList()[0].getHeartRateAVG());
        assertEquals(146, exercise.getLapList()[0].getHeartRateMax());
        assertEquals(122, exercise.getLapList()[0].getHeartRateSplit());
        assertEquals(11084, exercise.getLapList()[0].getSpeed().getDistance());
        assertEquals(22.7334, exercise.getLapList()[0].getSpeed().getSpeedAVG(), 0.001d);
        assertEquals(22.1364, exercise.getLapList()[0].getSpeed().getSpeedEnd(), 0.001d);
        assertEquals(151, exercise.getLapList()[0].getAltitude().getAscent());
        assertEquals(302, exercise.getLapList()[0].getAltitude().getAltitude());
        assertEquals(20, exercise.getLapList()[0].getTemperature().getTemperature());
        assertEquals(51.05553d, exercise.getLapList()[0].getPositionSplit().getLatitude(), 0.001d);
        assertEquals(13.93589d, exercise.getLapList()[0].getPositionSplit().getLongitude(), 0.001d);

        assertEquals(((2 * 3600) + (11 * 60) + 46) * 10, exercise.getLapList()[2].getTimeSplit());
        assertEquals(124, exercise.getLapList()[2].getHeartRateAVG());
        assertEquals(145, exercise.getLapList()[2].getHeartRateMax());
        assertEquals(98, exercise.getLapList()[2].getHeartRateSplit());
        assertEquals(48391, exercise.getLapList()[2].getSpeed().getDistance());
        assertEquals(21.7080, exercise.getLapList()[2].getSpeed().getSpeedAVG(), 0.001d);
        assertEquals(1.0440, exercise.getLapList()[2].getSpeed().getSpeedEnd(), 0.001d);
        assertEquals(342, exercise.getLapList()[2].getAltitude().getAscent());
        assertEquals(417, exercise.getLapList()[2].getAltitude().getAltitude());
        assertEquals(24, exercise.getLapList()[2].getTemperature().getTemperature());
        assertEquals(51.00746d, exercise.getLapList()[2].getPositionSplit().getLatitude(), 0.001d);
        assertEquals(14.20151d, exercise.getLapList()[2].getPositionSplit().getLongitude(), 0.001d);

        assertEquals(((4 * 3600) + (28 * 60) + 16) * 10, exercise.getLapList()[4].getTimeSplit());
        assertEquals(120, exercise.getLapList()[4].getHeartRateAVG());
        assertEquals(144, exercise.getLapList()[4].getHeartRateMax());
        assertEquals(94, exercise.getLapList()[4].getHeartRateSplit());
        assertEquals(101711, exercise.getLapList()[4].getSpeed().getDistance());
        assertEquals(26.0136, exercise.getLapList()[4].getSpeed().getSpeedAVG(), 0.001d);
        assertEquals(0d, exercise.getLapList()[4].getSpeed().getSpeedEnd(), 0.001d);
        assertEquals(206, exercise.getLapList()[4].getAltitude().getAscent());
        assertEquals(237, exercise.getLapList()[4].getAltitude().getAltitude());
        assertEquals(30, exercise.getLapList()[4].getTemperature().getTemperature());
        assertEquals(51.05450d, exercise.getLapList()[4].getPositionSplit().getLatitude(), 0.001d);
        assertEquals(13.83227d, exercise.getLapList()[4].getPositionSplit().getLongitude(), 0.001d);

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

        assertEquals(10 * 1000, exercise.getSampleList()[5].getTimestamp());
        assertEquals(110, exercise.getSampleList()[5].getHeartRate());
        assertEquals(34, exercise.getSampleList()[5].getDistance());
        assertEquals(12.2364, exercise.getSampleList()[5].getSpeed(), 0.001d);
        assertEquals(233, exercise.getSampleList()[5].getAltitude());
        assertEquals(69, exercise.getSampleList()[5].getCadence());
        assertEquals(51.05323d, exercise.getSampleList()[5].getPosition().getLatitude(), 0.001d);
        assertEquals(13.83324d, exercise.getSampleList()[5].getPosition().getLongitude(), 0.001d);
        assertEquals(20, exercise.getSampleList()[5].getTemperature());

        assertEquals(((4 * 3600) + (28 * 60) + 15) * 1000, exercise.getSampleList()[8234].getTimestamp());
        assertEquals(94, exercise.getSampleList()[8234].getHeartRate());
        assertEquals(101710, exercise.getSampleList()[8234].getDistance());
        assertEquals(0d, exercise.getSampleList()[8234].getSpeed(), 0.001d);
        assertEquals(237, exercise.getSampleList()[8234].getAltitude());
        assertEquals(0, exercise.getSampleList()[8234].getCadence());
        assertEquals(51.05450d, exercise.getSampleList()[8234].getPosition().getLatitude(), 0.001d);
        assertEquals(13.83227d, exercise.getSampleList()[8234].getPosition().getLongitude(), 0.001d);
        assertEquals(30, exercise.getSampleList()[8234].getTemperature());
    }

    /**
     * This method tests the parser with an exercise file with running data recorded by
     * a Garmin Forerunner 910XT. Mostly the differences of this device are tested here.
     */
    @Test
    public void testParseExerciseForerunner910XT() throws EVException {
        // parse exercise file
        EVExercise exercise = parser.parseExercise("misc/testdata/garmin-fit/Garmin_Forerunner_910XT-Running.fit");

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.getFileType());
        assertEquals("Garmin FR910XT", exercise.getDeviceName());
        assertTrue(exercise.getRecordingMode().isSpeed());
        assertTrue(exercise.getRecordingMode().isLocation());
        assertTrue(exercise.getRecordingMode().isAltitude());
        assertFalse(exercise.getRecordingMode().isCadence());
        assertFalse(exercise.getRecordingMode().isTemperature());

        assertEquals(LocalDateTime.of(2012, 9, 29, 17, 2, 19), exercise.getDateTime());
        assertEquals(30067, exercise.getDuration());

        assertEquals(155, exercise.getHeartRateAVG());
        assertEquals(168, exercise.getHeartRateMax());
        assertEquals(681, exercise.getEnergy());

        assertEquals(9843, exercise.getSpeed().getDistance());
        assertEquals(11.7841, exercise.getSpeed().getSpeedAVG(), 0.001d);
        assertEquals(15.2424, exercise.getSpeed().getSpeedMax(), 0.001d);

        assertEquals(69, exercise.getAltitude().getAscent());
        assertEquals(97, exercise.getAltitude().getAltitudeMin());
        assertEquals(108, exercise.getAltitude().getAltitudeAVG());
        assertEquals(116, exercise.getAltitude().getAltitudeMax());

        assertNull(exercise.getCadence());
        assertNull(exercise.getTemperature());

        // check some lap data
        assertEquals(10, exercise.getLapList().length);

        assertEquals(((0 * 3600) + (25 * 60) + 53) * 10, exercise.getLapList()[4].getTimeSplit());
        assertEquals(155, exercise.getLapList()[4].getHeartRateAVG());
        assertEquals(159, exercise.getLapList()[4].getHeartRateMax());
        assertEquals(159, exercise.getLapList()[4].getHeartRateSplit());
        assertEquals(5000, exercise.getLapList()[4].getSpeed().getDistance());
        assertEquals(11.5905, exercise.getLapList()[4].getSpeed().getSpeedAVG(), 0.001d);
        assertEquals(11.5848, exercise.getLapList()[4].getSpeed().getSpeedEnd(), 0.001d);
        assertEquals(5, exercise.getLapList()[4].getAltitude().getAscent());
        assertEquals(104, exercise.getLapList()[4].getAltitude().getAltitude());
        assertNull(exercise.getLapList()[4].getTemperature());
        assertEquals(49.42301d, exercise.getLapList()[4].getPositionSplit().getLatitude(), 0.001d);
        assertEquals(8.620427d, exercise.getLapList()[4].getPositionSplit().getLongitude(), 0.001d);

        // check some sample data
        assertEquals(743, exercise.getSampleList().length);
        assertEquals(0, exercise.getSampleList()[0].getTimestamp());
        assertEquals(79, exercise.getSampleList()[0].getHeartRate());
        assertEquals(1, exercise.getSampleList()[0].getDistance());
        assertEquals(2.5452d, exercise.getSampleList()[0].getSpeed(), 0.001d);
        assertEquals(109, exercise.getSampleList()[0].getAltitude());
        assertEquals(0, exercise.getSampleList()[0].getCadence());
        assertEquals(49.41165d, exercise.getSampleList()[0].getPosition().getLatitude(), 0.001d);
        assertEquals(8.65186d, exercise.getSampleList()[0].getPosition().getLongitude(), 0.001d);
        assertEquals(0, exercise.getSampleList()[0].getTemperature());

        assertEquals(2048000, exercise.getSampleList()[500].getTimestamp());
        assertEquals(157, exercise.getSampleList()[500].getHeartRate());
        assertEquals(6670, exercise.getSampleList()[500].getDistance());
        assertEquals(12.2508d, exercise.getSampleList()[500].getSpeed(), 0.001d);
        assertEquals(102, exercise.getSampleList()[500].getAltitude());
        assertEquals(0, exercise.getSampleList()[500].getCadence());
        assertEquals(49.42749d, exercise.getSampleList()[500].getPosition().getLatitude(), 0.001d);
        assertEquals(8.63364d, exercise.getSampleList()[500].getPosition().getLongitude(), 0.001d);
        assertEquals(0, exercise.getSampleList()[500].getTemperature());
    }
}
