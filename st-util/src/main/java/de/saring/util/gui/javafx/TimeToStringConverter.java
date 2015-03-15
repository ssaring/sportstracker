package de.saring.util.gui.javafx;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

/**
 * Custom JavaFX StringConverter class for converting a LocalTime value to
 * text by using the pattern HH:mm (hours from 0-23) in both directions.
 *
 * @author Stefan Saring
 */
public class TimeToStringConverter extends StringConverter<LocalTime> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_PARSER = DateTimeFormatter.ofPattern("H:m");

    @Override
    public String toString(final LocalTime time) {
        if (time == null) {
            return "";
        }
        return TIME_FORMATTER.format(time);
    }

    @Override
    public LocalTime fromString(final String timeText) {
        return LocalTime.from(TIME_PARSER.parse(timeText));
    }
}
