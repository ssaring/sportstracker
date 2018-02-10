package de.saring.sportstracker.gui.dialogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import de.saring.sportstracker.data.EntryFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;

/**
 * Unit tests of class FilterViewModel.
 *
 * @author Stefan Saring
 */
public class FilterViewModelTest {

    private EntryFilter entryFilter;

    @BeforeEach
    public void setUp() {
        entryFilter = new EntryFilter();
        entryFilter.setDateStart(LocalDate.of(2014, 10, 1));
        entryFilter.setDateEnd(LocalDate.of(2014, 10, 31));
        entryFilter.setEntryType(EntryFilter.EntryType.EXERCISE);
        entryFilter.setSportType(new SportType(100));
        entryFilter.setSportSubType(new SportSubType(200));
        entryFilter.setIntensity(Exercise.IntensityType.HIGH);
        entryFilter.setEquipment(new Equipment(300));
        entryFilter.setCommentSubString("Foo Bar");
        entryFilter.setRegularExpressionMode(true);
    }

    /**
     * Test of method getExerciseFilter().
     */
    @Test
    public void testGetExerciseFilter() {
        FilterViewModel viewModel = new FilterViewModel(entryFilter, true);

        // test without modifications
        EntryFilter unmodifiedFilter = viewModel.getExerciseFilter();
        assertEquals(entryFilter.getDateStart(), unmodifiedFilter.getDateStart());
        assertEquals(entryFilter.getDateEnd(), unmodifiedFilter.getDateEnd());
        assertEquals(entryFilter.getEntryType(), unmodifiedFilter.getEntryType());
        assertEquals(entryFilter.getSportType(), unmodifiedFilter.getSportType());
        assertEquals(entryFilter.getSportSubType(), unmodifiedFilter.getSportSubType());
        assertEquals(entryFilter.getIntensity(), unmodifiedFilter.getIntensity());
        assertEquals(entryFilter.getEquipment(), unmodifiedFilter.getEquipment());
        assertEquals(entryFilter.getCommentSubString(), unmodifiedFilter.getCommentSubString());
        assertEquals(entryFilter.isRegularExpressionMode(), unmodifiedFilter.isRegularExpressionMode());

        // test after modifications
        viewModel.dateEnd.set(LocalDate.of(2014, 12, 31));
        viewModel.entryType.set(EntryFilter.EntryType.WEIGHT);
        viewModel.sportSubtype.set(new SportSubType(201));
        viewModel.intensity.set(new FilterViewModel.IntensityItem(null));
        viewModel.commentSubString.set("   Bar Foo    ");

        EntryFilter modifiedFilter = viewModel.getExerciseFilter();
        assertEquals(LocalDate.of(2014, 12, 31), modifiedFilter.getDateEnd());
        assertEquals(EntryFilter.EntryType.WEIGHT, modifiedFilter.getEntryType());
        assertEquals(201, modifiedFilter.getSportSubType().getId());
        assertNull(modifiedFilter.getIntensity());
        assertEquals("Bar Foo", modifiedFilter.getCommentSubString());
    }
}
