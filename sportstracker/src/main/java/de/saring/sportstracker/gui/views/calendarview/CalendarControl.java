package de.saring.sportstracker.gui.views.calendarview;

import de.saring.util.AppResources;
import de.saring.util.data.IdDateObject;
import de.saring.util.data.IdObject;
import javafx.geometry.VPos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Stream;

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

    private AppResources resources;

    private GridPane gridHeaderCells;
    private GridPane gridDayCells;

    private CalendarHeaderCell[] headerCells = new CalendarHeaderCell[GRIDS_COLUMN_COUNT];
    private CalendarDayCell[] dayCells = new CalendarDayCell[7 * GRID_DAYS_ROW_COUNT];

    private int displayedMonth;
    private int displayedYear;
    private boolean weekStartsSunday;

    private CalendarEntryProvider calendarEntryProvider;

    private CalendarEntrySelectionListener externalCalendarEntrySelectionListener;

    /**
     * Standard c'tor.
     *
     * @param resources the application text resources
     */
    public CalendarControl(AppResources resources) {
        this.resources = resources;

        final LocalDate today = LocalDate.now();
        displayedMonth = today.getMonthValue();
        displayedYear = today.getYear();
        weekStartsSunday = false;

        setupLayout();
        setupSelectionListener();
        updateContent();
    }

    /**
     * TODO
     *
     * @param calendarEntryProvider
     */
    public void setCalendarEntryProvider(final CalendarEntryProvider calendarEntryProvider) {
        this.calendarEntryProvider = calendarEntryProvider;
    }

    /**
     * TODO
     *
     * @param entrySelectionListener
     */
    public void setCalendarEntrySelectionListener(final CalendarEntrySelectionListener entrySelectionListener) {
        this.externalCalendarEntrySelectionListener = entrySelectionListener;
    }

    /**
     * TODO
     *
     * @param year
     * @param month
     * @param weekStartsSunday
     */
    public void updateCalendar(final int year, final int month, final boolean weekStartsSunday) {
        this.displayedYear = year;
        this.displayedMonth = month;
        this.weekStartsSunday = weekStartsSunday;
        updateContent();
    }

    /**
     * Selects the specified entry, if it is currently displayed in the calendar.
     *
     * @param entry entry to select
     */
    public void selectEntry(final IdObject entry) {
        for (CalendarDayCell dayCell : dayCells) {
            if (dayCell.selectEntry(entry)) {
                break;
            }
        }
    }

    /**
     * Removes the entry selection, if there is one.
     */
    public void removeSelection() {
        Stream.of(dayCells).forEach(dayCell -> dayCell.removeSelectionExcept(null));
    }

    private void setupLayout() {

        // create GridPanes for header and day cells and add it to the VBox
        gridHeaderCells = new GridPane();
        gridDayCells = new GridPane();

        // TODO for test purposes only
        this.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));

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
            rowConstraints.setValignment(VPos.TOP);
            rowConstraints.setPercentHeight((100 / (double) (GRID_DAYS_ROW_COUNT)));
            // probably a JavaFX bug: min height must be 0, otherwise the row takes at least the computed height
            rowConstraints.setMinHeight(0);
            gridDayCells.getRowConstraints().add(rowConstraints);
        }

        // create header cells and add them to the header GridPane
        for (int i = 0; i< headerCells.length; i++) {
            headerCells[i] = new CalendarHeaderCell();
            gridHeaderCells.add(headerCells[i], i, 0);
        }

        // create day cells and add them to the days GridPane
        for (int row = 0; row < GRID_DAYS_ROW_COUNT; row++) {
            for (int column = 0; column < 7; column++) {
                final CalendarDayCell dayCell = new CalendarDayCell();
                dayCells[(row * 7) + column] = dayCell;
                gridDayCells.add(dayCell, column, row);
            }
        }

        // TODO add weekly summary cells
    }

    private void setupSelectionListener() {

        // setup an internal entry selection listener on all CalendarDayCells:
        // -> it removes any other existing entry selections first and then notifies the
        // external entry selection listener
        final CalendarEntrySelectionListener listener = (calendarEntry, selected) -> {
            if (selected) {
                Stream.of(dayCells).forEach(dayCell -> dayCell.removeSelectionExcept(calendarEntry));
            }

            if (externalCalendarEntrySelectionListener != null) {
                externalCalendarEntrySelectionListener.calendarEntrySelectionChanged(calendarEntry, selected);
            }
        };

        Stream.of(dayCells).forEach(dayCell -> dayCell.setCalendarEntrySelectionListener(listener));
    }

    private void updateContent() {
        updateHeaderCells();
        updateDayCells();
        // TODO updateSummaryCells();
    }

    /**
     * Updates the content of the header cell depending on the current week start day.
     */
    private void updateHeaderCells() {
        final int indexSunday = weekStartsSunday ? 0 : 6;
        final String[] weekdays = weekStartsSunday ? //
                new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"} : //
                new String[] {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

        for (int i = 0; i < weekdays.length; i++) {
            headerCells[i].setText(resources.getString("st.valview.weekdays." + weekdays[i]), indexSunday == i);
        }
        headerCells[7].setText(resources.getString("st.valview.week_sum"), false);
    }

    /**
     * Updates the content of all day cells for the current month and year.
     */
    private void updateDayCells() {
        LocalDate currentCellDate = getFirstDisplayedDay();

        for (int i = 0; i < dayCells.length; i++) {
            final boolean dateOfDisplayedMonth = currentCellDate.getMonthValue() == displayedMonth;
            dayCells[i].setDate(currentCellDate, dateOfDisplayedMonth);

            if (calendarEntryProvider != null) {
                final List<CalendarEntry> entries = calendarEntryProvider.getCalendarEntriesForDate(currentCellDate);
                dayCells[i].setEntries(entries);
            }

            currentCellDate = currentCellDate.plus(1, ChronoUnit.DAYS);
        }
    }

    /**
     * Calculates the first displayed day in the calendar, depending whether week start is sunday or
     * monday. This is mostly a day of the previous month.
     *
     * @return first displayed day in the calendar
     */
    private LocalDate getFirstDisplayedDay() {
        final LocalDate firstDayOfMonth = LocalDate.of(displayedYear, displayedMonth, 1);
        return firstDayOfMonth.with(TemporalAdjusters.previousOrSame( //
                weekStartsSunday ? DayOfWeek.SUNDAY : DayOfWeek.MONDAY));
    }
}
