package de.saring.util.unitcalc

import java.text.NumberFormat

/**
 * This class contains methods for converting data in different formats and units to formatted text strings.
 *
 * @property unitSystem the unit system to be used
 *
 * @author Stefan Saring
 */
class FormatUtils(val unitSystem: UnitSystem) {

    /**
     * The number format instance.
     */
    private val numberFormat = NumberFormat.getInstance()

    /**
     * Returns the name of the current distance unit.
     *
     * @return the current distance unit name
     */
    fun getDistanceUnitName(): String =
            if (this.unitSystem == UnitSystem.ENGLISH) "m" else "km"

    /**
     * Returns the name of the current temperature unit.
     *
     * @return the current temperature unit name
     */
    fun getTemperatureUnitName(): String =
            if (this.unitSystem == UnitSystem.ENGLISH) "F" else "C"

    /**
     * Returns the name of the current altitude unit.
     *
     * @return the current temperature unit name
     */
    fun getAltitudeUnitName(): String =
            if (this.unitSystem == UnitSystem.ENGLISH) "ft" else "m"

    /**
     * Returns the name of the current weight unit.
     *
     * @return the current temperature unit name
     */
    fun getWeightUnitName(): String =
            if (this.unitSystem == UnitSystem.ENGLISH) "lbs" else "kg"

    /**
     * Returns the name of the current speed unit for the specified speed mode.
     *
     * @param speedMode speed mode
     * @return the current speed unit name
     */
    fun getSpeedUnitName(speedMode: SpeedMode): String {
        return if (this.unitSystem == UnitSystem.ENGLISH) {
            if (speedMode === SpeedMode.SPEED) "mph" else "min/m"
        } else {
            if (speedMode === SpeedMode.SPEED) "km/h" else "min/km"
        }
    }

    /**
     * Converts the heart rate to a text incl. the unit name.
     *
     * @param heartRate heart rate in beats per minute
     * @return the heart rate as text
     */
    fun heartRateToString(heartRate: Int): String {
        numberFormat.maximumFractionDigits = 0
        return "${numberFormat.format(heartRate)} bpm"
    }

    /**
     * Converts the temperature to a text in the correct unit depending on what unit options are currently chosen.
     *
     * @param temperature temperature in Celsius
     * @return the temperature as text
     */
    fun temperatureToString(temperature: Short): String {
        numberFormat.maximumFractionDigits = 0
        return if (this.unitSystem == UnitSystem.ENGLISH) {
            "${numberFormat.format(ConvertUtils.convertCelsius2Fahrenheit(temperature))} ${getTemperatureUnitName()}"
        } else {
            "${numberFormat.format(temperature)} ${getTemperatureUnitName()}"
        }
    }

    /**
     * Converts the distance to a text in the correct unit depending on what unit options are currently chosen. The unit
     * name is not included in the text.
     *
     * @param distance distance in km
     * @param decimals the number of decimals (fraction digits) to show
     * @return the distance as text
     */
    fun distanceToStringWithoutUnitName(distance: Double, decimals: Int): String {
        numberFormat.maximumFractionDigits = decimals
        return if (this.unitSystem ==UnitSystem.ENGLISH) {
            numberFormat.format(ConvertUtils.convertKilometer2Miles(distance, false))
        } else {
            numberFormat.format(distance)
        }
    }

    /**
     * Converts the distance to a text in the correct unit depending on what unit options are currently chosen.
     *
     * @param distance distance in km
     * @param decimals the number of decimals (fraction digits) to show
     * @return the distance as text incl. unit name
     */
    fun distanceToString(distance: Double, decimals: Int): String =
            "${distanceToStringWithoutUnitName(distance, decimals)} ${getDistanceUnitName()}"

