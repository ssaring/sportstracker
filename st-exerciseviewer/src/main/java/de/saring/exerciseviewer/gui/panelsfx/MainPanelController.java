package de.saring.exerciseviewer.gui.panelsfx;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.HeartRateLimit;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.unitcalc.FormatUtils;
import javafx.util.StringConverter;

/**
 * Controller (MVC) class of the "Main" panel, which displays all the main exercise data.
 *
 * @author Stefan Saring
 */
public class MainPanelController extends AbstractPanelController {

    @FXML
    private Label laTypeValue;
    @FXML
    private Label laUserValue;
    @FXML
    private Label laDateTimeValue;
    @FXML
    private Label laDurationValue;
    @FXML
    private Label laEnergyValue;

    @FXML
    private Label laHeartrateAvgValue;
    @FXML
    private Label laHeartrateMaxValue;

    @FXML
    private ChoiceBox<HeartRateLimit> cbHeartrateRanges;
    @FXML
    private Label laTimeBelowValue;
    @FXML
    private Label laTimeWithinValue;
    @FXML
    private Label laTimeAboveValue;

    @FXML
    private Label laModeSpeedValue;
    @FXML
    private Label laModeAltitudeValue;
    @FXML
    private Label laModeCadendeValue;
    @FXML
    private Label laModePowerValue;

    @FXML
    private Label laTotalExerciseTimeValue;
    @FXML
    private Label laTotalRidingTimeValue;
    @FXML
    private Label laTotalEnergyValue;
    @FXML
    private Label laOdometerValue;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer model/document
     */
    public MainPanelController(final EVContext context, final EVDocument document) {
        super(context, document);
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/MainPanel.fxml";
    }

    @Override
    protected void setupPanel() {
        showExerciseData();
        setupHeartrateRangeSelection();
    }

    private void showExerciseData() {
        final EVExercise exercise = getDocument().getExercise();
        final FormatUtils formatUtils = getContext().getFormatUtils();

        if (exercise.getFileType() != EVExercise.ExerciseFileType.HRM) {

            // fill type and user data
            if (exercise.getType() > 0) {
                laTypeValue.setText("" + exercise.getType() + " (" + exercise.getTypeLabel() + ")");
            }
            if (exercise.getUserID() > 0) {
                laUserValue.setText("" + exercise.getUserID());
            }

            // fill energy data
            if (exercise.getEnergy() > 0) {
                laEnergyValue.setText(formatUtils.caloriesToString(exercise.getEnergy()));
            }

            // fill statistics data
            if (exercise.getSumExerciseTime() > 0) {
                laTotalExerciseTimeValue.setText(formatUtils.minutes2TimeString(exercise.getSumExerciseTime()));
            }
            if (exercise.getEnergyTotal() > 0) {
                laTotalEnergyValue.setText(formatUtils.caloriesToString(exercise.getEnergyTotal()));
            }

            // fill total riding time (available only in S710 exercises)
            if (exercise.getFileType() == EVExercise.ExerciseFileType.S710RAW) {
                laTotalRidingTimeValue.setText(formatUtils.minutes2TimeString(exercise.getSumRideTime()));
            }
        }

        // fill time data
        if (exercise.getDateTime() != null) {
            laDateTimeValue.setText(exercise.getDateTime().format(
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        }

        if (exercise.getDuration() > 0) {
            laDurationValue.setText(formatUtils.tenthSeconds2TimeString(exercise.getDuration()));
        }

        // fill heartrate data
        if (exercise.getHeartRateAVG() > 0) {
            laHeartrateAvgValue.setText(formatUtils.heartRateToString(exercise.getHeartRateAVG()));
        }
        if (exercise.getHeartRateMax() > 0) {
            laHeartrateMaxValue.setText(formatUtils.heartRateToString(exercise.getHeartRateMax()));
        }

        // fill recording mode data
        laModeSpeedValue.setText(boolean2EnabledString(exercise.getRecordingMode().isSpeed()));
        laModeAltitudeValue.setText(boolean2EnabledString(exercise.getRecordingMode().isAltitude()));
        laModeCadendeValue.setText(boolean2EnabledString(exercise.getRecordingMode().isCadence()));
        laModePowerValue.setText(boolean2EnabledString(exercise.getRecordingMode().isPower()));

        // fill odometer data (if available, e.g. not on Polar S410 or S610)
        if (exercise.getOdometer() != 0) {
            laOdometerValue.setText(formatUtils.distanceToString(exercise.getOdometer(), 2));
        }
    }

    /**
     * Fills the heartrate range selection choicebox with the ranges stored in the exercise and selects
     * the first one. When no ranges are available, the selection will be disabled.<br/>
     */
    private void setupHeartrateRangeSelection() {

        cbHeartrateRanges.addEventHandler(ActionEvent.ACTION, event -> updateHeartRateRangeTimes());

        cbHeartrateRanges.setConverter(new StringConverter<HeartRateLimit>() {
            @Override
            public String toString(final HeartRateLimit limit) {
                final String unitName = limit.isAbsoluteRange() ? "bpm" : "%";
                return "" + limit.getLowerHeartRate() + " - " + limit.getUpperHeartRate() + " " + unitName;
            }

            @Override
            public HeartRateLimit fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });

        final EVExercise exercise = getDocument().getExercise();
        if (exercise.getHeartRateLimits() != null && exercise.getHeartRateLimits().length > 0) {
            cbHeartrateRanges.getItems().addAll(exercise.getHeartRateLimits());
            cbHeartrateRanges.getSelectionModel().select(0);
        } else {
            cbHeartrateRanges.setDisable(true);
        }
    }

    /**
     * Updates the heartrate time value labels with the values of the current selected range.
     */
    private void updateHeartRateRangeTimes() {

        final EVExercise exercise = getDocument().getExercise();
        final FormatUtils formatUtils = getContext().getFormatUtils();
        final HeartRateLimit limit = cbHeartrateRanges.getValue();

        // calculate percentages of times below, within and above
        int percentsBelow = 0, percentsWithin = 0, percentsAbove = 0;
        if (exercise.getDuration() > 0) {
            percentsBelow = (int) Math.round(limit.getTimeBelow() / (double) exercise.getDuration() * 10 * 100);
            percentsWithin = (int) Math.round(limit.getTimeWithin() / (double) exercise.getDuration() * 10 * 100);
            percentsAbove = (int) Math.round(limit.getTimeAbove() / (double) exercise.getDuration() * 10 * 100);
        }

        laTimeBelowValue.setText(formatUtils.seconds2TimeString(limit.getTimeBelow()) + "   (" + percentsBelow + " %)");
        laTimeWithinValue.setText(formatUtils.seconds2TimeString(limit.getTimeWithin()) + "   (" + percentsWithin + " %)");
        laTimeAboveValue.setText(formatUtils.seconds2TimeString(limit.getTimeAbove()) + "   (" + percentsAbove + " %)");

        // TODO update heartrate range in diagram
        // diagramPanel.displayDiagramForHeartrateRange(index);
    }

    private String boolean2EnabledString(final boolean enabled) {
        return getContext().getResReader().getString(enabled ? "common.enabled" : "common.disabled");
    }
}
