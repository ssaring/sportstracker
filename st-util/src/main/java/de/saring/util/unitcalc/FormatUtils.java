package de.saring.util.unitcalc;

import java.text.NumberFormat;

/**
 * This class contains methods for converting data in different formats to
 * Strings.
 *
 * @author Stefan Saring, Jacob Ilsoe Christensen (parts of C# version)
 * @version 1.0
 */
public class FormatUtils {

    /**
     * This is the list of possible unit systems. Metric units are e.g.
     * kilometers for distance or celcius for temperature. English units are
     * e.g. miles for distance and fahrenheit for temperature.
     */
    public enum UnitSystem {
        Metric, English
    }

    /**
     * This is the list of possible speed unit views. Distance per hour (e.g.
     * km/h) is mostly used for all kind of sports, but minutes per distance
     * (e.g. m/km) is very common for runners.
     */
    public enum SpeedView {
        DistancePerHour, MinutesPerDistance
    }

    /**
     * The current unit system used by the formater.
     */
    private final UnitSystem unitSystem;

    /**
     * The current speed view used by the formater.
     */
    private final SpeedView speedView;

    /**
     * The number format instance.
     */
    private final NumberFormat numberFormat;

    /**
     * Creates a new FormatUtils instance for the specified unit system.
     *
     * @param unitSystem the unit system to be used
     * @param speedView the speed view to be used
     */
    public FormatUtils(UnitSystem unitSystem, SpeedView speedView) {
        this.unitSystem = unitSystem;
        this.speedView = speedView;
        this.numberFormat = NumberFormat.getInstance();
    }

    /**
     * Returns the current unit system.
     *
     * @return the current unit system
     */
    public UnitSystem getUnitSystem() {
        return unitSystem;
    }

    /**
     * Returns the current speed view.
     *
     * @return the current speed view
     */
    public SpeedView getSpeedView() {
        return speedView;
    }

    /**
     * Returns the String representation of the current distance unit.
     *
     * @return the current distance unit name
     */
    public String getDistanceUnitName() {
        switch (this.unitSystem) {
            case English:
                return "m";
            case Metric:
            default:
                return "km";
        }
    }

    /**
     * Returns the String representation of the current speed unit.
     *
     * @return the current speed unit name
     */
    public String getSpeedUnitName() {
        switch (this.unitSystem) {
            case English:
                switch (this.speedView) {
                    case MinutesPerDistance:
                        return "min/m";
                    case DistancePerHour:
                    default:
                        return "mph";
                }
            case Metric:
            default:
                switch (this.speedView) {
                    case MinutesPerDistance:
                        return "min/km";
                    case DistancePerHour:
                    default:
                        return "km/h";
                }
        }
    }

    /**
     * Returns the String representation of the current temperature unit.
     *
     * @return the current temperature unit name
     */
    public String getTemperatureUnitName() {
        switch (this.unitSystem) {
            case English:
                return "F";
            case Metric:
            default:
                return "C";
        }
    }

    /**
     * Returns the String representation of the current altitude unit.
     *
     * @return the current temperature unit name
     */
    public String getAltitudeUnitName() {
        switch (this.unitSystem) {
            case English:
                return "ft";
            case Metric:
            default:
                return "m";
        }
    }

    /**
     * Returns the String representation of the current weight unit.
     *
     * @return the current temperature unit name
     */
    public String getWeightUnitName() {
        switch (this.unitSystem) {
            case English:
                return "lbs";
            case Metric:
            default:
                return "kg";
        }
    }

    /**
     * Converts the specified minutes value to a time String (hh:mm).
     *
     * @param minutes minutes to convert
     * @return the created time String
     */
    public String minutes2TimeString(int minutes) {
        int hourPart = minutes / 60;
        int minutePart = minutes % 60;

        StringBuilder sBuilder = new StringBuilder();
        if (hourPart < 10) {
            sBuilder.append("0");
        }
        sBuilder.append(hourPart);
        sBuilder.append(":");
        if (minutePart < 10) {
            sBuilder.append("0");
        }
        sBuilder.append(minutePart);

        return sBuilder.toString();
    }

    /**
     * Converts the specified seconds value to a time String (hh:mm:ss).
     *
     * @param seconds seconds to convert
     * @return the created time String
     */
    public String seconds2TimeString(int seconds) {
        int secondPart = seconds % 60;

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(minutes2TimeString(seconds / 60));
        sBuilder.append(":");
        if (secondPart < 10) {
            sBuilder.append("0");
        }
        sBuilder.append(secondPart);

        return sBuilder.toString();
    }

