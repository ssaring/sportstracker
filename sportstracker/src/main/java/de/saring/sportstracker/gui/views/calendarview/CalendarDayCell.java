package de.saring.sportstracker.gui.views.calendarview;

import java.time.DayOfWeek;
import java.time.LocalDate;

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
class CalendarDayCell extends VBox {

    private Label laDay;

    private LocalDate date;
    private boolean displayedMonth;

    /**
     * Standard c'tor.
     */
    public CalendarDayCell() {
        setPadding(new Insets(4));
        setSpacing(4);
        // TODO use css
        setStyle("-fx-border-color: black; -fx-border-insets: -1");
        setBackground(new Background(new BackgroundFill(Color.color(0.9, 0.9, 0.9), null, null)));

        laDay = new Label();
        laDay.setAlignment(Pos.CENTER_RIGHT);
        laDay.setMaxWidth(Double.MAX_VALUE);
        getChildren().add(laDay);
    }

    /**
     * TODO
     *
     * @param date
     * @param displayedMonth
     */
    public void setDate(final LocalDate date, final boolean displayedMonth) {
        this.date = date;
        this.displayedMonth = displayedMonth;

        updateDayLabel();
    }

    private void updateDayLabel() {
        laDay.setText(String.valueOf(date.getDayOfMonth()));

        // TODO use CSS
        final boolean today = LocalDate.now().equals(date);
        Color color = Color.DARKBLUE;
        String style = "-fx-font-weight: bold;";

        if (!today) {
            color = displayedMonth ? Color.BLACK : Color.GRAY;
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                color = displayedMonth ? Color.RED : Color.SALMON;
            }
            style = "-fx-font-weight: normal;";
        }

        laDay.setTextFill(color);
        laDay.setStyle(style);
    }
}
