package de.saring.sportstracker.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.saring.exerciseviewer.gui.EVMain;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.dialogs.AboutDialog;
import de.saring.sportstracker.gui.dialogs.ExerciseDialog;
import de.saring.sportstracker.gui.dialogs.FilterDialog;
import de.saring.sportstracker.gui.dialogs.HRMFileOpenDialog;
import de.saring.sportstracker.gui.dialogs.NoteDialog;
import de.saring.sportstracker.gui.dialogs.OptionsDialog;
import de.saring.sportstracker.gui.dialogs.OverviewDialog;
import de.saring.sportstracker.gui.dialogs.SportTypeListDialog;
import de.saring.sportstracker.gui.dialogs.StatisticDialog;
import de.saring.sportstracker.gui.dialogs.WeightDialog;
import de.saring.sportstracker.gui.views.EntryView;
import de.saring.util.data.IdDateObject;
import de.saring.util.data.IdDateObjectList;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 * This class contains all controller (MVC) related functionality of the SportsTracker 
 * application.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
@Singleton
public class STControllerImpl implements STController {
    private static final Logger LOGGER = Logger.getLogger (STControllerImpl.class.getName ()); 

    private STContext context;
    private STDocument document;
    private STView view;
    
    @Inject private Provider<ExerciseDialog> prExerciseDialog;
    @Inject private Provider<NoteDialog> prNoteDialog;
    @Inject private Provider<WeightDialog> prWeightDialog;
    @Inject private Provider<OptionsDialog> prOptionsDialog;
    @Inject private Provider<FilterDialog> prFilterDialog;    
    @Inject private Provider<SportTypeListDialog> prSportTypeListDialog;    
    @Inject private Provider<StatisticDialog> prStatisticDialog;    
    @Inject private Provider<OverviewDialog> prOverviewDialog;    
    @Inject private Provider<AboutDialog> prAboutDialog;
    @Inject private Provider<HRMFileOpenDialog> prHRMFileOpenDialog;    
    @Inject private Provider<EVMain> prExerciseViewer;
    
    /** The action map of the controller class. */
    private ActionMap actionMap;
    
    /** The date to be set initially when the next entry will be added. */
    private Date dateForNewEntries;

    /**
     * Standard c'tor.
     * @param context the SportsTracker context
     * @param document the document component
     * @param view the view component
     */
    @Inject
    public STControllerImpl (STContext context, STDocument document, STView view) {
        this.context = context;
        this.document = document;
        this.view = view;
    }

    /** {@inheritDoc} */
    public ActionMap getActionMap () {
        if (actionMap == null) {
            actionMap = context.getSAFContext ().getActionMap (STControllerImpl.class, this);
        }
        return actionMap;
    }    
    
    /**
     * Action for loading the application data inside a background task.
     * @return the Task for the load action
     */
    @Action(name=ACTION_LOAD, block=Task.BlockingScope.WINDOW)
    public Task<Void, Void> load () {
        return new LoadTask ();
    }
    
    /**
     * This class executes the loading action inside a background task.
     */
    class LoadTask extends org.jdesktop.application.Task<Void, Void> {
        public LoadTask () {
            super (context.getSAFContext ().getApplication ());
            setUserCanCancel (false);
        }

        @Override
        protected Void doInBackground () throws Exception {
            document.readApplicationData ();
            return null;
        }

        @Override
        protected void failed (Throwable cause) {
            super.failed (cause);
            context.showMessageDialog (context.getMainFrame (), 
                JOptionPane.ERROR_MESSAGE, "common.error", "st.main.error.load_data");
        }

        @Override
        protected void finished () {
            view.updateView ();
            askForDefiningSportTypes ();
        }
    }

    /**
     * Action for saving data changes inside a background task.
     * @return the Task for the save action
     */
    @Action(name=ACTION_SAVE, block=Task.BlockingScope.WINDOW)
    public Task<Void, Void> save () {
        return new SaveTask ();
    }
    
    /**
     * This class executes the Save action inside a background task.
     */
    class SaveTask extends org.jdesktop.application.Task<Void, Void> {
        public SaveTask () {
            super (context.getSAFContext ().getApplication ());
            setUserCanCancel (false);
        }

        @Override
        protected Void doInBackground () throws Exception {
            executeSave ();
            return null;
        }
    }

