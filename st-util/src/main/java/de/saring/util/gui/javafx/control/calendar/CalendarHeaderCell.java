package de.saring.util.gui.javafx.control.calendar;

import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 * Custom label extension which displays the column header name.
 *
 * @author Stefan Saring
 */
class CalendarHeaderCell extends Label {

    private static final PseudoClass PSEUDO_CLASS_SUMMARY = PseudoClass.getPseudoClass("summary");

    /**
     * C'tor.
     */
    public CalendarHeaderCell() {
        setAlignment(Pos.CENTER);
        setMaxWidth(Double.MAX_VALUE);
        setPadding(new Insets(3));

        getStyleClass().addAll("calendar-control-cell", "calendar-control-header-cell");
    }

    /**
     * Sets the text to be shown in the header cell. The column for sunday will use a special color.
     *
     * @param text text
     * @param summary true when this is the summary column
     */
    public void setText(final String text, final boolean summary) {
        setText(text);
        pseudoClassStateChanged(PSEUDO_CLASS_SUMMARY, summary);
    }
}