    /**
     * Converts the specified seconds value to a time String (mm:ss).
     *
     * @param seconds seconds to convert
     * @return the created time String
     */
    public String seconds2MinuteTimeString(int seconds) {
        int secondPart = seconds % 60;
        int minutePart = seconds / 60;

        StringBuilder stringBuilder = new StringBuilder();

        if (minutePart < 10) {
            stringBuilder.append("0");
        }

        stringBuilder.append(minutePart);
        stringBuilder.append(":");

        if (secondPart < 10) {
            stringBuilder.append("0");
        }

        stringBuilder.append(secondPart);

        return stringBuilder.toString();
    }

    /**
     * Converts the specified 1/10 seconds value to a time String (hh:mm:ss.t).
     *
     * @param tenthSeconds 1/10 seconds to convert
     * @return the created time String
     */
    public String tenthSeconds2TimeString(int tenthSeconds) {
        int tenthSecondPart = tenthSeconds % 10;

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(seconds2TimeString(tenthSeconds / 10));
        sBuilder.append(".");
        sBuilder.append(tenthSecondPart);

        return sBuilder.toString();
    }

    /**
     * Converts the specified time String to number of total seconds. The String
     * has the format "h:m:s". The hour and minute parts of the Strings are
     * optional. The minute and second parts must be in range from 0 to 59. The
     * hour value can be greater. On conversion errors -1 will be returned.
     *
     * @param time the time String to convert
     * @return the number of seconds represented by the time String or -1 on
     *         errors
     */
    public int timeString2TotalSeconds(String time) {
        if ((time == null) || (time.length() == 0)) {
            return -1;
        }

        // split time String to hour, minute and second parts
        String[] timeSplitted = time.split(":");
        if ((timeSplitted.length == 0) || (timeSplitted.length > 3)) {
            return -1;
        }

        // get int values of hours, minutes and seconds parts
        int hours = 0, minutes = 0, seconds = 0;
        try {
            seconds = Integer.parseInt(timeSplitted[timeSplitted.length - 1]);

            if (timeSplitted.length >= 2) {
                minutes = Integer.parseInt(timeSplitted[timeSplitted.length - 2]);
            }

            if (timeSplitted.length >= 3) {
                hours = Integer.parseInt(timeSplitted[timeSplitted.length - 3]);
            }
        } catch (Exception e) {
            return -1;
        }

        // check range of hours, minutes and seconds parts
        if ((seconds < 0) || (seconds > 59)
                || (minutes < 0) || (minutes > 59)
                || (hours < 0)) {
            return -1;
        }

        // calculate total second
        return (hours * 60 * 60) + (minutes * 60) + seconds;
    }

    /**
     * Converts the heart rate to a String.
     *
     * @param heartRate heart rate in beats per minute
     * @return a String representation of the heart rate
     */
    public String heartRateToString(int heartRate) {
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(heartRate) + " bpm";
    }

    /**
     * Converts the temperature to a String in the correct unit depending on
     * what unit options are currently chosen.
     *
     * @param temperature temperature in Celsius
     * @return a String representation of the temperature
     */
    public String temperatureToString(short temperature) {
        numberFormat.setMaximumFractionDigits(0);
        switch (this.unitSystem) {
            case English:
                return numberFormat.format(ConvertUtils.convertCelsius2Fahrenheit(
                        temperature)) + " " + getTemperatureUnitName();
            case Metric:
            default:
                return numberFormat.format(temperature) + " " + getTemperatureUnitName();
        }
    }

    /**
     * Converts the distance to a String in the correct unit depending on what
     * unit options are currently chosen. The unit name is not included in the
     * String.
     *
     * @param distance distance in km
     * @param decimals the number of decimals (fraction digits) to show
     * @return a String representation of the distance
     */
    public String distanceToStringWithoutUnitName(double distance, int decimals) {
        numberFormat.setMaximumFractionDigits(decimals);
        switch (this.unitSystem) {
            case English:
                return numberFormat.format(ConvertUtils.convertKilometer2Miles(distance, false));
            case Metric:
            default:
                return numberFormat.format(distance);
        }
    }

    /**
     * Converts the distance to a String in the correct unit depending on what
     * unit options are currently chosen.
     *
     * @param distance distance in km
     * @param decimals the number of decimals (fraction digits) to show
     * @return a String representation of the distance
     */
    public String distanceToString(double distance, int decimals) {
        return distanceToStringWithoutUnitName(distance, decimals) + " " + getDistanceUnitName();
    }

