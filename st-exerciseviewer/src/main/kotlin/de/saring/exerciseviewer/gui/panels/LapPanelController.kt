package de.saring.exerciseviewer.gui.panels

import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.gui.EVContext
import de.saring.exerciseviewer.gui.EVDocument
import de.saring.util.gui.javafx.FormattedNumberCellFactory
import de.saring.util.gui.javafx.NumberCellFactory
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory

/**
 * Controller (MVC) class of the "Laps" panel, which displays all recorded laps of the exercise in a table.
 *
 * @constructor constructor for dependency injection
 * @param context the ExerciseViewer UI context
 * @param document the ExerciseViewer document / model
 *
 * @author Stefan Saring
 */
class LapPanelController(
        context: EVContext,
        document: EVDocument) : AbstractPanelController(context, document) {

    @FXML
    private lateinit var tvLaps: TableView<LapRow>

    @FXML
    private lateinit var tcLap: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcLapTime: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcSplitTime: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcHeartrate: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcAvgHeartrate: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcMaxHeartrate: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcEndSpeed: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcAvgSpeed: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcDistance: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcCadence: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcAltitude: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcAscent: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcDescent: TableColumn<LapRow, Number>
    @FXML
    private lateinit var tcTemperature: TableColumn<LapRow, Number>

    override val fxmlFilename: String = "/fxml/panels/LapPanel.fxml"

    override fun setupPanel() {

        // setup table columns
        tcLap.cellValueFactory = PropertyValueFactory("lapNumber")
        tcLapTime.cellValueFactory = PropertyValueFactory("lapTime")
        tcSplitTime.cellValueFactory = PropertyValueFactory("splitTime")
        tcHeartrate.cellValueFactory = PropertyValueFactory("heartrate")
        tcAvgHeartrate.cellValueFactory = PropertyValueFactory("avgHeartrate")
        tcMaxHeartrate.cellValueFactory = PropertyValueFactory("maxHeartrate")
        tcEndSpeed.cellValueFactory = PropertyValueFactory("endSpeed")
        tcAvgSpeed.cellValueFactory = PropertyValueFactory("avgSpeed")
        tcDistance.cellValueFactory = PropertyValueFactory("distance")
        tcCadence.cellValueFactory = PropertyValueFactory("cadence")
        tcAltitude.cellValueFactory = PropertyValueFactory("altitude")
        tcAscent.cellValueFactory = PropertyValueFactory("ascent")
        tcDescent.cellValueFactory = PropertyValueFactory("descent")
        tcTemperature.cellValueFactory = PropertyValueFactory("temperature")

        // setup custom number cell factories for all table columns
        tcLap.cellFactory = NumberCellFactory()
        tcLapTime.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.tenthSeconds2TimeString(it.toInt())
        }
        tcSplitTime.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.tenthSeconds2TimeString(it.toInt())
        }
        tcHeartrate.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.heartRateToString(it.toInt())
        }
        tcAvgHeartrate.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.heartRateToString(it.toInt())
        }
        tcMaxHeartrate.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.heartRateToString(it.toInt())
        }
        tcEndSpeed.cellFactory = FormattedNumberCellFactory {
            if (it == null) null
            else context.formatUtils.speedToString(it.toFloat(), 2, document.speedMode)
        }
        tcAvgSpeed.cellFactory = FormattedNumberCellFactory {
            if (it == null) null
            else context.formatUtils.speedToString(it.toFloat(), 2, document.speedMode)
        }
        tcDistance.cellFactory = FormattedNumberCellFactory {
            if (it == null) null
            else context.formatUtils.distanceToString(it.toInt() / 1000.0, 3)
        }
        tcCadence.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.cadenceToString(it.toInt())
        }
        tcAltitude.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.heightToString(it.toInt())
        }
        tcAscent.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.heightToString(it.toInt())
        }
        tcDescent.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.heightToString(it.toInt())
        }
        tcTemperature.cellFactory = FormattedNumberCellFactory {
            if (it == null) null else context.formatUtils.temperatureToString(it.toShort())
        }

        // set table data
        tvLaps.placeholder = Label(context.resources.getString("pv.info.no_data_available"))
        val lapRows = createLapRows(document.exercise)
        tvLaps.items = FXCollections.observableArrayList(lapRows)

        // default sort is the time column
        tvLaps.sortOrder.add(tcLap)
    }

    /**
     * Creates a list of LapRow objects for the laps in the specified exercise.
     *
     * @param exercise exercise with laps
     * @return list of LapRows
     */
    private fun createLapRows(exercise: EVExercise): List<LapRow> {

        val lapRows = mutableListOf<LapRow>()
        var previousLapSplitTime = 0

        exercise.lapList.forEach { lap ->

            lapRows.add(LapRow(
                    lapNumber = lapRows.size + 1,
                    lapTime = lap.timeSplit - previousLapSplitTime,
                    splitTime = lap.timeSplit,
                    heartrate = lap.heartRateSplit?.toInt(),
                    avgHeartrate = lap.heartRateAVG?.toInt(),
                    maxHeartrate = lap.heartRateMax?.toInt(),
                    endSpeed = lap.speed?.speedEnd,
                    avgSpeed = lap.speed?.speedAVG,
                    distance = lap.speed?.distance,
                    cadence = lap.speed?.cadence?.toInt(),
                    altitude = lap.altitude?.altitude?.toInt(),
                    ascent = lap.altitude?.ascent,
                    descent = lap.altitude?.descent,
                    temperature = lap.temperature?.temperature?.toInt()))

            previousLapSplitTime = lap.timeSplit
        }

        return lapRows
    }

    /**
     * Immutable container class which contains the data of one row in the Laps table. It contains properties for
     * each column of the Laps table. This class is needed for easy mapping by the PropertyValueFactory, the data
     * of many columns is not provided directly from the Lap class.
     *
     * @property lapNumber lap number
     * @property lapTime time (duration) of this lap
     * @property splitTime time (duration) at lap end
     * @property heartrate heartrate at lap end
     * @property avgHeartrate avg heartrate in lap
     * @property maxHeartrate max heartrate in lap
     * @property endSpeed speed at lap end
     * @property avgSpeed avg speed in lap
     * @property distance distance at lap end
     * @property cadence cadence at lap end
     * @property altitude altitude at lap end
     * @property ascent ascent in lap
     * @property descent descent in lap
     * @property temperature temperature at lap end
     */
    class LapRow(
            val lapNumber: Int,
            val lapTime: Int?,
            val splitTime: Int?,
            val heartrate: Int?,
            val avgHeartrate: Int?,
            val maxHeartrate: Int?,
            val endSpeed: Float?,
            val avgSpeed: Float?,
            val distance: Int?,
            val cadence: Int?,
            val altitude: Int?,
            val ascent: Int?,
            val descent: Int?,
            val temperature: Int?)
}
