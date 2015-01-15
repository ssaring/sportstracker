package de.saring.sportstracker.gui.views.calendarview;

import java.time.LocalDate;
import java.util.List;

/**
 * TODO
 */
public interface CalendarEntryProvider {

    List<CalendarEntry> getCalendarEntriesForDate(final LocalDate date);
}

