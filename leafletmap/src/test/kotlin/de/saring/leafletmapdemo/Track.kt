package de.saring.leafletmapdemo

import de.saring.leafletmap.LatLong
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Data class for storing a GPS track which contains a list of sample positions and a list of lap split positions.
 *
 * @author Stefan Saring
 */
data class Track(val positions: List<LatLong>, val lapsPositions: List<LatLong>) {

    companion object {

        fun readFromJson(resourceName: String): Track {

            try {
                val jsonString = Track::class.java.getResource(resourceName).readText()
                val trackSerializable = Json.decodeFromString<TrackSerializable>(jsonString)
                return trackSerializable.toTrack()
            }
            catch (e: Exception) {
                throw IllegalStateException("Failed to read track data from JSON resource '$resourceName'!", e)
            }
        }
    }
}

/**
 * Serializable data class for storing a GPS track which contains sample positions and lap split positions.  It's a
 * duplicate of Track to prevent the serialization dependency in the LatLong class.
 *
 */
@Serializable
private data class TrackSerializable(val positions: List<LatLongSerializable>, val lapsPositions: List<LatLongSerializable>) {

    fun toTrack() = Track(
            positions.map { LatLong(it.latitude, it.longitude) },
            lapsPositions.map { LatLong(it.latitude, it.longitude) })
}

/**
 * Serializable value class for defining a geo position. It's a duplicate of LatLong to prevent the serialization
 * dependency in the LatLong class (only needed for this test application).
 */
@Serializable
private data class LatLongSerializable(val latitude: Double, val longitude: Double)
