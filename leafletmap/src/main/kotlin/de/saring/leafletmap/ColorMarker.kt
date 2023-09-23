package de.saring.leafletmap

/**
 * Enumeration for all marker colors of the leaflet-color-markers JavaScript library.
 *
 * @property iconName name of the marker icon
 *
 * @author Stefan Saring
 */
enum class ColorMarker(override val iconName: String) : Marker {

    BLUE_MARKER("blueIcon"),
    GOLD_MARKER("goldIcon"),
    RED_MARKER("redIcon"),
    GREEN_MARKER("greenIcon"),
    ORANGE_MARKER("orangeIcon"),
    YELLOW_MARKER("yellowIcon"),
    VIOLET_MARKER("violetIcon"),
    GREY_MARKER("greyIcon"),
    BLACK_MARKER("blackIcon")
}
