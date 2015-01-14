package de.saring.sportstracker.gui.views.calendarview;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.views.AbstractEntryViewController;
import de.saring.util.data.IdObject;

/**
 * Controller class of the Calendar View, which displays all (or a filtered list) exercises, notes and
 * weights of the selected month. It also contains all the navigation controls for selecting the month.
 *
 * @author Stefan Saring
 */
@Singleton
public class CalendarViewController extends AbstractEntryViewController {

    @FXML
    private Label laCurrentMonth;

    @FXML
    private Label laCurrentYear;

    @FXML
    private StackPane spCalendar;

    /** The current displayed month. */
    private IntegerProperty currentMonth = new SimpleIntegerProperty();

    /** The current displayed year. */
    private IntegerProperty currentYear = new SimpleIntegerProperty();

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     * @param controller the SportsTracker UI controller
     */
    @Inject
    public CalendarViewController(final STContext context, final STDocument document, final STController controller) {
        super(context, document, controller);
    }

    @Override
    public ViewType getViewType() {
        return ViewType.CALENDAR;
    }

    @Override
    public void updateView() {
        // TODO
    }

    @Override
    public void selectEntry(final IdObject entry) {
        // TODO
    }

    @Override
    public void removeSelection() {
        // TODO
    }

    @Override
    public void print() throws STException {
        // TODO
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/views/CalendarView.fxml";
    }

    @Override
    protected void setupView() {

        // TODO is there a more elegant converter way?
        laCurrentMonth.textProperty().bindBidirectional(currentMonth, new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return getContext().getResources().getString("st.calview.months." + number.intValue());
            }

            @Override
            public Number fromString(String string) {
                throw new IllegalArgumentException();
            }
        });

        laCurrentYear.textProperty().bind(currentYear.asString());

        // TODO

        // display the current day at startup
        onToday(null);
    }

    /**
     * Action handler for showing the previous month in the calendar.
     */
    @FXML
    private void onPreviousMonth(final ActionEvent event) {
        if (currentMonth.get() > 1) {
            currentMonth.set(currentMonth.get() - 1);
        } else {
            currentMonth.set(12);
            currentYear.set(currentYear.get() - 1);
        }
        updateView();
    }

    /**
     * Action handler for showing the next month in the calendar.
     */
    @FXML
    private void onNextMonth(final ActionEvent event) {
        if (currentMonth.get() < 12) {
            currentMonth.set(currentMonth.get() + 1);
        } else {
            currentMonth.set(1);
            currentYear.set(currentYear.get() + 1);
        }
        updateView();
    }

    /**
     * Action handler for showing the previous year in the calendar.
     */
    @FXML
    private void onPreviousYear(final ActionEvent event) {
        currentYear.set(currentYear.get() - 1);
        updateView();
    }

    /**
     * Action handler for showing the next year in the calendar.
     */
    @FXML
    private void onNextYear(final ActionEvent event) {
        currentYear.set(currentYear.get() + 1);
        updateView();
    }

    /**
     * Action handler for showing the current day in the calendar.
     */
    @FXML
    private void onToday(final ActionEvent event) {
        final LocalDate today = LocalDate.now();
        currentMonth.set(today.getMonthValue());
        currentYear.set(today.getYear());
        updateView();
    }

    // TODO add date navigation by using the mouse wheel on the calendar
}
