package de.saring.util.gui.mac;

import java.util.Locale;

/**
 * Utility class for operating system specific UI helpers.
 *
 * @author Stefan Saring
 */
public final class PlatformUtils {

    private PlatformUtils() {
    }

    /**
     * Checks whether the operating system is Mac OS X.
     *
     * @return true when running on Mac OS X
     */
    public static boolean isMacOSX() {
        return getOperatingSystemName().startsWith("MAC OS X");
    }

    /**
     * Checks whether the operating system is a Linux variant.
     *
     * @return true when running on Linux
     */
    public static boolean isLinux() {
        return getOperatingSystemName().startsWith("LINUX");
    }

    /**
     * @return the operation system name in uppercase letters
     */
    private static String getOperatingSystemName() {
        return System.getProperty("os.name").toUpperCase(Locale.getDefault());
    }
}
