package de.saring.util.gui.javafx;

import javafx.scene.paint.Color;

/**
 * This utility class contains methods for converting Color objects between JavaFX and Swing/AWT.
 *
 * @author Stefan Saring
 */
public final class ColorConverter {

    private ColorConverter() {
    }

    /**
     * Converts the specified JavaFX Color object to a AWT/Swing Color object.
     *
     * @param fxColor JavaFX Color object
     * @return the AWT Color object
     */
    public static java.awt.Color toAwtColor(final javafx.scene.paint.Color fxColor) {
        return new java.awt.Color((float) fxColor.getRed(), (float) fxColor.getGreen(), (float) fxColor.getBlue());
    }

    /**
     * Converts the specified AWT/Swing Color object to a JavaFX Color object.
     *
     * @param awtColor AWT/Swing Color object
     * @return the JavaFX Color object
     */
    public static javafx.scene.paint.Color toFxColor(final java.awt.Color awtColor) {
        return Color.rgb(awtColor.getRed(), awtColor.getGreen(),awtColor.getBlue(), 1);
    }
}
