package de.saring.exerciseviewer.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.swing.JOptionPane;
import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.core.EVOptions;

/**
 * This is the main class of the ExerciseViewer which start the "sub-application" 
 * (is a child-dialog of the parent frame). It creates the document and the view 
 * instances of ExerciseViewer.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public class EVMain {
    private static final Logger LOGGER = Logger.getLogger (EVMain.class.getName ()); 
    
    private EVContext context;
    private EVDocument document;
    private EVView view;

    /**
     * Standard c'tor.
     * @param context the ExerciseViewer context
     * @param document the ExerciseViewer document component
     * @param view the ExerciseViewer view component
     */
    @Inject
    public EVMain (EVContext context, EVDocument document, EVView view) {
        this.context = context;
        this.document = document;
        this.view = view;
    }
    
    /**
     * Displays the exercise specified by the filename in the ExerciseViewer dialog.
     * @param exerciseFilename exercise file to display
     * @param options the options to be used in ExerciseViewer 
     * @param modal pass true when the dialog must be modal
     */
    public void showExercise (String exerciseFilename, EVOptions options, boolean modal) {
        
        // init document and load exercise file
        document.setOptions (options);        
        try {
            document.openExerciseFile (exerciseFilename);
        } 
        catch (EVException pe) {
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

        // display ExerciseViewer dialog
        context.showDialog (view);
    }
}
