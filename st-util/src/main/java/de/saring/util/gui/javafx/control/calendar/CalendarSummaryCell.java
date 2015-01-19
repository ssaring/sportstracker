package de.saring.util.gui.javafx.control.calendar;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * Calendar cell implementation which shows the summary cell of a week. It displays the
 * week number and the summary information below.
 *
 * @author Stefan Saring
 */
class CalendarSummaryCell extends AbstractCalendarCell {

    /**
     * Standard c'tor.
     */
    public CalendarSummaryCell() {
        // TODO use CSS
        super(Color.LIGHTPINK);
    }

    /**
     * Sets the summary entries to be shown in this cell. Each entry will be shown as
     * a separate label / line.
     *
     * @param entries summary entries
     */
    public void setEntries(final List<String> entries) {
        updateEntryLabels(entries.stream() //
                .map(entry -> new Label(entry)) //
                .collect(Collectors.toList()));
    }
}
