package de.saring.exerciseviewer.data

/**
 * This class stores the altitude summary of a recorded exercise.
 *
 * @author Stefan Saring
 */
data class ExerciseAltitude(

    /** Minimum altitude of exercise. */
    var altitudeMin: Short,
    /** Average altitude of exercise. */
    var altitudeAvg: Short,
    /** Maximum altitude of exercise. */
    var altitudeMax: Short,
    /** Ascent of exercise (climbed height meters). */
    var ascent: Int)
