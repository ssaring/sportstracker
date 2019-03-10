package de.saring.util.unitcalc

import java.text.NumberFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * Class for converting a speed (mostly the average speed) from the float value to a string value (both directions).
 * Depending on the specified SpeedMode the string value can be be either in km/h (or mph) or time/km or (time/mile),
 * the time is then in format mm:hh.
 * Unit system conversion (km or miles) will not be done here!
 *
 * @property speedMode speed mode to be used for the conversion
 *
 * @author Stefan Saring
 */
class SpeedToStringConverter(
        var speedMode: SpeedMode) {

    /**
     * Returns the specified speed value as a formatted string by using the defined speed mode.
     *
     * @param floatSpeed speed value as a float
     * @return the speed as string or null when conversion failed
     */
    fun floatSpeedtoString(floatSpeed: Float?): String? {
        if (floatSpeed == null) {
            return null
        }

        return if (speedMode === SpeedMode.SPEED) {
            NUMBER_FORMAT.format(floatSpeed)
        } else { // PACE
            if (floatSpeed == 0f) {
                ZERO_SPEED_TIME
            } else {
                FormatUtils.seconds2MinuteTimeString((3600 / floatSpeed).toInt())
            }
        }
    }

    /**
     * Returns the speed value as a float for the specified speed string by using the defined speed mode.
     *
     * @param strSpeed formatted speed string
     * @return the speed value or null when conversion failed
     */
    fun stringSpeedToFloat(strSpeed: String?): Float? {
        if (strSpeed == null) {
            return null
        }

        return try {
            val strSpeedTrimmed = strSpeed.trim()
            if (speedMode === SpeedMode.SPEED) {
                NumberFormat.getInstance().parse(strSpeed).toFloat()
            } else { // PACE
                if (ZERO_SPEED_TIME == strSpeedTrimmed) {
                    0f
                } else {
                    val time = LocalTime.parse("00:$strSpeedTrimmed", DateTimeFormatter.ISO_LOCAL_TIME)
                    3600 / (time.minute * 60 + time.second).toFloat()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val ZERO_SPEED_TIME = "00:00"

        private val NUMBER_FORMAT = NumberFormat.getInstance().apply {
            maximumFractionDigits = 3
        }
    }
}
