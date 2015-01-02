package de.saring.exerciseviewer.gui.panels;

import java.io.IOException;

import javafx.scene.Parent;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.gui.javafx.FxmlLoader;

/**
 * Abstract Controller (MVC) base class of for all ExerciseViewer panels.
 *
 * @author Stefan Saring
 */
public abstract class AbstractPanelController {

    private final EVContext context;

    private EVDocument document;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     */
    public AbstractPanelController(final EVContext context) {
        this.context = context;
    }

    /**
     * Sets the ExerciseViewer model/document.
     *
     * @param document document instance
     */
    public void setDocument(final EVDocument document) {
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
            root = FxmlLoader.load(this.getClass().getResource(fxmlFilename), context.getFxResources()
                    .getResourceBundle(), this);
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
