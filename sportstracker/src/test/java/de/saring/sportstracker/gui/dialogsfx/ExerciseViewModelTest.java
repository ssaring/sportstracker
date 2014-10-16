package de.saring.sportstracker.gui.dialogsfx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.util.unitcalc.FormatUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Unit tests of class ExerciseViewModel.
 *
 * @author Stefan Saring
 */
public class ExerciseViewModelTest {

    private Exercise exercise;

    @Before
    public void setUp() {
        exercise = new Exercise(123);
        exercise.setDateTime(LocalDateTime.of(2014, 10, 20, 7, 30, 0));
        exercise.setSportType(new SportType(234));
        exercise.setSportSubType(new SportSubType(345));
        exercise.setDuration(3600);
        exercise.setIntensity(Exercise.IntensityType.HIGH);
        exercise.setDistance(115f);
        exercise.setAvgSpeed(28.5f);
        exercise.setAvgHeartRate(128);
        exercise.setAscent(1240);
        exercise.setCalories(1950);
        exercise.setHrmFile("foo.hrm");
        exercise.setEquipment(new Equipment(456));
        exercise.setComment("Foo Bar");
    }

    /**
     * Test of method getExercise() with metric unit system.
     */
    @Test
    public void testGetExerciseWithMetricUnits() {
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.Metric);

        // test that metric units are not converted
        assertEquals(exercise.getDistance(), viewModel.distance.get(), 0.0001f);
        assertEquals(exercise.getAvgSpeed(), viewModel.avgSpeed.get(), 0.0001f);
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
        assertEquals(exercise.getCalories(), unmodifiedExercise.getCalories());
        assertEquals(exercise.getHrmFile(), unmodifiedExercise.getHrmFile());
        assertEquals(exercise.getEquipment(), unmodifiedExercise.getEquipment());
        assertEquals(exercise.getComment(), unmodifiedExercise.getComment());

        // test after modifications
        viewModel.hour.set(14);
        viewModel.minute.set(45);
        viewModel.distance.set(150f);
        viewModel.comment.set("  Bar Foo  ");

