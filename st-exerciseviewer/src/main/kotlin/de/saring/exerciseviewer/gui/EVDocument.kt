package de.saring.exerciseviewer.gui

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.core.EVOptions
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParserFactory
import de.saring.util.unitcalc.FormatUtils.SpeedMode

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

    /** The speed mode to be used for showing the speed values of the current exercise. */
    lateinit var speedMode: SpeedMode

    /**
     * Reads the specified exercise file and stores it in the document.
     *
     * @param filename exercise filename
     * @param speedMode the speed mode to be used for showing speed values
     * @throws EVException on parsing problems
     */
    fun openExerciseFile(filename: String, speedMode: SpeedMode) {

        val parser = ExerciseParserFactory.getParser(filename)
        exercise = parser.parseExercise(filename)
        exerciseFilename = filename
        this.speedMode = speedMode
    }
}
