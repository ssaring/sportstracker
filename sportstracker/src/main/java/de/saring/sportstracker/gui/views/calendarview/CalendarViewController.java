package de.saring.sportstracker.gui.views.calendarview;

import java.time.LocalDate;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.Weight;
import de.saring.util.data.IdDateObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private Label laDisplayedMonth;

    @FXML
    private Label laDisplayedYear;

    @FXML
    private StackPane spCalendar;

    private CalendarControl calendarControl;

    /**
     * The current displayed month.
     */
    private IntegerProperty displayedMonth = new SimpleIntegerProperty();

    /**
     * The current displayed year.
     */
    private IntegerProperty displayedYear = new SimpleIntegerProperty();

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
        calendarControl.updateCalendar(displayedYear.get(), displayedMonth.get(), //
                getDocument().getOptions().isWeekStartSunday());
    }

    @Override
    public int getSelectedExerciseCount() {
        return getSelectedExerciseIDs().length;
    }

    @Override
    public int[] getSelectedExerciseIDs() {
        return getSelectedEntryIdsOfClass(Exercise.class);
    }

    @Override
    public int getSelectedNoteCount() {
        return getSelectedNoteIDs().length;
    }

    @Override
    public int[] getSelectedNoteIDs() {
        return getSelectedEntryIdsOfClass(Note.class);
    }

    @Override
    public int getSelectedWeightCount() {
        return getSelectedWeightIDs().length;
    }

    @Override
    public int[] getSelectedWeightIDs() {
        return getSelectedEntryIdsOfClass(Weight.class);
    }

    @Override
    public void selectEntry(final IdObject entry) {
        if (entry instanceof IdDateObject) {
            IdDateObject dateEntry = (IdDateObject) entry;

            // set calendar to month/year of the entry
            displayedYear.set(dateEntry.getDateTime().getYear());
            displayedMonth.set(dateEntry.getDateTime().getMonthValue());
            updateView();

            calendarControl.selectEntry(dateEntry);
        }
    }

    @Override
    public void removeSelection() {
        calendarControl.removeSelection();
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
        setupCalendarControl();

        // bind month and year labels to current values
        displayedMonth.addListener((observable, oldValue, newValue) -> laDisplayedMonth.setText( //
                getContext().getResources().getString("st.calview.months." + newValue.intValue())));
        laDisplayedYear.textProperty().bind(displayedYear.asString());

        // display the current day at startup
        onToday(null);
    }

    private void setupCalendarControl() {
        calendarControl = new CalendarControl(getContext().getResources());
        calendarControl.setCalendarEntryProvider(new CalendarEntryProviderImpl(getContext(), getDocument()));
        spCalendar.getChildren().addAll(calendarControl);

        // update controller-actions and the status bar on selection changes
        calendarControl.selectedEntryProperty().addListener((observable, oldValue, newValue) -> //
                getController().updateActionsAndStatusBar());

        // scroll the displayed month when the user uses the mouse wheel on the calendar
        calendarControl.setOnScroll(event -> {
            if (event.getDeltaY() > 0) {
                onPreviousMonth(null);
            } else if (event.getDeltaY() < 0) {
                onNextMonth(null);
            }
        });
    }

    /**
     * Action handler for showing the previous month in the calendar.
     */
    @FXML
    private void onPreviousMonth(final ActionEvent event) {
        if (displayedMonth.get() > 1) {
            displayedMonth.set(displayedMonth.get() - 1);
        } else {
            displayedMonth.set(12);
            displayedYear.set(displayedYear.get() - 1);
        }
        updateView();
    }

    /**
     * Action handler for showing the next month in the calendar.
     */
    @FXML
    private void onNextMonth(final ActionEvent event) {
        if (displayedMonth.get() < 12) {
            displayedMonth.set(displayedMonth.get() + 1);
        } else {
            displayedMonth.set(1);
            displayedYear.set(displayedYear.get() + 1);
        }
        updateView();
    }

    /**
     * Action handler for showing the previous year in the calendar.
     */
    @FXML
    private void onPreviousYear(final ActionEvent event) {
        displayedYear.set(displayedYear.get() - 1);
        updateView();
    }

    /**
     * Action handler for showing the next year in the calendar.
     */
    @FXML
    private void onNextYear(final ActionEvent event) {
        displayedYear.set(displayedYear.get() + 1);
        updateView();
    }

    /**
     * Action handler for showing the current day in the calendar.
     */
    @FXML
    private void onToday(final ActionEvent event) {
        final LocalDate today = LocalDate.now();
        displayedMonth.set(today.getMonthValue());
        displayedYear.set(today.getYear());
        updateView();
    }

    /**
     * Returns an array with the ID's of the currently selected calendar entries of the
     * specified type (maximum count in the calendar view is 1).
     *
     * @return array of the selected CalendarEntry ID's (can be empty but not null)
     */
    private int[] getSelectedEntryIdsOfClass(final Class<? extends IdDateObject> clazz) {
        final IdObject selectedEntry = calendarControl.selectedEntryProperty().get();

        if ((selectedEntry == null) || (selectedEntry.getClass() != clazz)) {
            return new int[0];
        } else {
            return new int[]{ selectedEntry.getId() };
        }
    }

}
