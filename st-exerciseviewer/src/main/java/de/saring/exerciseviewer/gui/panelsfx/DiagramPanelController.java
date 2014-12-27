package de.saring.exerciseviewer.gui.panelsfx;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.util.AppResources;
import de.saring.util.unitcalc.FormatUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import javafx.util.StringConverter;

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
        setupAxisChoiceBoxes();
        // TODO updateDiagram();
    }

    /**
     * Setup of the axis choice boxes.
     */
    private void setupAxisChoiceBoxes() {
        EVExercise exercise = getDocument().getExercise();

        // setup axis type name converter
        final AxisTypeStringConverter axisTypeStringConverter = new AxisTypeStringConverter(
                getContext().getFxResources(), getContext().getFormatUtils());

        cbLeftAxis.setConverter(axisTypeStringConverter);
        cbRightAxis.setConverter(axisTypeStringConverter);
        cbBottomAxis.setConverter(axisTypeStringConverter);

        // fill axes with all possible types depending on the exercise recording mode
        cbLeftAxis.getItems().add(AxisType.HEARTRATE);
        cbRightAxis.getItems().addAll(AxisType.NOTHING, AxisType.HEARTRATE);
        cbBottomAxis.getItems().add(AxisType.TIME);

        cbLeftAxis.getSelectionModel().select(0);
        cbRightAxis.getSelectionModel().select(0);
        cbBottomAxis.getSelectionModel().select(0);

        // add altitude items if recorded
        if (exercise.getRecordingMode().isAltitude()) {
            cbLeftAxis.getItems().addAll(AxisType.ALTITUDE);
            cbRightAxis.getItems().add(AxisType.ALTITUDE);
        }

        // add speed and distance items if recorded
        if (exercise.getRecordingMode().isSpeed()) {
            cbLeftAxis.getItems().add(AxisType.SPEED);
            cbRightAxis.getItems().add(AxisType.SPEED);
            cbBottomAxis.getItems().add(AxisType.DISTANCE);
        }

        // add cadence items if recorded
        if (exercise.getRecordingMode().isCadence()) {
            cbLeftAxis.getItems().add(AxisType.CADENCE);
            cbRightAxis.getItems().add(AxisType.CADENCE);
        }

        // add temperature items if recorded
        if (exercise.getRecordingMode().isTemperature()) {
            cbLeftAxis.getItems().add(AxisType.TEMPERATURE);
            cbRightAxis.getItems().add(AxisType.TEMPERATURE);
        }

        // do we need to display the second diagram too?
        if (getDocument().getOptions().isDisplaySecondDiagram()) {
            // it's only possible when additional data is available (first 2 entries
            // are nothing and heartrate, which is already displayed)
            if (cbRightAxis.getItems().size() > 2) {
                cbRightAxis.getSelectionModel().select(2);
            }
        }
    }

    /**
     * The list of possible value types to be shown on the diagram axes. This enum also provides the
     * the localized displayed enum names.
     */
    private enum AxisType {
        NOTHING, HEARTRATE, ALTITUDE, SPEED, CADENCE, TEMPERATURE, TIME, DISTANCE
    }

    /**
     * StringConverter for the axis type choice boxes. It returns the name to be displayed for all
     * the available axis types.
     */
    private static class AxisTypeStringConverter extends StringConverter<AxisType> {

        private AppResources appResources;
        private FormatUtils formatUtils;

        /**
         * Default c'tor.
         *
         * @param appResources application resources for I18N
         * @param formatUtils current format utils instance
         */
        public AxisTypeStringConverter(final AppResources appResources, final FormatUtils formatUtils) {
            this.appResources = appResources;
            this.formatUtils = formatUtils;
        }

        @Override
        public String toString(final AxisType axisType) {
            switch (axisType) {
                case NOTHING:
                    return appResources.getString("pv.diagram.axis.nothing");
                case HEARTRATE:
                    return appResources.getString("pv.diagram.axis.heartrate");
                case ALTITUDE:
                    return appResources.getString("pv.diagram.axis.altitude", formatUtils.getAltitudeUnitName());
                case SPEED:
                    return appResources.getString("pv.diagram.axis.speed", formatUtils.getSpeedUnitName());
                case CADENCE:
                    return appResources.getString("pv.diagram.axis.cadence");
                case TEMPERATURE:
                    return appResources.getString("pv.diagram.axis.temperature", formatUtils.getTemperatureUnitName());
                case TIME:
                    return appResources.getString("pv.diagram.axis.time");
                case DISTANCE:
                    return appResources.getString("pv.diagram.axis.distance", formatUtils.getDistanceUnitName());
                default:
                    throw new IllegalArgumentException("Invalid AxisType: '" + axisType + "'!");
            }
        }

        @Override
        public AxisType fromString(final String string) {
            throw new UnsupportedOperationException();
        }
    }
}
