package de.saring.exerciseviewer.gui.panelsfx;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;

/**
 * Abstract Controller (MVC) base class of for all ExerciseViewer panels.
 *
 * @author Stefan Saring
 */
public abstract class AbstractPanelController {

    private EVContext context;
    private EVDocument document;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer model/document
     */
    public AbstractPanelController(final EVContext context, final EVDocument document) {
        this.context = context;
        this.document = document;
    }

    /**
     * Loads the panel content from FXML layout file and set up all the controls to shows the exercise data.
     *
     * @return the loaded and initialized panel content
     */
    public Parent loadAndSetupPanelContent() {
        final String fxmlFilename = getFxmlFilename();
        Parent root;

        try {
            // Guice and so GuiceLoader can't be used here, see comments in EVMain
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource(fxmlFilename));
            loader.setResources(context.getFxResources().getResourceBundle());
            loader.setControllerFactory(controllerClass -> this);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the FXML resource '" + fxmlFilename + "'!", e);
        }

        setupPanel();
        return root;
    }

    /**
     * Returns the name of the FXML file which contains the panel UI definition.
     *
     * @return FXML filename
     */
    protected abstract String getFxmlFilename();

    /**
     * Sets up all the panel controls and displays the exercise data. Will be called after
     * the UI has been loaded from FXML.
     */
    protected abstract void setupPanel();

    /**
     * Returns the ExerciseViewer UI context.
     *
     * @return EVContext
     */
    protected EVContext getContext() {
        return context;
    }

    /**
     * Returns the ExerciseViewer model/document.
     *
     * @return EVDocument
     */
    protected EVDocument getDocument() {
        return document;
    }
}
