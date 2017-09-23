package de.saring.exerciseviewer.data

/**
 * This class contains all altitude data of a lap of an exercise.
 *
 * @property altitude Altitude at lap end.
 * @property ascent Ascent (climbed height meters) of lap.
 *
 * @author Stefan Saring
 */
data class LapAltitude(

        var altitude: Short,
        var ascent: Int)
