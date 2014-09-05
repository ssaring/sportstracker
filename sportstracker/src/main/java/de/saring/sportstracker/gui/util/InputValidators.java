package de.saring.sportstracker.gui.util;

import de.saring.sportstracker.gui.STContext;
import javafx.scene.control.TextInputControl;

import java.text.NumberFormat;

/**
 * Util class for various JavaFX input control validations.
 *
 * @author Stefan Saring
 */
public final class InputValidators {

    private InputValidators() {}

    /**
     * Validates and returns the value of the specified TextControl. If the value is missing or empty, then
     * an error dialog with the specified title and message will be shown. The TextControl will get the
     * focus in error case.
     *
     * @param context the SportsTracker UI context
     * @param textControl TextControl with the value
     * @param errorTitleKey resource key of the error title
     * @param errorMessageKey resource key of the error message
     * @return the valid TextControl value if present or null
     */
    public static String getRequiredTextControlValue(final STContext context, final TextInputControl textControl,
        final String errorTitleKey, final String errorMessageKey) {

        String value = textControl.getText() == null ? "" : textControl.getText().trim();
        if (value.isEmpty()) {
            context.showFxErrorDialog(textControl.getScene().getWindow(), errorTitleKey, errorMessageKey);
            textControl.selectAll();
            textControl.requestFocus();
            return null;
        }
        return value;
    }

    /**
     * Validates and returns the integer value of the specified TextControl. If the value is missing or not in the
     * specified range, then an error dialog with the specified title and message will be shown. The TextControl
     * will be selected and gets the focus in error case.
     *
     * @param context the SportsTracker UI context
     * @param textControl TextControl with the value
     * @param minValue the minimum value
     * @param maxValue the maximum value
     * @param errorTitleKey resource key of the error title
     * @param errorMessageKey resource key of the error message
     * @return the valid TextControl integer value if present or null
     */
    public static Integer getRequiredTextControlIntegerValue(final STContext context,
        final TextInputControl textControl, final int minValue, final int maxValue,
        final String errorTitleKey, final String errorMessageKey) {

        try {
            int value = NumberFormat.getInstance().parse(textControl.getText()).intValue();
            if (value < minValue || value > maxValue) {
                throw new Exception("The value must be in range " + minValue + "..." + maxValue + " !");
            }
            return value;
        } catch (Exception e) {
            context.showFxErrorDialog(textControl.getScene().getWindow(), errorTitleKey, errorMessageKey);
            textControl.selectAll();
            textControl.requestFocus();
            return null;
        }
    }
}
