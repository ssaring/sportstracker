package de.saring.sportstracker.gui.dialogs;

import de.saring.sportstracker.data.Weight;
import de.saring.util.unitcalc.FormatUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests of class ExerciseViewModel.
 *
 * @author Stefan Saring
 */
public class WeightViewModelTest {

    private Weight weight;

    @Before
    public void setUp() {
        weight = new Weight(123);
        weight.setDateTime(LocalDateTime.of(2014, 10, 20, 7, 30, 0));
        weight.setValue(65.7f);
        weight.setComment("Foo Bar");
    }

    /**
     * Test of method getWeight() with metric unit system.
     */
    @Test
    public void testGetWeightWithMetricUnits() {
        WeightViewModel viewModel = new WeightViewModel(weight, FormatUtils.UnitSystem.Metric);

        // test that metric unit is not converted
        assertEquals(weight.getValue(), viewModel.value.get(), 0.0001f);

        // test without modifications
        Weight unmodifiedWeight = viewModel.getWeight();
        assertEquals(weight.getId(), unmodifiedWeight.getId());
        assertEquals(weight.getDateTime(), unmodifiedWeight.getDateTime());
        assertEquals(weight.getValue(), unmodifiedWeight.getValue(), 0.0001f);
        assertEquals(weight.getComment(), unmodifiedWeight.getComment());

        // test after modifications
        viewModel.hour.set(14);
        viewModel.minute.set(45);
        viewModel.value.set(67.5f);
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
        WeightViewModel viewModel = new WeightViewModel(weight, FormatUtils.UnitSystem.English);

        // test that metric unit is converted to english unit
        assertEquals(144.8422, viewModel.value.get(), 0.0001f);

        // test without modifications, english unit must be converted back to metric unit
        Weight unmodifiedWeight = viewModel.getWeight();
        assertEquals(weight.getValue(), unmodifiedWeight.getValue(), 0.0001f);
    }
}
