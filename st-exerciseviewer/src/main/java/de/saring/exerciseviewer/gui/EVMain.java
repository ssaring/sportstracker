package de.saring.exerciseviewer.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.inject.Inject;

import de.saring.exerciseviewer.core.EVOptions;

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
     */
    @Inject
    public EVMain(final EVContext context) {

        // The ExerciseViewer sub-application can be started multiple times in parallel, each with its
        // own document and controller objects which must be separated for all ExerciseViewer instances.
        // => Guice does not provide such a "Window" scope, so each ExerciseViewer uses its own Injector
        // => so each ExerciseViewer has it's own injected @Singleton components
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            public void configure() {
                // use context provided by the SportsTracker application
                bind(EVContext.class).toInstance(context);
            }
        });

        // get the ExerciseViewer components via dependency injection
        this.document = injector.getInstance(EVDocument.class);
        this.controller = injector.getInstance(EVController.class);
        this.context = injector.getInstance(EVContext.class);
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
            context.showMessageDialog(parent, Alert.AlertType.ERROR, "common.error",
                    "pv.error.read_exercise_console", exerciseFilename);
            return;
        }

        // create stage
        final Stage stage = new Stage();
        stage.initOwner(parent);
        stage.initModality(modal ? Modality.APPLICATION_MODAL : Modality.NONE);
        stage.setTitle(DIALOG_NAME + " - " + document.getExerciseFilename());

        // init controller and show dialog
        controller.show(stage);
    }
}
