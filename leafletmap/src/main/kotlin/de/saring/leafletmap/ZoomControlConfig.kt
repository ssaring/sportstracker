package de.saring.leafletmap

/**
 * Class for defining the zoom control of the map.

 * @author Stefan Saring
 */
class ZoomControlConfig @JvmOverloads constructor(
        val show: Boolean = true,
        val position: ControlPosition = ControlPosition.TOP_LEFT)