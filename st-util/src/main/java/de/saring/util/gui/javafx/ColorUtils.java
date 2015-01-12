package de.saring.util.gui.javafx;

import javafx.scene.paint.Color;

/**
 * This utility class contains conversion and helper methods for JavaFX Color objects.
 *
 * @author Stefan Saring
 */
public final class ColorUtils {

    private ColorUtils() {
    }

    /**
     * Converts the specified JavaFX Color object to a AWT/Swing Color object.
     *
     * @param fxColor JavaFX Color object
     * @return the AWT Color object
     */
    public static java.awt.Color toAwtColor(final Color fxColor) {
        return new java.awt.Color((float) fxColor.getRed(), (float) fxColor.getGreen(), (float) fxColor.getBlue());
    }

    /**
     * Converts the specified AWT/Swing Color object to a JavaFX Color object.
     *
     * @param awtColor AWT/Swing Color object
     * @return the JavaFX Color object
     */
    public static Color toFxColor(final java.awt.Color awtColor) {
        return Color.rgb(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), 1);
    }

    /**
     * Returns the hexadecimal RGB notation of the specified JavaFX color. Example for red: #ff0000.
     *
     * @param color color
     * @return hexadecimal RGB notation
     */
    public static String toRGBCode(final Color color) {
        return String.format("#%02X%02X%02X", //
                (int) (color.getRed() * 255), //
                (int) (color.getGreen() * 255), //
                (int) (color.getBlue() * 255));
    }
}
