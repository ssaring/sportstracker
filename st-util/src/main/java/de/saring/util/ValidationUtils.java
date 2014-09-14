package de.saring.util;

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
            final int intValue = Integer.parseInt(value);
            return intValue >= minValue && intValue <= maxValue;
        } catch (Exception e) {
            return false;
        }
    }
}
