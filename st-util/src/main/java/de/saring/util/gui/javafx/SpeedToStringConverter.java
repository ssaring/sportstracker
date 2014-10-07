package de.saring.util.gui.javafx;

import de.saring.util.unitcalc.FormatUtils;
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
    private final NumberFormat numberFormat;

    /**
     * Standard c'tor.
     *
     * @param formatUtils FormatUtils to use for conversion
     */
    public SpeedToStringConverter(final FormatUtils formatUtils) {
        this.formatUtils = formatUtils;

        this.numberFormat = NumberFormat.getInstance();
        this.numberFormat.setMaximumFractionDigits(3);
    }

    @Override
    public String toString(final Number nValue) {
        if (nValue == null) {
            return "";
        }

        final float speed = nValue.floatValue();

        switch (formatUtils.getSpeedView()) {
            case DistancePerHour:
                return numberFormat.format(nValue.floatValue());
            case MinutesPerDistance:
                if (speed == 0) {
                    return ZERO_SPEED_TIME;
                }
                return formatUtils.seconds2MinuteTimeString((int) (3600 / speed));
            default:
                throw new IllegalArgumentException("Invalid SpeedView " + formatUtils.getSpeedView() + "!");
        }
    }

    @Override
    public Number fromString(final String strValue) {
        try {
            final String strValueTrimmed = strValue.trim();

            switch (formatUtils.getSpeedView()) {
                case DistancePerHour:
                    return NumberFormat.getInstance().parse(strValueTrimmed).floatValue();
                case MinutesPerDistance:
                    if (ZERO_SPEED_TIME.equals(strValueTrimmed)) {
                        return 0f;
                    }
                    final LocalTime time = LocalTime.parse("00:" + strValueTrimmed, DateTimeFormatter.ISO_LOCAL_TIME);
                    return 3600 / (float) (time.getMinute() * 60 + time.getSecond());
                default:
                    throw new IllegalArgumentException("Invalid SpeedView " + formatUtils.getSpeedView() + "!");
            }
        } catch (Exception e) {
            return -1;
        }
    }
}
