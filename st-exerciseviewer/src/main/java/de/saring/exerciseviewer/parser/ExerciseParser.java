package de.saring.exerciseviewer.parser;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;

/**
 * This interface defines the functionality of any parser implementation,
 * which reads exercise files of heart rate monitor devices from a file.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public interface ExerciseParser {

    /**
     * Returns the informations about this parser implementation.
     * @return the parser informations
     */
    public ExerciseParserInfo getInfo ();
        
    /**
     * This method parses the specified exercise file and creates an
     * PVExercise object from it.
     *
     * @param filename name of exercise file to parse
     * @return the parsed PVExercise object
     * @throws EVException thrown on read/parse problems
     */
    public EVExercise parseExercise (String filename) throws EVException;
}
