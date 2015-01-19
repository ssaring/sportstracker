package de.saring.util.gui.javafx.control.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Stream;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import de.saring.util.Date310Utils;
import de.saring.util.data.IdObject;

/**
 * Custom control which displays a calendar for one month. It contains cells for all the days of the month,
 * each cell contains the SportsTracker entries for that day.<br/>
 * The layout uses a VBox which contains two GridPanes, one for the header cells (weekday names), the other
 * for the day cells (for all days of the displayed month and parts of the previous and next month).<br/>
 * Both GridPanes contains 8 columns of same width, 7 for all days of a week (Sunday - Saturday or
 * Monday - Sunday) and one for the weekly summary.<br/>
 * The GridPane for the header cells contains only one row with a fixed height. The GridPane for the day
 * cells contains always 6 rows, each for one week (6 weeks to make sure that always the complete month can
 * be displayed.) The GridPane for the day cells uses all the available vertical space.
 *
 * @author Stefan Saring
 */
public class CalendarControl extends VBox {

    private static final int GRIDS_COLUMN_COUNT = 8;
    private static final int GRID_DAYS_ROW_COUNT = 6;

    private static final String[] DEFAULT_COLUMN_NAMES = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su", "Sum"};

    private GridPane gridHeaderCells;
    private GridPane gridDayCells;

    private CalendarHeaderCell[] headerCells = new CalendarHeaderCell[GRIDS_COLUMN_COUNT];
    private CalendarDayCell[] dayCells = new CalendarDayCell[7 * GRID_DAYS_ROW_COUNT];
    private CalendarSummaryCell[] summaryCells = new CalendarSummaryCell[GRID_DAYS_ROW_COUNT];

    private int displayedMonth;
    private int displayedYear;
    private boolean weekStartsSunday;

    private String[] columnNames;

    private CalendarDataProvider calendarDataProvider;

    private ObjectProperty<IdObject> selectedEntry = new SimpleObjectProperty<>();

    private ContextMenu contextMenu;
    private LocalDate dateOfContextMenu;

    /**
     * Standard c'tor.
     */
    public CalendarControl() {
        final LocalDate today = LocalDate.now();
        displayedMonth = today.getMonthValue();
        displayedYear = today.getYear();
        weekStartsSunday = false;

        setupLayout();
        setupListeners();
        updateContent();
    }

    /**
     * Sets the names to be displayed in the calendar header cells. This method can be used to
     * specify localized names, otherwise the calendar displays the default english names.
     *
     * @param columnNames header names for the 8 columns (monday - sunday + summary)
     */
    public void setColumnNames(final String[] columnNames) {
        if (columnNames != null && columnNames.length != GRIDS_COLUMN_COUNT) {
            throw new IllegalArgumentException("Array must contain names for 8 columns!");
        }
        this.columnNames = columnNames;
    }

    /**
     * Sets the provider which provides the entries to be shown in the calendar.
     *
     * @param calendarDataProvider calendar entry provider
     */
    public void setCalendarDataProvider(final CalendarDataProvider calendarDataProvider) {
        this.calendarDataProvider = calendarDataProvider;
    }

    /**
     * Updates the calendar to show the specified month. An existing entry selection will be removed.
     *
     * @param year             year of month to show
     * @param month            month to show
     * @param weekStartsSunday flag whether the week starts on sunday or monday
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

    /**
     * Returns the property which provides the selected entry or null when no entry is selected
     *
     * @return property of the selected entry
     */
    public ObjectProperty<IdObject> selectedEntryProperty() {
        return selectedEntry;
    }

    /**
     * Sets the listener for handling actions on the calendar.
     *
     * @param calendarActionListener listener implementation
     */
    public void setCalendarActionListener(final CalendarActionListener calendarActionListener) {
        Stream.of(dayCells).forEach(dayCell -> dayCell.setCalendarActionListener(calendarActionListener));
    }

