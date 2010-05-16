package de.saring.polarviewer.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import javax.swing.JOptionPane;
import de.saring.polarviewer.core.PVException;
import de.saring.polarviewer.core.PVOptions;

/**
 * This is the main class of the PolarViewer which start the "sub-application" 
 * (is a child-dialog of the parent frame). It creates the document and the view 
 * instances of PolarViewer.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public class PVMain {
    private static final Logger LOGGER = Logger.getLogger (PVMain.class.getName ()); 
    
    private PVContext context;
    private PVDocument document;
    private PVView view;

    /**
     * Standard c'tor.
     * @param context the PolarViewer context
     * @param document the PolarViewer document component
     * @param view the PolarViewer view component
     */
    @Inject
    public PVMain (PVContext context, PVDocument document, PVView view) {
        this.context = context;
        this.document = document;
        this.view = view;
    }
    
    /**
     * Displays the exercise specified by the filename in the PolarViewer dialog.
     * @param exerciseFilename exercise file to display
     * @param options the options to be used in PolarViewer 
     * @param modal pass true when the dialog must be modal
     */
    public void showExercise (String exerciseFilename, PVOptions options, boolean modal) {
        
        // init document and load exercise file
        document.setOptions (options);        
        try {
            document.openExerciseFile (exerciseFilename);
        } 
        catch (PVException pe) {
            LOGGER.log (Level.SEVERE, "Failed to open exercise file " + exerciseFilename + "!", pe);
            JOptionPane.showMessageDialog (context.getMainFrame (),
                context.getResReader ().getString ("pv.error.read_exercise", exerciseFilename),
                context.getResReader ().getString ("common.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }        

        // init view
        view.initView (document);
        view.setModal (modal);
        view.displayExercise ();

        // display PolarViewer dialog
        context.showDialog (view);
    }
}
