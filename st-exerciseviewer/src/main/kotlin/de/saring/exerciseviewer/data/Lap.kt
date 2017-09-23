package de.saring.exerciseviewer.data

/**
 * This class contains all data of a lap in an exercise.
 *
 * @property timeSplit Lap split time (in 1/10 seconds).
 * @property heartRateSplit Heartrate at lap split time (if recorded).
 * @property heartRateAVG Average heartrate at lap (if recorded).
 * @property heartRateMax Maximum heartrate at lap (if recorded).
 * @property speed Lap speed data (if recorded).
 * @property altitude Lap altitude data (if recorded).
 * @property temperature Lap temperature (if recorded).
 * @property positionSplit The geographical location at lap split time (if recorded).
 *
 * @author Stefan Saring
 */
data class Lap(

        var timeSplit: Int = 0,
        var heartRateSplit: Short? = null,
        var heartRateAVG: Short? = null,
        var heartRateMax: Short? = null,
        var speed: LapSpeed? = null,
        var altitude: LapAltitude? = null,
        var temperature: LapTemperature? = null,
        var positionSplit: Position? = null)
