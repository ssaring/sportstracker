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
     * The list of possible value types to be shown on the diagram axes. This enum also
     * provides the keys of the localized displayed enum names.
     */
    private enum AxisType {
        NOTHING("pv.diagram.axis.nothing"),
        HEARTRATE("pv.diagram.axis.heartrate"),
        ALTITUDE("pv.diagram.axis.altitude"),
        SPEED("pv.diagram.axis.speed"),
        CADENCE("pv.diagram.axis.cadence"),
        TEMPERATURE("pv.diagram.axis.temperature"),
        TIME("pv.diagram.axis.time"),
        DISTANCE("pv.diagram.axis.distance");

        private String resourceKey;

        private AxisType(final String resourceKey) {
            this.resourceKey = resourceKey;
        }

        public String getResourceKey() {
            return resourceKey;
        }
    }
}
