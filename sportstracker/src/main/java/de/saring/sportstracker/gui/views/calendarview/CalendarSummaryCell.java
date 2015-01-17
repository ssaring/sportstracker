package de.saring.sportstracker.gui.views.calendarview;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * TODO
 *
 * @author Stefan Saring
 */
class CalendarSummaryCell extends AbstractCalendarCell {

    /**
     * Standard c'tor.
     */
    public CalendarSummaryCell() {
        super(Color.LIGHTPINK);
    }

    /**
     * TODO
     */
    public void setEntries(final List<String> entries) {
        updateEntryLabels(entries.stream() //
                .map(entry -> new Label(entry)) //
                .collect(Collectors.toList()));
    }
}
