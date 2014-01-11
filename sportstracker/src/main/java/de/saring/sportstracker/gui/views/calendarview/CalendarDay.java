package de.saring.sportstracker.gui.views.calendarview;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the data of a calendar day, which contains the date and
 * a list of exercises entries of this day.
 *
 * @author Stefan Saring
 */
public class CalendarDay {

    /**
     * Date of calendar day.
     */
    private final LocalDate date;

    /**
     * List of the exercises entries of this day.
     */
    private final List<CalendarEntry> calendarEntries;

    /**
     * Creates a CalendarDay instance for the specified date.
     *
     * @param date the date of the calendar day
     */
    public CalendarDay(LocalDate date) {
        this.date = date;
        this.calendarEntries = new ArrayList<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public List<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }
}
