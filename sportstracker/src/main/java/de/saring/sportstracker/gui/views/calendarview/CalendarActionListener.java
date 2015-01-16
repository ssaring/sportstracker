package de.saring.sportstracker.gui.views.calendarview;

/**
 * This interfaces is for handling actions on the calendar control.
 *
 * @author Stefan Saring
 */
public interface CalendarActionListener {

    // TODO add action for double clicks on the calendar day => add new exercises

    /**
     * Called when the user has double clicked on a calendar entry.
     *
     * @param calendarEntry the clicked calendar entry
     */
    void onCalendarEntryAction(CalendarEntry calendarEntry);
}