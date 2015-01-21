package de.saring.util.gui.javafx.control.calendar;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import de.saring.util.Date310Utils;
import de.saring.util.data.IdObject;

/**
 * Skin implementation of the {@link de.saring.util.gui.javafx.control.calendar.CalendarControl} custom control which
 * displays a calendar and entries for one month. The skin class contains the view-part of the controller, the control
 * class contains the controller (interface for the control client).<br/>
 * The view contains cells for all the days of the month, each cell contains the calendar entries for that day.<br/>
 * The layout uses a VBox which contains two GridPanes, one for the header cells (weekday names), the other for the
 * day cells (for all days of the displayed month and parts of the previous and next month).<br/>
 * Both GridPanes contains 8 columns of same width, 7 for all days of a week (Sunday - Saturday or Monday - Sunday) and
 * one for the weekly summary.<br/>
 * The GridPane for the header cells contains only one row with a fixed height. The GridPane for the day cells contains
 * always 6 rows, each for one week (6 weeks to make sure that always the complete month can be displayed.) The
 * GridPane for the day cells uses all the available vertical space.
 *
 * @author Stefan Saring
 */
public class CalendarControlSkin extends SkinBase<CalendarControl> implements CalendarControl.CalendarSelector {

    private VBox controlRoot;

    private GridPane gridHeaderCells;
    private GridPane gridDayCells;

    private CalendarHeaderCell[] headerCells = new CalendarHeaderCell[CalendarControl.GRIDS_COLUMN_COUNT];
    private CalendarDayCell[] dayCells = new CalendarDayCell[7 * CalendarControl.GRID_DAYS_ROW_COUNT];
    private CalendarSummaryCell[] summaryCells = new CalendarSummaryCell[CalendarControl.GRID_DAYS_ROW_COUNT];

    /**
     * Standard c'tor.
     *
     * @param calendarControl CalendarControl instance
     */
    public CalendarControlSkin(final CalendarControl calendarControl) {
        super(calendarControl);
        calendarControl.setCalendarSelector(this);

        setupLayout();
        setupListeners();
        updateContent();
    }

    @Override
    public void selectEntry(final IdObject entry) {
        for (CalendarDayCell dayCell : dayCells) {
            if (dayCell.selectEntry(entry)) {
                break;
            }
        }
    }

    @Override
    public void removeSelection() {
        Stream.of(dayCells).forEach(dayCell -> dayCell.removeSelectionExcept(null));
    }

    private void setupLayout() {

        // create GridPanes for header and day cells
        gridHeaderCells = new GridPane();
        gridDayCells = new GridPane();

        // define column constraints for both GridPanes, all 8 columns must have the same width
        for (int column = 0; column < CalendarControl.GRIDS_COLUMN_COUNT; column++) {
            final ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100 / (double) CalendarControl.GRIDS_COLUMN_COUNT);
            gridHeaderCells.getColumnConstraints().add(columnConstraints);
            gridDayCells.getColumnConstraints().add(columnConstraints);
        }

        // define row constraint for header GridPane (one row with a fixed height)
        final RowConstraints headerRowConstraints = new RowConstraints();
        gridHeaderCells.getRowConstraints().add(headerRowConstraints);

        // define row constraints for days GridPane (6 rows with same height which are using all the available space)
        for (int row = 0; row < CalendarControl.GRID_DAYS_ROW_COUNT; row++) {
            final RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setValignment(VPos.TOP);
            rowConstraints.setPercentHeight((100 / (double) (CalendarControl.GRID_DAYS_ROW_COUNT)));
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
        for (int row = 0; row < CalendarControl.GRID_DAYS_ROW_COUNT; row++) {
            for (int column = 0; column < 7; column++) {
                final CalendarDayCell dayCell = new CalendarDayCell();
                dayCells[(row * 7) + column] = dayCell;
                gridDayCells.add(dayCell, column, row);
            }
        }

        // create day cells and add them to the days GridPane
        for (int row = 0; row < CalendarControl.GRID_DAYS_ROW_COUNT; row++) {
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
            gridDayCells.add(summaryCell, CalendarControl.GRIDS_COLUMN_COUNT - 1, row);
        }

        // create the VBox root pane and insert the header an cell grid panes
        controlRoot = new VBox();
        getChildren().add(controlRoot);

        VBox.setVgrow(gridHeaderCells, Priority.NEVER);
        VBox.setVgrow(gridDayCells, Priority.ALWAYS);
        controlRoot.getChildren().addAll(gridHeaderCells, gridDayCells);
    }

