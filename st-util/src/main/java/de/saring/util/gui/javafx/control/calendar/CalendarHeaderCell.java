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

    private static final PseudoClass PSEUDO_CLASS_SUNDAY = PseudoClass.getPseudoClass("sunday");

    /**
     * C'tor.
     */
    public CalendarHeaderCell() {
        setAlignment(Pos.CENTER);
        setMaxWidth(Double.MAX_VALUE);
        setPadding(new Insets(4));

        getStyleClass().addAll("calendar-control-cell", "calendar-control-header-cell");
    }

    /**
     * Sets the text to be shown in the header cell. The column for sunday will use a special color.
     *
     * @param text text
     * @param isSunday true when this column is for sunday
     */
    public void setText(final String text, final boolean isSunday) {
        setText(text);
        pseudoClassStateChanged(PSEUDO_CLASS_SUNDAY, isSunday);
    }
}