    /**
     * Executes the save of application data.
     * @return true on success
     */
    private boolean executeSave () {       
        try {
            document.storeApplicationData ();
            return true;
        }
        catch (STException se) {
            LOGGER.log (Level.SEVERE, "Failed to store application data!", se);
            context.showMessageDialog (context.getMainFrame (), JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.main.error.save_data");
            return false;
        }
        finally {
            view.updateSaveAction ();
        }
    }
    
    /**
     * Action for open the exercise HRM file in the ExerciseViewer sub-application (dialog).
     */
    @Action(name=ACTION_OPEN_EXERCISEVIEWER)
    public void openExerciseViewer () {

        // show file open dialog for HRM file selection
        File selectedFile = prHRMFileOpenDialog.get ().selectHRMFile (document.getOptions (), null);
        if (selectedFile != null) {
            
            // start ExerciseViewer 
            EVMain pv = prExerciseViewer.get ();
            pv.showExercise (selectedFile.getAbsolutePath (), document.getOptions (), false);
        }
    }
    
    /**
     * Action for printing the exercises of the current view.
     */
    @Action(name=ACTION_PRINT)
    public void print () {
        try {            
            view.getCurrentView ().print ();
        } 
        catch (STException se) {
            LOGGER.log (Level.WARNING, "Failed to print current view!", se);
            context.showMessageDialog (context.getMainFrame (), JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.main.error.print_view");
        }
    }
    
    /**
     * Action for quitting the SportsTracker application.
     */
    @Action(name=ACTION_QUIT)
    public void quitApplication () {
        context.getSAFContext ().getApplication ().exit ();
    }
    
    /** {@inheritDoc} */
    @Action(name=ACTION_EXERCISE_ADD)
    public void addExercise () {
        if (!checkForExistingSportTypes ()) {
            return;
        }

        // start exercise dialog for a new created exercise
        Exercise newExercise = createNewExercise (dateForNewEntries);
        ExerciseDialog dlg = prExerciseDialog.get ();
        dlg.setExercise (newExercise);
        context.showDialog (dlg);
        view.updateView ();        
    }

    /** {@inheritDoc} */
    @Action(name=ACTION_NOTE_ADD)
    public void addNote () {        
        // start Note dialog for a new created Note
        Note newNote = new Note (document.getNoteList ().getNewID ());
        newNote.setDate (setTimeForDate (dateForNewEntries));

        NoteDialog dlg = prNoteDialog.get ();
        dlg.setNote (newNote);
        context.showDialog (dlg);
        view.updateView ();
    }

    /** {@inheritDoc} */
    @Action(name=ACTION_WEIGHT_ADD)
    public void addWeight () {
        // start Weight dialog for a new created Weight
        Weight newWeight = new Weight (document.getWeightList ().getNewID ());
        newWeight.setDate (setTimeForDate (dateForNewEntries));

        WeightDialog dlg = prWeightDialog.get ();
        dlg.setWeight (newWeight);
        context.showDialog (dlg);
        view.updateView ();
    }

    /** {@inheritDoc} */
    public void setDateForNewEntries (Date date) {
        this.dateForNewEntries = date;        
    }

    /** {@inheritDoc} */
    @Action(name=ACTION_ENTRY_DELETE)
    public void deleteEntry () {
        int[] selectedEntryIDs = null;
        IdDateObjectList<? extends IdDateObject> entryList = null;

        // get selected entry IDs and the type of their list
        if (view.getCurrentView ().getSelectedExerciseCount () > 0) {
            selectedEntryIDs = view.getCurrentView ().getSelectedExerciseIDs ();
            entryList = document.getExerciseList ();
        }
        else if (view.getCurrentView ().getSelectedNoteCount () > 0) {
            selectedEntryIDs = view.getCurrentView ().getSelectedNoteIDs ();
            entryList = document.getNoteList ();
        }
        else if (view.getCurrentView ().getSelectedWeightCount () > 0) {
            selectedEntryIDs = view.getCurrentView ().getSelectedWeightIDs ();
            entryList = document.getWeightList ();
        }

        // cancel when no selection found
        if (selectedEntryIDs == null || selectedEntryIDs.length == 0) {
            return;
        }

        // show confirmation dialog first
        if (context.showConfirmDialog (context.getMainFrame (), "st.view.confirm.delete.title", 
            "st.view.confirm.delete.text") != JOptionPane.YES_OPTION) {
            return;
        }

        // finally remove the entries
        for (int id : selectedEntryIDs) {
            entryList.removeByID (id);
        }
        view.updateView ();
    }
        
    /** {@inheritDoc} */
    @Action(name=ACTION_ENTRY_EDIT)
    public void editEntry () {

        // start edit action depending on entry type
        if (view.getCurrentView ().getSelectedExerciseCount () == 1) {
            editExercise (view.getCurrentView ().getSelectedExerciseIDs ()[0]);
        }
        else if (view.getCurrentView ().getSelectedNoteCount () == 1) {
            editNote (view.getCurrentView ().getSelectedNoteIDs ()[0]);
        }
        else if (view.getCurrentView ().getSelectedWeightCount () == 1) {
            editWeight (view.getCurrentView ().getSelectedWeightIDs ()[0]);
        }
    }

    /**
     * Starts Edit dialog for the specified exercise ID.
     * @param exerciseID ID of the exercise entry
     */
    private void editExercise (int exerciseID) {
        Exercise selExercise = document.getExerciseList ().getByID (exerciseID);
        ExerciseDialog dlg = prExerciseDialog.get ();
        dlg.setExercise (selExercise);
        context.showDialog (dlg);
        view.updateView ();
    }

    /**
     * Starts Edit dialog for the specified note ID.
     * @param noteID ID of the note entry
     */
    private void editNote (int noteID) {
        Note selNote = document.getNoteList ().getByID (noteID);
        NoteDialog dlg = prNoteDialog.get ();
        dlg.setNote (selNote);
        context.showDialog (dlg);
        view.updateView ();
    }

    /**
     * Starts Edit dialog for the specified weight ID.
     * @param weightID ID of the weight entry
     */
    private void editWeight (int weightID) {
        Weight selWeight = document.getWeightList ().getByID (weightID);
        WeightDialog dlg = prWeightDialog.get ();
        dlg.setWeight (selWeight);
        context.showDialog (dlg);
        view.updateView ();
    }

    /**
     * Action for viewing the HRM file of the selected exercise.
     */
    @Action(name=ACTION_VIEW_HRM)
    public void viewHRMFile () {
        
        // get selected exercise and start ExerciseViewer for it's HRM file
        // (special checks not needed here, done by the STView:updateExerciseActions() method)
        int exerciseID = view.getCurrentView ().getSelectedExerciseIDs ()[0];
        Exercise exercise = document.getExerciseList ().getByID (exerciseID);
        EVMain pv = prExerciseViewer.get ();
        pv.showExercise (exercise.getHrmFile (), document.getOptions (), false);
    }
    
    /**
     * Action for editing the application preferences.
     */
    @Action(name=ACTION_PREFERENCES)
    public void editPreferences () {
        context.showDialog (prOptionsDialog.get ());
        // unit system may be changed
        view.updateView ();        
    }
    
    /**
     * Action for showing the exercises in the calendar view.
     */
    @Action(name=ACTION_CALENDAR_VIEW)
    public void showCalendarView () {
        view.switchToView (EntryView.ViewType.CALENDAR);
    }
    
    /**
     * Action for showing the exercise list (table) view.
     */
    @Action(name=ACTION_EXERCISE_LIST_VIEW)
    public void showExerciseListView () {
        view.switchToView (EntryView.ViewType.EXERCISE_LIST);
    }
    
    /**
     * Action for showing the note list (table) view.
     */
    @Action(name=ACTION_NOTE_LIST_VIEW)
    public void showNoteListView () {
        view.switchToView (EntryView.ViewType.NOTE_LIST);
    }

    /**
     * Action for showing the weight list (table) view.
     */
    @Action(name=ACTION_WEIGHT_LIST_VIEW)
    public void showWeightListView () {
        view.switchToView (EntryView.ViewType.WEIGHT_LIST);
    }

    /**
     * Action for defining and enabling a filter for the exercises.
     */
    @Action(name=ACTION_FILTER_EXERCISES)
    public void filterExercises () {
        FilterDialog dlg = prFilterDialog.get ();
        dlg.setInitialFilter (document.getCurrentFilter ());
        context.showDialog (dlg);
        
        // set and enable filter when available
        if (dlg.getSelectedFilter () != null) {
            document.setCurrentFilter (dlg.getSelectedFilter ());
            document.setFilterEnabled (true);
            view.updateView (); 
        }
    }
    
    /**
     * Action for disabling the current exercise filter.
     */
    @Action(name=ACTION_FILTER_DISABLE)
    public void disableFilter () {
        document.setFilterEnabled (false);
        view.updateView ();
    }
    
    /**
     * Action for showing the sport type editor dialog.
     */
    @Action(name=ACTION_SPORTTYPE_EDITOR)
    public void showSportTypeEditor () {
        context.showDialog (prSportTypeListDialog.get ());
        
        // sport type and subtype objects may have been changed 
        // => these will be new objects => update all exercises and the 
        // current filter, they need to reference to these new objects
        SportTypeList stList = document.getSportTypeList ();
        document.getExerciseList ().updateSportTypes (stList);
        document.getCurrentFilter ().updateSportTypes (stList);
        view.updateView ();
    }
    
    /**
     * Action for showing the statistics dialog.
     */
    @Action(name=ACTION_STATISTICS)
    public void showStatistics () {
        if (!checkForExistingExercises ()) {
            return;
        }
        
        context.showDialog(prStatisticDialog.get ());
    }
    
    /**
     * Action for showing the overview diagram dialog.
     */
    @Action(name=ACTION_OVERVIEW_DIAGRAM)
    public void showOverviewDiagram () {
        if (!checkForExistingExercises ()) {
            return;
        }
        
        context.showDialog(prOverviewDialog.get ());
    }
    
    /**
     * Action for showing the applications About dialog.
     */
    @Action(name=ACTION_ABOUT)
    public void showAboutDialog () {
        context.showDialog (prAboutDialog.get ());
    }

    /** {@inheritDoc} */
    public boolean dropHrmFile (String filename, Exercise exercise) {
        if (!checkForExistingExercises ()) {
            return false;
        }

        if (exercise != null) {
            // an exercise was passed => assign the HRM file to it
            exercise.setHrmFile (filename);
            document.getExerciseList ().set (exercise);
            context.showMessageDialog (context.getMainFrame (), JOptionPane.INFORMATION_MESSAGE,
                "common.info", "st.calview.draganddrop.assigned");
        }
        else {
            // create a new exercise and assign the HRM file
            Exercise newExercise = createNewExercise (null);
            newExercise.setHrmFile (filename);

            // start exercise dialog for it and import the HRM data
            ExerciseDialog dlg = prExerciseDialog.get ();
            dlg.setExercise (newExercise);
            dlg.importHRMFile ();
            context.showDialog (dlg);
        }

        view.updateView ();
        return true;
    }

    /** {@inheritDoc} */
    public boolean checkForExistingSportTypes () {
        if (document.getSportTypeList ().size () == 0) {
            context.showMessageDialog (context.getMainFrame (), JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.main.error.no_sporttype");
            return false;
        }
        return true;
    }
    
    /** {@inheritDoc} */
    public boolean checkForExistingExercises () {
        if (document.getExerciseList ().size () == 0) {
            context.showMessageDialog (context.getMainFrame (), JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.main.error.no_exercise");
            return false;
        }
        return true;
    }
    
    /** {@inheritDoc} */
    public boolean checkForExistingNotes () {
        if (document.getNoteList ().size () == 0) {
            context.showMessageDialog (context.getMainFrame (), JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.main.error.no_note");
            return false;
        }
        return true;
    }
    
    /** {@inheritDoc} */
    public boolean checkForExistingWeights() {
        if (document.getWeightList ().size () == 0) {
            context.showMessageDialog (context.getMainFrame (), JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.main.error.no_weight");
            return false;
        }
        return true;
    }
    
    /** {@inheritDoc} */
    public boolean saveBeforeExit () {        
        if (!document.isDirtyData ()) {
            return true;
        }
        
        // display confirmation dialog when automatic save is disabled
        if (!document.getOptions().isSaveOnExit()) {
            if (context.showConfirmDialog (context.getMainFrame (), "st.main.confirm.save_exit.title", 
                "st.main.confirm.save_exit.text") != JOptionPane.YES_OPTION) {
                return true;
            }
        }        
        return executeSave ();
    }

    /** {@inheritDoc} */
    public void startActionManually (String actionName) {
        // TODO: right now that's the only way to execute a action (can
        // also be a blocking task) manually, but future application framework 
        // releases will have a convinient execute method
        // (see https://appframework.dev.java.net/issues/show_bug.cgi?id=39)
        getActionMap ().get (actionName).actionPerformed (new ActionEvent (
            context.getMainFrame (), ActionEvent.ACTION_PERFORMED, actionName));
    }    
    
    /**
     * Checks for existing sport types. When there are no sport types yet, then
     * the user will be asked if he wants to define a sport type. If yes, then 
     * the sport type editor will be opened.
     */
    private void askForDefiningSportTypes () {
        if (document.getSportTypeList ().size () == 0) {            
            if (context.showConfirmDialog (context.getMainFrame (), "common.info", 
                "st.main.confirm.define_first_sporttype") == JOptionPane.YES_OPTION) {
                showSportTypeEditor ();
            }
        }
    }    

    /**
     * Creates a new Exercise. The exercise date will be the specified date at 
     * 12:00:00 or if no date is specified the current day at 12:00.
     * @param date the date to be set in the exercise (can be null for the current date)
     * @return the created Exercise
     */
    private Exercise createNewExercise (Date date) {
        Exercise exercise = new Exercise (
            document.getExerciseList ().getNewID ());
        exercise.setDate (setTimeForDate (date));
        return exercise;
    }

    /**
     * Returns a Date object for the specified date, the time is set to 12:00:00.
     * When no date is specified then the current date will be used.
     * @param date contains the date to be used (optional)
     * @return the created Date
     */
    private Date setTimeForDate (final Date date) {
        Calendar cal = Calendar.getInstance ();
        if (date != null) {
            cal.setTime (date);
        }
        cal.set (Calendar.HOUR_OF_DAY, 12);
        cal.set (Calendar.MINUTE, 0);
        cal.set (Calendar.SECOND, 0);
        cal.set (Calendar.MILLISECOND, 0);
        return cal.getTime ();
    }
}
