package de.saring.util

import java.util.Locale

/**
 * Utility class for operating system specific UI helpers.
 *
 * @author Stefan Saring
 */
object PlatformUtils {

    /**
     * Checks whether the operating system is Mac OS X.
     *
     * @return true when running on Mac OS X
     */
    @JvmStatic
    fun isMacOS(): Boolean = getOperatingSystemName().startsWith("MAC OS X")

    /**
     * Checks whether the operating system is a Linux variant.
     *
     * @return true when running on Linux
     */
    @JvmStatic
    fun isLinux(): Boolean = getOperatingSystemName().startsWith("LINUX")

    private fun getOperatingSystemName(): String =
            System.getProperty("os.name").toUpperCase(Locale.getDefault())
}