        Exercise modifiedExercise = viewModel.getExercise();
        assertEquals(LocalDateTime.of(2014, 10, 20, 14, 45, 0), modifiedExercise.getDateTime());
        assertEquals(150f, modifiedExercise.getDistance(), 0.0001f);
        assertEquals("Bar Foo", modifiedExercise.getComment());
    }

    /**
     * Test of method getExercise() with english unit system.
     */
    @Test
    public void testGetExerciseWithEnglishUnits() {
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.English);

        // test that metric units are converted to english units
        assertEquals(71.4577, viewModel.distance.get(), 0.0001f);
        assertEquals(17.7091, viewModel.avgSpeed.get(), 0.0001f);
        assertEquals(4068, viewModel.ascent.get());

        // test without modifications, english units must be converted back to metric units
        Exercise unmodifiedExercise = viewModel.getExercise();
        assertEquals(exercise.getDistance(), unmodifiedExercise.getDistance(), 0.0001f);
        assertEquals(exercise.getAvgSpeed(), unmodifiedExercise.getAvgSpeed(), 0.0001f);
        assertEquals(exercise.getAscent(), unmodifiedExercise.getAscent());
    }

    /**
     * Test of the sport type change listener, which resets the distance and avg speed
     * properties when the new sport type does not records the distance.
     */
    @Test
    public void testSportTypeListener() {
        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.Metric);

        // test with sport type where the distance is recorded
        SportType stWithDistance = new SportType(100);
        stWithDistance.setRecordDistance(true);
        viewModel.sportType.set(stWithDistance);

        assertEquals(exercise.getDistance(), viewModel.distance.get(), 0.0001f);
        assertEquals(exercise.getAvgSpeed(), viewModel.avgSpeed.get(), 0.0001f);
        assertTrue(exercise.getDuration() > 0);
        assertTrue(viewModel.sportTypeRecordDistance.get());

        // test with sport type where the distance is not recorded
        SportType stWithoutDistance = new SportType(101);
        stWithoutDistance.setRecordDistance(false);
        viewModel.sportType.set(stWithoutDistance);

        assertEquals(0f, viewModel.distance.get(), 0.0001f);
        assertEquals(0f, viewModel.avgSpeed.get(), 0.0001f);
        assertTrue(exercise.getDuration() > 0);
        assertFalse(viewModel.sportTypeRecordDistance.get());
    }

    /**
     * Test of the automatic calculation of the distance on changes of the avg speed or duration values.
     */
    @Test
    public void testAutoCalculationOfDistance() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.Metric);
        viewModel.autoCalcDistance.set(true);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(false);

        // change avg speed
        viewModel.avgSpeed.set(20f);
        assertEquals(80f, viewModel.distance.get(), 0.0001f);
        assertEquals(4 * 3600, viewModel.duration.get());

        // change duration
        viewModel.duration.set((int) (4.75 * 3600));
        assertEquals(95f, viewModel.distance.get(), 0.0001f);
        assertEquals(20f, viewModel.avgSpeed.get(), 0.0001f);
    }

    /**
     * Test of the automatic calculation of the avg speed on changes of the distance or duration values.
     */
    @Test
    public void testAutoCalculationOfAvgSpeed() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.Metric);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(true);
        viewModel.autoCalcDuration.set(false);

        // change distance
        viewModel.distance.set(110f);
        assertEquals(27.5f, viewModel.avgSpeed.get(), 0.0001f);
        assertEquals(4 * 3600, viewModel.duration.get());

        // change duration
        viewModel.duration.set((int) (3.5 * 3600));
        assertEquals(110f, viewModel.distance.get(), 0.0001f);
        assertEquals(31.4286f, viewModel.avgSpeed.get(), 0.0001f);
    }

    /**
     * Test of the automatic calculation of the duration on changes of the distance or avg speed values.
     */
    @Test
    public void testAutoCalculationOfDuration() {

        exercise.setDistance(100f);
        exercise.setAvgSpeed(25f);
        exercise.setDuration(4 * 3600);

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.Metric);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(true);

        // change distance
        viewModel.distance.set(112.5f);
        assertEquals(25f, viewModel.avgSpeed.get(), 0.0001f);
        assertEquals((int) (4.5 * 3600), viewModel.duration.get());

        // change avg speed
        viewModel.avgSpeed.set(30f);
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

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.Metric);
        viewModel.autoCalcDistance.set(true);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(false);

        viewModel.setAutoCalcFields(120f, 40f, 5 * 3600);

        // the distance must not be set, must be calculated properly instead
        assertEquals(200f, viewModel.distance.get(), 0.0001f);
        assertEquals(40f, viewModel.avgSpeed.get(), 0.0001f);
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

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.Metric);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(true);
        viewModel.autoCalcDuration.set(false);

        viewModel.setAutoCalcFields(120f, 40f, 5 * 3600);

        // the avg speed must not be set, must be calculated properly instead
        assertEquals(120f, viewModel.distance.get(), 0.0001f);
        assertEquals(24f, viewModel.avgSpeed.get(), 0.0001f);
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

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.Metric);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(true);

        viewModel.setAutoCalcFields(120f, 40f, 5 * 3600);

        // the duration must not be set, must be calculated properly instead
        assertEquals(120f, viewModel.distance.get(), 0.0001f);
        assertEquals(40f, viewModel.avgSpeed.get(), 0.0001f);
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

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.English);
        viewModel.autoCalcDistance.set(false);
        viewModel.autoCalcAvgSpeed.set(false);
        viewModel.autoCalcDuration.set(true);

        viewModel.setAutoCalcFields(120f, 40f, 5 * 3600);

        // the duration must not be set, must be calculated properly instead
        assertEquals(74.5645f, viewModel.distance.get(), 0.0001f);
        assertEquals(24.8548f, viewModel.avgSpeed.get(), 0.0001f);
        assertEquals(3 * 3600, viewModel.duration.get());
    }
}
