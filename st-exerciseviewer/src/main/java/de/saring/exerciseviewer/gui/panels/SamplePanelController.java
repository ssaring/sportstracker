package de.saring.exerciseviewer.gui.panels;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.util.gui.javafx.FormattedNumberCellFactory;

import javax.inject.Inject;

/**
 * Controller (MVC) class of the "Samples" panel, which displays all recorded samples of the exercise in a table.
 *
 * @author Stefan Saring
 */
public class SamplePanelController extends AbstractPanelController {

    @FXML
    private TableView<ExerciseSample> tvSamples;

    @FXML
    private TableColumn<ExerciseSample, Number> tcTime;
    @FXML
    private TableColumn<ExerciseSample, Number> tcHeartrate;
    @FXML
    private TableColumn<ExerciseSample, Number> tcAltitude;
    @FXML
    private TableColumn<ExerciseSample, Number> tcSpeed;
    @FXML
    private TableColumn<ExerciseSample, Number> tcDistance;
    @FXML
    private TableColumn<ExerciseSample, Number> tcCadence;
    @FXML
    private TableColumn<ExerciseSample, Number> tcTemperature;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     */
    @Inject
    public SamplePanelController(final EVContext context) {
        super(context);
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/SamplePanel.fxml";
    }

    @Override
    protected void setupPanel() {

        // setup table columns
        tcTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        tcHeartrate.setCellValueFactory(new PropertyValueFactory<>("heartRate"));
        tcAltitude.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        tcSpeed.setCellValueFactory(new PropertyValueFactory<>("speed"));
        tcDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        tcCadence.setCellValueFactory(new PropertyValueFactory<>("cadence"));
        tcTemperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        // setup custom number cell factories for all table columns
        final RecordingMode recordingMode = getDocument().getExercise().getRecordingMode();

        tcTime.setCellFactory(new FormattedNumberCellFactory<>(value -> getContext().getFormatUtils()
                .seconds2TimeString(value.intValue() / 1000)));

        tcHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value -> getContext().getFormatUtils()
                .heartRateToString(value.intValue())));

        tcAltitude.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isAltitude() ? getContext()
                .getFormatUtils().heightToString(value.intValue()) : null));

        tcSpeed.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isSpeed() ? getContext()
                .getFormatUtils().speedToString(value.floatValue(), 2) : null));

        tcDistance.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isSpeed() ? getContext()
                .getFormatUtils().distanceToString(value.doubleValue() / 1000d, 3) : null));

        tcCadence.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isCadence() ? getContext()
                .getFormatUtils().cadenceToString(value.intValue()) : null));

        tcTemperature.setCellFactory(new FormattedNumberCellFactory<>(
                value -> recordingMode.isTemperature() ? getContext().getFormatUtils().temperatureToString(
                        value.shortValue()) : null));

        // set table data
        tvSamples.setPlaceholder(new Label(getContext().getFxResources().getString("pv.info.no_data_available")));
        final ExerciseSample[] samples = getDocument().getExercise().getSampleList();
        tvSamples.setItems(FXCollections.observableArrayList(samples == null ? new ExerciseSample[0] : samples));

        // default sort is the time column
        tvSamples.getSortOrder().add(tcTime);
    }
}
