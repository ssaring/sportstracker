package de.saring.util;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Provides text resources of an application from a properties file.
 *
 * @author Stefan Saring
 */
public class AppResources {

    private static final Logger LOGGER = Logger.getLogger(AppResources.class.getName());

    private ResourceBundle resourceBundle;

    /**
     * Creates the AppResources by loading the specified ResourceBundle.
     *
     * @param resourceBundleName name of the ResourceBundle
     */
    public AppResources(final String resourceBundleName) {
        this.resourceBundle = ResourceBundle.getBundle(resourceBundleName);
    }

    /**
     * Returns the loaded ResourceBundle.
     *
     * @return ResourceBundle
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Returns the String resource for the specified key.
     *
     * @param key resource key
     * @return String resource value
     */
    public String getString(final String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            LOGGER.severe("Failed to get string resource for key '" + key + "'!");
            return "???";
        }
    }
}
