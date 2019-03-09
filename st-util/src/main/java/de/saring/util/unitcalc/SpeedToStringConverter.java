package de.saring.util.unitcalc;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * Class for converting a speed (mostly the average speed) from the float value to a string value (both directions).
 * Depending on the specified SpeedMode the string value can be be either in km/h (or mph) or time/km or (time/mile),
 * the time is then in format mm:hh.
 * Unit system conversion (km or miles) will not be done here!
 *
 * @author Stefan Saring
 */
public class SpeedToStringConverter {

    private static final String ZERO_SPEED_TIME = "00:00";

    private SpeedMode speedMode;
    private final NumberFormat numberFormat;

    /**
     * Standard c'tor.
     *
     * @param speedMode speed mode to use
     */
    public SpeedToStringConverter(final SpeedMode speedMode) {

        if (speedMode != SpeedMode.SPEED && speedMode != SpeedMode.PACE) {
            throw new IllegalArgumentException("Invalid SpeedMode " + speedMode + "!");
        }

        this.speedMode = speedMode;
        this.numberFormat = NumberFormat.getInstance();
        this.numberFormat.setMaximumFractionDigits(3);
    }

    /**
     * Returns the currently used speed mode.
     *
     * @return speed mode
     */
    public SpeedMode getSpeedMode() {
        return speedMode;
    }

    /**
     * Sets the speed mode to be used for conversion.
     *
     * @param speedMode speed mode
     */
    public void setSpeedMode(SpeedMode speedMode) {
        this.speedMode = speedMode;
    }

    /**
     * Returns the specified speed value as a formatted string by using the defined speed mode.
     *
     * @param floatSpeed speed value as a float
     * @return the speed as string or null when conversion failed
     */
    public String floatSpeedtoString(final Float floatSpeed) {
        if (floatSpeed == null) {
            return null;
        }

        if (speedMode == SpeedMode.SPEED) {
            return numberFormat.format(floatSpeed);
        }
        else { // PACE
            if (floatSpeed == 0) {
                return ZERO_SPEED_TIME;
            }
            return FormatUtils.seconds2MinuteTimeString((int) (3600 / floatSpeed));
        }
    }

    /**
     * Returns the speed value as a float for the specified speed string by using the defined speed mode.
     *
     * @param strSpeed formatted speed string
     * @return the speed value or null when conversion failed
     */
    public Float stringSpeedToFloat(final String strSpeed) {
        try {
            final String strSpeedTrimmed = strSpeed.trim();

            if (speedMode == SpeedMode.SPEED) {
                return NumberFormat.getInstance().parse(strSpeed).floatValue();
            }
            else { // PACE
                if (ZERO_SPEED_TIME.equals(strSpeedTrimmed)) {
                    return 0f;
                }
                final LocalTime time = LocalTime.parse("00:" + strSpeedTrimmed, DateTimeFormatter.ISO_LOCAL_TIME);
                return 3600 / (float) (time.getMinute() * 60 + time.getSecond());
            }
        }
        catch (Exception e) {
            return null;
        }
    }
}
