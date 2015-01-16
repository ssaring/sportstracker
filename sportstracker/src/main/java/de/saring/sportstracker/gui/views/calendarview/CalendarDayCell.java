package de.saring.sportstracker.gui.views.calendarview;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import de.saring.util.data.IdObject;

/**
 * TODO
 *
 * @author Stefan Saring
 */
class CalendarDayCell extends VBox {

    private Label laDay;

    private LocalDate date;
    private boolean displayedMonth;

    private List<CalendarEntryLabel> calendarEntryLabels = new ArrayList<>();

    private CalendarEntrySelectionListener calendarEntrySelectionListener;
    private CalendarActionListener calendarActionListener;

    /**
     * Standard c'tor.
     */
    public CalendarDayCell() {
        setPadding(new Insets(4));
        setSpacing(4);
        // TODO use css
        setStyle("-fx-border-color: black; -fx-border-insets: -1");
        setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

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

    /**
     * Displays the specified calendar entries inside this day cell. All previous entries
     * will be removed before.
     *
     * @param entries list of calendar entries (must not be null)
     */
    public void setEntries(final List<CalendarEntry> entries) {

        if (getChildren().size() > 1) {
            getChildren().remove(1, getChildren().size());
        }

        calendarEntryLabels = entries.stream() //
                .map(entry -> //
                        new CalendarEntryLabel(entry, calendarEntrySelectionListener, calendarActionListener)) //
                .collect(Collectors.toList());

        getChildren().addAll(calendarEntryLabels);
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
     * Sets the listener for handling actions on the calendar entries (double click). The new
     * listener is not set for already displayed calendar entries.
     *
     * @param calendarActionListener listener implementation
     */
    public void setCalendarActionListener(final CalendarActionListener calendarActionListener) {
        this.calendarActionListener = calendarActionListener;

        // setup action listener for double clicks on the day cell (not on entries)
        if (calendarActionListener == null) {
            setOnMouseClicked(null);
        } else {
            setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    calendarActionListener.onCalendarDayAction(date);
                }
            });
        }
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

    /**
     * TODO
     *
     * @author Stefan Saring
     */
    interface CalendarEntrySelectionListener {

        /**
         * TODO
         *
         * @param calendarEntry
         * @param selected
         */
        void calendarEntrySelectionChanged(CalendarEntry calendarEntry, boolean selected);
    }

    /**
     * TODO
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

            // update selection status when the user clicks on the entry label and
            // notify selection listener, if registered
            addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                event.consume();

                if (!selected.get()) {
                    selected.set(true);
                }

                if (selectionListener != null) {
                    selectionListener.calendarEntrySelectionChanged(entry, selected.get());
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
