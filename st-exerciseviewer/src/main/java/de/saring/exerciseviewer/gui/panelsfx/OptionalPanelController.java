package de.saring.exerciseviewer.gui.panelsfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Controller (MVC) class of the "Optional" panel, which displays all the optional exercise data,
 * such as speed, altitude ...
 *
 * @author Stefan Saring
 */
public class OptionalPanelController extends AbstractPanelController {

    @FXML
    private Label laSpeedAvgValue;
    @FXML
    private Label laSpeedMaxValue;
    @FXML
    private Label laDistanceValue;
    @FXML
    private Label laBikeNrValue;

    @FXML
    private Label laCadenceAvgValue;
    @FXML
    private Label laCadenceMaxValue;

    @FXML
    private Label laAltitudeMinValue;
    @FXML
    private Label laAltitudeAvgValue;
    @FXML
    private Label laAltitudeMaxValue;
    @FXML
    private Label laAscentValue;

    @FXML
    private Label laTemperatureMinValue;
    @FXML
    private Label laTemperatureAvgValue;
    @FXML
    private Label laTemperatureMaxValue;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer model/document
     */
    public OptionalPanelController(final EVContext context, final EVDocument document) {
        super(context, document);
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/OptionalPanel.fxml";
    }

    @Override
    protected void setupPanel() {
        showExerciseData();
    }

    private void showExerciseData() {
        final EVExercise exercise = getDocument().getExercise();
        final FormatUtils formatUtils = getContext().getFormatUtils();

        // fill speed data
        if (exercise.getSpeed() != null) {
            laSpeedAvgValue.setText(formatUtils.speedToString(exercise.getSpeed().getSpeedAVG(), 2));
            laSpeedMaxValue.setText(formatUtils.speedToString(exercise.getSpeed().getSpeedMax(), 2));
            laDistanceValue.setText(formatUtils.distanceToString(exercise.getSpeed().getDistance() / 1000f, 2));

            // bike number can't be displayed for HRM files
            if (exercise.getFileType() != EVExercise.ExerciseFileType.HRM) {
                laBikeNrValue.setText(String.valueOf(exercise.getRecordingMode().getBikeNumber()));
            }
        }

        // fill cadence data
        if (exercise.getCadence() != null) {
            laCadenceAvgValue.setText(formatUtils.cadenceToString(exercise.getCadence().getCadenceAVG()));
            laCadenceMaxValue.setText(formatUtils.cadenceToString(exercise.getCadence().getCadenceMax()));
        }

        // fill altitude data
        if (exercise.getAltitude() != null) {
            laAltitudeMinValue.setText(formatUtils.heightToString(exercise.getAltitude().getAltitudeMin()));
            laAltitudeAvgValue.setText(formatUtils.heightToString(exercise.getAltitude().getAltitudeAVG()));
            laAltitudeMaxValue.setText(formatUtils.heightToString(exercise.getAltitude().getAltitudeMax()));
            laAscentValue.setText(formatUtils.heightToString(exercise.getAltitude().getAscent()));
        }

        // fill temperature data
        if (exercise.getTemperature() != null) {
            laTemperatureMinValue.setText(formatUtils
                    .temperatureToString(exercise.getTemperature().getTemperatureMin()));
            laTemperatureAvgValue.setText(formatUtils
                    .temperatureToString(exercise.getTemperature().getTemperatureAVG()));
            laTemperatureMaxValue.setText(formatUtils
                    .temperatureToString(exercise.getTemperature().getTemperatureMax()));
        }
    }
}
