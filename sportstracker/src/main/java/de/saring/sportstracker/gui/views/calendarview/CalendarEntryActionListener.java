package de.saring.sportstracker.gui.views.calendarview;

/**
 * This interfaces is for handling actions on calendar entries.
 *
 * @author Stefan Saring
 */
public interface CalendarEntryActionListener {

    /**
     * Called when the user has double clicked on a calendar entry.
     *
     * @param calendarEntry the clicked calendar entry
     */
    void onCalendarEntryAction(CalendarEntry calendarEntry);
}