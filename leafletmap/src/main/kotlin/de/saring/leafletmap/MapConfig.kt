package de.saring.leafletmap

/**
 * Class for defining the layers and controls in the map to be shown.

 * @author Stefan Saring
 */
class MapConfig @JvmOverloads constructor(

        /**
         * List of layers to be shown in the map, the default layer is OpenStreetMap. If more than one layer is
         * specified, then a layer selection control will be shown in the top right corner.
         */
        val layers: List<MapLayer> = listOf(MapLayer.OPENSTREETMAP),

        /** Zoom control definition, by default it's shown in the top left corner. */
        val zoomControlConfig: ZoomControlConfig = ZoomControlConfig(),

        /** Scale control definition, by default it's not shown. */
        val scaleControlConfig: ScaleControlConfig = ScaleControlConfig(),

        /** Initial center position of the map (default is London city). */
        val initialCenter: LatLong = LatLong(51.505, -0.09)
)