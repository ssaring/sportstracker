package de.saring.exerciseviewer.gui.panelsfx;

import de.saring.exerciseviewer.data.Lap;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.gui.javafx.FormattedNumberCellFactory;

/**
 * Controller (MVC) class of the "Laps" panel, which displays all recorded laps of the exercise in a table.
 *
 * @author Stefan Saring
 */
public class LapPanelController extends AbstractPanelController {

    @FXML
    private TableView<Lap> tvLaps;

    @FXML
    private TableColumn<Lap, Number> tcLap;
    @FXML
    private TableColumn<Lap, Number> tcLapTime;
    @FXML
    private TableColumn<Lap, Number> tcSplitTime;
    @FXML
    private TableColumn<Lap, Number> tcHeartrate;
    @FXML
    private TableColumn<Lap, Number> tcAvgHeartrate;
    @FXML
    private TableColumn<Lap, Number> tcMaxHeartrate;
    @FXML
    private TableColumn<Lap, Number> tcEndSpeed;
    @FXML
    private TableColumn<Lap, Number> tcAvgSpeed;
    @FXML
    private TableColumn<Lap, Number> tcDistance;
    @FXML
    private TableColumn<Lap, Number> tcCadence;
    @FXML
    private TableColumn<Lap, Number> tcAltitude;
    @FXML
    private TableColumn<Lap, Number> tcAscent;
    @FXML
    private TableColumn<Lap, Number> tcTemperature;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer model/document
     */
    public LapPanelController(final EVContext context, final EVDocument document) {
        super(context, document);
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/LapPanel.fxml";
    }

    @Override
    protected void setupPanel() {

        // setup table columns
        // TODO tcLap.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        // TODO tcLapTime.setCellValueFactory(new PropertyValueFactory<>("timeSplit"));
        tcSplitTime.setCellValueFactory(new PropertyValueFactory<>("timeSplit"));
        tcHeartrate.setCellValueFactory(new PropertyValueFactory<>("heartRateSplit"));
        tcAvgHeartrate.setCellValueFactory(new PropertyValueFactory<>("heartRateAVG"));
        tcMaxHeartrate.setCellValueFactory(new PropertyValueFactory<>("heartRateMax"));
        // TODO tcEndSpeed.setCellValueFactory(new PropertyValueFactory<>("heartRateMax"));
        // TODO tcAvgSpeed.setCellValueFactory(new PropertyValueFactory<>("heartRateMax"));
        // TODO tcDistance.setCellValueFactory(new PropertyValueFactory<>("heartRateMax"));
        // TODO tcCadence.setCellValueFactory(new PropertyValueFactory<>("heartRateMax"));
        // TODO tcAltitude.setCellValueFactory(new PropertyValueFactory<>("heartRateMax"));
        // TODO tcAscent.setCellValueFactory(new PropertyValueFactory<>("heartRateMax"));
        // TODO tcTemperature.setCellValueFactory(new PropertyValueFactory<>("heartRateMax"));

        // setup custom number cell factories for all table columns
        // TODO add missing columns
        tcSplitTime.setCellFactory(new FormattedNumberCellFactory<>(value -> getContext().getFormatUtils()
                .tenthSeconds2TimeString(value.intValue())));
        tcHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value -> getContext().getFormatUtils()
                .heartRateToString(value.intValue())));
        tcAvgHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value -> getContext().getFormatUtils()
                .heartRateToString(value.intValue())));
        tcMaxHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value -> getContext().getFormatUtils()
                .heartRateToString(value.intValue())));

        // set table data
        tvLaps.setPlaceholder(new Label(getContext().getFxResources().getString("pv.info.no_data_available")));
        final Lap[] laps = getDocument().getExercise().getLapList();
        tvLaps.setItems(FXCollections.observableArrayList(laps == null ? new Lap[0] : laps));

        // default sort is the time column
        tvLaps.getSortOrder().add(tcLap);
    }
}
