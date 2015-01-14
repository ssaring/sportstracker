package de.saring.sportstracker.gui.views.calendarview;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

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
    private StackPane spCalendar;

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
        // TODO
    }

    /**
     * Action handler for showing the previous month in the calendar.
     */
    @FXML
    private void onPreviousMonth(final ActionEvent event) {
        // TODO
    }

    /**
     * Action handler for showing the next month in the calendar.
     */
    @FXML
    private void onNextMonth(final ActionEvent event) {
        // TODO
    }

    /**
     * Action handler for showing the previous year in the calendar.
     */
    @FXML
    private void onPreviousYear(final ActionEvent event) {
        // TODO
    }

    /**
     * Action handler for showing the next year in the calendar.
     */
    @FXML
    private void onNextYear(final ActionEvent event) {
        // TODO
    }

    /**
     * Action handler for showing the current day in the calendar.
     */
    @FXML
    private void onToday(final ActionEvent event) {
        // TODO
    }
}
