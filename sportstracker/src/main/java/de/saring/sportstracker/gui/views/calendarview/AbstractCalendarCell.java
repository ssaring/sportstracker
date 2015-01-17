package de.saring.sportstracker.gui.views.calendarview;

import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * TODO
 *
 * @author Stefan Saring
 */
abstract class AbstractCalendarCell extends VBox {

    private Label laNumber;

    /**
     * Standard c'tor.
     *
     * @param backgroundColor background color of the cell
     */
    public AbstractCalendarCell(final Color backgroundColor) {
        setPadding(new Insets(4));
        setSpacing(4);

        // TODO use css
        setStyle("-fx-border-color: black; -fx-border-insets: -1");
        setBackground(new Background(new BackgroundFill(backgroundColor, null, null)));

        laNumber = new Label();
        laNumber.setAlignment(Pos.CENTER_RIGHT);
        laNumber.setMaxWidth(Double.MAX_VALUE);
        getChildren().add(laNumber);
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
