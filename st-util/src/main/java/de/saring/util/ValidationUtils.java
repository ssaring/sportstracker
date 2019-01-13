package de.saring.util;

import de.saring.util.unitcalc.SpeedToStringConverter;
import de.saring.util.unitcalc.FormatUtils;

import java.text.NumberFormat;

/**
 * Helper class which contains several utility methods for validation purposes.
 *
 * @author Stefan Saring
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Checks the specified String value whether this is an integer value in the specified range.
     *
     * @param value value to check
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return true when it's an integer and in the specified range
     */
    public static boolean isValueIntegerBetween(final String value, final int minValue, final int maxValue) {
        try {
            final int intValue = NumberFormat.getIntegerInstance().parse(value).intValue();
            return intValue >= minValue && intValue <= maxValue;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks the specified optional String value whether this is an integer value in the specified range.
     * An empty string value is also valid.
     *
     * @param value value to check
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return true when it's an integer and in the specified range or when the string value is empty
     */
    public static boolean isOptionalValueIntegerBetween(final String value, final int minValue, final int maxValue) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return isValueIntegerBetween(value, minValue, maxValue);
    }

    /**
     * Checks the specified String value whether this is a double value in the specified range.
     *
     * @param value value to check
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return true when it's an double and in the specified range
     */
    public static boolean isValueDoubleBetween(final String value, final double minValue, final double maxValue) {
        try {
            final double doubleValue = NumberFormat.getInstance().parse(value).doubleValue();
            return doubleValue >= minValue && doubleValue <= maxValue;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks the specified String value whether this is a valid time in seconds value in the specified range.
     * This method does not support negative second values!
     *
     * @param value value to check
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return true when it's an valid time in seconds value and in the specified range
     */
    public static boolean isValueTimeInSecondsBetween(final String value, final int minValue, final int maxValue) {
        final int seconds = FormatUtils.timeString2TotalSeconds(value);
        return seconds >= minValue && seconds <= maxValue;
    }

    /**
     * Checks the specified String value whether this is a valid speed > 0, if it is required. When not required, the
     * speed value needs to be 0 (or 00:00 for speed mode pace).
     *
     * @param value value to check
     * @param speedConverter speed converter to be used, it also handles the speed mode
     * @param required whether a valid speed value > 0 is required or it has to be 0
     * @return true when it's an valid speed value
     */
    public static boolean isValueSpeed(final String value, final SpeedToStringConverter speedConverter, final boolean required) {

        final Float fSpeedValue = speedConverter.stringSpeedToFloat(value);
        if (required) {
            return fSpeedValue != null && fSpeedValue > 0f;
        }
        return fSpeedValue != null && fSpeedValue == 0f;
    }
}
