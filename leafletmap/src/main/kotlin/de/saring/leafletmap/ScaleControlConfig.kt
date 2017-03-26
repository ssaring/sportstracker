package de.saring.leafletmap

/**
 * Class for defining the scale control of the map. The scale can show either metric or imperial units.

 * @author Stefan Saring
 */
class ScaleControlConfig @JvmOverloads constructor(
        val show: Boolean = false,
        val position: ControlPosition = ControlPosition.BOTTOM_LEFT,
        val metric: Boolean = true)