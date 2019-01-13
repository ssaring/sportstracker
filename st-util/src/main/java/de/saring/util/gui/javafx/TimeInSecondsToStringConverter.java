package de.saring.util.gui.javafx;

import de.saring.util.unitcalc.FormatUtils;
import javafx.util.StringConverter;

/**
 * Custom JavaFX StringConverter class for converting a time duration in seconds from the integer value
 * to a string formatted in the pattern hh:mm:ss in both directions.
 *
 * @author Stefan Saring
 */
public class TimeInSecondsToStringConverter extends StringConverter<Number> {

    @Override
    public String toString(final Number nValue) {
        if (nValue == null) {
            return "";
        }
        return FormatUtils.seconds2TimeString(nValue.intValue());
    }

    @Override
    public Number fromString(final String strValue) {
        if (strValue == null) {
            return -1;
        }
        return FormatUtils.timeString2TotalSeconds(strValue.trim());
    }
}
