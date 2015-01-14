package de.saring.sportstracker.gui.views.calendarview;

import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

/**
 * TODO
 *
 * Custom control which displays a calendar month view. It's based on a GridPane, the
 * grid cells contain either header information or the days of the displayed month
 * (or days from previous and next month).<br/>
 * The grid always contains 8 columns and 7 rows. The first row displays the names of
 * the week days, the next 6 rows contain each one week (6 weeks to make sure that always
 * the complete month is displayed.)<br/>
 * The first 7 columns contain the week days (Sunday - Saturday or Monday - Sunday),
 * the last column contains the weekly summary.
 *
 * @author Stefan Saring
 */
public class Calendar extends GridPane {

    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 7;

    // private GridPane gridHeaderCells;
    // private GridPane gridDayCells;

    /**
     * TODO
     */
    public Calendar() {

        this.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        setGridLinesVisible(true);

        for (int column = 0; column < COLUMN_COUNT; column++) {
            final ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100 / (double) COLUMN_COUNT);
            this.getColumnConstraints().add(columnConstraints);
        }

        final RowConstraints headerRowConstraints = new RowConstraints();
        getRowConstraints().add(headerRowConstraints);

        for (int row = 1; row < ROW_COUNT; row++) {
            final RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight((100 / (double) (ROW_COUNT)));
            getRowConstraints().add(rowConstraints);
        }


        // add header row for week day names and summary
        for (int column = 0; column < COLUMN_COUNT - 1; column++) {
            add(new Label("Day " + (column + 1)), column, 0);
        }
        add(new Label("Summary"), COLUMN_COUNT - 1, 0);

        // add cells for the days
        for (int row = 1; row < ROW_COUNT; row++) {
            for (int column = 0; column < COLUMN_COUNT - 1; column++) {
                add(new Label(String.valueOf((7 * (row - 1)) + (column + 1))), column, row);
            }
        }
    }
}
