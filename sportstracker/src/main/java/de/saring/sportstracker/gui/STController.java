package de.saring.sportstracker.gui;

import java.util.Date;

import com.google.inject.ImplementedBy;
import de.saring.sportstracker.data.Exercise;
import javax.swing.ActionMap;

/**
 * This interface provides all controller (MVC) related functionality of the 
 * SportsTracker application.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
@ImplementedBy(STControllerImpl.class)
public interface STController {

    /** Constants for action names. */
    String ACTION_OPEN_EXERCISEVIEWER = "st.view.open_exerciseviewer";
    String ACTION_LOAD = "st.view.load";
    String ACTION_SAVE = "st.view.save";
    String ACTION_PRINT = "st.view.print";
    String ACTION_QUIT = "st.view.quit";
    String ACTION_EXERCISE_ADD = "st.view.exercise_add";
    String ACTION_NOTE_ADD = "st.view.note_add";
    String ACTION_WEIGHT_ADD = "st.view.weight_add";
    String ACTION_ENTRY_DELETE = "st.view.entry_delete";
    String ACTION_ENTRY_EDIT = "st.view.entry_edit";
    String ACTION_VIEW_HRM = "st.view.view_hrm";
    String ACTION_PREFERENCES = "st.view.preferences";
    String ACTION_CALENDAR_VIEW = "st.view.calendar_view";
    String ACTION_EXERCISE_LIST_VIEW = "st.view.exercise_list_view";
    String ACTION_NOTE_LIST_VIEW = "st.view.note_list_view";
    String ACTION_WEIGHT_LIST_VIEW = "st.view.weight_list_view";
    String ACTION_FILTER_EXERCISES = "st.view.filter_exercises";
    String ACTION_FILTER_DISABLE = "st.view.filter_disable";
    String ACTION_SPORTTYPE_EDITOR = "st.view.sporttype_editor";
    String ACTION_STATISTICS = "st.view.statistics";
    String ACTION_OVERVIEW_DIAGRAM = "st.view.overview_diagram";    
    String ACTION_ABOUT = "st.view.about";

    
    /**
     * Returns the ActionMap with all actions of the STController component.
     * @return the ActionMap of the controller
     */
    ActionMap getActionMap ();
    
    /**
     * Action for printing the exercises of the current view.
     */
    void print ();
    
    /**
     * Action for adding a new exercise.
     */
    void addExercise ();
    
    /**
     * Sets the date to be used when new entries (e.g. exercises) will be created.
     * When set to null then the current date will be used instead.
     * @param date the date to be used (the time will be set to 12:00)
     */
    void setDateForNewEntries (Date date);

    /**
     * Action for editing the selected entry.
     */
    void editEntry ();
    
    /**
     * Action for deleting the selected entry (or entries).
     */
    void deleteEntry ();
    
    /**
     * Assigns the specified HRM filename to the specified exercise (when a
     * HRM file was dropped to an exercise in the calendar). If the exercise
     * is null, then a new exercise will be created and displayed in the
     * Exercise dialog (the HRM data will be imported).
     *
     * @param filename the name of the HRM file
     * @param exercise the destination exercise or null
     * @return true when the HRM file has been assigned
     */
    boolean dropHrmFile (String filename, Exercise exercise);

    /**
     * Checks for existing sport types. A message dialog will be displayed when 
     * there are no sport types available yet.
     * @return true when there's at least one sport type
     */
    boolean checkForExistingSportTypes ();
    
    /**
     * Checks for existing exercises. A message dialog will be displayed when 
     * there are no exercises available yet.
     * @return true when there's at least one exercise
     */
    boolean checkForExistingExercises ();
    
    /**
     * Checks for existing note entries. A message dialog will be displayed when 
     * there are no notes available yet.
     * @return true when there's at least one note
     */
    boolean checkForExistingNotes ();
    
    /**
     * Checks for existing weight entries. A message dialog will be displayed when 
     * there are no weight available yet.
     * @return true when there's at least one weight
     */
    boolean checkForExistingWeights();

    /**
     * This action asks the user for saving modified data before application exit.
     * It does nothing when there's no modified data.
     * @return returns true when the application can be exited or false on save problems
     */
    boolean saveBeforeExit ();
    
    /**
     * Starts the specified action manually (without GUI input).
     * @param actionName name o fthe action to execute
     */
    void startActionManually (String actionName);    
}
