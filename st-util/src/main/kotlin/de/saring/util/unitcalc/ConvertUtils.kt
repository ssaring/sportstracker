package de.saring.util.unitcalc

/**
 * This class contains several static methods for converting data in different formats and units.
 *
 * @author Stefan Saring
 */
object ConvertUtils {

    private const val KILOMETER_2_MILES: Double = 1.609344
    private const val METER_2_FEET: Float = 0.30479f
    private const val CELSIUS_2_FAHRENHEIT: Float = 0.555f
    private const val FAHRENHEIT_OFFSET: Int = 32
    private const val KILOGRAM_2_LBS: Double = 2.2046
    private val SEMICIRCLE_2_DEGREES: Double = 180 / Math.pow(2.0, 31.0)

    /**
     * Converts the length value from miles to kilometers.
     *
     * @param miles value in miles
     * @return value in kilometers
     */
    @JvmStatic
    fun convertMiles2Kilometer(miles: Double): Double = miles * KILOMETER_2_MILES

    /**
     * Converts the length value from miles to kilometers.
     *
     * @param miles value in miles
     * @return value in kilometers
     */
    @JvmStatic
    fun convertMiles2Kilometer(miles: Int): Int =
            Math.round(convertMiles2Kilometer(miles.toDouble())).toInt()

    /**
     * Converts the length value from kilometers to miles. The round is very useful for displaying values in english
     * unit mode. This avoids problems when e.g. the user has entered 45 miles, he will see e.g. 44,999999 miles
     * again. The reason is the internal storage in metric unit system.
     *
     * @param kilometers value in kilometers
     * @param fRound flag for round the result with 3 decimals
     * @return value in miles
     */
    @JvmStatic
    fun convertKilometer2Miles(kilometers: Double, fRound: Boolean): Double {
        val dResult = kilometers / KILOMETER_2_MILES
        return if (fRound) {
            Math.round(1000 * dResult) / 1000.0
        } else {
            dResult
        }
    }

    /**
     * Converts the length value from kilometers to miles.
     *
     * @param kilometers value in kilometers
     * @return value in miles
     */
    @JvmStatic
    fun convertKilometer2Miles(kilometers: Int): Int =
        Math.round(convertKilometer2Miles(kilometers.toDouble(), false)).toInt()

    /**
     * Converts the length value from feet to meter.
     *
     * @param feet value in feet
     * @return value in meters
     */
    @JvmStatic
    fun convertFeet2Meter(feet: Int): Int = Math.round(feet * METER_2_FEET)

    /**
     * Converts the length value from meter to feet.
     *
     * @param meters value in meters
     * @return value in feets
     */
    @JvmStatic
    fun convertMeter2Feet(meters: Int): Int = Math.round(meters / METER_2_FEET)

    /**
     * Converts the speed value from m/s to km/h.
     *
     * @param meterPerSecond speed value in m/s
     * @return speed value in km/h
     */
    @JvmStatic
    fun convertMeterPerSecond2KilometerPerHour(meterPerSecond: Float): Float = meterPerSecond * 3.6f

    /**
     * Converts the temperature value from fahrenheit to celsius.
     *
     * @param fahrenheit value in fahrenheit
     * @return value in celsius
     */
    @JvmStatic
    fun convertFahrenheit2Celsius(fahrenheit: Short): Short =
            Math.round((fahrenheit - FAHRENHEIT_OFFSET) * CELSIUS_2_FAHRENHEIT).toShort()

    /**
     * Converts the temperature value from celsius to fahrenheit..
     *
     * @param celsius value in celsius
     * @return value in fahrenheit
     */
    @JvmStatic
    fun convertCelsius2Fahrenheit(celsius: Short): Short =
            (Math.round(celsius / CELSIUS_2_FAHRENHEIT) + FAHRENHEIT_OFFSET).toShort()

    /**
     * Converts the weight value from kilogram to pounds (lbs).
     *
     * @param kilogram weight value in kilogram
     * @return value in pounds
     */
    @JvmStatic
    fun convertKilogram2Lbs(kilogram: Double): Double = kilogram * KILOGRAM_2_LBS

    /**
     * Converts the weight value from pounds (lbs) to kilogram.
     *
     * @param pounds weight value in pounds (lbs)
     * @return value in kilogram
     */
    @JvmStatic
    fun convertLbs2Kilogram(pounds: Double): Double = pounds / KILOGRAM_2_LBS

    /**
     * Converts the position value from semicircles to degrees.
     *
     * @param semicircles value in semicircles
     * @return value in degrees
     */
    @JvmStatic
    fun convertSemicircle2Degree(semicircles: Int): Double = semicircles * SEMICIRCLE_2_DEGREES
}
