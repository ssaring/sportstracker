package de.saring.exerciseviewer.parser.testutil;

public final class Utils {

    public static short toShort(int value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException("value is not in range of a short: " + value);
        }
        return (short) value;

    }
}
