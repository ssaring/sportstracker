package de.saring.util.gui.javafx.control.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

import de.saring.util.data.IdObject;

/**
 * Calendar cell implementation which shows a single day cell. It displays the day number
 * and the entries of the day below.
 *
 * @author Stefan Saring
 */
class CalendarDayCell extends AbstractCalendarCell {

    private LocalDate date;
    private boolean displayedMonth;

    private List<CalendarEntryLabel> calendarEntryLabels = new ArrayList<>();

    private CalendarEntrySelectionListener calendarEntrySelectionListener;
    private CalendarActionListener calendarActionListener;

    /**
     * Standard c'tor.
     */
    public CalendarDayCell() {
        super(Color.WHITE);
        setupListeners();
    }

    /**
     * Returns the date of this day cell.
     *
     * @return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of this day cell
     *
     * @param date           new date
     * @param displayedMonth flag whether this date is inside the currently displayed month
     */
    public void setDate(final LocalDate date, final boolean displayedMonth) {
        this.date = date;
        this.displayedMonth = displayedMonth;

        updateDayLabel();
    }

    /**
     * Displays the specified calendar entries inside this day cell.
     *
     * @param entries list of calendar entries (must not be null)
     */
    public void setEntries(final List<CalendarEntry> entries) {

        calendarEntryLabels = entries.stream() //
                .map(entry -> new CalendarEntryLabel(entry, calendarEntrySelectionListener, calendarActionListener)) //
                .collect(Collectors.toList());

        updateEntryLabels(calendarEntryLabels);
    }

    /**
     * Sets the listener for the selection status of calendar entries. The new listener is not
     * set for already displayed calendar entries.
     *
     * @param selectionListener listener implementation
     */
    public void setCalendarEntrySelectionListener(final CalendarEntrySelectionListener selectionListener) {
        this.calendarEntrySelectionListener = selectionListener;
    }

    /**
     * Sets the listener for handling actions on the calendar entries.
     *
     * @param calendarActionListener listener implementation
     */
    public void setCalendarActionListener(final CalendarActionListener calendarActionListener) {
        this.calendarActionListener = calendarActionListener;
    }

    /**
     * Removes all calendar entry selections of this day cell, except the specified calendar entry.
     *
     * @param calendarEntryExcept entry for which the selection must not be removed (can be null)
     */
    public void removeSelectionExcept(final CalendarEntry calendarEntryExcept) {
        final IdObject entryExcept = calendarEntryExcept == null ? null : calendarEntryExcept.getEntry();

        calendarEntryLabels.stream() //
                .filter(entryLabel -> entryLabel.selected.get() && //
                        (entryExcept == null || !entryExcept.equals(entryLabel.entry.getEntry()))) //
                .forEach(calendarEntryLabel -> calendarEntryLabel.selected.set(false));
    }

    /**
     * Selects the specified entry, if it is displayed in this day cell.
     *
     * @param entry entry to select
     * @return true when the entry was selected
     */
    public boolean selectEntry(final IdObject entry) {

        for (CalendarEntryLabel calendarEntryLabel : calendarEntryLabels) {
            if (calendarEntryLabel.entry.getEntry().equals(entry)) {
                calendarEntryLabel.selected.set(true);
                return true;
            }
        }
        return false;
    }

    private void setupListeners() {

        // setup action listener for double clicks on the day cell (not on entries)
        setOnMouseClicked(event -> {
            if (calendarActionListener != null && event.getClickCount() > 1) {
                calendarActionListener.onCalendarDayAction(date);
            }
        });

        // all day cells support drag&drap of ONE FILE in mode 'copy' ()
        setOnDragOver(event -> {
            final Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles() && dragboard.getFiles().size() == 1 && dragboard.getFiles().get(0).isFile()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        // notify action listener when a file has been dropped on the day cell or on a calendar entry
        setOnDragDropped(event -> {
            final Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasFiles() && calendarActionListener != null) {
                success = true;
                final String filePath = dragboard.getFiles().get(0).getAbsolutePath();

                final CalendarEntry droppedOnEntry = getEntryAtScreenPosition(event.getScreenX(), event.getScreenY());
                if (droppedOnEntry == null) {
                    calendarActionListener.onDraggedFileDroppedOnCalendarDay(filePath);
                } else {
                    calendarActionListener.onDraggedFileDroppedOnCalendarEntry(droppedOnEntry.getEntry(), filePath);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void updateDayLabel() {
        setNumber(date.getDayOfMonth());

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

        getNumberLabel().setTextFill(color);
        getNumberLabel().setStyle(style);
    }

    /**
     * Returns the CalendarEntry at the specified screen position or null when there is no entry.
     *
     * @param screenX X position on screen
     * @param screenY Y position on screen
     * @return CalendarEntry or null
     */
    private CalendarEntry getEntryAtScreenPosition(final double screenX, final double screenY) {
        for (CalendarEntryLabel calendarEntryLabel : calendarEntryLabels) {
            final Point2D localPosition = calendarEntryLabel.screenToLocal(screenX, screenY);
            if (calendarEntryLabel.getBoundsInLocal().contains(localPosition)) {
                return calendarEntryLabel.entry;
            }
        }
        return null;
    }

    /**
     * Listener interface for notification when the calendar entry selection changes.
     */
    interface CalendarEntrySelectionListener {

        /**
         * This method is called whenever the selection of a calendar entry changes (when
         * selected or deselected).
         *
         * @param calendarEntry entry of selection change
         * @param selected      true when the entry gets selected
         */
        void calendarEntrySelectionChanged(CalendarEntry calendarEntry, boolean selected);
    }

    /**
     * Custom label extension which displays a single entry inside a CalendarDayCell.
     * Calendar entries are selectable, the status is provided by the property {@code selected}.
     */
    private static class CalendarEntryLabel extends Label {

        private CalendarEntry entry;

        private BooleanProperty selected = new SimpleBooleanProperty(false);

        public CalendarEntryLabel(final CalendarEntry entry, final CalendarEntrySelectionListener selectionListener,
                                  final CalendarActionListener actionListener) {
            this.entry = entry;

            setMaxWidth(Double.MAX_VALUE);
            this.setText(entry.getText());

            if (entry.getToolTipText() != null) {
                this.setTooltip(new Tooltip(entry.getToolTipText()));
            }

            if (entry.getColor() != null) {
                this.setTextFill(entry.getColor());
            }

            // bind the background color to the selection status
            // TODO use CSS
            selected.addListener((observable, oldValue, newValue) -> //
                    setStyle("-fx-background-color: " + (newValue ? "lightskyblue;" : "transparent")));

            setupListeners(selectionListener, actionListener);
        }

        private void setupListeners(final CalendarEntrySelectionListener selectionListener,
                                    final CalendarActionListener actionListener) {

            // notify selection listener on changes (if registered)
            if (selectionListener != null) {
                selected.addListener((observable, oldValue, newValue) -> //
                        selectionListener.calendarEntrySelectionChanged(entry, newValue));
            }

            // update selection status when the user clicks on the entry label
            addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                event.consume();

                if (!selected.get()) {
                    selected.set(true);
                }
            });

            // register action listener for double clicks on the entry
            if (actionListener != null) {
                addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    event.consume();

                    if (event.getClickCount() > 1) {
                        actionListener.onCalendarEntryAction(entry);
                    }
                });
            }
        }
    }
}
