package de.saring.polarviewer.parser;

import de.saring.polarviewer.core.PVException;
import de.saring.polarviewer.data.PVExercise;

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
     * @throws PVException thrown on read/parse problems
     */
    public PVExercise parseExercise (String filename) throws PVException;
}
