package de.saring.leafletmapdemo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.saring.leafletmap.LatLong

/**
 * Data class for storing a GPS track which contains a list of sample positions and a list of lap split positions.
 *
 * @author Stefan Saring
 */
data class Track(val positions: List<LatLong>, val lapsPositions: List<LatLong>) {

    companion object {

        fun readFromJson(resourceName: String): Track {
            val mapper = jacksonObjectMapper()

            try {
                return mapper.readValue(Track::class.java.getResourceAsStream(resourceName), Track::class.java)
            }
            catch (e: Exception) {
                throw IllegalStateException("Failed to read track data from JSON resource '$resourceName'!", e)
            }
        }
    }
}