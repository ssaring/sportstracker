package de.saring.sportstracker.gui.views.calendarview;

import java.time.LocalDate;

import de.saring.sportstracker.gui.views.ViewPrinter;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.views.AbstractEntryViewController;
import de.saring.util.AppResources;
import de.saring.util.data.IdDateObject;
import de.saring.util.data.IdObject;
import de.saring.util.gui.javafx.control.calendar.CalendarActionListener;
import de.saring.util.gui.javafx.control.calendar.CalendarControl;
import de.saring.util.gui.javafx.control.calendar.CalendarEntry;

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
     * @param viewPrinter the printer of the SportsTracker views
     */
    @Inject
    public CalendarViewController(final STContext context, final STDocument document, final ViewPrinter viewPrinter) {
        super(context, document, viewPrinter);
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
    protected String getFxmlFilename() {
        return "/fxml/views/CalendarView.fxml";
    }

    @Override
    protected void setupView() {
        setupCalendarControl();
        setCalendarActionListener();
        setupCalendarContextMenu();

        // bind month and year labels to current values
        displayedMonth.addListener((observable, oldValue, newValue) -> laDisplayedMonth.setText( //
                getContext().getResources().getString("st.calview.months." + newValue.intValue())));
        laDisplayedYear.textProperty().bind(displayedYear.asString());

        // display the current day at startup
        onToday(null);
    }

    private void setupCalendarControl() {
        calendarControl = new CalendarControl();
        calendarControl.setCalendarDataProvider(new CalendarDataProviderImpl(getContext(), getDocument()));
        spCalendar.getChildren().addAll(calendarControl);

        // set localized column header names
        final AppResources resources = getContext().getResources();
        calendarControl.setColumnNames(new String[] { resources.getString("st.valview.weekdays.monday"), //
                resources.getString("st.valview.weekdays.tuesday"), //
                resources.getString("st.valview.weekdays.wednesday"), //
                resources.getString("st.valview.weekdays.thursday"), //
                resources.getString("st.valview.weekdays.friday"), //
                resources.getString("st.valview.weekdays.saturday"), //
                resources.getString("st.valview.weekdays.sunday"), //
                resources.getString("st.valview.week_sum") });

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

    private void setCalendarActionListener() {
        calendarControl.calendarActionListenerProperty().set(new CalendarActionListener() {

            @Override
            public void onCalendarDayAction(final LocalDate date) {
                // execute action 'Add Exercise' for this date when the user double clicks a calendar day cell
                addExerciseForDate(date);
            }

            @Override
            public void onCalendarEntryAction(final CalendarEntry calendarEntry) {
                // execute action 'Edit Entry' when the user double clicks a calendar entry
                getController().onEditEntry(null);
            }

            @Override
            public void onDraggedFileDroppedOnCalendarDay(final String filePath) {
                getController().onAddExerciseForDroppedHrmFile(filePath);
            }

            @Override
            public void onDraggedFileDroppedOnCalendarEntry(final IdDateObject entry, final String filePath) {
                if (entry instanceof Exercise) {
                    getController().onAssignDroppedHrmFileToExercise(filePath, (Exercise) entry);
                } else {
                    // the target entry was not an exercise -> add a new exercise for the dropped entry
                    getController().onAddExerciseForDroppedHrmFile(filePath);
                }
            }
        });
    }

    /**
     * Sets up the context menu for the calendar control. Unfortunately it can't be defined in
     * FXML, the Pane classes does not support context menus directly.
     */
    private void setupCalendarContextMenu() {
        final BooleanBinding bindingNoEntrySelected = Bindings.isNull(calendarControl.selectedEntryProperty());

        final MenuItem miCtxAddExercise = createContextMenuItem( //
                "miCtxAddExercise", "st.view.exercise_add.Action.text", //
                event -> addExerciseForDate(calendarControl.dateOfContextMenuProperty().get()));

        final MenuItem miCtxAddNote = createContextMenuItem( //
                "miCtxAddNote", "st.view.note_add.Action.text", //
                event -> addNoteForDate(calendarControl.dateOfContextMenuProperty().get()));

        final MenuItem miCtxAddWeight = createContextMenuItem( //
                "miCtxAddWeight", "st.view.weight_add.Action.text", //
                event -> addWeightForDate(calendarControl.dateOfContextMenuProperty().get()));

        final MenuItem miCtxEditEntry = createContextMenuItem( //
                "miCtxEditEntry", "st.view.entry_edit.Action.text", //
                event -> getController().onEditEntry(event));
        miCtxEditEntry.disableProperty().bind(bindingNoEntrySelected);

        final MenuItem miCtxCopyEntry = createContextMenuItem( //
                "miCtxCopyEntry", "st.view.entry_copy.Action.text", //
                event -> getController().onCopyEntry(event));
        miCtxCopyEntry.disableProperty().bind(bindingNoEntrySelected);

        final MenuItem miCtxDeleteEntry = createContextMenuItem( //
                "miCtxDeleteEntry", "st.view.entry_delete.Action.text", //
                event -> getController().onDeleteEntry(event));
        miCtxDeleteEntry.disableProperty().bind(bindingNoEntrySelected);

        calendarControl.setContextMenu(new ContextMenu( //
                miCtxAddExercise, miCtxAddNote, miCtxAddWeight, miCtxEditEntry, miCtxCopyEntry, miCtxDeleteEntry));
    }

    private MenuItem createContextMenuItem(final String id, final String resourceKey, //
            final EventHandler<ActionEvent> handler) {
        final MenuItem menuItem = new MenuItem(getContext().getResources().getString(resourceKey));
        menuItem.setId(id);
        menuItem.setOnAction(handler);
        menuItem.getStyleClass().add("contextMenuItem");
        return menuItem;
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
            return new int[] { selectedEntry.getId() };
        }
    }

    private void addExerciseForDate(final LocalDate date) {
        getController().setDateForNewEntries(date);
        getController().onAddExercise(null);
        getController().setDateForNewEntries(null);
    }

    private void addNoteForDate(final LocalDate date) {
        getController().setDateForNewEntries(date);
        getController().onAddNote(null);
        getController().setDateForNewEntries(null);
    }

    private void addWeightForDate(final LocalDate date) {
        getController().setDateForNewEntries(date);
        getController().onAddWeight(null);
        getController().setDateForNewEntries(null);
    }
}
