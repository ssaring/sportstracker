package de.saring.exerciseviewer.gui.panelsfx;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.gui.javafx.NumberCellFactory;

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

        // TODO use proper format and metrics for all the columns

        // setup specific number cell factories
        tcTime.setCellFactory(column -> new TableCell<ExerciseSample, Number>() {
            @Override
            protected void updateItem(final Number value, final boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(context.getFormatUtils().seconds2TimeString(value.intValue() / 1000));
                }
            }
        });

        // these columns use a generic number cell factory
        tcHeartrate.setCellFactory(new NumberCellFactory<>());
        tcAltitude.setCellFactory(new NumberCellFactory<>());
        tcSpeed.setCellFactory(new NumberCellFactory<>());
        tcDistance.setCellFactory(new NumberCellFactory<>());
        tcCadence.setCellFactory(new NumberCellFactory<>());
        tcTemperature.setCellFactory(new NumberCellFactory<>());

        // TODO null check
        // TODO special text when no samples available?
        tvSamples.setItems(FXCollections.observableArrayList(document.getExercise().getSampleList()));

        // default sort is the time column
        tvSamples.getSortOrder().add(tcTime);
    }
}
