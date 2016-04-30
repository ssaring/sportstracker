package de.saring.sportstracker.gui;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.views.EntryViewEventHandler;
import javafx.event.ActionEvent;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.time.LocalDate;

/**
 * This interface provides all controller (MVC) functionality of the SportsTracker main application window.
 *
 * @author Stefan Saring
 */
public interface STController extends EntryViewEventHandler {

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
     * Event handler for action "Export to SQLite".
     */
    void onExportSqlite(ActionEvent event);

    /**
     * Event handler for action "Print current view".
     */
    void onPrint(ActionEvent event);

    /**
     * Event handler for action "Quit SportsTracker".
     */
    void onQuit(ActionEvent event);

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
}
