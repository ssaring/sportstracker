package de.saring.util.unitcalc;

/**
 * This class contains several static methods for converting data in different
 * formats.
 *
 * @author Stefan Saring, Jacob Ilsoe Christensen (parts of C# version)
 * @version 1.0
 */
public class ConvertUtils {

    private static final double SEMICIRCLE_2_DEGREES = 180 / Math.pow(2, 31);

    /**
     * Converts the length value from miles to kilometers.
     *
     * @param miles value in miles
     * @return value in kilometers
     */
    public static double convertMiles2Kilometer(double miles) {
        return miles * 1.609344d;
    }

    /**
     * Converts the length value from miles to kilometers.
     *
     * @param miles value in miles
     * @return value in kilometers
     */
    public static int convertMiles2Kilometer(int miles) {
        return (int) Math.round(convertMiles2Kilometer((double) miles));
    }

    /**
     * Converts the length value from kilometers to miles. The round is very
     * usefull for displaying values in english unit mode. This avoids problems
     * when e.g. the user has entered 45 miles, he will see e.g. 44,999999 miles
     * again. The reason is the internal storage in metric unit system.
     *
     * @param kilometers value in kilometers
     * @param fRound flag for round the result with 3 decimals
     * @return value in miles
     */
    public static double convertKilometer2Miles(double kilometers, boolean fRound) {
        double dResult = kilometers / 1.609344d;
        if (fRound) {
            long lValue = Math.round(1000 * dResult);
            return lValue / 1000d;
        } else {
            return dResult;
        }
    }

    /**
     * Converts the length value from kilometers to miles.
     *
     * @param kilometers value in kilometers
     * @return value in miles
     */
    public static int convertKilometer2Miles(int kilometers) {
        return (int) Math.round(convertKilometer2Miles((double) kilometers, false));
    }

    /**
     * Converts the length value from feet to meter.
     *
     * @param feet value in feet
     * @return value in meters
     */
    public static int convertFeet2Meter(int feet) {
        return (int) Math.round(feet * 0.30479f);
    }

    /**
     * Converts the length value from meter to feet.
     *
     * @param meters value in meters
     * @return value in feets
     */
    public static int convertMeter2Feet(int meters) {
        return (int) Math.round(meters / 0.30479f);
    }

    /**
     * Converts the speed value from m/s to km/h.
     *
     * @param meterPerSecond speed value in m/s
     * @return speed value in km/h
     */
    public static float convertMeterPerSecond2KilometerPerHour(float meterPerSecond) {
        return meterPerSecond * 3.6f;
    }

    /**
     * Converts the temperature value from fahrenheit to celsius.
     *
     * @param fahrenheit value in fahrenheit
     * @return value in celsius
     */
    public static short convertFahrenheit2Celsius(short fahrenheit) {
        return (short) Math.round((fahrenheit - 32) * 0.555f);
    }

    /**
     * Converts the temperature value from celsius to fahrenheit..
     *
     * @param celsius value in celsius
     * @return value in fahrenheit
     */
    public static short convertCelsius2Fahrenheit(short celsius) {
        return (short) (Math.round(celsius / 0.555f) + 32);
    }

    /**
     * Converts the weight value from kilogram to pounds (lbs).
     *
     * @param weight value in kilogram
     * @return value in pounds
     */
    public static double convertKilogram2Lbs(double kilogram) {
        return kilogram * 2.2046d;
    }

    /**
     * Converts the weight value from pounds (lbs) to kilogram.
     *
     * @param weight value in pounds (lbs)
     * @return value in kilogram
     */
    public static double convertLbs2Kilogram(double pounds) {
        return pounds / 2.2046d;
    }

    /**
     * Converts the position value from semicircles to degrees.
     *
     * @param semicircles value in semicircles
     * @return value in degrees
     */
    public static double convertSemicircle2Degree(int semicircles) {
        return semicircles * SEMICIRCLE_2_DEGREES;
    }
}
