package de.saring.polarviewer.gui.panels;

import de.saring.polarviewer.gui.PVContext;
import de.saring.polarviewer.gui.PVDocument;
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
     * @param context the PolarViewer context
     * @param document the PolarViewer document
     */
    public BasePanel (PVContext context) {
        this.context = context;
    }

    /**
     * Fills the controls of this panel with the current exercise values.
     */
    public abstract void displayExercise ();
    
    /**
     * Returns the PolarViewer context.
     * @return the PolarViewer context
     */
    protected PVContext getContext () {
        return context;
    }
    
    /**
     * Returns the PolarViewer document component (MVC).
     * @return the PolarViewer document
     */
    protected PVDocument getDocument () {
        return document;
    }

    /**
     * Sets the PolarViewer document component (MVC).
     * @param document the PolarViewer document
     */
    public void setDocument (PVDocument document) {
        this.document = document;
    }
}
