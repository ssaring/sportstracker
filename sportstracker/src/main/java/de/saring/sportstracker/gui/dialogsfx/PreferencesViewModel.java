package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.core.STOptions;

/**
 * This ViewModel class provides JavaFX properties of all STOptions attributes to be edited in the dialog.
 * So they can be bound to the appropriate dialog view controls.
 *
 * @author Stefan Saring
 */
public class PreferencesViewModel {

    // TODO public final IntegerProperty hour;

    /**
     * Creates the PreferencesViewModel with JavaFX properties for the passed STOptions object.
     *
     * @param options options to be edited
     */
    public PreferencesViewModel(final STOptions options) {
        // TODO this.hour = new SimpleIntegerProperty(note.getDateTime().getHour());
    }

    /**
     * Creates a new STOptions object from the edited JavaFX properties.
     *
     * @return STOptions
     */
    public STOptions getOptions() {
        final STOptions options = new STOptions();
        // TODO note.setDateTime(LocalDateTime.of(date.get(), LocalTime.of(hour.getValue(), minute.getValue())));
        return options;
    }
}
