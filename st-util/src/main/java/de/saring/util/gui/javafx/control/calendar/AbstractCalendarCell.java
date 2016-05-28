package de.saring.util.gui.javafx.control.calendar;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Abstract base class for all calendar content cell implementations. Each cell is a VBox
 * which contains the number label (of day or week) in the upper left corner and the cell
 * entries as labels below.
 *
 * @author Stefan Saring
 */
abstract class AbstractCalendarCell extends VBox {

    private Label laNumber;

    /**
     * Standard c'tor.
     */
    public AbstractCalendarCell() {
        setPadding(new Insets(3, 4, 3, 4));
        setSpacing(3);

        laNumber = new Label();
        laNumber.setAlignment(Pos.CENTER_RIGHT);
        laNumber.setMaxWidth(Double.MAX_VALUE);
        getChildren().add(laNumber);

        getStyleClass().add("calendar-control-cell");
        getNumberLabel().getStyleClass().add("number");
    }

    /**
     * Sets the number value (e.g. day or week number) of this cell.
     *
     * @param number number to display
     */
    public void setNumber(final int number) {
        laNumber.setText(String.valueOf(number));
    }

    /**
     * Returns the label which shows the cell number.
     *
     * @return Label
     */
    protected Label getNumberLabel() {
        return laNumber;
    }

    /**
     * Updates the entry labels (below the number label) of the cell. It removes any
     * previous displayed entry labels before
     *
     * @param entryLabels entry labels to show
     */
    protected void updateEntryLabels(final List<? extends Label> entryLabels) {
        if (getChildren().size() > 1) {
            getChildren().remove(1, getChildren().size());
        }

        getChildren().addAll(entryLabels);
    }
}
