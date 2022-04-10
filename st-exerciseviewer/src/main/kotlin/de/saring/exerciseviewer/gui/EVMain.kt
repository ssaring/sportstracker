package de.saring.exerciseviewer.gui

import de.saring.exerciseviewer.core.EVOptions
import de.saring.util.gui.javafx.WindowBoundsPersistence
import de.saring.util.unitcalc.SpeedMode
import javafx.scene.control.Alert
import javafx.stage.Modality
import javafx.stage.Stage
import java.util.logging.Level
import java.util.logging.Logger
import jakarta.inject.Inject

/**
 * This is the main class of the ExerciseViewer "sub-application" (is a child-dialog of the parent frame).
 * It creates all the components via Guice dependency injection and starts the ExerciseViewer for the passed exercise.
 *
 * @constructor constructor for dependency injection
 * @property context the ExerciseViewer UI context
 * @param options the options to be used in ExerciseViewer
 *
 * @author Stefan Saring
 */
class EVMain @Inject constructor(
        private val context: EVContext,
        options: EVOptions) {

    private val logger = Logger.getLogger(EVMain::class.java.name)
    private val dialogName = "ExerciseViewer"

    // create ExerciseViewer components by using manual dependency injection
    // => Guice can't be used here, it does not provide a scope for dialogs
    // => Guice-Workaround would be the use of a new Injector per EV window,
    // but this costs performance and can cause memory leaks
    private val document = EVDocument(options)
    private val controller = EVController(context, document)

    /**
     * Displays the exercise specified by the filename in the ExerciseViewer dialog.
     *
     * @param exerciseFilename exercise file to display
     * @param parent parent stage/window of this dialog
     * @param modal pass true when the dialog must be modal
     * @param speedMode the speed mode to be used for showing speed values
     */
    fun showExercise(exerciseFilename: String, parent: Stage, modal: Boolean, speedMode: SpeedMode) {

        // load exercise file
        try {
            document.openExerciseFile(exerciseFilename, speedMode)
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Failed to open exercise file $exerciseFilename!", e)
            context.showMessageDialog(parent, Alert.AlertType.ERROR, //
                    "common.error", "pv.error.read_exercise_console", exerciseFilename)
            return
        }

        // create stage
        val stage = Stage()
        stage.initOwner(parent)
        stage.initModality(if (modal) Modality.APPLICATION_MODAL else Modality.NONE)
        stage.title = "$dialogName - ${document.exerciseFilename}"
        stage.icons.setAll(parent.icons)
        WindowBoundsPersistence.addWindowBoundsPersistence(stage, dialogName)

        // init controller and show dialog
        controller.show(stage)
    }
}
