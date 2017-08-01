package de.saring.exerciseviewer.data

/**
 * This class contains all speed data of a lap of an exercise.
 *
 * @author Stefan Saring
 */
data class LapSpeed(

        /** Speed at end of the lap (km/h). */
        var speedEnd: Float,
        /** Average speed of the lap (km/h). */
        var speedAVG: Float,
        /** Distance of the lap (meters) from the beginning of the exercise, not from the beginning of the lap! */
        var distance: Int,
        /** Cadence at the end of the lap (rpm). */
        var cadence: Short? = null)
