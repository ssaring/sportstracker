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
class CalendarSummaryCell extends VBox {

    // TODO use same baseclass aus day cell?

    private Label laWeek;

    /**
     * Standard c'tor.
     */
    public CalendarSummaryCell() {
        setPadding(new Insets(4));
        setSpacing(4);
        // TODO use css
        setStyle("-fx-border-color: black; -fx-border-insets: -1");
        setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));

        laWeek = new Label();
        laWeek.setAlignment(Pos.CENTER_RIGHT);
        laWeek.setMaxWidth(Double.MAX_VALUE);
        getChildren().add(laWeek);
    }

    /**
     * TODO
     *
     * @param weekNr
     */
    public void setWeek(final int weekNr) {
        laWeek.setText(String.valueOf(weekNr));
    }

    /**
     * TODO
     */
    public void setEntries(final List<String> entries) {

        if (getChildren().size() > 1) {
            getChildren().remove(1, getChildren().size());
        }

        getChildren().addAll(entries.stream() //
                .map(entry -> new Label(entry)) //
                .collect(Collectors.toList()));
    }
}