    /**
     * Sets the context menu to be displayed for this calendar control.
     *
     * @param contextMenu context menu to show (or null for removing)
     */
    public void setContextMenu(final ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    /**
     * Returns the date of the cell on which the current context menu has been displayed or null,
     * when it was not displayed on a day cell.
     *
     * @return date or null
     */
    public LocalDate getDateOfContextMenu() {
        return dateOfContextMenu;
    }

    private void setupLayout() {

        // create GridPanes for header and day cells and add it to the VBox
        gridHeaderCells = new GridPane();
        gridDayCells = new GridPane();

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
        for (int i = 0; i < headerCells.length; i++) {
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

        // create day cells and add them to the days GridPane
        for (int row = 0; row < GRID_DAYS_ROW_COUNT; row++) {
            for (int column = 0; column < 7; column++) {
                final CalendarDayCell dayCell = new CalendarDayCell();
                dayCells[(row * 7) + column] = dayCell;
                gridDayCells.add(dayCell, column, row);
            }
        }

        // create weekly summary cells and add them to the days GridPane
        for (int row = 0; row < summaryCells.length; row++) {
            final CalendarSummaryCell summaryCell = new CalendarSummaryCell();
            summaryCells[row] = summaryCell;
            gridDayCells.add(summaryCell, GRIDS_COLUMN_COUNT - 1, row);
        }
    }

    private void setupListeners() {

        // setup an entry selection listener on all CalendarDayCells:
        // it removes any previous entry selections and stores the selected entry
        final CalendarDayCell.CalendarEntrySelectionListener listener = (calendarEntry, selected) -> {
            if (selected) {
                Stream.of(dayCells).forEach(dayCell -> dayCell.removeSelectionExcept(calendarEntry));
            }

            selectedEntry.set(selected ? calendarEntry.getEntry() : null);
        };

        // setup handler for removing the current selection when the user clicks in empty day cell area
        final EventHandler<MouseEvent> dayCellPressedHandler = event -> removeSelection();

        Stream.of(dayCells).forEach(dayCell -> {
            dayCell.setCalendarEntrySelectionListener(listener);
            dayCell.addEventHandler(MouseEvent.MOUSE_PRESSED, dayCellPressedHandler);
        });

        // display context menu when requested (Pane classes have no convenience method setContextMenu())
        setOnContextMenuRequested(event -> {
            if (contextMenu != null) {
                dateOfContextMenu = getDateAtScreenPosition(event.getScreenX(), event.getScreenY());
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
                event.consume();
            }
        });

        // close context menu whenever the user clicks somewhere else on the calendar
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (contextMenu != null && contextMenu.isShowing()) {
                contextMenu.hide();
            }
        });
    }

    private void updateContent() {
        selectedEntry.set(null);

        updateHeaderCells();
        updateDayCells();
        updateSummaryCells();
    }

    /**
     * Updates the content of the header cell depending on the current week start day.
     */
    private void updateHeaderCells() {
        final int indexSunday = weekStartsSunday ? 0 : 6;
        final String[] columnHeaderNames = columnNames == null ? DEFAULT_COLUMN_NAMES : columnNames;

        if (weekStartsSunday) {
            headerCells[0].setText(columnHeaderNames[6], true);
            for (int i = 1; i < 7; i++) {
                headerCells[i].setText(columnHeaderNames[i - 1], false);
            }
            headerCells[7].setText(columnHeaderNames[7], false);
        } else {
            for (int i = 0; i < columnHeaderNames.length; i++) {
                headerCells[i].setText(columnHeaderNames[i], indexSunday == i);
            }
        }
    }

    /**
     * Updates the content of all day cells for the displayed month and year.
     */
    private void updateDayCells() {
        LocalDate currentCellDate = getFirstDisplayedDay();

        for (int i = 0; i < dayCells.length; i++) {
            final boolean dateOfDisplayedMonth = currentCellDate.getMonthValue() == displayedMonth;
            dayCells[i].setDate(currentCellDate, dateOfDisplayedMonth);

            if (calendarDataProvider != null) {
                final List<CalendarEntry> entries = calendarDataProvider.getCalendarEntriesForDate(currentCellDate);
                dayCells[i].setEntries(entries);
            }

            currentCellDate = currentCellDate.plus(1, ChronoUnit.DAYS);
        }
    }

    /**
     * Updates the content of all summary cells for the displayed weeks.
     */
    private void updateSummaryCells() {

        for (int row = 0; row < summaryCells.length; row++) {

            final LocalDate dateWeekStart = dayCells[row * 7].getDate();
            final LocalDate dateWeekEnd = dayCells[row * 7 + 6].getDate();

            // TODO test with dateWeekStart with weekStartsSunday true/false
            // get week number for a date in the middle of the week (otherwise problems with JSR 310
            // DateTime API, it sometimes returns week ranges 53, 0, 1 or 53, 2, 3)
            final LocalDate dateWeekMiddle = dateWeekStart.plusDays(3);
            final int weekNr = Date310Utils.getWeekNumber(dateWeekMiddle, weekStartsSunday);
            summaryCells[row].setNumber(weekNr);

            // get and update summary entries for date range
            if (calendarDataProvider != null) {
                final List<String> summaryLines = calendarDataProvider.getSummaryForDateRange( //
                        dateWeekStart, dateWeekEnd);
                summaryCells[row].setEntries(summaryLines);
            }
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

    /**
     * Returns the date of the calendar day cell at the specified screen position.
     *
     * @param screenX X position in screen
     * @param screenY Y position in screen
     * @return the date or null when there is no day cell at this position
     */
    private LocalDate getDateAtScreenPosition(final double screenX, final double screenY) {
        for (CalendarDayCell dayCell : dayCells) {
            final Point2D localPosition = dayCell.screenToLocal(screenX, screenY);
            if (dayCell.getBoundsInLocal().contains(localPosition)) {
                return dayCell.getDate();
            }
        }
        return null;
    }
}
