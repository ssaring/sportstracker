package de.saring.exerciseviewer.gui.panels

import de.saring.exerciseviewer.gui.EVContext
import de.saring.exerciseviewer.gui.EVDocument
import javafx.fxml.FXML
import javafx.scene.control.Label

/**
 * Controller (MVC) class of the "Optional" panel, which displays all the optional exercise data, such as speed,
 * altitude ...
 *
 * @constructor constructor for dependency injection
 * @param context the ExerciseViewer UI context
 * @param document the ExerciseViewer document / model
 *
 * @author Stefan Saring
 */
 class OptionalPanelController(
        context: EVContext,
        document: EVDocument) : AbstractPanelController(context, document) {

    @FXML
    private lateinit var laSpeedAvgValue: Label
    @FXML
    private lateinit var laSpeedMaxValue: Label
    @FXML
    private lateinit var laDistanceValue: Label
    @FXML
    private lateinit var laBikeNrValue: Label

    @FXML
    private lateinit var laCadenceAvgValue: Label
    @FXML
    private lateinit var laCadenceMaxValue: Label
    @FXML
    private lateinit var laCyclesTotalValue: Label

    @FXML
    private lateinit var laAltitudeMinValue: Label
    @FXML
    private lateinit var laAltitudeAvgValue: Label
    @FXML
    private lateinit var laAltitudeMaxValue: Label
    @FXML
    private lateinit var laAscentValue: Label
    @FXML
    private lateinit var laDescentValue: Label

    @FXML
    private lateinit var laTemperatureMinValue: Label
    @FXML
    private lateinit var laTemperatureAvgValue: Label
    @FXML
    private lateinit var laTemperatureMaxValue: Label

    override val fxmlFilename: String = "/fxml/panels/OptionalPanel.fxml"

    override fun setupPanel() = showExerciseData()

    private fun showExerciseData() {
        val exercise = document.exercise
        val formatUtils = context.formatUtils

        // fill speed data
        exercise.speed?.let {
            laSpeedAvgValue.text = formatUtils.speedToString(it.speedAvg, 2)
            laSpeedMaxValue.text = formatUtils.speedToString(it.speedMax, 2)
            laDistanceValue.text = formatUtils.distanceToString((it.distance / 1000f).toDouble(), 2)
        }

        exercise.recordingMode.bikeNumber?.let {
            laBikeNrValue.text = it.toString()
        }

        // fill cadence data
        exercise.cadence?.let {
            laCadenceAvgValue.text = formatUtils.cadenceToString(it.cadenceAvg.toInt())
            laCadenceMaxValue.text = formatUtils.cadenceToString(it.cadenceMax.toInt())
            laCyclesTotalValue.text = formatUtils.totcyclesToString(it.cyclesTotal.toInt())
        }

        // fill altitude data
        exercise.altitude?.let {
            laAltitudeMinValue.text = formatUtils.heightToString(it.altitudeMin.toInt())
            laAltitudeAvgValue.text = formatUtils.heightToString(it.altitudeAvg.toInt())
            laAltitudeMaxValue.text = formatUtils.heightToString(it.altitudeMax.toInt())
            laAscentValue.text = formatUtils.heightToString(it.ascent)
            laDescentValue.text = formatUtils.heightToString(it.descent)
        }

        // fill temperature data
        exercise.temperature?.let {
            laTemperatureMinValue.text = formatUtils.temperatureToString(it.temperatureMin)
            laTemperatureAvgValue.text = formatUtils.temperatureToString(it.temperatureAvg)
            laTemperatureMaxValue.text = formatUtils.temperatureToString(it.temperatureMax)
        }
    }
}
