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
                    return "N/A";
                }
                return formatUtils.seconds2MinuteTimeString((int) (3600 / speed));
            default:
                throw new IllegalArgumentException("Invalid SpeedView " + formatUtils.getSpeedView() + "!");
        }
    }

    @Override
    public Number fromString(final String strValue) {
        try {
            switch (formatUtils.getSpeedView()) {
                case DistancePerHour:
                    return NumberFormat.getInstance().parse(strValue.trim()).floatValue();
                case MinutesPerDistance:
                    final LocalTime time = LocalTime.parse("00:" + strValue.trim(), DateTimeFormatter.ISO_LOCAL_TIME);
                    return 3600 / (float) (time.getMinute() * 60 + time.getSecond());
                default:
                    throw new IllegalArgumentException("Invalid SpeedView " + formatUtils.getSpeedView() + "!");
            }
        } catch (Exception e) {
            return -1;
        }
    }
}
