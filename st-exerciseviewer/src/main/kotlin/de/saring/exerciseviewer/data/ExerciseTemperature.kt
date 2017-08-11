package de.saring.exerciseviewer.data

/**
 * This class stores the temperature summary of a recorded exercise.
 *
 * @author Stefan Saring
 */
data class ExerciseTemperature(

    /** Minimum temperature of an exercise (in celsius degrees). */
    var temperatureMin: Short,
    /** Average temperature of an exercise (in celsius degrees). */
    var temperatureAvg: Short,
    /** Maximum temperature of an exercise (in celsius degrees). */
    var temperatureMax: Short)
