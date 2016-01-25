package de.saring.sportstracker.gui.views;

import de.saring.sportstracker.data.Exercise;
import javafx.event.ActionEvent;

import java.time.LocalDate;

/**
 * Interface for handling all events which are triggered by the entry views, but can't be handled by the entry view
 * controller itself.
 *
 * @author Stefan Saring
 */
public interface EntryViewEventHandler {

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
     * Updates the action status properties and the status bar content according to the
     * current entry selection in the current view. The actions are enabled or disabled
     * depending on the current entry selection, document state and displayed view.
     * The status bar displays a summary of the currently selected entries.
     */
    void updateActionsAndStatusBar();
}
