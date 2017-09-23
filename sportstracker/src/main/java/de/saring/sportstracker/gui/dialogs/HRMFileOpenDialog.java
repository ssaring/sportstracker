package de.saring.sportstracker.gui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.inject.Inject;

import de.saring.exerciseviewer.parser.ExerciseParserFactory;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;
import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.gui.STContext;

/**
 * File Open dialog for HRM file selection.
 *
 * @author Stefan Saring
 */
public class HRMFileOpenDialog {

    private final STContext context;

    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     */
    @Inject
    public HRMFileOpenDialog(STContext context) {
        this.context = context;
    }

    /**
     * Displays the HRM File Open dialog and returns the selected file or null
     * when the user has nothing selected.
     *
     * @param parent the parent window
     * @param options the application options
     * @param initialFile the filename to be initially selected (optional)
     * @return the selected file or null when nothing selected
     */
    public File selectHRMFile(final Window parent, final STOptions options, final String initialFile) {

        // create file chooser
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(context.getResources().getString("st.dlg.hrm_file_open.title"));
        addFileExtensionFilters(fileChooser);

        // do we need to select an initial file ?
        final File fInitialFile = initialFile == null ? null : new File(initialFile);
        if (fInitialFile != null && fInitialFile.exists() && fInitialFile.isFile()) {
            fileChooser.setInitialDirectory(fInitialFile.getParentFile());
        } else {
            // use previous exercise directory as initial directory when available
            File initialDirectory;
            String strPreviousExerciseDirectory = options.getPreviousExerciseDirectory();

            if (strPreviousExerciseDirectory != null) {
                initialDirectory = new File(strPreviousExerciseDirectory);
            } else {
                // on first selection: use the user home directory
                initialDirectory = new File(System.getProperty("user.home"));
            }

            if (initialDirectory.exists()) {
                fileChooser.setInitialDirectory(initialDirectory);
            }
        }

        // display file chooser
        final File selectedFile = fileChooser.showOpenDialog(parent);
        if (selectedFile == null) {
            // nothing selected
            return null;
        }

        // store selected directory and return the selected file
        options.setPreviousExerciseDirectory(
                selectedFile.getParentFile().getAbsolutePath());
        return selectedFile;
    }

    /**
     * Adds the file extension filters for all supported parsers.
     *
     * @param fileChooser file chooser to add to
     */
    private void addFileExtensionFilters(FileChooser fileChooser) {
        List<ExerciseParserInfo> parserInfos = ExerciseParserFactory.INSTANCE.getExerciseParserInfos();
        List<String> lAllExtensions = new ArrayList<>();

        // append a file filter for all files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                context.getResources().getString("st.dlg.hrm_file_open.filter_all_files"), "*.*"));

        parserInfos.forEach(parserInfo -> {
            final String filterName = String.format(
                    context.getResources().getString("st.dlg.hrm_file_open.filter_specific"), parserInfo.getName());

            // extend all filename suffixes with prefix "*."
            final List<String> extendedSuffixes = Stream.of(parserInfo.getSuffixes())
                    .map(suffix -> "*." + suffix)
                    .collect(Collectors.toList());

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterName, extendedSuffixes));

            lAllExtensions.addAll(extendedSuffixes);
        });

        // append a file filter for all ExerciseViewer file extensions
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                context.getResources().getString("st.dlg.hrm_file_open.filter_all"), lAllExtensions));
    }
}
