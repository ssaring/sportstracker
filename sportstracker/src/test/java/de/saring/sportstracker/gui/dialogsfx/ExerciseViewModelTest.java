package de.saring.sportstracker.gui.dialogsfx;

import static org.junit.Assert.assertEquals;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.util.unitcalc.FormatUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Unit tests of class ExerciseViewModelTest.
 *
 * @author Stefan Saring
 */
public class ExerciseViewModelTest {

    private Exercise exercise;

    @Before
    public void setUp() {
        exercise = new Exercise(123);
        exercise.setDateTime(LocalDateTime.of(2014, 10, 20, 7, 30, 40));
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
        viewModel.autoCalcDuration.set(true);

        // test that metric units are not converted
        assertEquals(exercise.getDistance(), viewModel.distance.get(), 0.0001f);
        assertEquals(exercise.getAvgSpeed(), viewModel.avgSpeed.get(), 0.0001f);
        assertEquals(exercise.getAscent(), viewModel.ascent.get());

        // test without modifications
        Exercise unmodifiedExercise = viewModel.getExercise();
        assertEquals(exercise.getId(), unmodifiedExercise.getId());
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
        viewModel.distance.set(150f);
        viewModel.comment.set("Bar Foo");

        Exercise modifiedExercise = viewModel.getExercise();
        assertEquals(150f, modifiedExercise.getDistance(), 0.0001f);
        assertEquals("Bar Foo", modifiedExercise.getComment());
    }

    /**
     * Test of method getExercise() with english unit system.
     */
    @Test
    public void testGetExerciseWithEnglishUnits() {

        ExerciseViewModel viewModel = new ExerciseViewModel(exercise, FormatUtils.UnitSystem.English);
        viewModel.autoCalcDuration.set(true);

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
}
