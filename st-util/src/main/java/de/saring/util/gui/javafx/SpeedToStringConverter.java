package de.saring.util.gui.javafx;

import de.saring.util.unitcalc.FormatUtils;
import de.saring.util.unitcalc.FormatUtils.SpeedMode;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom JavaFX StringConverter class for converting a speed (mostly the average speed) from the
 * float value to a string value (both directions). Depending on the configured SpeedView stored in
 * FormatUtils the string value can be be either in km/h (or mph) or time/km or (time/mile), the
 * time is then in format mm:hh.<br/>
 * Unit system conversion (km or miles) will not be done here!
 *
 * @author Stefan Saring
 */
public class SpeedToStringConverter extends StringConverter<Number> {

    private static final String ZERO_SPEED_TIME = "00:00";

    private final FormatUtils formatUtils;
    private final SpeedMode speedMode;
    private final NumberFormat numberFormat;

    /**
     * Standard c'tor.
     *
     * @param formatUtils FormatUtils to use for conversion
     * @param speedMode speed mode to use
     */
    public SpeedToStringConverter(final FormatUtils formatUtils, final SpeedMode speedMode) {
        this.formatUtils = formatUtils;
        this.speedMode = speedMode;
        this.numberFormat = NumberFormat.getInstance();
        this.numberFormat.setMaximumFractionDigits(3);
    }

    @Override
    public String toString(final Number nValue) {
        if (nValue == null) {
            return "";
        }

        final float speed = nValue.floatValue();

        switch (speedMode) {
            case SPEED:
                return numberFormat.format(nValue.floatValue());
            case PACE:
                if (speed == 0) {
                    return ZERO_SPEED_TIME;
                }
                return formatUtils.seconds2MinuteTimeString((int) (3600 / speed));
            default:
                throw new IllegalArgumentException("Invalid SpeedMode" + speedMode + "!");
        }
    }

    @Override
    public Number fromString(final String strValue) {
        try {
            final String strValueTrimmed = strValue.trim();

            switch (speedMode) {
                case SPEED:
                    return NumberFormat.getInstance().parse(strValueTrimmed).floatValue();
                case PACE:
                    if (ZERO_SPEED_TIME.equals(strValueTrimmed)) {
                        return 0f;
                    }
                    final LocalTime time = LocalTime.parse("00:" + strValueTrimmed, DateTimeFormatter.ISO_LOCAL_TIME);
                    return 3600 / (float) (time.getMinute() * 60 + time.getSecond());
                default:
                    throw new IllegalArgumentException("Invalid SpeedMode " + speedMode + "!");
            }
        } catch (Exception e) {
            return -1;
        }
    }
}
