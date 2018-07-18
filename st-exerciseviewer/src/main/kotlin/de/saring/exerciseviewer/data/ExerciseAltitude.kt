package de.saring.exerciseviewer.data

/**
 * This class stores the altitude summary of a recorded exercise.
 *
 * @property altitudeMin Minimum altitude of exercise.
 * @property altitudeAvg Average altitude of exercise.
 * @property altitudeMax Maximum altitude of exercise.
 * @property ascent Ascent of exercise (climbed height meters).
 * @property descent Descent data of exercise (height meters).
 *
 * @author Stefan Saring
 */
data class ExerciseAltitude(

    var altitudeMin: Short,
    var altitudeAvg: Short,
    var altitudeMax: Short,
    var ascent: Int,
    var descent: Int)
