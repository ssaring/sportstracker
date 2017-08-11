package de.saring.exerciseviewer.data

/**
 * This class stores the cadence summary of a recorded exercise.
 *
 * @author Stefan Saring
 */
data class ExerciseCadence(

    /** Average cadence of exercise (rpm). */
    var cadenceAvg: Short,
    /** Maximum cadence of exercise (rpm). */
    var cadenceMax: Short)
