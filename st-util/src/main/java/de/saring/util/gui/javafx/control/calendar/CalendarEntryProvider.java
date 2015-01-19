package de.saring.util.gui.javafx.control.calendar;

import java.time.LocalDate;
import java.util.List;

/**
 * TODO, rename to CalendarDataProvider?
 */
public interface CalendarEntryProvider {

    List<CalendarEntry> getCalendarEntriesForDate(LocalDate date);

    /**
     * Returns the summary text to be displayed for the specified date range (week) in
     * the calendar summary cells. The returned list contains one string per summary line
     * or is empty when there are no entries in the specified date range.
     *
     *
     * @param dateStart start of date range
     * @param dateEnd end of date range
     * @return list of strings per summary line (not null, can be empty)
     */
    List<String> getSummaryForDateRange(LocalDate dateStart, LocalDate dateEnd);
}

