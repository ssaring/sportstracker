package de.saring.sportstracker.gui.dialogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.saring.sportstracker.core.STOptions;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Unit tests of class PreferencesViewModel.
 *
 * @author Stefan Saring
 */
public class PreferencesViewModelTest {

    private STOptions options;

    @Before
    public void setUp() {
        options = new STOptions();
        options.setInitialView(STOptions.View.List);
        options.setUnitSystem(FormatUtils.UnitSystem.English);
        options.setSpeedView(FormatUtils.SpeedView.DistancePerHour);
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
        assertEquals(FormatUtils.UnitSystem.English, options.getUnitSystem());
        assertEquals(FormatUtils.SpeedView.DistancePerHour, options.getSpeedView());
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
        viewModel.unitSystem.set(FormatUtils.UnitSystem.Metric);
        viewModel.speedView.set(FormatUtils.SpeedView.MinutesPerDistance);
        viewModel.weekStart.set(PreferencesViewModel.WeekStart.MONDAY);
        viewModel.defaultAutoCalculation.set(STOptions.AutoCalculation.Duration);
        viewModel.saveOnExit.set(false);

        viewModel.storeInOptions(options);
        assertEquals(STOptions.View.Calendar, options.getInitialView());
        assertEquals(FormatUtils.UnitSystem.Metric, options.getUnitSystem());
        assertEquals(FormatUtils.SpeedView.MinutesPerDistance, options.getSpeedView());
        assertEquals(STOptions.AutoCalculation.Duration, options.getDefaultAutoCalcuation());
        assertFalse(options.isSaveOnExit());
    }
}
