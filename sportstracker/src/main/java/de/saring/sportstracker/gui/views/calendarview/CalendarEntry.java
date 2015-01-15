package de.saring.sportstracker.gui.views.calendarview;

import de.saring.util.data.IdDateObject;
import javafx.scene.paint.Color;

/**
 * TODO
 */
public class CalendarEntry {

    private IdDateObject entry;
    private String text;
    private String toolTipText;
    private Color color;

    /**
     *
     * @param entry
     * @param text
     * @param toolTipText
     * @param color (optional)
     */
    public CalendarEntry(final IdDateObject entry, final String text, final String toolTipText, final Color color) {
        this.entry = entry;
        this.text = text;
        this.toolTipText = toolTipText;
        this.color = color;
    }

    public IdDateObject getEntry() {
        return entry;
    }

    public String getText() {
        return text;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public Color getColor() {
        return color;
    }
}
