package de.saring.util.gui.javafx.control.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;

import de.saring.util.data.IdObject;

/**
 * Custom control which displays a calendar for one month. It contains cells for all the days of the month,
 * each cell contains the calendar entries for that day.<br/>
 * This class contains the controller-part of the control, the view-part is implemented in the skin class.
 *
 * @author Stefan Saring
 */
public class CalendarControl extends Control {

    static final int GRIDS_COLUMN_COUNT = 8;
    static final int GRID_DAYS_ROW_COUNT = 6;

    private static final String[] DEFAULT_COLUMN_NAMES = { "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su", "Sum" };

    private ObjectProperty<CalendarDate> displayedDate = new SimpleObjectProperty<>();

    private String[] columnNames;

    private CalendarDataProvider calendarDataProvider;

    private ObjectProperty<CalendarActionListener> calendarActionListener = new SimpleObjectProperty<>();

    private ObjectProperty<IdObject> selectedEntry = new SimpleObjectProperty<>();

    private ObjectProperty<LocalDate> dateOfContextMenu = new SimpleObjectProperty<>();


    /**
     * Standard c'tor.
     */
    public CalendarControl() {
        getStyleClass().add("calendar-control");

        final LocalDate today = LocalDate.now();
        displayedDate.set(new CalendarDate(today.getYear(), today.getMonthValue(), false));
    }

    /**
     * Returns the names to be displayed in the column headers.
     *
     * @return column names
     */
    public String[] getColumnNames() {
        return columnNames == null ? DEFAULT_COLUMN_NAMES : columnNames;
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
     * Returns the provider which provides the entries to be shown in the calendar.
     *
     * @return CalendarDataProvider
     */
    public CalendarDataProvider getCalendarDataProvider() {
        return calendarDataProvider;
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
     * @param year year of month to show
     * @param month month to show
     * @param weekStartsSunday flag whether the week starts on sunday or monday
     */
    public void updateCalendar(final int year, final int month, final boolean weekStartsSunday) {
        selectedEntry.set(null);
        displayedDate.set(new CalendarDate(year, month, weekStartsSunday));
    }

    /**
     * Returns the currently displayed year, month and week start data.
     *
     * @return CalendarMonth property
     */
    public ObjectProperty<CalendarDate> displayedDateProperty() {
        return displayedDate;
    }

    /**
     * Selects the specified entry, if it is currently displayed in the calendar.
     *
     * @param entry entry to select
     */
    public void selectEntry(final IdObject entry) {
        // TODO refactor
        ((CalendarControlSkin)getSkin()).selectEntry(entry);
    }

    /**
     * Removes the entry selection, if there is one.
     */
    public void removeSelection() {
        // TODO refactor
        if (getSkin() != null) {
            ((CalendarControlSkin) getSkin()).removeSelection();
        }
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
     * Returns the property of the listener for handling actions on the calendar.
     *
     * @return listener property
     */
    public ObjectProperty<CalendarActionListener> calendarActionListenerProperty() {
        return calendarActionListener;
    }

    /**
     * Returns the property for the date of the cell on which the current context menu has been displayed.
     * The property value is null, when the context menu was not displayed on a day cell.
     *
     * @return date property
     */
    public ObjectProperty<LocalDate> dateOfContextMenuProperty() {
        return dateOfContextMenu;
    }

    /**
     * Calculates the first displayed day in the calendar, depending whether week start is sunday or
     * monday. This is mostly a day of the previous month.
     *
     * @return first displayed day in the calendar
     */
    public LocalDate getFirstDisplayedDay() {
        final LocalDate firstDayOfMonth = LocalDate.of( //
                displayedDate.get().getYear(), displayedDate.get().getMonth(), 1);
        return firstDayOfMonth.with(TemporalAdjusters.previousOrSame( //
                displayedDate.get().isWeekStartsSunday() ? DayOfWeek.SUNDAY : DayOfWeek.MONDAY));
    }

    @Override
    protected String getUserAgentStylesheet() {
        return getClass().getResource("CalendarControl.css").toExternalForm();
    }

    /**
     * Value class for storing the year, month and week start to be shown in the calendar control.
     * They are bundled in this container, so it's possible to change them and update the calendar
     * in one run.
     */
    static class CalendarDate {

        private int year;
        private int month;
        private boolean weekStartsSunday;

        /**
         * C'tor.
         *
         * @param year year
         * @param month month
         * @param weekStartsSunday week start flag
         */
        public CalendarDate(final int year, final int month, final boolean weekStartsSunday) {
            this.year = year;
            this.month = month;
            this.weekStartsSunday = weekStartsSunday;
        }

        /**
         * @return year to display
         */
        public int getYear() {
            return year;
        }

        /**
         * @return month to display
         */
        public int getMonth() {
            return month;
        }

        /**
         * @return week start to use for display (sunday = true, monday = false)
         */
        public boolean isWeekStartsSunday() {
            return weekStartsSunday;
        }
    }
}