    /**
     * Converts the speed to a String in the correct unit depending on what unit
     * options and speed view are currently chosen. The unit name is not
     * included in the String.
     *
     * @param speed speed in km/h
     * @param decimals the number of decimals (fraction digits) to show
     * @return a String representation of the speed
     */
    public String speedToStringWithoutUnitName(float speed, int decimals) {
        numberFormat.setMaximumFractionDigits(decimals);
        switch (this.unitSystem) {
            case English:
                switch (this.speedView) {
                    case MinutesPerDistance:
                        if (speed == 0) {
                            return "N/A";
                        }
                        return seconds2MinuteTimeString((int) (3600 / ConvertUtils.convertKilometer2Miles(speed, false)));
                    case DistancePerHour:
                    default:
                        return numberFormat.format(ConvertUtils.convertKilometer2Miles(speed, false));
                }
            case Metric:
            default:
                switch (this.speedView) {
                    case MinutesPerDistance:
                        if (speed == 0) {
                            return "N/A";
                        }
                        return seconds2MinuteTimeString((int) (3600 / speed));
                    case DistancePerHour:
                    default:
                        return numberFormat.format(speed);
                }
        }
    }

    /**
     * Converts the speed to a String in the correct unit depending on what unit
     * options and speed view are currently chosen.
     *
     * @param speed speed in km/h
     * @param decimals the number of decimals (fraction digits) to show
     * @return a String representation of the speed
     */
    public String speedToString(float speed, int decimals) {
        if (speed == 0) {
            return speedToStringWithoutUnitName(speed, decimals);
        } else {
            return speedToStringWithoutUnitName(speed, decimals) + " " + getSpeedUnitName();
        }
    }

    /**
     * Converts the height to a String in the correct unit depending on what
     * unit options are currently chosen. The unit name is not included in the
     * String.
     *
     * @param height height in meters
     * @return a string representation of the height
     */
    public String heightToStringWithoutUnitName(int height) {
        numberFormat.setMaximumFractionDigits(0);
        switch (this.unitSystem) {
            case English:
                return numberFormat.format(ConvertUtils.convertMeter2Feet(height));
            case Metric:
            default:
                return numberFormat.format(height);
        }
    }

    /**
     * Converts the height to a String in the correct unit depending on what
     * unit options are currently chosen.
     *
     * @param height height in meters
     * @return a string representation of the height
     */
    public String heightToString(int height) {
        return heightToStringWithoutUnitName(height) + " " + getAltitudeUnitName();
    }

    /**
     * Converts the cadence to a String.
     *
     * @param cadence cadence in rounds per minute
     * @return a String representation of the cadence incl. unit name
     */
    public String cadenceToString(int cadence) {
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(cadence) + " rpm / spm";
    }

    /**
     * Converts the amount of cycles (e.g. rotations or steps) to a String.
     *
     * @param cycles number of cycles
     * @return a String representation of the total cycles incl. unit name
     */
    public String cyclesToString(long cycles) {
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(cycles) + " rotations / steps";
    }

    /**
     * Converts the calorie consumption value to a String.
     *
     * @param calories the calorie consumption
     * @return a String representation of the calorie consumption incl. unit
     *         name
     */
    public String caloriesToString(int calories) {
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(calories) + " kCal";
    }

    /**
     * Converts the weight to a String in the correct unit depending on what
     * unit options are currently chosen. The unit name is not included in the
     * String.
     *
     * @param weight weight in kilograms
     * @param maxFractionDigits maximum fraction digits to be shown in the
     * String
     * @return a string representation of the weight
     */
    public String weightToStringWithoutUnitName(float weight, int maxFractionDigits) {
        numberFormat.setMaximumFractionDigits(maxFractionDigits);
        switch (this.unitSystem) {
            case English:
                return numberFormat.format(ConvertUtils.convertKilogram2Lbs(weight));
            case Metric:
            default:
                return numberFormat.format(weight);
        }
    }

    /**
     * Converts the weight to a String in the correct unit depending on what
     * unit options are currently chosen. The String contains the unit name.
     *
     * @param weight weight in kilograms
     * @param maxFractionDigits maximum fraction digits to be shown in the
     * String
     * @return a string representation of the weight
     */
    public String weightToString(float weight, int maxFractionDigits) {
        return weightToStringWithoutUnitName(weight, maxFractionDigits) + " " + getWeightUnitName();
    }
}
