package de.saring.sportstracker.gui.dialogs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import de.saring.sportstracker.core.STOptions;
import de.saring.util.unitcalc.FormatUtils;

/**
 * This ViewModel class provides JavaFX properties of all STOptions attributes to be edited in the dialog.
 * So they can be bound to the appropriate dialog view controls.
 *
 * @author Stefan Saring
 */
public class PreferencesViewModel {

    /**
     * List of possible week start days.
     */
    public enum WeekStart {

        /** Week starts at monday. */
        MONDAY,
        /** Week starts at sunday. */
        SUNDAY
    }
    
    public final ObjectProperty<STOptions.View> initialView;
    public final ObjectProperty<STOptions.AutoCalculation> defaultAutoCalculation;
    public final BooleanProperty saveOnExit;

    public final ObjectProperty<FormatUtils.UnitSystem> unitSystem;
    public final ObjectProperty<FormatUtils.SpeedView> speedView;
    public final ObjectProperty<WeekStart> weekStart;

    public final BooleanProperty listViewShowAvgHeartrate;
    public final BooleanProperty listViewShowAscent;
    public final BooleanProperty listViewShowDescent;
    public final BooleanProperty listViewShowEnergy;
    public final BooleanProperty listViewShowEquipment;
    public final BooleanProperty listViewShowComment;

    public final BooleanProperty evDisplaySecondChart;
    public final BooleanProperty evDisplaySmoothedCharts;

    /**
     * Creates the PreferencesViewModel with JavaFX properties for the passed STOptions object.
     *
     * @param options options to be edited
     */
    public PreferencesViewModel(final STOptions options) {

        this.initialView = new SimpleObjectProperty<>(options.getInitialView());
        this.defaultAutoCalculation = new SimpleObjectProperty<>(options.getDefaultAutoCalcuation());
        this.saveOnExit = new SimpleBooleanProperty(options.isSaveOnExit());

        this.unitSystem = new SimpleObjectProperty<>(options.getUnitSystem());
        this.speedView = new SimpleObjectProperty<>(options.getSpeedView());
        this.weekStart = new SimpleObjectProperty<>(options.isWeekStartSunday() ? WeekStart.SUNDAY : WeekStart.MONDAY);

        this.listViewShowAvgHeartrate = new SimpleBooleanProperty(options.isListViewShowAvgHeartrate());
        this.listViewShowAscent = new SimpleBooleanProperty(options.isListViewShowAscent());
        this.listViewShowDescent = new SimpleBooleanProperty(options.isListViewShowDescent());
        this.listViewShowEnergy = new SimpleBooleanProperty(options.isListViewShowEnergy());
        this.listViewShowEquipment = new SimpleBooleanProperty(options.isListViewShowEquipment());
        this.listViewShowComment = new SimpleBooleanProperty(options.isListViewShowComment());

        this.evDisplaySecondChart = new SimpleBooleanProperty(options.isDisplaySecondChart());
        this.evDisplaySmoothedCharts = new SimpleBooleanProperty(options.isDisplaySmoothedCharts());
    }

    /**
     * Stores the value of the edited JavaFX properties in the passed STOptions object.
     *
     * @param options options object for storing
     */
    public void storeInOptions(final STOptions options) {

        options.setInitialView(initialView.get());
        options.setDefaultAutoCalcuation(defaultAutoCalculation.get());
        options.setSaveOnExit(saveOnExit.get());

        options.setUnitSystem(unitSystem.get());
        options.setSpeedView(speedView.get());
        options.setWeekStartSunday(weekStart.get() == WeekStart.SUNDAY);

        options.setListViewShowAvgHeartrate(listViewShowAvgHeartrate.get());
        options.setListViewShowAscent(listViewShowAscent.get());
        options.setListViewShowDescent(listViewShowDescent.get());
        options.setListViewShowEnergy(listViewShowEnergy.get());
        options.setListViewShowEquipment(listViewShowEquipment.get());
        options.setListViewShowComment(listViewShowComment.get());

        options.setDisplaySecondChart(evDisplaySecondChart.get());
        options.setDisplaySmoothedCharts(evDisplaySmoothedCharts.get());
    }
}
