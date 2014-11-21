package de.saring.exerciseviewer.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.inject.Inject;

import de.saring.exerciseviewer.core.EVOptions;

/**
 * This is the main class of the ExerciseViewer which start the "sub-application"
 * (is a child-dialog of the parent frame). It creates the document and the controller
 * instances of ExerciseViewer.
 *
 * @author Stefan Saring
 */
public class EVMain {

    private static final Logger LOGGER = Logger.getLogger(EVMain.class.getName());

    private static final String DIALOG_NAME = "ExerciseViewer";

    private final EVContext context;
    private final EVDocument document;
    private final EVController controller;

    /**
     * Standard c'tor.
     *
     * @param context the ExerciseViewer context
     */
    @Inject
    public EVMain(final EVContext context) {
        this.context = context;

        // Guice can't be used inside ExerciseViewer for Dependency Injection, it doesn't support a window scope
        // (ExerciseViewer can be started multiple times, each instance needs it own document and controller).
        // Workaround: manual Dependency Injection.
        this.document = new EVDocument();
        this.controller = new EVController(context, document);
    }

    /**
     * Displays the exercise specified by the filename in the ExerciseViewer dialog.
     *
     * @param exerciseFilename exercise file to display
     * @param options the options to be used in ExerciseViewer
     * @param parent parent window of this dialog
     * @param modal pass true when the dialog must be modal
     */
    public void showExercise(final String exerciseFilename, final EVOptions options, final Window parent,
            final boolean modal) {

        // init document and load exercise file
        document.setOptions(options);

        try {
            document.openExerciseFile(exerciseFilename);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to open exercise file " + exerciseFilename + "!", e);
            context.showFxMessageDialog(parent, Alert.AlertType.ERROR, "common.error",
                    "pv.error.read_exercise_console", exerciseFilename);
            return;
        }

        // create stage
        final Stage stage = new Stage();
        stage.initOwner(parent);
        stage.setAlwaysOnTop(true);
        stage.initModality(modal ? Modality.APPLICATION_MODAL : Modality.NONE);
        stage.setTitle(DIALOG_NAME + " - " + document.getExerciseFilename());

        // init controller and show dialog
        controller.show(stage);
    }
}