    private void setupListeners() {

        // update the calendar content whenever the displayed date property in the control changes
        getSkinnable().displayedDateProperty().addListener((observable, oldValue, newValue) -> updateContent());

        // set the calendar action listener in all day cells and update them whenever the specified listener changes
        setCalendarActionListenerInDayCells(getSkinnable().calendarActionListenerProperty().get());
        getSkinnable().calendarActionListenerProperty().addListener((observable, oldValue, newValue) -> //
                setCalendarActionListenerInDayCells(newValue));

        // setup an entry selection listener on all CalendarDayCells:
        // it removes any previous entry selections and updates the selected entry property in the control
        final CalendarDayCell.CalendarEntrySelectionListener listener = (calendarEntry, selected) -> {
            if (selected) {
                Stream.of(dayCells).forEach(dayCell -> dayCell.removeSelectionExcept(calendarEntry));
            }

            getSkinnable().selectedEntryProperty().set(selected ? calendarEntry.getEntry() : null);
        };

        // setup handler for removing the current selection when the user clicks in empty day cell area
        final EventHandler<MouseEvent> dayCellPressedHandler = event -> removeSelection();

        Stream.of(dayCells).forEach(dayCell -> {
            dayCell.setCalendarEntrySelectionListener(listener);
            dayCell.addEventHandler(MouseEvent.MOUSE_PRESSED, dayCellPressedHandler);
        });

        // when the calendar context menu has been requested:
        // - store the date of the day cell on which the menu has been displayed
        // - select the calendar entry at mouse position (not done automatically when the menu is already shown)
        getSkinnable().setOnContextMenuRequested(event -> {
            final LocalDate dateOfContextMenu = getDateAtScreenPosition(event.getScreenX(), event.getScreenY());
            getSkinnable().dateOfContextMenuProperty().set(dateOfContextMenu);
            selectCalendarEntryAtScreenPosition(event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    private void setCalendarActionListenerInDayCells(final CalendarActionListener calendarActionListener) {
        Stream.of(dayCells).forEach(dayCell -> dayCell.setCalendarActionListener(calendarActionListener));
    }

    /**
     * Updates the content of the calendar component (all header, day and summary cells).
     */
    private void updateContent() {
        updateHeaderCells();
        updateDayCells();
        updateSummaryCells();
    }

    /**
     * Updates the content of the header cell depending on the current week start day.
     */
    private void updateHeaderCells() {
        final boolean weekStartsSunday = getSkinnable().displayedDateProperty().get().isWeekStartsSunday();
        final int indexSunday = weekStartsSunday ? 0 : 6;
        final String[] columnNames = getSkinnable().getColumnNames();

        if (weekStartsSunday) {
            headerCells[0].setText(columnNames[6], true);
            for (int i = 1; i < 7; i++) {
                headerCells[i].setText(columnNames[i - 1], false);
            }
            headerCells[7].setText(columnNames[7], false);
        } else {
            for (int i = 0; i < columnNames.length; i++) {
                headerCells[i].setText(columnNames[i], indexSunday == i);
            }
        }
    }

    /**
     * Updates the content of all day cells for the displayed month and year.
     */
    private void updateDayCells() {
        LocalDate currentCellDate = getSkinnable().getFirstDisplayedDay();
        final int displayedMonth = getSkinnable().displayedDateProperty().get().getMonth();

        for (int i = 0; i < dayCells.length; i++) {
            final boolean dateOfDisplayedMonth = currentCellDate.getMonthValue() == displayedMonth;
            dayCells[i].setDate(currentCellDate, dateOfDisplayedMonth);

            final CalendarDataProvider dataProvider = getSkinnable().getCalendarDataProvider();
            if (dataProvider != null) {
                final List<CalendarEntry> entries = dataProvider.getCalendarEntriesForDate(currentCellDate);
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

            final int weekNr = Date310Utils.getWeekNumber(dateWeekStart, //
                    getSkinnable().displayedDateProperty().get().isWeekStartsSunday());
            summaryCells[row].setNumber(weekNr);

            // get and update summary entries for date range
            final CalendarDataProvider dataProvider = getSkinnable().getCalendarDataProvider();
            if (dataProvider != null) {
                final List<String> summaryLines = dataProvider.getSummaryForDateRange(dateWeekStart, dateWeekEnd);
                summaryCells[row].setEntries(summaryLines);
            }
        }
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

    private void selectCalendarEntryAtScreenPosition(final double screenX, final double screenY) {
        removeSelection();

        for (CalendarDayCell dayCell : dayCells) {
            final CalendarEntry entryAtScreenPosition = dayCell.getEntryAtScreenPosition(screenX, screenY);
            if (entryAtScreenPosition != null) {
                selectEntry(entryAtScreenPosition.getEntry());
                break;
            }
        }
    }
}
