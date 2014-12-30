package de.saring.exerciseviewer.gui;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.core.EVOptions;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.ExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserFactory;

/**
 * This class contains all document (MVC) related data and functionality of the
 * ExerciseViewer application.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class EVDocument {

    /**
     * The current exercise to be displayed.
     */
    private EVExercise exercise;

    /**
     * The filename of the current exercise.
     */
    private String exerciseFilename;

    /**
     * The ExerciseViewer options.
     */
    private EVOptions options;


    /**
     * Reads the specified exercise file and stores it in the document.
     *
     * @param filename exercise filename
     * @throws EVException on parsing problems
     */
    public void openExerciseFile(String filename) throws EVException {

        ExerciseParser parser = ExerciseParserFactory.getParser(filename);
        exercise = parser.parseExercise(filename);
        exerciseFilename = filename;
    }

    public EVExercise getExercise() {
        return exercise;
    }

    public String getExerciseFilename() {
        return exerciseFilename;
    }

    public EVOptions getOptions() {
        return options;
    }

    public void setOptions(EVOptions options) {
        this.options = options;
    }
}
