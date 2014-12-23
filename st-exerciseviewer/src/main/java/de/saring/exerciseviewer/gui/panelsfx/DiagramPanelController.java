package de.saring.exerciseviewer.gui.panelsfx;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;

/**
 * Controller (MVC) class of the "Samples" panel, which displays the exercise graphically
 * (heartrate, altitude, speed and cadence).
 *
 * @author Stefan Saring
 */
public class DiagramPanelController extends AbstractPanelController {

    @FXML
    private ChoiceBox<AxisType> cbLeftAxis;
    @FXML
    private ChoiceBox<AxisType> cbRightAxis;
    @FXML
    private ChoiceBox<AxisType> cbBottomAxis;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer model/document
     */
    public DiagramPanelController(final EVContext context, final EVDocument document) {
        super(context, document);
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/DiagramPanel.fxml";
    }

    @Override
    protected void setupPanel() {
        // TODO
    }

    /**
     * The list of possible value types to be shown on the diagram axes.
     */
    private enum AxisType {
        Nothing, Heartrate, Altitude, Speed, Cadence, Temperature, Time, Distance;
    }
}
