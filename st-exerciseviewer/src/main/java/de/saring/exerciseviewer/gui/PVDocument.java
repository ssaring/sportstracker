package de.saring.exerciseviewer.gui;

import de.saring.exerciseviewer.core.PVException;
import de.saring.exerciseviewer.core.PVOptions;
import de.saring.exerciseviewer.data.PVExercise;
import de.saring.exerciseviewer.parser.ExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserFactory;

/**
 * This class contains all document (MVC) related data and functionality of the 
 * ExerciseViewer application.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class PVDocument {
    
    /** The current exercise to be displayed. */
    private PVExercise exercise;
    
    /** The filename of the current exercise. */
    private String exerciseFilename;

    /** The ExerciseViewer options. */
    private PVOptions options;
    

    /**
     * Reads the specified exercise file and stores it in the document.
     * @param filename exercise filename
     * @throws PVException on parsing problems
     */
    public void openExerciseFile (String filename) throws PVException {        
        
        ExerciseParser parser = ExerciseParserFactory.getParser (filename);
        exercise = parser.parseExercise (filename);
        exerciseFilename = filename;
    }

    // ***** BEGIN: Getters and Setters ***** //
    
    public PVExercise getExercise () {
        return exercise;
    }

    public String getExerciseFilename () {
        return exerciseFilename;
    }

    public PVOptions getOptions () {
        return options;
    }

    public void setOptions (PVOptions options) {
        this.options = options;
    }

    // ***** END: Getters and Setters ***** //
}
