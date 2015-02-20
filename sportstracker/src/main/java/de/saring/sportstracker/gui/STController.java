package de.saring.sportstracker.gui;

import de.saring.sportstracker.data.Exercise;
import javafx.event.ActionEvent;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.time.LocalDate;

/**
 * This interface provides all controller (MVC) functionality of the SportsTracker main application window.
 *
 * @author Stefan Saring
 */
public interface STController {

    /**
     * Initializes the main SportsTracker application window (loaded from FXML).
     */
    void initApplicationWindow() throws IOException;

    /**
     * Loads the SportsTracker user data asynchronously without blocking the UI. After completion the
     * loaded data is displayed in the FX application thread (or showing an error message when failed).
     */
    void loadApplicationData();

    /**
     * Event handler for action "Open HRM file in ExerciseViewer".
     */
    void onOpenHrmFile(ActionEvent event);

    /**
     * Event handler for action "Save changes".
     */
    void onSave(ActionEvent event);

    /**
     * Event handler for action "Print current view".
     */
    void onPrint(ActionEvent event);

    /**
     * Event handler for action "Quit SportsTracker".
     */
    void onQuit(ActionEvent event);

    /**
     * Event handler for action "Add new Exercise".
     */
    void onAddExercise(ActionEvent event);

    /**
     * Event handler for action "Add new Note".
     */
    void onAddNote(ActionEvent event);

    /**
     * Event handler for action "Add new Weight".
     */
    void onAddWeight(ActionEvent event);

    /**
     * Sets the date to be used when new entries (e.g. exercises) will be created.
     * When set to null then the current date will be used instead.
     *
     * @param date the date to be used (the time will be set to 12:00)
     */
    void setDateForNewEntries(LocalDate date);

    /**
     * Event handler for action "Edit selected Entry".
     */
    void onEditEntry(ActionEvent event);

    /**
     * Event handler for action "Copy selected Entry".
     */
    void onCopyEntry(ActionEvent event);

    /**
     * Event handler for action "Delete selected Entry".
     */
    void onDeleteEntry(ActionEvent event);

    /**
     * Event handler for action "View HRM file of selected entry in ExerciseViewer".
     */
    void onViewHrmFile(ActionEvent event);

    /**
     * Event handler for action "Edit SportsTracker Preferences".
     */
    void onPreferences(ActionEvent event);

    /**
     * Event handler for action "Show Calendar View".
     */
    void onCalendarView(ActionEvent event);

    /**
     * Event handler for action "Show Exercise List View".
     */
    void onExerciseListView(ActionEvent event);

    /**
     * Event handler for action "Show Note List View".
     */
    void onNoteListView(ActionEvent event);

    /**
     * Event handler for action "Show Weight List View".
     */
    void onWeightListView(ActionEvent event);

    /**
     * Event handler for action "Set Exercise Filter".
     */
    void onFilterExercises(ActionEvent event);

    /**
     * Event handler for action "Disable Exercise Filter".
     */
    void onFilterDisable(ActionEvent event);

    /**
     * Event handler for action "Edit Sport Types".
     */
    void onSportTypeEditor(ActionEvent event);

    /**
     * Event handler for action "Calculate Statistics".
     */
    void onStatistics(ActionEvent event);

    /**
     * Event handler for action "Show Overview Diagram".
     */
    void onOverviewDiagram(ActionEvent event);

    /**
     * Event handler for action "Project Website".
     */
    void onWebsite(ActionEvent event);

    /**
     * Event handler for action "About SportsTracker".
     */
    void onAbout(ActionEvent event);

    /**
     * Starts the action for adding a new exercise for the specified HRM file (called when
     * a HRM file has been dropped on a day cell in the calendar view). The new exercise
     * will contain the data imported from the specified HRM file.
     *
     * @param hrmFilePath the absolute path of the HRM file
     */
    void onAddExerciseForDroppedHrmFile(String hrmFilePath);

    /**
     * Assigns the specified HRM filename to the specified exercise (called when a HRM
     * file has been dropped on an exercise entry in the calendar view). If the exercise
     * is null, then a new exercise will be created and displayed in the
     * Exercise dialog (the HRM data will be imported).
     *
     * @param hrmFilePath the absolute path of the HRM file
     * @param exercise the exercise on which the file has been dropped
     */
    void onAssignDroppedHrmFileToExercise(String hrmFilePath, Exercise exercise);

    /**
     * Checks for existing sport types. A message dialog will be displayed when
     * there are no sport types available yet.
     *
     * @return true when there's at least one sport type
     */
    boolean checkForExistingSportTypes();

    /**
     * Checks for existing exercises. A message dialog will be displayed when
     * there are no exercises available yet.
     *
     * @return true when there's at least one exercise
     */
    boolean checkForExistingExercises();

    /**
     * Updates the complete view to show the current application data.
     */
    void updateView();

    /**
     * Updates the action status properties and the status bar content according to the
     * current entry selection in the current view. The actions are enabled or disabled
     * depending on the current entry selection, document state and displayed view.
     * The status bar displays a summary of the currently selected entries.
     */
    void updateActionsAndStatusBar();
}
