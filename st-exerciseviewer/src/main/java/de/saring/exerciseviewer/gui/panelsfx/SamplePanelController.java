package de.saring.exerciseviewer.gui.panelsfx;

import java.io.IOException;

import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.util.gui.javafx.FormattedNumberCellFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;

/**
 * Controller (MVC) class of the "Samples" panel, which displays all recorded samples of the exercise in a table.
 *
 * @author Stefan Saring
 */
public class SamplePanelController {

    private static final String FXML_FILE = "/fxml/SamplePanel.fxml";

    private EVContext context;
    private EVDocument document;

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
     * @param document the ExerciseViewer model/document
     */
    public SamplePanelController(final EVContext context, final EVDocument document) {
        this.context = context;
        this.document = document;
    }

    /**
     * Loads the panel content from FXML layout file and set up all the controls to shows the exercise data.
     *
     * @return the loaded and initialized panel content
     */
    // TODO setup abstract class for loading or move to a loader util class
    public Parent loadAndSetupPanelContent() {
        Parent root;

        try {
            // Guice and so GuiceLoader can't be used here, see comments in EVMain
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SamplePanelController.class.getResource(FXML_FILE));
            loader.setResources(context.getFxResources().getResourceBundle());
            loader.setControllerFactory(controllerClass -> this);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the FXML resource '" + FXML_FILE + "'!", e);
        }

        setupPanel();
        return root;
    }

    /**
     * Sets up all the panel controls and displays the exercise data.
     */
    private void setupPanel() {

        // setup table columns
        tcTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        tcHeartrate.setCellValueFactory(new PropertyValueFactory<>("heartRate"));
        tcAltitude.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        tcSpeed.setCellValueFactory(new PropertyValueFactory<>("speed"));
        tcDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        tcCadence.setCellValueFactory(new PropertyValueFactory<>("cadence"));
        tcTemperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        // setup custom number cell factories for all table columns
        final RecordingMode recordingMode = document.getExercise().getRecordingMode();

        tcTime.setCellFactory(new FormattedNumberCellFactory<>(value ->
            context.getFormatUtils().seconds2TimeString(value.intValue() / 1000)));

        tcHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value ->
                context.getFormatUtils().heartRateToString(value.intValue())));

        tcAltitude.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isAltitude() ?
                context.getFormatUtils().heightToString(value.intValue()) : null));

        tcSpeed.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isSpeed() ?
                context.getFormatUtils().speedToString(value.floatValue(), 2) : null));

        tcDistance.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isSpeed() ?
                context.getFormatUtils().distanceToString(value.doubleValue() / 1000d, 3) : null));

        tcCadence.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isCadence() ?
                context.getFormatUtils().cadenceToString(value.intValue()) : null));

        tcTemperature.setCellFactory(new FormattedNumberCellFactory<>(value -> recordingMode.isTemperature() ?
                context.getFormatUtils().temperatureToString(value.shortValue()) : null));

        // set table data
        tvSamples.setPlaceholder(new Label(context.getFxResources().getString("pv.info.no_data_available")));
        final ExerciseSample[] samples = document.getExercise().getSampleList();
        tvSamples.setItems(FXCollections.observableArrayList(samples == null ? new ExerciseSample[0] : samples));

        // default sort is the time column
        tvSamples.getSortOrder().add(tcTime);
    }
}
