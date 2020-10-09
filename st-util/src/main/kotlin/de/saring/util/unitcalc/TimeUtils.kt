package de.saring.util.unitcalc

/**
 * This class contains several static methods for converting and formatting time data.
 *
 * @author Stefan Saring
 */
object TimeUtils {

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

        if (time == null || time.isBlank()) {
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