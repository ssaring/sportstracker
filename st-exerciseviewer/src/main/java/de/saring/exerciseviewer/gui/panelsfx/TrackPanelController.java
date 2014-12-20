package de.saring.exerciseviewer.gui.panelsfx;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;

/**
 * Controller (MVC) class of the "Track" panel, which displays the recorded location data of
 * the exercise (if available) in a map.<br/>
 * The map component is JXMapKit from the SwingLabs project, the data provider is OpenStreetMap.
 *
 * @author Stefan Saring
 */
public class TrackPanelController extends AbstractPanelController {

    @FXML
    private SwingNode snMapViewer;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer model/document
     */
    public TrackPanelController(final EVContext context, final EVDocument document) {
        super(context, document);
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/TrackPanel.fxml";
    }

    @Override
    protected void setupPanel() {
        showExerciseData();
    }

    private void showExerciseData() {
        // TODO final EVExercise exercise = getDocument().getExercise();
        // TODO final FormatUtils formatUtils = getContext().getFormatUtils();
    }
}
