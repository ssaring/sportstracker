package de.saring.util.gui.javafx.control.calendar;

import java.time.LocalDate;

import de.saring.util.data.IdDateObject;

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

    /**
     * Called when a single file has been dragged and dropped on a calendar day cell
     * (on empty space, not on an entry).
     *
     * @param filePath absolute path of the dropped file
     */
    void onDraggedFileDroppedOnCalendarDay(String filePath);

    /**
     * Called when a single file has been dragged and dropped on a calendar day entry in
     * a day cell.
     *
     * @param entry entry objects on which the file has been dropped
     * @param filePath absolute path of the dropped file
     */
    void onDraggedFileDroppedOnCalendarEntry(IdDateObject entry, String filePath);
}