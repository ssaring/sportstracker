package de.saring.sportstracker.gui;

import javafx.event.ActionEvent;
import javafx.stage.WindowEvent;

/**
 * This interface provides all controller (MVC) functionality of the SportsTracker main application window.
 *
 * @author Stefan Saring
 */
public interface STController {

    /**
     * Called when the user wants to close the application.
     *
     * @param event WindowEvent
     */
    void onWindowCloseRequest(WindowEvent event);

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
     * Event handler for action "About SportsTracker".
     */
    void onAbout(ActionEvent event);
}
