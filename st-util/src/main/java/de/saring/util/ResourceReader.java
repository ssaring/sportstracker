package de.saring.util;

import org.jdesktop.application.ResourceMap;

import java.awt.*;

/**
 * Helper class for for reading resources from the applications properties files
 * for an specified locale. It uses the JSR 296 ResourceMap class.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class ResourceReader {

    /**
     * Constants for resource keys used by multiple classes.
     */
    public static final String COMMON_TABLE_BACKGROUND_ODD = "common.table.background_odd";
    public static final String COMMON_TABLE_BACKGROUND_EVEN = "common.table.background_even";

    /**
     * This ResourceMap contains all the application resources.
     */
    private final ResourceMap resourceMap;

    /**
     * Standard c'tor. It sets the ResourceMap instance which can be used by the
     * entire application.
     *
     * @param resourceMap the resource map with translations
     */
    public ResourceReader(ResourceMap resourceMap) {
        this.resourceMap = resourceMap;
    }

    /**
     * Returns the value of the string resource for the specified key. The
     * variable length arguments can contain objects which will be inserted in
     * the resource string at the appropriate positions.
     *
     * @param key the key (name) of the string resource
     * @param arguments list of objects which needs to be inserted in the
     * resource string (optional)
     * @return the resource string
     */
    public String getString(String key, Object... arguments) {
        if (resourceMap == null) {
            return "???";
        }
        return resourceMap.getString(key, arguments);
    }

    /**
     * Returns the color for the specified key from the resource map.
     *
     * @param key the key (name) of the color resource
     * @return the resource color
     */
    public Color getColor(String key) {
        if (resourceMap == null) {
            return null;
        }
        return resourceMap.getColor(key);
    }
}
