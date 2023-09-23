package de.saring.leafletmap

/**
 * Enumeration for all supported map layers.
 *
 * @author Stefan Saring
 */
enum class MapLayer(val displayName: String, val javaScriptCode: String) {

    /** OpenStreetMap layer. */
    OPENSTREETMAP("OpenStreetMap", """
        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors',
        })"""),

    /** OpenStreetMap layer. */
    OPEN_TOPO_MAP("OpenTopoMap", """
        L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
        	maxZoom: 17,
            attribution: 'Map data: &copy; OpenStreetMap contributors, SRTM | Map style: &copy; OpenTopoMap (CC-BY-SA)',
        })"""),

    /** OpenCycleMap layer. */
    OPENCYCLEMAP("OpenCycleMap", """
        L.tileLayer('http://{s}.tile.opencyclemap.org/cycle/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenCycleMap, Map data &copy; OpenStreetMap contributors',
        })"""),

    /** MTB map (mtbmap.cz). */
    MTB_MAP("MTB Map", """
        L.tileLayer('https://tile.mtbmap.cz/mtbmap_tiles/{z}/{x}/{y}.png', {
	        attribution: '&copy; OpenStreetMap contributors &amp; USGS',
        })"""),

        /** Sattelite view (esri). */
    SATELITTE("Satellite Esri", """
        L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
            attribution: '&copy; Esri, DigitalGlobe, GeoEye, i-cubed, USDA FSA, USGS, AEX, Getmapping, Aerogrid, IGN, IGP, swisstopo and the GIS User Community'
        })""")
}
