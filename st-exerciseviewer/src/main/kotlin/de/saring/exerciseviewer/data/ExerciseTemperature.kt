package de.saring.exerciseviewer.data

/**
 * This class stores the temperature summary of a recorded exercise.
 *
 * @property temperatureMin Minimum temperature of an exercise (in celsius degrees).
 * @property temperatureAvg Average temperature of an exercise (in celsius degrees).
 * @property temperatureMax Maximum temperature of an exercise (in celsius degrees).
 *
 * @author Stefan Saring
 */
data class ExerciseTemperature(

    var temperatureMin: Short,
    var temperatureAvg: Short,
    var temperatureMax: Short)
