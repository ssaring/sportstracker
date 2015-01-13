package de.saring.util.gui.javafx;

import java.util.prefs.Preferences;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Helper class for persisting and restoring the position and size of JavaFX windows.
 * The data is stored by using the {@link java.util.prefs.Preferences} API of Java.
 *
 * @author Stefan Saring
 */
public final class WindowBoundsPersistence {

    private static final String PATH_PREFIX = "/de/saring/util/gui/javafx/WindowBoundsPersistence/";

    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";

    private WindowBoundsPersistence() {
    }

    /**
     * Enables persistence of the bounds (position and size) for the specified window. It
     * restores the previous stored bounds with the same identifier, if available. Otherwise
     * the current window bounds are not modified. Before the window closes it stores the
     * current bounds.<br/>
     * This method does not persist the screen and the fullscreen state of the window. It
     * makes sure, that the upper left corner of the window is visible on the current screen.
     * It also ensures, that the restored window size is not bigger than the available usable
     * space current screen.
     *
     * @param window window for persisting bounds
     * @param identifier unique identifier (name) of the window
     */
    public static void addWindowBoundsPersistence(final Window window, final String identifier) {
        window.addEventHandler(WindowEvent.WINDOW_SHOWING, event -> restoreWindowBounds(window, identifier));
        window.addEventHandler(WindowEvent.WINDOW_HIDING, event -> storeWindowBounds(window, identifier));
    }

    private static void restoreWindowBounds(final Window window, final String identifier) {
        final Preferences preferences = getPreferences(identifier);
        final double previousX = preferences.getDouble(KEY_X, Double.MAX_VALUE);
        final double previousY = preferences.getDouble(KEY_Y, Double.MAX_VALUE);
        final double previousWidth = preferences.getDouble(KEY_WIDTH, Double.MAX_VALUE);
        final double previousHeight = preferences.getDouble(KEY_HEIGHT, Double.MAX_VALUE);

        // make sure that previous bounds were stored completely in preferences
        boolean validPreviousBounds = previousX != Double.MAX_VALUE && previousY != Double.MAX_VALUE
                && previousWidth != Double.MAX_VALUE && previousHeight != Double.MAX_VALUE;

        if (validPreviousBounds) {
            // get visible and usable dimensions of the current primary screen (without task bar, ...)
            final Rectangle2D visualScreenBounds = Screen.getPrimary().getVisualBounds();

            // restore window position, make sure the upper left corner is visible on screen (at least 100 pixels)
            window.setX(Math.max(0, Math.min(visualScreenBounds.getWidth() - 100, previousX)));
            window.setY(Math.max(0, Math.min(visualScreenBounds.getHeight() - 100, previousY)));
            // restore window size, make sure that it is not bigger than the current screen
            window.setWidth(Math.min(visualScreenBounds.getWidth(), previousWidth));
            window.setHeight(Math.min(visualScreenBounds.getHeight(), previousHeight));
        }
    }

    private static void storeWindowBounds(final Window window, final String identifier) {
        final Preferences preferences = getPreferences(identifier);
        preferences.putDouble(KEY_X, window.getX());
        preferences.putDouble(KEY_Y, window.getY());
        preferences.putDouble(KEY_WIDTH, window.getWidth());
        preferences.putDouble(KEY_HEIGHT, window.getHeight());
    }

    private static Preferences getPreferences(final String identifier) {
        return Preferences.userRoot().node(PATH_PREFIX + identifier);
    }
}
