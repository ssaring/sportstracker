package de.saring.util;

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
     * Checks the specified String value whether this is an double value in the specified range.
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
}