    /**
     * Converts the speed to a text in the correct unit depending on what unit options and speed view are currently
     * chosen. The unit name is not included in the text.
     *
     * @param speed speed in km/h
     * @param decimals the number of decimals (fraction digits) to show
     * @param speedMode speed mode
     * @return the speed as text
     */
    fun speedToStringWithoutUnitName(speed: Float, decimals: Int, speedMode: SpeedMode): String {
        numberFormat.maximumFractionDigits = decimals

        return if (this.unitSystem == UnitSystem.ENGLISH) {
            if (speedMode == SpeedMode.PACE) {
                if (speed == 0f) {
                    "N/A"
                }
                else {
                    seconds2MinuteTimeString((3600 / ConvertUtils.convertKilometer2Miles(speed.toDouble(), false)).toInt())
                }
            } else { // SpeedMode.Speed
                numberFormat.format(ConvertUtils.convertKilometer2Miles(speed.toDouble(), false))
            }
        } else { // UnitSystem.METRIC
            if (speedMode == SpeedMode.PACE) {
                if (speed == 0f) {
                    "N/A"
                }
                else {
                    seconds2MinuteTimeString((3600 / speed).toInt())
                }
            } else { // SpeedMode.Speed
                numberFormat.format(speed.toDouble())
            }
        }
    }

    /**
     * Converts the speed to a text in the correct unit depending on what unit options and speed view are currently
     * chosen. The unit name is contained if the speed is > 0.
     *
     * @param speed speed in km/h
     * @param decimals the number of decimals (fraction digits) to show
     * @param speedMode speed mode
     * @return the speed as text
     */
    fun speedToString(speed: Float, decimals: Int, speedMode: SpeedMode): String {
        return if (speed == 0f) {
            speedToStringWithoutUnitName(speed, decimals, speedMode)
        } else {
            speedToStringWithoutUnitName(speed, decimals, speedMode) + " " + getSpeedUnitName(speedMode)
        }
    }

    /**
     * Converts the height to a text in the correct unit depending on what unit options are currently chosen. The unit
     * name is not included in the String.
     *
     * @param height height in meters
     * @return the height as text
     */
    fun heightToStringWithoutUnitName(height: Int): String {
        numberFormat.maximumFractionDigits = 0

        return if (this.unitSystem == UnitSystem.ENGLISH) {
            numberFormat.format(ConvertUtils.convertMeter2Feet(height))
        } else {
            numberFormat.format(height)
        }
    }

    /**
     * Converts the height to a text in the correct unit depending on what unit options are currently chosen.
     *
     * @param height height in meters
     * @return the height as text
     */
    fun heightToString(height: Int): String =
            "${heightToStringWithoutUnitName(height)} ${getAltitudeUnitName()}"

    /**
     * Converts the cadence to a text.
     *
     * @param cadence cadence in rounds per minute
     * @return the cadence incl. unit name as text
     */
    fun cadenceToString(cadence: Int): String {
        numberFormat.maximumFractionDigits = 0
        return "${numberFormat.format(cadence.toLong())} rpm / spm"
    }

    /**
     * Converts the amount of cycles (e.g. rotations or steps) to a text.
     *
     * @param cycles number of cycles
     * @return the total cycles incl. unit name as text
     */
    fun cyclesToString(cycles: Long): String {
        numberFormat.maximumFractionDigits = 0
        return "${numberFormat.format(cycles)} rotations / steps"
    }

    /**
     * Converts the calorie consumption value to a text.
     *
     * @param calories the calorie consumption
     * @return the calorie consumption incl. unit as text
     * name
     */
    fun caloriesToString(calories: Int): String {
        numberFormat.maximumFractionDigits = 0
        return "${numberFormat.format(calories.toLong())} kCal"
    }

    /**
     * Converts the weight to a text in the correct unit depending on what unit options are currently chosen. The unit
     * name is not included in the text.
     *
     * @param weight weight in kilograms
     * @param maxFractionDigits maximum fraction digits to be shown in the text
     * @return the weight as text
     */
    fun weightToStringWithoutUnitName(weight: Float, maxFractionDigits: Int): String {
        numberFormat.maximumFractionDigits = maxFractionDigits

        return if (this.unitSystem == UnitSystem.ENGLISH) {
            numberFormat.format(ConvertUtils.convertKilogram2Lbs(weight.toDouble()))
        } else {
            numberFormat.format(weight)
        }
    }

