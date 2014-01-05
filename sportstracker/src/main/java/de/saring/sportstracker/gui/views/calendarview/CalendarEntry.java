package de.saring.sportstracker.gui.views.calendarview;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.Weight;
import de.saring.util.data.IdDateObject;

import java.awt.*;

/**
 * This class contains all the data of an calendar entry (any IdDateObject child class)
 * inside a calendar day cell.
 */
public class CalendarEntry {

    /**
     * The displayed calendar entry.
     */
    private final IdDateObject entry;
    /**
     * The location rectangle inside the calendar widget which shows the entry (set while drawing).
     */
    private Rectangle locationRect;
    /**
     * The tooltip text when the mouse is above the calendar entry (set while drawing).
     */
    private String toolTipText;

    /**
     * Standard c'tor.
     *
     * @param entry the displayed entry
     */
    public CalendarEntry(IdDateObject entry) {
        this.entry = entry;
    }

    public IdDateObject getEntry() {
        return entry;
    }

    public Rectangle getLocationRect() {
        return locationRect;
    }

    public void setLocationRect(Rectangle locationRect) {
        this.locationRect = locationRect;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }

    /**
     * Checks that this entry is an Exercise entry.
     *
     * @return true when it's an Exercise
     */
    public boolean isExercise() {
        return entry instanceof Exercise;
    }

    /**
     * Checks that this entry is a Note entry.
     *
     * @return true when it's a Note
     */
    public boolean isNote() {
        return entry instanceof Note;
    }

    /**
     * Checks that this entry is a Weight entry.
     *
     * @return true when it's a Weight
     */
    public boolean isWeight() {
        return entry instanceof Weight;
    }
}
