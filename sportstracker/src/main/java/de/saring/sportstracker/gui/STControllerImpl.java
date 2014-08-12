package de.saring.sportstracker.gui;

import de.saring.exerciseviewer.gui.EVMain;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.dialogs.*;
import de.saring.sportstracker.gui.dialogsfx.AboutDialogController;
import de.saring.sportstracker.gui.views.EntryView;
import de.saring.util.data.IdDateObject;
import de.saring.util.data.IdDateObjectList;
import javafx.application.Platform;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains all controller (MVC) related functionality of the SportsTracker
 * application.
 *
 * @author Stefan Saring
 * @version 1.0
 */
@Singleton
public class STControllerImpl implements STController {
    private static final Logger LOGGER = Logger.getLogger(STControllerImpl.class.getName());

    private final STContext context;
    private final STDocument document;
    private final STView view;

    @Inject
    private Provider<ExerciseDialog> prExerciseDialog;
    @Inject
    private Provider<NoteDialog> prNoteDialog;
    @Inject
    private Provider<WeightDialog> prWeightDialog;
    @Inject
    private Provider<OptionsDialog> prOptionsDialog;
    @Inject
    private Provider<FilterDialog> prFilterDialog;
    @Inject
    private Provider<SportTypeListDialog> prSportTypeListDialog;
    @Inject
    private Provider<StatisticDialog> prStatisticDialog;
    @Inject
    private Provider<OverviewDialog> prOverviewDialog;
    @Inject
    private Provider<HRMFileOpenDialog> prHRMFileOpenDialog;
    @Inject
    private Provider<EVMain> prExerciseViewer;

    @Inject
    private Provider<AboutDialogController> prAboutDialogController;

    /**
     * The action map of the controller class.
     */
    private ActionMap actionMap;

    /**
     * The date to be set initially when the next entry will be added.
     */
    private LocalDate dateForNewEntries;

    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     * @param document the document component
     * @param view the view component
     */
    @Inject
    public STControllerImpl(STContext context, STDocument document, STView view) {
        this.context = context;
        this.document = document;
        this.view = view;
    }

    @Override
    public ActionMap getActionMap() {
        if (actionMap == null) {
            actionMap = context.getSAFContext().getActionMap(STControllerImpl.class, this);
        }
        return actionMap;
    }

    /**
     * Action for loading the application data inside a background task.
     *
     * @return the Task for the load action
     */
    @Action(name = ACTION_LOAD, block = Task.BlockingScope.WINDOW)
    public Task<Void, Void> load() {
        return new LoadTask();
    }

    /**
     * This class executes the loading action inside a background task. It also
     * checks the existence of all attached exercise files.
     */
    class LoadTask extends org.jdesktop.application.Task<Void, Void> {

        private List<Exercise> corruptExercises;

        public LoadTask() {
            super(context.getSAFContext().getApplication());
            setUserCanCancel(false);
        }

        @Override
        protected Void doInBackground() throws Exception {
            document.readApplicationData();
            corruptExercises = document.checkExerciseFiles();
            return null;
        }

        @Override
        protected void failed(Throwable cause) {
            super.failed(cause);
            context.showMessageDialog(context.getMainFrame(),
                    JOptionPane.ERROR_MESSAGE, "common.error", "st.main.error.load_data");
        }

        @Override
        protected void finished() {
            view.updateView();
            view.registerViewForDataChanges();
            displayCorruptExercises();
            askForDefiningSportTypes();
        }

        private void displayCorruptExercises() {
            if (corruptExercises != null && !corruptExercises.isEmpty()) {

                StringBuilder sb = new StringBuilder();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

                for (int i = 0; i < corruptExercises.size(); i++) {
                    if (i > 15) {
                        sb.append("...\n");
                        break;
                    }

                    sb.append(corruptExercises.get(i).getDateTime().format(dateTimeFormatter));
                    sb.append("\n");
                }

                context.showMessageDialog(context.getMainFrame(),
                        JOptionPane.WARNING_MESSAGE, "common.warning",
                        "st.main.error.missing_exercise_files", sb.toString());
            }
        }
    }

    /**
     * Action for saving data changes inside a background task.
     *
     * @return the Task for the save action
     */
    @Action(name = ACTION_SAVE, block = Task.BlockingScope.WINDOW)
    public Task<Void, Void> save() {
        return new SaveTask();
    }

    /**
     * This class executes the Save action inside a background task.
     */
    class SaveTask extends org.jdesktop.application.Task<Void, Void> {
        public SaveTask() {
            super(context.getSAFContext().getApplication());
            setUserCanCancel(false);
        }

