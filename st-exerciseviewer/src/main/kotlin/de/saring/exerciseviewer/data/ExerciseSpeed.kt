package de.saring.exerciseviewer.data

/**
 * This class stores the speed summary of a recorded exercise.
 *
 * @property speedAvg Average speed of exercise (in km/h).
 * @property speedMax Maximum speed of exercise (in km/h).
 * @property distance Distance of exercise (in meters).
 *
 * @author Stefan Saring
 */
data class ExerciseSpeed(

    var speedAvg: Float,
    var speedMax: Float,
    var distance: Int)
