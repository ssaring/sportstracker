package de.saring.exerciseviewer.data

/**
 * This class stores the cadence summary of a recorded exercise.
 *
 * @property cadenceAvg Average cadence of exercise (rpm).
 * @property cadenceMax Maximum cadence of exercise (rpm).
 *
 * @author Stefan Saring
 */
data class ExerciseCadence(

    var cadenceAvg: Short,
    var cadenceMax: Short)
