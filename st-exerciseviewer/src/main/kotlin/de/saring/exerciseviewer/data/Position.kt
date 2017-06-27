package de.saring.exerciseviewer.data

/**
 * The Position class defines the geographical location of one specific point of the exercise track
 * (also known as track point).
 *
 * @author Stefan Saring
 */
data class Position(

        /** Latitude of this trackpoint in degrees. */
        val latitude: Double,
        /** Longitude of this trackpoint in degrees. */
        val longitude: Double)