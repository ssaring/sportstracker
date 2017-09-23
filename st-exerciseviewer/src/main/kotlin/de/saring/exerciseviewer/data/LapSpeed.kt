package de.saring.exerciseviewer.data

/**
 * This class contains all speed data of a lap of an exercise.
 *
 * @property speedEnd Speed at end of the lap (km/h).
 * @property speedAVG Average speed of the lap (km/h).
 * @property distance Distance of the lap (meters) from the beginning of the exercise, not from the beginning of the lap!
 * @property cadence Cadence at the end of the lap (rpm).
 *
 * @author Stefan Saring
 */
data class LapSpeed(

        var speedEnd: Float,
        var speedAVG: Float,
        var distance: Int,
        var cadence: Short? = null)
