package de.saring.util.gui.javafx.control.calendar;

import javafx.scene.paint.Color;

import de.saring.util.data.IdDateObject;

/**
 * Data object which contains the informations of a single calendar entry.
 */
public class CalendarEntry {

    private IdDateObject entry;
    private String text;
    private String toolTipText;
    private Color color;

    /**
     * C'tor.
     *
     * @param entry entry object
     * @param text text to be shown in the calendar
     * @param toolTipText tooltip text to be shown in the calendar
     * @param color color to be used in the calendar (optional, default is black)
     */
    public CalendarEntry(final IdDateObject entry, final String text, final String toolTipText, final Color color) {
        this.entry = entry;
        this.text = text;
        this.toolTipText = toolTipText;
        this.color = color;
    }

    /**
     * @return the entry object
     */
    public IdDateObject getEntry() {
        return entry;
    }

    /**
     * @return text to be shown in the calendar
     */
    public String getText() {
        return text;
    }

    /**
     * @return tooltip text to be shown in the calendar
     */
    public String getToolTipText() {
        return toolTipText;
    }

    /**
     * @return color to be used in the calendar (optional, default is black)
     */
    public Color getColor() {
        return color;
    }
}
