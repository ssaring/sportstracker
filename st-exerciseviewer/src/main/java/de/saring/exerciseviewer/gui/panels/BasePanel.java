package de.saring.exerciseviewer.gui.panels;

import de.saring.exerciseviewer.gui.PVContext;
import de.saring.exerciseviewer.gui.PVDocument;
import javax.swing.JPanel;

/**
 * This is the base class of all special Panel object of this application. It 
 * contains all the common functionality and defines some abstract methods, 
 * which needs to be implemented by the special Panel classes. The document must
 * be set manually for all panels.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public abstract class BasePanel extends JPanel {

    private PVContext context;
    private PVDocument document;

    /**
     * Standard c'tor.
     * @param context the ExerciseViewer context
     * @param document the ExerciseViewer document
     */
    public BasePanel (PVContext context) {
        this.context = context;
    }

    /**
     * Fills the controls of this panel with the current exercise values.
     */
    public abstract void displayExercise ();
    
    /**
     * Returns the ExerciseViewer context.
     * @return the ExerciseViewer context
     */
    protected PVContext getContext () {
        return context;
    }
    
    /**
     * Returns the ExerciseViewer document component (MVC).
     * @return the ExerciseViewer document
     */
    protected PVDocument getDocument () {
        return document;
    }

    /**
     * Sets the ExerciseViewer document component (MVC).
     * @param document the ExerciseViewer document
     */
    public void setDocument (PVDocument document) {
        this.document = document;
    }
}
