package de.saring.sportstracker.gui.dialogs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;

import de.saring.util.unitcalc.UnitSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.saring.sportstracker.data.Weight;

/**
 * Unit tests of class ExerciseViewModel.
 *
 * @author Stefan Saring
 */
public class WeightViewModelTest {

    private Weight weight;

    @BeforeEach
    public void setUp() {
        weight = new Weight(123L);
        weight.setDateTime(LocalDateTime.of(2014, 10, 20, 7, 30, 0));
        weight.setValue(65.7f);
        weight.setComment("Foo Bar");
    }

    /**
     * Test of method getWeight() with metric unit system.
     */
    @Test
    public void testGetWeightWithMetricUnits() {
        WeightViewModel viewModel = new WeightViewModel(weight, UnitSystem.METRIC);

        // test that metric unit is not converted
        assertEquals(weight.getValue(), viewModel.value.get(), 0.0001f);

        // test without modifications
        Weight unmodifiedWeight = viewModel.getWeight();
        assertEquals(weight.getId(), unmodifiedWeight.getId());
        assertEquals(weight.getDateTime(), unmodifiedWeight.getDateTime());
        assertEquals(weight.getValue(), unmodifiedWeight.getValue(), 0.0001f);
        assertEquals(weight.getComment(), unmodifiedWeight.getComment());

        // test after modifications
        viewModel.time.set(LocalTime.of(14, 45));
        viewModel.value.set(67.5);
        viewModel.comment.set("  Bar Foo  ");

        Weight modifiedWeight = viewModel.getWeight();
        assertEquals(LocalDateTime.of(2014, 10, 20, 14, 45, 0), modifiedWeight.getDateTime());
        assertEquals(67.5f, modifiedWeight.getValue(), 0.0001f);
        assertEquals("Bar Foo", modifiedWeight.getComment());
    }

    /**
     * Test of method getWeight() with english unit system.
     */
    @Test
    public void testGetWeightWithEnglishUnits() {
        WeightViewModel viewModel = new WeightViewModel(weight, UnitSystem.ENGLISH);

        // test that metric unit is converted to english unit
        assertEquals(144.8422, viewModel.value.get(), 0.0001f);

        // test without modifications, english unit must be converted back to metric unit
        Weight unmodifiedWeight = viewModel.getWeight();
        assertEquals(weight.getValue(), unmodifiedWeight.getValue(), 0.0001f);
    }
}
