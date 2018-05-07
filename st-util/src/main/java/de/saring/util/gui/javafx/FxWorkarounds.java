package de.saring.util.gui.javafx;

import javafx.scene.control.DatePicker;

import java.time.DateTimeException;

/**
 * Collection of workarounds for JavaFX control problems.
 *
 * @author Stefan Saring
 */
public final class FxWorkarounds {

    private FxWorkarounds() {
    }

    /**
     * Fixes the text entry of DatePicker components. Due to the bug https://bugs.openjdk.java.net/browse/JDK-8136838
     * all changes in the date picker textfield are never accepted. Although this bug is marked as closed, it is still
     * present in JDK 10.<br/
     * This workaround adds a focus listener to the texfield. When the focus gets lost, then it sets the new date value
     * from the textfield.
     *
     * @param datePicker DatePicker control
     */
    public static void fixDatePickerTextEntry(DatePicker datePicker) {

        datePicker.getEditor().focusedProperty().addListener((observable, oldValue, isFocused) -> {
            if (!isFocused) {
                String editorText = datePicker.getEditor().getText();
                try {
                    datePicker.setValue(datePicker.getConverter().fromString(editorText));
                }
                catch (DateTimeException e) {
                    // can be thrown on invalid user inputs
                }
            }
        });
    }
}