        @Override
        protected Void doInBackground() throws Exception {
            executeSave();
            return null;
        }
    }

    /**
     * Executes the save of application data.
     *
     * @return true on success
     */
    private boolean executeSave() {
        try {
            document.storeApplicationData();
            return true;
        } catch (STException se) {
            LOGGER.log(Level.SEVERE, "Failed to store application data!", se);
            context.showMessageDialog(context.getMainFrame(), JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.main.error.save_data");
            return false;
        } finally {
            view.updateSaveAction();
        }
    }

    /**
     * Action for open the exercise HRM file in the ExerciseViewer sub-application (dialog).
     */
    @Action(name = ACTION_OPEN_EXERCISEVIEWER)
    public void openExerciseViewer() {

        // show file open dialog for HRM file selection
        File selectedFile = prHRMFileOpenDialog.get().selectHRMFile(document.getOptions(), null);
        if (selectedFile != null) {

            // start ExerciseViewer 
            EVMain pv = prExerciseViewer.get();
            pv.showExercise(selectedFile.getAbsolutePath(), document.getOptions(), false);
        }
    }

    /**
     * Action for printing the exercises of the current view.
     */
    @Action(name = ACTION_PRINT)
    public void print() {
        try {
            view.getCurrentView().print();
        } catch (STException se) {
            LOGGER.log(Level.WARNING, "Failed to print current view!", se);
            context.showMessageDialog(context.getMainFrame(), JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.main.error.print_view");
        }
    }

    /**
     * Action for quitting the SportsTracker application.
     */
    @Action(name = ACTION_QUIT)
    public void quitApplication() {
        context.getSAFContext().getApplication().exit();
    }

    @Override
    @Action(name = ACTION_EXERCISE_ADD)
    public void addExercise() {
        if (!checkForExistingSportTypes()) {
            return;
        }

        // start exercise dialog for a new created exercise
        Exercise newExercise = createNewExercise(dateForNewEntries);
        ExerciseDialog dlg = prExerciseDialog.get();
        dlg.setExercise(newExercise);
        context.showDialog(dlg);
    }

    @Override
    @Action(name = ACTION_NOTE_ADD)
    public void addNote() {
        // start Note dialog for a new created Note
        Note newNote = new Note(document.getNoteList().getNewID());
        newNote.setDateTime(getNoonDateTimeForDate(dateForNewEntries));

        NoteDialog dlg = prNoteDialog.get();
        dlg.setNote(newNote);
        context.showDialog(dlg);
    }

    @Override
    @Action(name = ACTION_WEIGHT_ADD)
    public void addWeight() {
        // start Weight dialog for a new created Weight
        Weight newWeight = new Weight(document.getWeightList().getNewID());
        newWeight.setDateTime(getNoonDateTimeForDate(dateForNewEntries));

        // initialize with the weight value of previous entry (if there is some)
        int weightCount = document.getWeightList().size();
        if (weightCount > 0) {
            Weight lastWeight = document.getWeightList().getAt(weightCount - 1);
            newWeight.setValue(lastWeight.getValue());
        }

        WeightDialog dlg = prWeightDialog.get();
        dlg.setWeight(newWeight);
        context.showDialog(dlg);
    }

    @Override
    public void setDateForNewEntries(LocalDate date) {
        this.dateForNewEntries = date;
    }

    @Override
    @Action(name = ACTION_ENTRY_EDIT)
    public void editEntry() {

        // start edit action depending on entry type
        if (view.getCurrentView().getSelectedExerciseCount() == 1) {
            editExercise(view.getCurrentView().getSelectedExerciseIDs()[0]);
        } else if (view.getCurrentView().getSelectedNoteCount() == 1) {
            editNote(view.getCurrentView().getSelectedNoteIDs()[0]);
        } else if (view.getCurrentView().getSelectedWeightCount() == 1) {
            editWeight(view.getCurrentView().getSelectedWeightIDs()[0]);
        }
    }

    /**
     * Starts Edit dialog for the specified exercise ID.
     *
     * @param exerciseID ID of the exercise entry
     */
    private void editExercise(int exerciseID) {
        Exercise selExercise = document.getExerciseList().getByID(exerciseID);
        ExerciseDialog dlg = prExerciseDialog.get();
        dlg.setExercise(selExercise);
        context.showDialog(dlg);
    }

    /**
     * Starts Edit dialog for the specified note ID.
     *
     * @param noteID ID of the note entry
     */
    private void editNote(int noteID) {
        Note selNote = document.getNoteList().getByID(noteID);
        NoteDialog dlg = prNoteDialog.get();
        dlg.setNote(selNote);
        context.showDialog(dlg);
    }

    /**
     * Starts Edit dialog for the specified weight ID.
     *
     * @param weightID ID of the weight entry
     */
    private void editWeight(int weightID) {
        Weight selWeight = document.getWeightList().getByID(weightID);
        WeightDialog dlg = prWeightDialog.get();
        dlg.setWeight(selWeight);
        context.showDialog(dlg);
    }

    @Override
    @Action(name = ACTION_ENTRY_COPY)
    public void copyEntry() {

        // start copy action depending on entry type
        if (view.getCurrentView().getSelectedExerciseCount() == 1) {
            copyExercise(view.getCurrentView().getSelectedExerciseIDs()[0]);
        } else if (view.getCurrentView().getSelectedNoteCount() == 1) {
            copyNote(view.getCurrentView().getSelectedNoteIDs()[0]);
        } else if (view.getCurrentView().getSelectedWeightCount() == 1) {
            copyWeight(view.getCurrentView().getSelectedWeightIDs()[0]);
        }
    }

    /**
     * Creates a copy of the specified Exercise and displays it in the Exercise dialog.
     *
     * @param exerciseID ID of the exercise entry to copy
     */
    private void copyExercise(int exerciseID) {

        Exercise selExercise = document.getExerciseList().getByID(exerciseID);
        Exercise copiedExercise = createExerciseCopy(selExercise);

        // start exercise dialog for the copied exercise
        ExerciseDialog dlg = prExerciseDialog.get();
        dlg.setExercise(copiedExercise);
        context.showDialog(dlg);
    }

    /**
     * Creates a copy of the specified Note and displays it in the Note dialog.
     *
     * @param noteID ID of the note entry to copy
     */
    private void copyNote(int noteID) {

        Note selNote = document.getNoteList().getByID(noteID);
        Note copiedNote = createNoteCopy(selNote);

        // start note dialog for the copied note
        NoteDialog dlg = prNoteDialog.get();
        dlg.setNote(copiedNote);
        context.showDialog(dlg);
    }

    /**
     * Creates a copy of the specified Weight and displays it in the Weight dialog.
     *
     * @param weightID ID of the weight entry to copy
     */
    private void copyWeight(int weightID) {

        Weight selWeight = document.getWeightList().getByID(weightID);
        Weight copiedWeight = createWeightCopy(selWeight);

        // start weight dialog for the copied weight
        WeightDialog dlg = prWeightDialog.get();
        dlg.setWeight(copiedWeight);
        context.showDialog(dlg);
    }

    @Override
    @Action(name = ACTION_ENTRY_DELETE)
    public void deleteEntry() {
        int[] selectedEntryIDs = null;
        IdDateObjectList<? extends IdDateObject> entryList = null;

        // get selected entry IDs and the type of their list
        if (view.getCurrentView().getSelectedExerciseCount() > 0) {
            selectedEntryIDs = view.getCurrentView().getSelectedExerciseIDs();
            entryList = document.getExerciseList();
        } else if (view.getCurrentView().getSelectedNoteCount() > 0) {
            selectedEntryIDs = view.getCurrentView().getSelectedNoteIDs();
            entryList = document.getNoteList();
        } else if (view.getCurrentView().getSelectedWeightCount() > 0) {
            selectedEntryIDs = view.getCurrentView().getSelectedWeightIDs();
            entryList = document.getWeightList();
        }

        // cancel when no selection found
        if (selectedEntryIDs == null || selectedEntryIDs.length == 0) {
            return;
        }

        // show confirmation dialog first
        if (context.showConfirmDialog(context.getMainFrame(), "st.view.confirm.delete.title",
                "st.view.confirm.delete.text") != JOptionPane.YES_OPTION) {
            return;
        }

        // finally remove the entries
        for (int id : selectedEntryIDs) {
            entryList.removeByID(id);
        }
    }

    /**
     * Action for viewing the HRM file of the selected exercise.
     */
    @Action(name = ACTION_VIEW_HRM)
    public void viewHRMFile() {

        // get selected exercise and start ExerciseViewer for it's HRM file
        // (special checks not needed here, done by the STView:updateExerciseActions() method)
        int exerciseID = view.getCurrentView().getSelectedExerciseIDs()[0];
        Exercise exercise = document.getExerciseList().getByID(exerciseID);
        EVMain pv = prExerciseViewer.get();
        pv.showExercise(exercise.getHrmFile(), document.getOptions(), false);
    }

    /**
     * Action for editing the application preferences.
     */
    @Action(name = ACTION_PREFERENCES)
    public void editPreferences() {
        context.showDialog(prOptionsDialog.get());
        // unit system may be changed
        view.updateView();
    }

    /**
     * Action for showing the exercises in the calendar view.
     */
    @Action(name = ACTION_CALENDAR_VIEW)
    public void showCalendarView() {
        view.switchToView(EntryView.ViewType.CALENDAR);
    }

    /**
     * Action for showing the exercise list (table) view.
     */
    @Action(name = ACTION_EXERCISE_LIST_VIEW)
    public void showExerciseListView() {
        view.switchToView(EntryView.ViewType.EXERCISE_LIST);
    }

    /**
     * Action for showing the note list (table) view.
     */
    @Action(name = ACTION_NOTE_LIST_VIEW)
    public void showNoteListView() {
        view.switchToView(EntryView.ViewType.NOTE_LIST);
    }

    /**
     * Action for showing the weight list (table) view.
     */
    @Action(name = ACTION_WEIGHT_LIST_VIEW)
    public void showWeightListView() {
        view.switchToView(EntryView.ViewType.WEIGHT_LIST);
    }

    /**
     * Action for defining and enabling a filter for the exercises.
     */
    @Action(name = ACTION_FILTER_EXERCISES)
    public void filterExercises() {
        FilterDialog dlg = prFilterDialog.get();
        dlg.setInitialFilter(document.getCurrentFilter());
        context.showDialog(dlg);

        // set and enable filter when available
        if (dlg.getSelectedFilter() != null) {
            document.setCurrentFilter(dlg.getSelectedFilter());
            document.setFilterEnabled(true);
            view.updateView();
        }
    }

    /**
     * Action for disabling the current exercise filter.
     */
    @Action(name = ACTION_FILTER_DISABLE)
    public void disableFilter() {
        document.setFilterEnabled(false);
        view.updateView();
    }

    /**
     * Action for showing the sport type editor dialog.
     */
    @Action(name = ACTION_SPORTTYPE_EDITOR)
    public void showSportTypeEditor() {
        context.showDialog(prSportTypeListDialog.get());

        // sport type and subtype objects may have been changed 
        // => these will be new objects => update all exercises and the 
        // current filter, they need to reference to these new objects
        SportTypeList stList = document.getSportTypeList();
        document.getExerciseList().updateSportTypes(stList);
        document.getCurrentFilter().updateSportTypes(stList);
        view.updateView();
    }

    /**
     * Action for showing the statistics dialog.
     */
    @Action(name = ACTION_STATISTICS)
    public void showStatistics() {
        if (!checkForExistingExercises()) {
            return;
        }

        context.showDialog(prStatisticDialog.get());
    }

    /**
     * Action for showing the overview diagram dialog.
     */
    @Action(name = ACTION_OVERVIEW_DIAGRAM)
    public void showOverviewDiagram() {
        if (!checkForExistingExercises()) {
            return;
        }

        context.showDialog(prOverviewDialog.get());
    }

    /**
     * Action for showing the applications About dialog.
     */
    @Action(name = ACTION_ABOUT)
    public void showAboutDialog() {
        // TODO remove workarounds after migration of main window to JavaFX
        // JavaFX UI code needs to be executed on the JavaFX application thread.
        // Bring dummy JavaFX window to front, is always behind the Swing window.
        Platform.runLater(() -> {
            context.getPrimaryStage().toFront();
            prAboutDialogController.get().show(context.getPrimaryStage());
        });
    }

    @Override
    public boolean dropHrmFile(String filename, Exercise exercise) {
        if (!checkForExistingExercises()) {
            return false;
        }

        if (exercise != null) {
            // an exercise was passed => assign the HRM file to it
            exercise.setHrmFile(filename);
            document.getExerciseList().set(exercise);
            context.showMessageDialog(context.getMainFrame(), JOptionPane.INFORMATION_MESSAGE,
                    "common.info", "st.calview.draganddrop.assigned");
        } else {
            // create a new exercise and assign the HRM file
            Exercise newExercise = createNewExercise(null);
            newExercise.setHrmFile(filename);

            // start exercise dialog for it and import the HRM data
            ExerciseDialog dlg = prExerciseDialog.get();
            dlg.setExercise(newExercise);
            dlg.importHRMFile();
            context.showDialog(dlg);
        }
        return true;
    }

    @Override
    public boolean checkForExistingSportTypes() {
        if (document.getSportTypeList().size() == 0) {
            context.showMessageDialog(context.getMainFrame(), JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.main.error.no_sporttype");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkForExistingExercises() {
        if (document.getExerciseList().size() == 0) {
            context.showMessageDialog(context.getMainFrame(), JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.main.error.no_exercise");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkForExistingNotes() {
        if (document.getNoteList().size() == 0) {
            context.showMessageDialog(context.getMainFrame(), JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.main.error.no_note");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkForExistingWeights() {
        if (document.getWeightList().size() == 0) {
            context.showMessageDialog(context.getMainFrame(), JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.main.error.no_weight");
            return false;
        }
        return true;
    }

    @Override
    public boolean saveBeforeExit() {
        if (!document.isDirtyData()) {
            return true;
        }

        // display confirmation dialog when automatic save is disabled
        if (!document.getOptions().isSaveOnExit()) {
            if (context.showConfirmDialog(context.getMainFrame(), "st.main.confirm.save_exit.title",
                    "st.main.confirm.save_exit.text") != JOptionPane.YES_OPTION) {
                return true;
            }
        }
        return executeSave();
    }

    @Override
    public void startActionManually(String actionName) {
        // TODO: right now that's the only way to execute a action (can
        // also be a blocking task) manually, but future application framework 
        // releases will have a convinient execute method
        // (see https://appframework.dev.java.net/issues/show_bug.cgi?id=39)
        getActionMap().get(actionName).actionPerformed(new ActionEvent(
                context.getMainFrame(), ActionEvent.ACTION_PERFORMED, actionName));
    }

    /**
     * Checks for existing sport types. When there are no sport types yet, then
     * the user will be asked if he wants to define a sport type. If yes, then
     * the sport type editor will be opened.
     */
    private void askForDefiningSportTypes() {
        if (document.getSportTypeList().size() == 0) {
            if (context.showConfirmDialog(context.getMainFrame(), "common.info",
                    "st.main.confirm.define_first_sporttype") == JOptionPane.YES_OPTION) {
                showSportTypeEditor();
            }
        }
    }

    /**
     * Creates a new Exercise. The exercise date will be the specified date at
     * 12:00:00 or if no date is specified the current day at 12:00.
     *
     * @param date the date to be set in the exercise (can be null for the current date)
     * @return the created Exercise
     */
    private Exercise createNewExercise(LocalDate date) {
        Exercise exercise = new Exercise(document.getExerciseList().getNewID());
        exercise.setDateTime(getNoonDateTimeForDate(date));
        return exercise;
    }

    /**
     * Creates a copy/clone of the specified Exercise. The clone has a new unique
     * ID and the date is reseted (it will be the current day at 12:00). The HRM
     * file in the clone is deleted too, the copy must get a new one.
     *
     * @param exercise the Exercise to copy
     * @return the created Exercise copy
     */
    private Exercise createExerciseCopy(Exercise exercise) {
        Exercise clonedExercise = exercise.clone(document.getExerciseList().getNewID());
        clonedExercise.setDateTime(getNoonDateTimeForDate(null));
        clonedExercise.setHrmFile(null);
        return clonedExercise;
    }

    /**
     * Creates a copy/clone of the specified Note. The clone has a new unique
     * ID and the date is reseted (it will be the current day at 12:00).
     *
     * @param note the Note to copy
     * @return the created Note copy
     */
    private Note createNoteCopy(Note note) {
        Note clonedNote = note.clone(document.getNoteList().getNewID());
        clonedNote.setDateTime(getNoonDateTimeForDate(null));
        return clonedNote;
    }

    /**
     * Creates a copy/clone of the specified Weight. The clone has a new unique
     * ID and the date is reseted (it will be the current day at 12:00).
     *
     * @param weight the Weight to copy
     * @return the created Weight copy
     */
    private Weight createWeightCopy(Weight weight) {
        Weight clonedWeight = weight.clone(document.getWeightList().getNewID());
        clonedWeight.setDateTime(getNoonDateTimeForDate(null));
        return clonedWeight;
    }

    /**
     * Returns a Date LocalDateTime for the specified date, the time is set to 12:00:00.
     * When no date is specified then the current date will be used.
     *
     * @param date contains the date to be used (optional)
     * @return the created LocalDateTime
     */
    private LocalDateTime getNoonDateTimeForDate(final LocalDate date) {
        final LocalDate tempDate = date == null ? LocalDate.now() : date;
        return LocalDateTime.of(tempDate, LocalTime.of(12, 0));
    }
}
