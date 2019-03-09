package de.saring.util

import java.util.ResourceBundle
import java.util.logging.Logger

/**
 * Provides text resources of an application from a properties file.
 *
 * @param resourceBundleName name of the ResourceBundle to load
 *
 * @author Stefan Saring
 */
class AppResources(resourceBundleName: String) {

    /**
     * The loaded ResourceBundle.
     */
    val resourceBundle = ResourceBundle.getBundle(resourceBundleName)

    /**
     * Returns the String resource for the specified key.
     *
     * @param key resource key
     * @param arguments list of objects which needs to be inserted in the message text (optional)
     * @return String resource value
     */
    fun getString(key: String, vararg arguments: Any): String {

        try {
            // replace placeholders in message with arguments if specified
            var stringValue = resourceBundle.getString(key)
            return if (arguments.isEmpty()) stringValue else String.format(stringValue, *arguments)
        } catch (e: Exception) {
            LOGGER.severe("Failed to get string resource for key '$key'!")
            return "???"
        }
    }

    companion object {
        private val LOGGER = Logger.getLogger(AppResources::class.java.name)
    }
}
