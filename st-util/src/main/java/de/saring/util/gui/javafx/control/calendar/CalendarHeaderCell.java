package de.saring.util.gui.javafx.control.calendar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * Custom label extension which displays the column header name.
 *
 * @author Stefan Saring
 */
class CalendarHeaderCell extends Label {

    /**
     * C'tor.
     */
    public CalendarHeaderCell() {
        setAlignment(Pos.CENTER);
        setMaxWidth(Double.MAX_VALUE);
        setPadding(new Insets(4));

        // TODO use css
        setStyle("-fx-border-color: black; -fx-border-insets: -1; -fx-background-color: cornflowerblue;");
    }

    /**
     * Sets the text to be shown in the header cell. The column for sunday will use a special color.
     *
     * @param text text
     * @param isSunday true when this column is for sunday
     */
    public void setText(final String text, final boolean isSunday) {
        setText(text);
        // TODO use css
        setTextFill(isSunday ? Color.RED : Color.WHITE);

    }
}
