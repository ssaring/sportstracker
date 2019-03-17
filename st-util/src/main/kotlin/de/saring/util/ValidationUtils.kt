package de.saring.util

import de.saring.util.unitcalc.SpeedToStringConverter
import de.saring.util.unitcalc.TimeUtils
import java.text.NumberFormat

/**
 * Helper class which contains several utility methods for validation purposes.
 *
 * @author Stefan Saring
 */
object ValidationUtils {

    /**
     * Checks the specified String value whether this is an integer value in the specified range.
     *
     * @param value value to check
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return true when it's an integer and in the specified range
     */
    @JvmStatic
    fun isValueIntegerBetween(value: String?, minValue: Int, maxValue: Int): Boolean {

        return try {
            val intValue = NumberFormat.getIntegerInstance().parse(value).toInt()
            intValue in minValue .. maxValue
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks the specified optional String value whether this is an integer value in the specified range.
     * An empty string value is also valid.
     *
     * @param value value to check
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return true when it's an integer and in the specified range or when the string value is empty
     */
    @JvmStatic
    fun isOptionalValueIntegerBetween(value: String?, minValue: Int, maxValue: Int): Boolean {

        if (value == null || value.trim().isEmpty()) {
            return true
        }
        return isValueIntegerBetween(value, minValue, maxValue)
    }

    /**
     * Checks the specified String value whether this is a double value in the specified range.
     *
     * @param value value to check
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return true when it's an double and in the specified range
     */
    @JvmStatic
    fun isValueDoubleBetween(value: String?, minValue: Double, maxValue: Double): Boolean {

        return try {
            val doubleValue = NumberFormat.getInstance().parse(value).toDouble()
            doubleValue in minValue .. maxValue
        } catch (e: Exception) {
            false
        }

    }

    /**
     * Checks the specified String value whether this is a valid time in seconds value in the specified range.
     * This method does not support negative second values!
     *
     * @param value value to check
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return true when it's an valid time in seconds value and in the specified range
     */
    @JvmStatic
    fun isValueTimeInSecondsBetween(value: String?, minValue: Int, maxValue: Int): Boolean {
        val seconds = TimeUtils.timeString2TotalSeconds(value)
        return seconds in minValue .. maxValue
    }

    /**
     * Checks the specified String value whether this is a valid speed > 0, if it is required. When not required, the
     * speed value needs to be 0 (or 00:00 for speed mode pace).
     *
     * @param value value to check
     * @param speedConverter speed converter to be used, it also handles the speed mode
     * @param required whether a valid speed value > 0 is required or it has to be 0
     * @return true when it's an valid speed value
     */
    @JvmStatic
    fun isValueSpeed(value: String?, speedConverter: SpeedToStringConverter, required: Boolean): Boolean {

        val fSpeedValue = speedConverter.stringSpeedToFloat(value)
        if (required) {
            return fSpeedValue != null && fSpeedValue > 0f
        }
        return fSpeedValue != null && fSpeedValue == 0f
    }
}
