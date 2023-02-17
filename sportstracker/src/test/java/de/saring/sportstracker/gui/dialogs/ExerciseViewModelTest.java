package de.saring.sportstracker.gui.dialogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import de.saring.util.unitcalc.UnitSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.util.unitcalc.FormatUtils;
import de.saring.util.unitcalc.SpeedMode;

/**
 * Unit tests of class ExerciseViewModel.
 *
 * @author Stefan Saring
 */
public class ExerciseViewModelTest {

    private static Locale defaultLocale;

    private Exercise exercise;

    @BeforeEach
    public void setUp() {
        exercise = new Exercise(123L);
        exercise.setDateTime(LocalDateTime.of(2014, 10, 20, 7, 30, 0));
        exercise.setSportType(new SportType(234L));
        exercise.setSportSubType(new SportSubType(345L));
        exercise.setDuration(3600);
        exercise.setIntensity(Exercise.IntensityType.HIGH);
        exercise.setDistance(115f);
        exercise.setAvgSpeed(28.5f);
        exercise.setAvgHeartRate(128);
        exercise.setAscent(1240);
        exercise.setDescent(1230);
        exercise.setCalories(1950);
        exercise.setHrmFile("foo.hrm");
        exercise.setEquipment(new Equipment(456L));
        exercise.setComment("Foo Bar");
    }

    /**
     * The tests needs to use the English locale, results look different for other.
     */
    @BeforeAll
    static void setUpLocale() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    /**
     * Restore default Locale afterwards.
     */
    @AfterAll
    static void cleanLocale() {
        Locale.setDefault(defaultLocale);
    }

    /**
     * Test of method getExercise() with metric unit system.
     */
    @Test
    public void testGetExerciseWithMetricUnits() {
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);

        // test that metric units are not converted
        assertEquals(exercise.getDistance(), viewModel.distance.get(), 0.0001f);
        assertEquals("28.5", viewModel.avgSpeed.get());
        assertEquals(exercise.getAscent(), viewModel.ascent.get());

        // test without modifications
        Exercise unmodifiedExercise = viewModel.getExercise();
        assertEquals(exercise.getId(), unmodifiedExercise.getId());
        assertEquals(exercise.getDateTime(), unmodifiedExercise.getDateTime());
        assertEquals(exercise.getSportType(), unmodifiedExercise.getSportType());
        assertEquals(exercise.getSportSubType(), unmodifiedExercise.getSportSubType());
        assertEquals(exercise.getDuration(), unmodifiedExercise.getDuration());
        assertEquals(exercise.getIntensity(), unmodifiedExercise.getIntensity());
        assertEquals(exercise.getDistance(), unmodifiedExercise.getDistance(), 0.0001f);
        assertEquals(exercise.getAvgSpeed(), unmodifiedExercise.getAvgSpeed(), 0.0001f);
        assertEquals(exercise.getAvgHeartRate(), unmodifiedExercise.getAvgHeartRate());
        assertEquals(exercise.getAscent(), unmodifiedExercise.getAscent());
        assertEquals(exercise.getDescent(), unmodifiedExercise.getDescent());
        assertEquals(exercise.getCalories(), unmodifiedExercise.getCalories());
        assertEquals(exercise.getHrmFile(), unmodifiedExercise.getHrmFile());
        assertEquals(exercise.getEquipment(), unmodifiedExercise.getEquipment());
        assertEquals(exercise.getComment(), unmodifiedExercise.getComment());

        // test after modifications
        viewModel.time.set(LocalTime.of(14, 45));
        viewModel.distance.set(150f);
        viewModel.comment.set("  Bar Foo  ");

