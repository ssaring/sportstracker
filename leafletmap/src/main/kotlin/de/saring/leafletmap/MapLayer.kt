package de.saring.leafletmap

/**
 * Enumeration for all supported map layers.
 *
 * @author Stefan Saring
 */
enum class MapLayer(val displayName: String, val javaScriptCode: String) {

    /** OpenStreetMap layer. */
    OPENSTREETMAP("OpenStreetMap", """
        L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: 'Map data &copy; OpenStreetMap and contributors',
        })"""),

    /** OpenCycleMap layer. */
    OPENCYCLEMAP("OpenCycleMap", """
        L.tileLayer('http://{s}.tile.opencyclemap.org/cycle/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenCycleMap, Map data &copy; OpenStreetMap contributors',
        })"""),

    /** Hike & bike maps layer (HikeBikeMap.org). */
    HIKE_BIKE_MAP("Hike & Bike Map", """
        L.tileLayer('http://{s}.tiles.wmflabs.org/hikebike/{z}/{x}/{y}.png', {
            attribution: '&copy; HikeBikeMap.org, Map data &copy; OpenStreetMap and contributors',
        })"""),

    /** MTB map (mtbmap.cz). */
    MTB_MAP("MTB Map", """
        L.tileLayer('http://tile.mtbmap.cz/mtbmap_tiles/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap and USGS',
        })"""),

    /** MapBox layer in streets mode (consider: a project specific access token is required!). */
    MAPBOX("MapBox", """
        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
            id: 'mapbox.streets',
            attribution: 'Map data &copy; OpenStreetMap contributors, Imagery &copy; Mapbox'
        })""")
}
