package de.saring.sportstracker.gui.views;

import de.saring.sportstracker.core.STException;
import de.saring.util.data.IdObject;

/**
 * Interface for all content view controllers for displaying SportsTracker entries
 * (e.g. in a list or a calendar).
 *
 * @author Stefan Saring
 */
public interface EntryViewController {

    /**
     * Enumeration of all available entry view types.
     */
    enum ViewType {
        CALENDAR, EXERCISE_LIST, NOTE_LIST, WEIGHT_LIST
    }

    /**
     * Updates the view after data was modified.
     */
    void updateView();

    /**
     * This methods returns the number of selected exercises.
     *
     * @return number of selected exercises
     */
    int getSelectedExerciseCount();

    /**
     * This methods returns the list of the currently selected exercise ID's.
     *
     * @return array of the selected exercise ID's (can be empty)
     */
    int[] getSelectedExerciseIDs();

    /**
     * This methods returns the number of selected notes.
     *
     * @return number of selected notes
     */
    int getSelectedNoteCount();

    /**
     * This methods returns the list of the currently selected note ID's.
     *
     * @return array of the selected note ID's (can be empty)
     */
    int[] getSelectedNoteIDs();

    /**
     * This methods returns the number of selected weights.
     *
     * @return number of selected weights
     */
    int getSelectedWeightCount();

    /**
     * This methods returns the list of the currently selected weight ID's.
     *
     * @return array of the selected weight ID's (can be empty)
     */
    int[] getSelectedWeightIDs();

    /**
     * Selects the specified entry (of type Exercise, Weight or Note) in this
     * view. Not all views support all entry types (e.g. no Weight entry in
     * Note list). If not supported or the entry is not displayed, this method
     * does nothing.
     *
     * @param entry the entry to select
     */
    void selectEntry(IdObject entry);

    /**
     * Removes the current selection in the page.
     */
    void removeSelection();

    /**
     * Prints the entries displayed in this view.
     *
     * @throws de.saring.sportstracker.core.STException on printing problems
     */
    void print() throws STException;
}
