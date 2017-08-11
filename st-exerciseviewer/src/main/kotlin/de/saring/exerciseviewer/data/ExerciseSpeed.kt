package de.saring.exerciseviewer.data

/**
 * This class stores the speed summary of a recorded exercise.
 *
 * @author Stefan Saring
 */
data class ExerciseSpeed(

    /** Average speed of exercise (in km/h). */
    var speedAvg: Float,
    /** Maximum speed of exercise (in km/h). */
    var speedMax: Float,
    /** Distance of exercise (in meters). */
    var distance: Int)
