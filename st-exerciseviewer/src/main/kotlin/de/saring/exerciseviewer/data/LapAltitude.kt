package de.saring.exerciseviewer.data

/**
 * This class contains all altitude data of a lap of an exercise.
 *
 * @author Stefan Saring
 */
data class LapAltitude(

        /** Altitude at lap end. */
        var altitude: Short,
        /** Ascent (climbed height meters) of lap. */
        var ascent: Int)
