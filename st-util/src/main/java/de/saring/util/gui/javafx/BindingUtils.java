package de.saring.util.gui.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 * Utility class with various JavaFX property binding extensions.
 *
 * @author Stefan Saring
 */
public final class BindingUtils {

    private BindingUtils() {
    }

    /**
     * Binds the specified ToggleGroup to the specified ObjectProperty (this feature is missing in JavaFX).
     * This binding is not fully bidirectional. The initial selection of the ToggleGroup will be taken from
     * the current value of the ObjectProperty. Whenever the ToogleGroup selection changes (user input),
     * the ObjectProperty will have the value of the selected Toggle. Changes to the ObjectProperty value
     * will not be transferred to the current Toggle selection.<br/>
     * All Toggle controls of the ToogleGroup must define an userData object which is a potential value
     * of the bound ObjectProperty.<br/>
     * This method is helpful for binding ToggleButton or RadioButton groups to appropriate view model
     * properties.
     *
     * @param toggleGroup the toggle group to be bound
     * @param property the object property to be bound
     * @param <T> type of the object property to be bound
     */
    public static <T> void bindToggleGroupToProperty(final ToggleGroup toggleGroup, final ObjectProperty<T> property) {

        // check all toggles for required user data
        toggleGroup.getToggles().forEach(toggle -> {
            if (toggle.getUserData() == null) {
                throw new IllegalArgumentException("The ToggleGroup contains at least one Toggle without user data!");
            }
        });

        // select initial toggle for current property state
        for (Toggle toggle : toggleGroup.getToggles()) {
            if (property.getValue() != null && property.getValue().equals(toggle.getUserData())) {
                toggleGroup.selectToggle(toggle);
                break;
            }
        }

        // update property value on toggle selection changes
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            property.setValue((T) newValue.getUserData());
        });
    }
}