        Exercise modifiedExercise = viewModel.getExercise();
        assertEquals(LocalDateTime.of(2014, 10, 20, 14, 45, 0), modifiedExercise.getDateTime());
        assertEquals(150f, modifiedExercise.getDistance(), 0.0001f);
        assertEquals("Bar Foo", modifiedExercise.getComment());
    }

    /**
     * Test of method getExercise() with optional Integer values set to null.
     */
    @Test
    public void testGetExerciseWithOptionalNullValues() {

        exercise.setAvgHeartRate(null);
        exercise.setAscent(null);
        exercise.setDescent(null);
        exercise.setCalories(null);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);

        // test ViewModel, null values needs to be 0 there
        assertEquals(Integer.valueOf(0), viewModel.avgHeartRate.getValue());
        assertEquals(Integer.valueOf(0), viewModel.ascent.getValue());
        assertEquals(Integer.valueOf(0), viewModel.descent.getValue());
        assertEquals(Integer.valueOf(0), viewModel.calories.getValue());

        // test created Exercise, all 0 values must be null
        Exercise createdExercise = viewModel.getExercise();
        assertNull(createdExercise.getAvgHeartRate());
        assertNull(createdExercise.getAscent());
        assertNull(createdExercise.getDescent());
        assertNull(createdExercise.getCalories());

    }

    /**
     * Test of method getExercise() with english unit system.
     */
    @Test
    public void testGetExerciseWithEnglishUnits() {
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.ENGLISH), SpeedMode.SPEED);

        // test that metric units are converted to english units
        assertEquals(71.4577, viewModel.distance.get(), 0.0001f);
        assertEquals("17.709", viewModel.avgSpeed.get());
        assertEquals(4068, viewModel.ascent.get());

        // test without modifications, english units must be converted back to metric units
        // (a tiny delta needs to be accepted due to metric to english unit system conversion)
        Exercise unmodifiedExercise = viewModel.getExercise();
        assertEquals(exercise.getDistance(), unmodifiedExercise.getDistance(), 0.0002f);
        assertEquals(exercise.getAvgSpeed(), unmodifiedExercise.getAvgSpeed(), 0.0002f);
        assertEquals(exercise.getAscent(), unmodifiedExercise.getAscent());
    }

    /**
     * Test of method getExercise() with english unit system and the speed mode 'pace'.
     */
    @Test
    public void testGetExerciseWithEnglishUnitsAndSpeedModePace() {
        exercise.getSportType().setSpeedMode(SpeedMode.PACE);
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.ENGLISH), SpeedMode.SPEED);

        // test that metric units are converted to english units
        assertEquals(71.4577, viewModel.distance.get(), 0.0001f);
        // test that avg speed is formatted as pace by using the minutes/mile unit
        assertEquals("03:23", viewModel.avgSpeed.get());

        // test without modifications, the avg speed must be converted back to a float value for metric units
        // (a small delta needs to be accepted due to unit system and speed mode conversion)
        Exercise unmodifiedExercise = viewModel.getExercise();
        assertEquals(exercise.getAvgSpeed(), unmodifiedExercise.getAvgSpeed(), 0.1f);
    }

    /**
     * Test of the sport type change listener, which resets the distance and avg speed
     * properties when the new sport type does not records the distance.
     */
    @Test
    public void testSportTypeListener() {
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);

        // test with sport type where the distance is recorded
        SportType stWithDistance = new SportType(100L);
        stWithDistance.setRecordDistance(true);
        viewModel.sportType.set(stWithDistance);

        assertEquals(exercise.getDistance(), viewModel.distance.get(), 0.0001f);
        assertEquals("28.5", viewModel.avgSpeed.get());
        assertTrue(exercise.getDuration() > 0);
        assertTrue(viewModel.sportTypeRecordDistance.get());

        // test with sport type where the distance is not recorded
        SportType stWithoutDistance = new SportType(101L);
        stWithoutDistance.setRecordDistance(false);
        viewModel.sportType.set(stWithoutDistance);

        assertEquals(0f, viewModel.distance.get(), 0.0001f);
        assertEquals("0", viewModel.avgSpeed.get());
        assertTrue(exercise.getDuration() > 0);
        assertFalse(viewModel.sportTypeRecordDistance.get());
    }

    /**
     * Test of the automatic calculation of the distance on changes of the avg speed or duration values in speed mode
     * 'speed'.
     */
    @Test
    public void testAutoCalculationOfDistanceForSpeedModeSpeed() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(true);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(false);

        // change avg speed
        viewModel.avgSpeed.set("20.0");
        assertEquals(80f, viewModel.distance.get(), 0.0001f);
        assertEquals(4 * 3600, viewModel.duration.get());

        // change duration
        viewModel.duration.set((int) (4.75 * 3600));
        assertEquals(95f, viewModel.distance.get(), 0.0001f);
        assertEquals("20.0", viewModel.avgSpeed.get());
    }

    /**
     * Test of the automatic calculation of the distance on changes of the avg speed or duration values in speed mode
     * 'pace'.
     */
    @Test
    public void testAutoCalculationOfDistanceForSpeedModePace() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);
        exercise.getSportType().setSpeedMode(SpeedMode.PACE);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(true);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(false);

        // change avg speed
        viewModel.avgSpeed.set("02:00");
        assertEquals(120f, viewModel.distance.get(), 0.0001f);
        assertEquals(4 * 3600, viewModel.duration.get());

        // change duration
        viewModel.duration.set((int) (5 * 3600));
        assertEquals(150f, viewModel.distance.get(), 0.0001f);
        assertEquals("02:00", viewModel.avgSpeed.get());
    }

    /**
     * Test of the automatic calculation of the avg speed on changes of the distance or duration values in speed mode
     * 'speed'.
     */
    @Test
    public void testAutoCalculationOfAvgSpeedForSpeedModeSpeed() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(true);
        viewModel.autoCalcDuration.set(false);

        // change distance
        viewModel.distance.set(110f);
        assertEquals("27.5", viewModel.avgSpeed.get());
        assertEquals(4 * 3600, viewModel.duration.get());

        // change duration
        viewModel.duration.set((int) (3.5 * 3600));
        assertEquals(110f, viewModel.distance.get(), 0.0001f);
        assertEquals("31.429", viewModel.avgSpeed.get());
    }

    /**
     * Test of the automatic calculation of the avg speed on changes of the distance or duration values in speed mode
     * 'pace'.
     */
    @Test
    public void testAutoCalculationOfAvgSpeedForSpeedModePace() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);
        exercise.getSportType().setSpeedMode(SpeedMode.PACE);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(true);
        viewModel.autoCalcDuration.set(false);

        // change distance
        viewModel.distance.set(110f);
        assertEquals("02:10", viewModel.avgSpeed.get());
        assertEquals(4 * 3600, viewModel.duration.get());

        // change duration
        viewModel.duration.set((int) (3.5 * 3600));
        assertEquals(110f, viewModel.distance.get(), 0.0001f);
        assertEquals("01:54", viewModel.avgSpeed.get());
    }

    /**
     * Test of the automatic calculation of the duration on changes of the distance or avg speed values.
     */
    @Test
    public void testAutoCalculationOfDuration() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(true);

        // change distance
        viewModel.distance.set(112.5f);
        assertEquals("25", viewModel.avgSpeed.get());
        assertEquals((int) (4.5 * 3600), viewModel.duration.get());

        // change avg speed
        viewModel.avgSpeed.set("30");
        assertEquals(112.5f, viewModel.distance.get(), 0.0001f);
        assertEquals((int) (3.75 * 3600), viewModel.duration.get());
    }

    /**
     * Test of method setAutoCalcFields() when the distance is being calculated automatically.
     */
    @Test
    public void testSetAutoCalcFieldsWithAutomaticDistance() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(true);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(false);

        viewModel.setAutoCalcFields(120f, 40f, 5 * 3600);

        // the distance must not be set, must be calculated properly instead
        assertEquals(200f, viewModel.distance.get(), 0.0001f);
        assertEquals("40", viewModel.avgSpeed.get());
        assertEquals(5 * 3600, viewModel.duration.get());
    }

    /**
     * Test of method setAutoCalcFields() when the avg speed is being calculated automatically.
     */
    @Test
    public void testSetAutoCalcFieldsWithAutomaticAvgSpeed() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(true);
        viewModel.autoCalcDuration.set(false);

        viewModel.setAutoCalcFields(120f, 40f, 5 * 3600);

        // the avg speed must not be set, must be calculated properly instead
        assertEquals(120f, viewModel.distance.get(), 0.0001f);
        assertEquals("24", viewModel.avgSpeed.get());
        assertEquals(5 * 3600, viewModel.duration.get());
    }

    /**
     * Test of method setAutoCalcFields() when the duration is being calculated automatically.
     */
    @Test
    public void testSetAutoCalcFieldsWithAutomaticDuration() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(true);

        viewModel.setAutoCalcFields(120f, 40f, 5 * 3600);

        // the duration must not be set, must be calculated properly instead
        assertEquals(120f, viewModel.distance.get(), 0.0001f);
        assertEquals("40", viewModel.avgSpeed.get());
        assertEquals(3 * 3600, viewModel.duration.get());
    }

    /**
     * Test of method setAutoCalcFields() when the duration is being calculated automatically.
     * The specified distance and avg speed must be converted to english unit system.
     */
    @Test
    public void testSetAutoCalcFieldsWithAutomaticDurationAndEnglishUnits() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.ENGLISH), SpeedMode.SPEED);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(true);

        viewModel.setAutoCalcFields(120f, 40f, 5 * 3600);

        // the duration must not be set, must be calculated properly instead
        assertEquals(74.5645f, viewModel.distance.get(), 0.0001f);
        assertEquals("24.855", viewModel.avgSpeed.get());
        // (a tiny delta needs to be accepted due to metric to english unit system conversion)
        assertEquals(3 * 3600, viewModel.duration.get(), 1);
    }

    /**
     * Test of method setAvgSpeedFloatValue(): the exercise view model uses the metric units and the 'speed' speed mode.
     * So the avg string needs to be the same formatted value (km/h).
     */
    @Test
    public void testSetAvgSpeedFloatValueMetricSpeed() {

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);

        viewModel.setAvgSpeedFloatValue(21.3f);
        assertEquals("21.3", viewModel.avgSpeed.get());
    }

    /**
     * Test of method setAvgSpeedFloatValue(): the exercise view model uses the english units and the 'pace' speed mode.
     * So the avg string needs to be the converted formatted value (minutes/mile).
     */
    @Test
    public void testSetAvgSpeedFloatValueEnglishPace() {

        exercise.getSportType().setSpeedMode(SpeedMode.PACE);
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.ENGLISH), SpeedMode.SPEED);

        viewModel.setAvgSpeedFloatValue(21.3f);
        assertEquals("04:32", viewModel.avgSpeed.get());
    }

    /**
     * Test of method setAvgSpeedFloatValue(): the exercise view model uses the english units and the 'pace' speed mode.
     * The avg speed is 0, so the avg string needs to be the converted formatted value "00:00" (minutes/mile).
     */
    @Test
    public void testSetAvgSpeedFloatValueEnglishPaceZero() {

        exercise.getSportType().setSpeedMode(SpeedMode.PACE);
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.ENGLISH), SpeedMode.SPEED);

        viewModel.setAvgSpeedFloatValue(0f);
        assertEquals("00:00", viewModel.avgSpeed.get());
    }

    /**
     * Test of the proper conversion of the avg speed when the sport type and so the speed mode changes. 
     * Scenario: the model uses the 'speed' speed mode and the new sport type uses the same speed mode. So the avg speed
     * must not be changed.
     */
    @Test
    public void testAvgSpeedConversionOnSpeedModeChangesSame() {

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        assertEquals("28.5", viewModel.avgSpeed.get());

        viewModel.sportType.set(createSportType(432, SpeedMode.SPEED));
        assertEquals("28.5", viewModel.avgSpeed.get());
    }

    /**
     * Test of the proper conversion of the avg speed when the sport type and so the speed mode changes.
     * Scenario: the model uses the 'speed' speed mode and the new sport type uses the 'pace' speed mode. So the avg
     * speed must be formatted as minutes/kilometer.
     */
    @Test
    public void testAvgSpeedConversionOnSpeedModeChangesOther() {

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        assertEquals("28.5", viewModel.avgSpeed.get());

        viewModel.sportType.set(createSportType(432, SpeedMode.PACE));
        assertEquals("02:06", viewModel.avgSpeed.get());
    }

    /**
     * Test of the proper conversion of the avg speed when the sport type and so the speed mode changes.
     * Scenario: the model uses the 'speed' speed mode and the avg speed is 0. The new sport type uses the 'pace' speed
     * mode. So the avg speed must be 00:00 (minutes/kilometer).
     */
    @Test
    public void testAvgSpeedConversionOnSpeedModeChangesOtherNull() {

        exercise.setAvgSpeed(0f);
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.METRIC), SpeedMode.SPEED);
        assertEquals("0", viewModel.avgSpeed.get());

        viewModel.sportType.set(createSportType(432, SpeedMode.PACE));
        assertEquals("00:00", viewModel.avgSpeed.get());
    }

    /**
     * Test of the proper conversion of the avg speed when the sport type and so the speed mode changes.
     * Scenario: the model uses the 'speed' speed mode and the english unit system. The new sport type uses the 'pace'
     * speed mode. So the avg speed must be formatted as minutes/miles.
     */
    @Test
    public void testAvgSpeedConversionOnSpeedModeChangesEnglish() {

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise,
                new FormatUtils(UnitSystem.ENGLISH), SpeedMode.SPEED);
        assertEquals("17.709", viewModel.avgSpeed.get());

        viewModel.sportType.set(createSportType(432, SpeedMode.PACE));
        assertEquals("03:23", viewModel.avgSpeed.get());
    }

    private SportType createSportType(long id, SpeedMode speedMode) {
        SportType sportType = new SportType(id);
        sportType.setSpeedMode(speedMode);
        return sportType;
    }
}
