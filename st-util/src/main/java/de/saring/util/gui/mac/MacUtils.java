package de.saring.util.gui.mac;

import java.util.Locale;

/**
 * Utility class for Mac OS X specific UI helpers.
 *
 * @author Stefan Saring
 */
public final class MacUtils {

    private MacUtils() {
    }

    /**
     * Check if running operating system is MacOS X.
     *
     * @return true when running on Mac OS X
     */
    public static boolean isMacOSX() {
        String os = System.getProperty("os.name").toUpperCase(Locale.getDefault());
        return os.startsWith("MAC OS X");
    }
}
