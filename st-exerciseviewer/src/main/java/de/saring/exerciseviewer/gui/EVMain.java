package de.saring.exerciseviewer.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;

import de.saring.exerciseviewer.core.EVOptions;
import de.saring.util.gui.javafx.WindowBoundsPersistence;

/**
 * This is the main class of the ExerciseViewer "sub-application" (is a child-dialog of the parent frame).
 * It creates all the components via Guice dependency injection and starts the ExerciseViewer for the passed
 * exercise.
 *
 * @author Stefan Saring
 */
public class EVMain {

    private static final Logger LOGGER = Logger.getLogger(EVMain.class.getName());

    private static final String DIALOG_NAME = "ExerciseViewer";

    private final EVDocument document;
    private final EVController controller;
    private final EVContext context;

    /**
     * Standard c'tor.
     *
     * @param context the ExerciseViewer context
     * @param options the options to be used in ExerciseViewer
     */
    @Inject
    public EVMain(final EVContext context, final EVOptions options) {

        // create ExerciseViewer components by using manual dependency injection
        // => Guice can't be used here, it does not provide a scope for dialogs
        // => Guice-Workaround would be the use of a new Injector per EV window,
        // but this costs performance and can cause memory leaks
        this.context = context;
        this.document = new EVDocument(options);
        this.controller = new EVController(context, document);
    }

    /**
     * Displays the exercise specified by the filename in the ExerciseViewer dialog.
     *
     * @param exerciseFilename exercise file to display
     * @param options the options to be used in ExerciseViewer
     * @param parent parent stage/window of this dialog
     * @param modal pass true when the dialog must be modal
     */
    public void showExercise(final String exerciseFilename, final EVOptions options, final Stage parent,
            final boolean modal) {

        // load exercise file
        try {
            document.openExerciseFile(exerciseFilename);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to open exercise file " + exerciseFilename + "!", e);
            context.showMessageDialog(parent, Alert.AlertType.ERROR, //
                    "common.error", "pv.error.read_exercise_console", exerciseFilename);
            return;
        }

        // create stage
        final Stage stage = new Stage();
        stage.initOwner(parent);
        stage.initModality(modal ? Modality.APPLICATION_MODAL : Modality.NONE);
        stage.setTitle(DIALOG_NAME + " - " + document.getExerciseFilename());
        stage.getIcons().setAll(parent.getIcons());
        WindowBoundsPersistence.addWindowBoundsPersistence(stage, "ExerciseViewer");

        // init controller and show dialog
        controller.show(stage);
    }
}
