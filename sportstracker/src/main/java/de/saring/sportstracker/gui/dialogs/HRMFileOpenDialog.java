package de.saring.sportstracker.gui.dialogs;

import com.google.inject.Inject;
import de.saring.exerciseviewer.parser.ExerciseParserFactory;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;
import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.gui.STContext;
import de.saring.util.ResourceReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * File Open dialog for HRM file selection.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class HRMFileOpenDialog {

    private static final String DEFAULT_S710_DATA_DIRECTORY = "/var/polar";
    
    private STContext context;

    /**
     * Standard c'tor.
     * @param context the SportsTracker context
     */
    @Inject
    public HRMFileOpenDialog (STContext context) {
        this.context = context;
    }

    /**
     * Displays the HRM File Open dialog and returns the selected file or null
     * when the user has nothing selected.
     * @param options the application aptions
     * @param initialFile the filename to be initially selected (optional)
     * @return the selected file or null when nothing selected
     */
    public File selectHRMFile (STOptions options, File initialFile) {
        
        // create file chooser
        JFileChooser fChooser = new JFileChooser ();
        fChooser.setDialogTitle (context.getResReader ().getString ("st.dlg.hrm_file_open.title"));
        addFileFilters (fChooser, context.getResReader ());
        
        // do we need to select an inital file ?
        if (initialFile != null) {
            fChooser.setSelectedFile (initialFile);
        }
        else {
            // use previous exercise directory as initial directory when available
            File initialDirectory = null;
            String strPreviousExerciseDirectory = options.getPreviousExerciseDirectory ();
            
            if (strPreviousExerciseDirectory != null) {
                initialDirectory = new File (strPreviousExerciseDirectory);
            }
            else {
                // on first selection: try to use "/var/polar" (default file directory 
                // of s710 tool) as start directory if it exists
                initialDirectory = new File (DEFAULT_S710_DATA_DIRECTORY);
            }
            
            if (initialDirectory != null && initialDirectory.exists ()) {
                fChooser.setCurrentDirectory (initialDirectory);
            }
        }
        
        // display file chooser
        int option = fChooser.showOpenDialog (context.getMainFrame ());
        if (option != JFileChooser.APPROVE_OPTION) {
            // nothing selected
            return null;
        }
        
        // store selected directory and return the selected file
        File selectedFile = fChooser.getSelectedFile ();
        options.setPreviousExerciseDirectory (
            selectedFile.getParentFile ().getAbsolutePath ());
        return selectedFile;
    }
    
    /** 
     * Adds the file filters for all supported parsers.
     * @param fChooser file chooser to add to
     * @param resReader the ResourceReader helper class
     */
    private void addFileFilters (JFileChooser fChooser, ResourceReader resReader) {        
        List<ExerciseParserInfo> parserInfos = ExerciseParserFactory.getExerciseParserInfos ();

        // append a file filter for each exercise parsers in the registry
        ArrayList<String> lAllExtensions = new ArrayList<String> ();
        for (ExerciseParserInfo parserInfo : parserInfos) {
            
            fChooser.addChoosableFileFilter (new FileNameExtensionFilter (
                resReader.getString ("st.dlg.hrm_file_open.filter_specific", parserInfo.getName ()), 
                parserInfo.getSuffixes ()));
            
            // append the parsers suffixes to the list of all extensions
            for (String extension : parserInfo.getSuffixes ()) {
                lAllExtensions.add (extension);
            }
        }
        
        // append a file filter for all ExerciseViewer file extensions
        fChooser.addChoosableFileFilter (new FileNameExtensionFilter (
            resReader.getString ("st.dlg.hrm_file_open.filter_all"), lAllExtensions.toArray(new String[0])));
    }
}
