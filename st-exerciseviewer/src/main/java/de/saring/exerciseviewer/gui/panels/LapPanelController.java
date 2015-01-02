package de.saring.exerciseviewer.gui.panels;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.gui.javafx.NumberCellFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.util.gui.javafx.FormattedNumberCellFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Controller (MVC) class of the "Laps" panel, which displays all recorded laps of the exercise in a table.
 *
 * @author Stefan Saring
 */
@Singleton
public class LapPanelController extends AbstractPanelController {

    @FXML
    private TableView<LapRow> tvLaps;

    @FXML
    private TableColumn<LapRow, Number> tcLap;
    @FXML
    private TableColumn<LapRow, Number> tcLapTime;
    @FXML
    private TableColumn<LapRow, Number> tcSplitTime;
    @FXML
    private TableColumn<LapRow, Number> tcHeartrate;
    @FXML
    private TableColumn<LapRow, Number> tcAvgHeartrate;
    @FXML
    private TableColumn<LapRow, Number> tcMaxHeartrate;
    @FXML
    private TableColumn<LapRow, Number> tcEndSpeed;
    @FXML
    private TableColumn<LapRow, Number> tcAvgSpeed;
    @FXML
    private TableColumn<LapRow, Number> tcDistance;
    @FXML
    private TableColumn<LapRow, Number> tcCadence;
    @FXML
    private TableColumn<LapRow, Number> tcAltitude;
    @FXML
    private TableColumn<LapRow, Number> tcAscent;
    @FXML
    private TableColumn<LapRow, Number> tcTemperature;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer document / model
     */
    @Inject
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
        tcLap.setCellValueFactory(new PropertyValueFactory<>("lapNumber"));
        tcLapTime.setCellValueFactory(new PropertyValueFactory<>("lapTime"));
        tcSplitTime.setCellValueFactory(new PropertyValueFactory<>("splitTime"));
        tcHeartrate.setCellValueFactory(new PropertyValueFactory<>("heartrate"));
        tcAvgHeartrate.setCellValueFactory(new PropertyValueFactory<>("avgHeartrate"));
        tcMaxHeartrate.setCellValueFactory(new PropertyValueFactory<>("maxHeartrate"));
        tcEndSpeed.setCellValueFactory(new PropertyValueFactory<>("endSpeed"));
        tcAvgSpeed.setCellValueFactory(new PropertyValueFactory<>("avgSpeed"));
        tcDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        tcCadence.setCellValueFactory(new PropertyValueFactory<>("cadence"));
        tcAltitude.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        tcAscent.setCellValueFactory(new PropertyValueFactory<>("ascent"));
        tcTemperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        // setup custom number cell factories for all table columns
        tcLap.setCellFactory(new NumberCellFactory<>());
        tcLapTime.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().tenthSeconds2TimeString(value.intValue())));
        tcSplitTime.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().tenthSeconds2TimeString(value.intValue())));
        tcHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().heartRateToString(value.intValue())));
        tcAvgHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().heartRateToString(value.intValue())));
        tcMaxHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().heartRateToString(value.intValue())));
        tcEndSpeed.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().speedToString(value.floatValue(), 2)));
        tcAvgSpeed.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().speedToString(value.floatValue(), 2)));
        tcDistance.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().distanceToString(value.intValue() / 1000d, 3)));
        tcCadence.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().cadenceToString(value.intValue())));
        tcAltitude.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().heightToString(value.intValue())));
        tcAscent.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().heightToString(value.intValue())));
        tcTemperature.setCellFactory(new FormattedNumberCellFactory<>(value -> value == null ? null :
                getContext().getFormatUtils().temperatureToString(value.shortValue())));

        // set table data
        tvLaps.setPlaceholder(new Label(getContext().getFxResources().getString("pv.info.no_data_available")));
        final LapRow[] lapRows = LapRow.createLapRows(getDocument().getExercise());
        tvLaps.setItems(FXCollections.observableArrayList(lapRows));

        // default sort is the time column
        tvLaps.getSortOrder().add(tcLap);
    }

    /**
     * Container class which contains the data of one row in the Laps table. It contains attributes for
     * each column of the Laps table. This class is needed for easy mapping by the PropertyValueFactory,
     * the data of many columns is not provided directly from the Lap class.
     */
    public static final class LapRow {

        private int lapNumber;
        private Integer lapTime;
        private Integer splitTime;
        private Integer heartrate;
        private Integer avgHeartrate;
        private Integer maxHeartrate;
        private Float endSpeed;
        private Float avgSpeed;
        private Integer distance;
        private Integer cadence;
        private Integer altitude;
        private Integer ascent;
        private Integer temperature;

        private LapRow() {
        }

        /**
         * Creates an array of LapRow objects for the laps in the specified exercise.
         *
         * @param exercise exercise with laps
         * @return array of LapRows
         */
        public static LapRow[] createLapRows(final EVExercise exercise) {

            final Lap[] laps = exercise.getLapList();
            if (laps == null || laps.length == 0) {
                return new LapRow[0];
            }

            final LapRow[] lapRows = new LapRow[laps.length];
            for (int rowNr = 0; rowNr < laps.length; rowNr++) {

                final Lap lap = laps[rowNr];
                final LapRow lapRow = new LapRow();
                lapRows[rowNr] = lapRow;
                lapRow.lapNumber = rowNr + 1;

                // lap time (= split time of current - split time of previous lap)
                int previousLapSplitTime = 0;
                if (rowNr > 0) {
                    previousLapSplitTime = laps[rowNr - 1].getTimeSplit();
                }
                lapRow.lapTime = lap.getTimeSplit() - previousLapSplitTime;
                lapRow.splitTime = lap.getTimeSplit();

                lapRow.heartrate = Integer.valueOf(lap.getHeartRateSplit());
                lapRow.avgHeartrate = Integer.valueOf(lap.getHeartRateAVG());
                lapRow.maxHeartrate = Integer.valueOf(lap.getHeartRateMax());

                if (lap.getSpeed() != null) {
                    lapRow.endSpeed = lap.getSpeed().getSpeedEnd();
                    lapRow.avgSpeed = lap.getSpeed().getSpeedAVG();
                    lapRow.distance = lap.getSpeed().getDistance();

                    if (exercise.getRecordingMode().isCadence()) {
                        lapRow.cadence = Integer.valueOf(lap.getSpeed().getCadence());
                    }
                }

                if (lap.getAltitude() != null) {
                    lapRow.altitude = Integer.valueOf(lap.getAltitude().getAltitude());

                    // ascent at lap split (lap ascent can't be displayed for HRM files)
                    if (exercise.getFileType() != EVExercise.ExerciseFileType.HRM) {
                        lapRow.ascent = lap.getAltitude().getAscent();
                    }
                }

                if (lap.getTemperature() != null) {
                    lapRow.temperature = Integer.valueOf(lap.getTemperature().getTemperature());
                }
            }

            return lapRows;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return lap number
         */
        public int getLapNumber() {
            return lapNumber;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return time (duration) of this lap
         */
        public Integer getLapTime() {
            return lapTime;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return time (duration) at lap end
         */
        public Integer getSplitTime() {
            return splitTime;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return heartrate at lap end
         */
        public Integer getHeartrate() {
            return heartrate;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return avg heartrate in lap
         */
        public Integer getAvgHeartrate() {
            return avgHeartrate;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return max heartrate in lap
         */
        public Integer getMaxHeartrate() {
            return maxHeartrate;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return speed at lap end
         */
        public Float getEndSpeed() {
            return endSpeed;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return avg speed in lap
         */
        public Float getAvgSpeed() {
            return avgSpeed;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return distance at lap end
         */
        public Integer getDistance() {
            return distance;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return cadence at lap end
         */
        public Integer getCadence() {
            return cadence;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return altitude at lap end
         */
        public Integer getAltitude() {
            return altitude;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return ascent in lap
         */
        public Integer getAscent() {
            return ascent;
        }

        /**
         * Returns the appropriate column value.
         *
         * @return temperature at lap end
         */
        public Integer getTemperature() {
            return temperature;
        }
    }
}
