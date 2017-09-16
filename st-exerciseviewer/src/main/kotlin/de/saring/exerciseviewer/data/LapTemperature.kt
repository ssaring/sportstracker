package de.saring.exerciseviewer.data

/**
 * This class contains all temperature data of a lap of an exercise. It's a separate class because it's recorded
 * optionally.
 *
 * @property temperature Temperature at lap (in celsius degrees).
 *
 * @author Stefan Saring
 */
data class LapTemperature(

        var temperature: Short)
