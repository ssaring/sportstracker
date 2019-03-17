package de.saring.exerciseviewer.gui.panels

import de.saring.exerciseviewer.data.ExerciseSample
import de.saring.exerciseviewer.gui.EVContext
import de.saring.exerciseviewer.gui.EVDocument
import de.saring.util.gui.javafx.FormattedNumberCellFactory
import de.saring.util.unitcalc.TimeUtils
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory

/**
 * Controller (MVC) class of the "Samples" panel, which displays all recorded samples of the exercise in a table.
 *
 * @constructor constructor for dependency injection
 * @param context the ExerciseViewer UI context
 * @param document the ExerciseViewer document / model
 *
 * @author Stefan Saring
 */
class SamplePanelController(
        context: EVContext,
        document: EVDocument) : AbstractPanelController(context, document) {

    @FXML
    private lateinit var tvSamples: TableView<ExerciseSample>

    @FXML
    private lateinit var tcTime: TableColumn<ExerciseSample, Number>
    @FXML
    private lateinit var tcHeartrate: TableColumn<ExerciseSample, Number>
    @FXML
    private lateinit var tcAltitude: TableColumn<ExerciseSample, Number>
    @FXML
    private lateinit var tcSpeed: TableColumn<ExerciseSample, Number>
    @FXML
    private lateinit var tcDistance: TableColumn<ExerciseSample, Number>
    @FXML
    private lateinit var tcCadence: TableColumn<ExerciseSample, Number>
    @FXML
    private lateinit var tcTemperature: TableColumn<ExerciseSample, Number>

    override val fxmlFilename: String = "/fxml/panels/SamplePanel.fxml"

    override fun setupPanel() {

        // setup table columns
        tcTime.cellValueFactory = PropertyValueFactory("timestamp")
        tcHeartrate.cellValueFactory = PropertyValueFactory("heartRate")
        tcAltitude.cellValueFactory = PropertyValueFactory("altitude")
        tcSpeed.cellValueFactory = PropertyValueFactory("speed")
        tcDistance.cellValueFactory = PropertyValueFactory("distance")
        tcCadence.cellValueFactory = PropertyValueFactory("cadence")
        tcTemperature.cellValueFactory = PropertyValueFactory("temperature")

        // setup custom number cell factories for all table columns

        tcTime.cellFactory = FormattedNumberCellFactory {
            if (it != null) TimeUtils.seconds2TimeString(it.toInt() / 1000) else null
        }

        tcHeartrate.cellFactory = FormattedNumberCellFactory {
            if (it != null) context.formatUtils.heartRateToString(it.toInt()) else null
        }

        tcAltitude.cellFactory = FormattedNumberCellFactory {
            if (it != null) context.formatUtils.heightToString(it.toInt()) else null
        }

        tcSpeed.cellFactory = FormattedNumberCellFactory {
            if (it != null) context.formatUtils.speedToString(it.toFloat(), 2, document.speedMode) else null
        }

        tcDistance.cellFactory = FormattedNumberCellFactory {
            if (it != null) context.formatUtils.distanceToString(it.toDouble() / 1000.0, 3) else null
        }

        tcCadence.cellFactory = FormattedNumberCellFactory {
            if (it != null) context.formatUtils.cadenceToString(it.toInt()) else null
        }

        tcTemperature.cellFactory = FormattedNumberCellFactory {
            if (it != null) context.formatUtils.temperatureToString(it.toShort()) else null
        }

        // set table data
        tvSamples.placeholder = Label(context.resources.getString("pv.info.no_data_available"))
        tvSamples.items = FXCollections.observableArrayList(document.exercise.sampleList)

        // default sort is the time column
        tvSamples.sortOrder.add(tcTime)
    }
}
