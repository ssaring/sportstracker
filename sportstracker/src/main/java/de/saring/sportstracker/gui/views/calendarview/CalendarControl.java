package de.saring.sportstracker.gui.views.calendarview;

import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * <p>Custom control which displays a calendar for one month. It contains cells for all
 * the days of the month, each cell contains the SportsTracker entries for that day.</p>
 * <p>The layout uses a VBox which contains two GridPanes, one for the header cells
 * (weekday names), the other for the day cells (for all days of the displayed month
 * and parts of the previous and next month).</p>
 * <p>Both GridPanes contains 8 columns of same width, 7 for all days of a week
 * (Sunday - Saturday or Monday - Sunday) and one for the weekly summary.</p>
 * <p>The GridPane for the header cells contains only one row with a fixed height. The
 * GridPane for the day cells contains always 6 rows, each for one week (6 weeks to
 * make sure that always the complete month can be displayed.) The GridPane for the day
 * cells uses all the available vertical space.</p>
 *
 * @author Stefan Saring
 */
public class CalendarControl extends VBox {

    private static final int GRIDS_COLUMN_COUNT = 8;
    private static final int GRID_DAYS_ROW_COUNT = 6;

    private GridPane gridHeaderCells;
    private GridPane gridDayCells;

    /**
     * Standard c'tor.
     */
    public CalendarControl() {
        setupLayout();
    }

    private void setupLayout() {

        // create GridPanes for header and day cells and add it to the VBox
        gridHeaderCells = new GridPane();
        gridDayCells = new GridPane();

        // TODO for test purposes only
        this.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        gridHeaderCells.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        gridDayCells.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        gridHeaderCells.setGridLinesVisible(true);
        gridDayCells.setGridLinesVisible(true);

        VBox.setVgrow(gridHeaderCells, Priority.NEVER);
        VBox.setVgrow(gridDayCells, Priority.ALWAYS);
        this.getChildren().addAll(gridHeaderCells, gridDayCells);

        // define column constraints for both GridPanes, all 8 columns must have the same width
        for (int column = 0; column < GRIDS_COLUMN_COUNT; column++) {
            final ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100 / (double) GRIDS_COLUMN_COUNT);
            gridHeaderCells.getColumnConstraints().add(columnConstraints);
            gridDayCells.getColumnConstraints().add(columnConstraints);
        }

        // define row constraint for header GridPane (one row with a fixed height)
        final RowConstraints headerRowConstraints = new RowConstraints();
        gridHeaderCells.getRowConstraints().add(headerRowConstraints);

        // define row constraints for days GridPane (6 rows with same height which are using all the available space)
        for (int row = 0; row < GRID_DAYS_ROW_COUNT; row++) {
            final RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight((100 / (double) (GRID_DAYS_ROW_COUNT)));
            gridDayCells.getRowConstraints().add(rowConstraints);
        }

        // TODO use a custom control for the header cells
        // add cells for week day names and summary to the header GridPane
        for (int column = 0; column < GRIDS_COLUMN_COUNT - 1; column++) {
            gridHeaderCells.add(new Label("Day " + (column + 1)), column, 0);
        }
        gridHeaderCells.add(new Label("Summary"), GRIDS_COLUMN_COUNT - 1, 0);

        // TODO use a custom control for the day cells
        // add cells for all displayed days to the days GridPane
        for (int row = 0; row < GRID_DAYS_ROW_COUNT; row++) {
            for (int column = 0; column < GRIDS_COLUMN_COUNT - 1; column++) {
                gridDayCells.add(new Label(String.valueOf((7 * row) + (column + 1))), column, row);
            }
        }
    }
}
