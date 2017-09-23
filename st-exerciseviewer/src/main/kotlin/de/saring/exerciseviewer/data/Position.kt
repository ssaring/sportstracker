package de.saring.exerciseviewer.data

/**
 * The Position class defines the geographical location of one specific point of the exercise track
 * (also known as track point).
 *
 * @property latitude Latitude of this trackpoint in degrees.
 * @property longitude Longitude of this trackpoint in degrees.
 *
 * @author Stefan Saring
 */
data class Position(

        val latitude: Double,
        val longitude: Double)