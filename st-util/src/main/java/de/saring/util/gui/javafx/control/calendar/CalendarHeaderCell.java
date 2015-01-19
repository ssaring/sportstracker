package de.saring.util.gui.javafx.control.calendar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * TODO
 *
 * @author Stefan Saring
 */
class CalendarHeaderCell extends Label {

    /**
     * Standard c'tor.
     */
    public CalendarHeaderCell() {
        setAlignment(Pos.CENTER);
        setMaxWidth(Double.MAX_VALUE);
        setPadding(new Insets(4));

        // TODO use css
        setStyle("-fx-border-color: black; -fx-border-insets: -1; -fx-background-color: cornflowerblue;");
    }

    /**
     * TODO
     *
     * @param text
     * @param isSunday
     */
    public void setText(final String text, final boolean isSunday) {
        setText(text);
        // TODO use css
        setTextFill(isSunday ? Color.RED : Color.WHITE);

    }
}
