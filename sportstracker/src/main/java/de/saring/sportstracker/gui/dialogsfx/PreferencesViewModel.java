package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.core.STOptions;
import de.saring.util.unitcalc.FormatUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * This ViewModel class provides JavaFX properties of all STOptions attributes to be edited in the dialog.
 * So they can be bound to the appropriate dialog view controls.
 *
 * @author Stefan Saring
 */
public class PreferencesViewModel {

    public final ObjectProperty<STOptions.View> initialView;
    public final ObjectProperty<STOptions.AutoCalculation> defaultAutoCalculation;
    public final BooleanProperty saveOnExit;

    public final ObjectProperty<FormatUtils.UnitSystem> unitSystem;
    public final ObjectProperty<FormatUtils.SpeedView> speedView;
    public final BooleanProperty weekStartMonday;

    public final BooleanProperty listViewShowAvgHeartrate;
    public final BooleanProperty listViewShowAscent;
    public final BooleanProperty listViewShowEnergy;
    public final BooleanProperty listViewShowEquipment;
    public final BooleanProperty listViewShowComment;

    public final BooleanProperty evDisplaySecondDiagram;

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
        this.weekStartMonday = new SimpleBooleanProperty(options.isWeekStartSunday());

        this.listViewShowAvgHeartrate = new SimpleBooleanProperty(options.isListViewShowAvgHeartrate());
        this.listViewShowAscent = new SimpleBooleanProperty(options.isListViewShowAscent());
        this.listViewShowEnergy = new SimpleBooleanProperty(options.isListViewShowEnergy());
        this.listViewShowEquipment = new SimpleBooleanProperty(options.isListViewShowEquipment());
        this.listViewShowComment = new SimpleBooleanProperty(options.isDisplaySecondDiagram());

        this.evDisplaySecondDiagram = new SimpleBooleanProperty(options.isDisplaySecondDiagram());
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
        options.setWeekStartSunday(!weekStartMonday.get());

        options.setListViewShowAvgHeartrate(listViewShowAvgHeartrate.get());
        options.setListViewShowAscent(listViewShowAscent.get());
        options.setListViewShowEnergy(listViewShowEnergy.get());
        options.setListViewShowEquipment(listViewShowEquipment.get());
        options.setListViewShowComment(listViewShowComment.get());

        options.setDisplaySecondDiagram(evDisplaySecondDiagram.get());
    }
}
