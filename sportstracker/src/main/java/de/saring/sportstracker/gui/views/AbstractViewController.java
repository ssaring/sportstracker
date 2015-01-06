package de.saring.sportstracker.gui.views;

import java.io.IOException;

import javafx.scene.Parent;

import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.FxmlLoader;

/**
 * Abstract Controller (MVC) base class of for all SportsTracker content views.
 *
 * @author Stefan Saring
 */
public abstract class AbstractViewController {

    private final STContext context;
    private final STDocument document;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     */
    public AbstractViewController(final STContext context, final STDocument document) {
        this.context = context;
        this.document = document;
    }

    /**
     * Loads the view content from FXML and set up all the controls.
     *
     * @return the loaded and initialized panel content
     */
    public Parent loadAndSetupViewContent() {
        final String fxmlFilename = getFxmlFilename();
        Parent root;

        try {
            root = FxmlLoader.load(this.getClass().getResource(fxmlFilename), //
                    context.getResources().getResourceBundle(), this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the FXML resource '" + fxmlFilename + "'!", e);
        }

        setupView();
        return root;
    }

    /**
     * Returns the name of the FXML file which contains the UI definition of the view.
     *
     * @return FXML filename
     */
    protected abstract String getFxmlFilename();

    /**
     * Sets up all the view controls. Will be called after the UI has been loaded from FXML.
     */
    protected abstract void setupView();

    /**
     * Returns the SportsTracker UI context.
     *
     * @return STContext
     */
    protected STContext getContext() {
        return context;
    }

    /**
     * Returns the SportsTracker model/document.
     *
     * @return STDocument
     */
    protected STDocument getDocument() {
        return document;
    }
}