    /**
     * Converts the weight to a text in the correct unit depending on what unit options are currently chosen. The text
     * contains the unit name.
     *
     * @param weight weight in kilograms
     * @param maxFractionDigits maximum fraction digits to be shown in the text
     * @return the weight as text
     */
    fun weightToString(weight: Float, maxFractionDigits: Int): String =
            "${weightToStringWithoutUnitName(weight, maxFractionDigits)} ${getWeightUnitName()}"

    companion object {

        /**
         * Converts the specified minutes value to a time text (hh:mm).
         *
         * @param minutes minutes to convert
         * @return the time as text
         */
        @JvmStatic
        fun minutes2TimeString(minutes: Int): String {

            val hoursPart = minutes / 60
            val minutesPart = minutes % 60
            val strHoursPrefix = if (hoursPart < 10) "0" else ""
            val strMinutesPrefix = if (minutesPart < 10) "0" else ""
            return "$strHoursPrefix$hoursPart:$strMinutesPrefix$minutesPart"
        }

        /**
         * Converts the specified seconds value to a time text (hh:mm:ss).
         *
         * @param seconds seconds to convert
         * @return the time as text
         */
        @JvmStatic
        fun seconds2TimeString(seconds: Int): String {

            val secondsPart = seconds % 60
            val strSecondsPrefix = if (secondsPart < 10) "0" else ""

            val strHoursAndMinutes = minutes2TimeString(seconds / 60)
            return "$strHoursAndMinutes:$strSecondsPrefix$secondsPart"
        }

        /**
         * Converts the specified seconds value to a time text (mm:ss).
         *
         * @param seconds seconds to convert
         * @return the time as text
         */
        @JvmStatic
        fun seconds2MinuteTimeString(seconds: Int): String {

            val secondsPart = seconds % 60
            val minutesPart = seconds / 60
            val strMinutesPrefix = if (minutesPart < 10) "0" else ""
            val strSecondsPrefix = if (secondsPart < 10) "0" else ""
            return "$strMinutesPrefix$minutesPart:$strSecondsPrefix$secondsPart"
        }

        /**
         * Converts the specified 1/10 seconds value to a time text (hh:mm:ss.t).
         *
         * @param tenthSeconds 1/10 seconds to convert
         * @return the time as text
         */
        @JvmStatic
        fun tenthSeconds2TimeString(tenthSeconds: Int): String {

            val tenthSecondPart = tenthSeconds % 10
            val strHourMinutesSeconds = seconds2TimeString(tenthSeconds / 10)
            return "$strHourMinutesSeconds.$tenthSecondPart"
        }

        /**
         * Converts the specified time string to number of total seconds. The String has the format "h:m:s". The hour
         * and minute parts of the Strings are optional. The minute and second parts must be in range from 0 to 59. The
         * hour value can be greater. On conversion errors -1 will be returned.
         *
         * @param time the time string to convert
         * @return the number of seconds represented by the time String or -1 on errors
         */
        @JvmStatic
        fun timeString2TotalSeconds(time: String?): Int {

            if (time == null || time.trim().isEmpty()) {
                return -1
            }

            // split time String to hour, minute and second parts
            val timeSplitted = time.split(":")
            if (timeSplitted.isEmpty() || timeSplitted.size > 3) {
                return -1
            }

            // get int values of hours, minutes and seconds parts
            var hours = 0
            var minutes = 0
            var seconds: Int
            try {
                seconds = Integer.parseInt(timeSplitted[timeSplitted.size - 1])

                if (timeSplitted.size >= 2) {
                    minutes = Integer.parseInt(timeSplitted[timeSplitted.size - 2])
                }

                if (timeSplitted.size >= 3) {
                    hours = Integer.parseInt(timeSplitted[timeSplitted.size - 3])
                }
            } catch (e: Exception) {
                return -1
            }

            // check range of hours, minutes and seconds parts
            return if (seconds < 0 || seconds > 59 || minutes < 0 || minutes > 59 || hours < 0) {
                -1
            } else {
                // calculate total second
                hours * 60 * 60 + minutes * 60 + seconds
            }
        }
    }
}
