package de.saring.exerciseviewer.parser

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise

/**
 * This interface defines the functionality of any parser implementation,
 * which reads exercise files of heart rate monitor devices from a file.
 *
 * @author Stefan Saring
 */
interface ExerciseParser {

    /**
     * Returns the exercise parser information.
     *
     * @return the parser information
     */
    val info: ExerciseParserInfo

    /**
     * This method parses the specified exercise file and creates an EVExercise object from it.
     *
     * @param filename name of exercise file to parse
     * @return the parsed PVExercise object
     * @throws EVException thrown on read/parse problems
     */
    @Throws(EVException::class)
    fun parseExercise(filename: String): EVExercise
}
