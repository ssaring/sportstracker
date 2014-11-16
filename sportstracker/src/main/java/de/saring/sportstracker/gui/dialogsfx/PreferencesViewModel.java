package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.core.STOptions;
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

    public final ObjectProperty<STOptions.AutoCalculation> defaultAutoCalculation;
    public final BooleanProperty saveOnExit;

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

        // TODO add missing
        this.defaultAutoCalculation = new SimpleObjectProperty<>(options.getDefaultAutoCalcuation());
        this.saveOnExit = new SimpleBooleanProperty(options.isSaveOnExit());

        this.listViewShowAvgHeartrate = new SimpleBooleanProperty(options.isListViewShowAvgHeartrate());
        this.listViewShowAscent = new SimpleBooleanProperty(options.isListViewShowAscent());
        this.listViewShowEnergy = new SimpleBooleanProperty(options.isListViewShowEnergy());
        this.listViewShowEquipment = new SimpleBooleanProperty(options.isListViewShowEquipment());
        this.listViewShowComment = new SimpleBooleanProperty(options.isDisplaySecondDiagram());

        this.evDisplaySecondDiagram = new SimpleBooleanProperty(options.isDisplaySecondDiagram());
    }

    /**
     * Creates a new STOptions object from the edited JavaFX properties.
     *
     * @return STOptions
     */
    public STOptions getOptions() {
        final STOptions options = new STOptions();
        // TODO add missing
        options.setDefaultAutoCalcuation(defaultAutoCalculation.get());
        options.setSaveOnExit(saveOnExit.get());

        options.setListViewShowAvgHeartrate(listViewShowAvgHeartrate.get());
        options.setListViewShowAscent(listViewShowAscent.get());
        options.setListViewShowEnergy(listViewShowEnergy.get());
        options.setListViewShowEquipment(listViewShowEquipment.get());
        options.setListViewShowComment(listViewShowComment.get());

        options.setDisplaySecondDiagram(evDisplaySecondDiagram.get());
        return options;
    }
}
