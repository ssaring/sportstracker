package de.saring.exerciseviewer.gui

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.core.EVOptions
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParserFactory

/**
 * This class contains all model / document (MVC) related data and functionality of the ExerciseViewer application.
 *
 * @property options The ExerciseViewer options.
 *
 * @author Stefan Saring
 */
class EVDocument(val options: EVOptions) {

    /** The current exercise to be displayed.  */
    lateinit var exercise: EVExercise

    /** The filename of the current exercise.  */
    lateinit var exerciseFilename: String

    /**
     * Reads the specified exercise file and stores it in the document.
     *
     * @param filename exercise filename
     * @throws EVException on parsing problems
     */
    fun openExerciseFile(filename: String) {

        val parser = ExerciseParserFactory.getParser(filename)
        exercise = parser.parseExercise(filename)
        exerciseFilename = filename
    }
}
