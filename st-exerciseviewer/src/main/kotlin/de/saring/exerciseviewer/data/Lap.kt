package de.saring.exerciseviewer.data

/**
 * This class contains all data of a lap in an exercise.
 *
 * @author Stefan Saring
 */
data class Lap(

        /** Lap split time (in 1/10 seconds). */
        var timeSplit: Int = 0,
        /** Heartrate at lap split time (if recorded). */
        var heartRateSplit: Short? = null,
        /** Average heartrate at lap (if recorded). */
        var heartRateAVG: Short? = null,
        /** Maximum heartrate at lap (if recorded). */
        var heartRateMax: Short? = null,
        /** Lap speed data (if recorded). */
        var speed: LapSpeed? = null,
        /** Lap altitude data (if recorded). */
        var altitude: LapAltitude? = null,
        /** Lap temperature (if recorded). */
        var temperature: LapTemperature? = null,
        /** The geographical location at lap split time (if recorded). */
        var positionSplit: Position? = null)
