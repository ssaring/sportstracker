package de.saring.leafletmap

/**
 * Class for defining the layers and controls in the map to be shown.
 *
 * @property layers List of layers to be shown in the map, the default layer is OpenStreetMap. If more than one layer is
 * specified, then a layer selection control will be shown in the top right corner.
 * @property zoomControlConfig Zoom control definition, by default it's shown in the top left corner.
 * @property scaleControlConfig Scale control definition, by default it's not shown.
 * @property initialCenter Initial center position of the map (default is London city).
 *
 * @author Stefan Saring
 */
class MapConfig @JvmOverloads constructor(

        val layers: List<MapLayer> = listOf(MapLayer.OPENSTREETMAP),
        val zoomControlConfig: ZoomControlConfig = ZoomControlConfig(),
        val scaleControlConfig: ScaleControlConfig = ScaleControlConfig(),
        val initialCenter: LatLong = LatLong(51.505, -0.09)
)