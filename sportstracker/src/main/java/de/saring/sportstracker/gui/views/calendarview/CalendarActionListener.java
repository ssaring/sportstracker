package de.saring.sportstracker.gui.views.calendarview;

import java.time.LocalDate;

/**
 * This interfaces is for handling actions on the calendar control.
 *
 * @author Stefan Saring
 */
public interface CalendarActionListener {

    /**
     * Called when the user has double clicked on a calendar day cell, not on an entry.
     *
     * @param date the date of the clicked calendar day cell
     */
    void onCalendarDayAction(LocalDate date);

    /**
     * Called when the user has double clicked on a calendar entry.
     *
     * @param calendarEntry the clicked calendar entry
     */
    void onCalendarEntryAction(CalendarEntry calendarEntry);
}