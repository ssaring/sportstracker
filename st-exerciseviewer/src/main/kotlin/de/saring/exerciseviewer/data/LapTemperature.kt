package de.saring.exerciseviewer.data

/**
 * This class contains all temperature data of a lap of an exercise. It's a separate class because it's recorded
 * optionally.
 *
 * @author Stefan Saring
 */
data class LapTemperature(

        /** Temperature at lap (in celcius degrees). */
        var temperature: Short)
