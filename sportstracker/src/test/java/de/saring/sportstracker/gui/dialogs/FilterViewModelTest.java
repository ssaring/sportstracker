package de.saring.sportstracker.gui.dialogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;

/**
 * Unit tests of class FilterViewModel.
 *
 * @author Stefan Saring
 */
public class FilterViewModelTest {

    private ExerciseFilter exerciseFilter;

    @Before
    public void setUp() {
        exerciseFilter = new ExerciseFilter();
        exerciseFilter.setDateStart(LocalDate.of(2014, 10, 1));
        exerciseFilter.setDateEnd(LocalDate.of(2014, 10, 31));
        exerciseFilter.setSportType(new SportType(100));
        exerciseFilter.setSportSubType(new SportSubType(200));
        exerciseFilter.setIntensity(Exercise.IntensityType.HIGH);
        exerciseFilter.setEquipment(new Equipment(300));
        exerciseFilter.setCommentSubString("Foo Bar");
        exerciseFilter.setRegularExpressionMode(true);
    }

    /**
     * Test of method getExerciseFilter().
     */
    @Test
    public void testGetExerciseFilter() {
        FilterViewModel viewModel = new FilterViewModel(exerciseFilter);

        // test without modifications
        ExerciseFilter unmodifiedFilter = viewModel.getExerciseFilter();
        assertEquals(exerciseFilter.getDateStart(), unmodifiedFilter.getDateStart());
        assertEquals(exerciseFilter.getDateEnd(), unmodifiedFilter.getDateEnd());
        assertEquals(exerciseFilter.getSportType(), unmodifiedFilter.getSportType());
        assertEquals(exerciseFilter.getSportSubType(), unmodifiedFilter.getSportSubType());
        assertEquals(exerciseFilter.getIntensity(), unmodifiedFilter.getIntensity());
        assertEquals(exerciseFilter.getEquipment(), unmodifiedFilter.getEquipment());
        assertEquals(exerciseFilter.getCommentSubString(), unmodifiedFilter.getCommentSubString());
        assertEquals(exerciseFilter.isRegularExpressionMode(), unmodifiedFilter.isRegularExpressionMode());

        // test after modifications
        viewModel.dateEnd.set(LocalDate.of(2014, 12, 31));
        viewModel.sportSubtype.set(new SportSubType(201));
        viewModel.intensity.set(new FilterViewModel.IntensityItem(null));
        viewModel.commentSubString.set("   Bar Foo    ");

        ExerciseFilter modifiedFilter = viewModel.getExerciseFilter();
        assertEquals(LocalDate.of(2014, 12, 31), modifiedFilter.getDateEnd());
        assertEquals(201, modifiedFilter.getSportSubType().getId());
        assertNull(modifiedFilter.getIntensity());
        assertEquals("Bar Foo", modifiedFilter.getCommentSubString());
    }
}
