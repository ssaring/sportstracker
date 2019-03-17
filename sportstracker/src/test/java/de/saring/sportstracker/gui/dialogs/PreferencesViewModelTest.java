package de.saring.sportstracker.gui.dialogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.saring.util.unitcalc.SpeedMode;
import de.saring.util.unitcalc.UnitSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.saring.sportstracker.core.STOptions;

/**
 * Unit tests of class PreferencesViewModel.
 *
 * @author Stefan Saring
 */
public class PreferencesViewModelTest {

    private STOptions options;

    @BeforeEach
    public void setUp() {
        options = new STOptions();
        options.setInitialView(STOptions.View.List);
        options.setUnitSystem(UnitSystem.ENGLISH);
        options.setPreferredSpeedMode(SpeedMode.SPEED);
        options.setDefaultAutoCalcuation(STOptions.AutoCalculation.AvgSpeed);
        options.setSaveOnExit(true);
        options.setDisplaySecondChart(false);
        options.setDisplaySmoothedCharts(false);
        options.setWeekStartSunday(true);
        options.setListViewShowAvgHeartrate(true);
        options.setListViewShowAscent(false);
        options.setListViewShowEnergy(true);
        options.setListViewShowEquipment(false);
        options.setListViewShowComment(true);
        options.setPreviousExerciseDirectory("/Foo/Bar");
    }

    /**
     * Test of method storeInOptions().
     */
    @Test
    public void testStoreInOptions() {
        PreferencesViewModel viewModel = new PreferencesViewModel(options);

        // test without modifications
        viewModel.storeInOptions(options);
        assertEquals(STOptions.View.List, options.getInitialView());
        assertEquals(UnitSystem.ENGLISH, options.getUnitSystem());
        assertEquals(SpeedMode.SPEED, options.getPreferredSpeedMode());
        assertEquals(STOptions.AutoCalculation.AvgSpeed, options.getDefaultAutoCalcuation());
        assertTrue(options.isSaveOnExit());
        assertFalse(options.isDisplaySecondChart());
        assertFalse(options.isDisplaySmoothedCharts());
        assertTrue(options.isWeekStartSunday());
        assertTrue(options.isListViewShowAvgHeartrate());
        assertFalse(options.isListViewShowAscent());
        assertTrue(options.isListViewShowEnergy());
        assertFalse(options.isListViewShowEquipment());
        assertTrue(options.isListViewShowComment());
        assertEquals("/Foo/Bar", options.getPreviousExerciseDirectory());

        // test after modifications
        viewModel.initialView.set(STOptions.View.Calendar);
        viewModel.unitSystem.set(UnitSystem.METRIC);
        viewModel.preferredSpeedMode.set(SpeedMode.PACE);
        viewModel.weekStart.set(PreferencesViewModel.WeekStart.MONDAY);
        viewModel.defaultAutoCalculation.set(STOptions.AutoCalculation.Duration);
        viewModel.saveOnExit.set(false);

        viewModel.storeInOptions(options);
        assertEquals(STOptions.View.Calendar, options.getInitialView());
        assertEquals(UnitSystem.METRIC, options.getUnitSystem());
        assertEquals(SpeedMode.PACE, options.getPreferredSpeedMode());
        assertEquals(STOptions.AutoCalculation.Duration, options.getDefaultAutoCalcuation());
        assertFalse(options.isSaveOnExit());
    }
}
