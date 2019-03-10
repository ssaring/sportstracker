package de.saring.exerciseviewer.gui.panels

import de.saring.exerciseviewer.data.HeartRateLimit
import de.saring.exerciseviewer.gui.EVContext
import de.saring.exerciseviewer.gui.EVDocument
import de.saring.util.StringUtils
import de.saring.util.unitcalc.TimeUtils
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.util.StringConverter
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Controller (MVC) class of the "Main" panel, which displays all the main exercise data.
 *
 * @constructor constructor for dependency injection
 * @param context the ExerciseViewer UI context
 * @param document the ExerciseViewer document / model
 *
 * @author Stefan Saring
 */
class MainPanelController(
        context: EVContext,
        document: EVDocument) : AbstractPanelController(context, document) {

    lateinit var diagramPanelController: DiagramPanelController

    @FXML
    private lateinit var laDateTimeValue: Label
    @FXML
    private lateinit var laDurationValue: Label
    @FXML
    private lateinit var laEnergyValue: Label
    @FXML
    private lateinit var laDeviceValue: Label
    @FXML
    private lateinit var laTypeValue: Label

    @FXML
    private lateinit var laHeartrateAvgValue: Label
    @FXML
    private lateinit var laHeartrateMaxValue: Label

    @FXML
    private lateinit var cbHeartrateRanges: ChoiceBox<HeartRateLimit>
    @FXML
    private lateinit var laTimeBelowValue: Label
    @FXML
    private lateinit var laTimeWithinValue: Label
    @FXML
    private lateinit var laTimeAboveValue: Label

    @FXML
    private lateinit var laModeHeartrateValue: Label
    @FXML
    private lateinit var laModeSpeedValue: Label
    @FXML
    private lateinit var laModeAltitudeValue: Label
    @FXML
    private lateinit var laModeCadendeValue: Label
    @FXML
    private lateinit var laModePowerValue: Label

    @FXML
    private lateinit var laTotalExerciseTimeValue: Label
    @FXML
    private lateinit var laTotalRidingTimeValue: Label
    @FXML
    private lateinit var laTotalEnergyValue: Label
    @FXML
    private lateinit var laOdometerValue: Label


    override val fxmlFilename: String = "/fxml/panels/MainPanel.fxml"

    override fun setupPanel() {
        showExerciseData()
        setupHeartrateRangeSelection()
    }

    private fun showExerciseData() {
        val exercise = document.exercise
        val formatUtils = context.formatUtils

        // fill energy data
        exercise.energy?.let {
            laEnergyValue.text = formatUtils.caloriesToString(it)
        }

        // fill statistics data
        exercise.sumExerciseTime?.let {
            laTotalExerciseTimeValue.text = TimeUtils.minutes2TimeString(it)
        }
        // fill total riding time (available only in S710 exercises)
        exercise.sumRideTime?.let {
            laTotalRidingTimeValue.text = TimeUtils.minutes2TimeString(it)
        }
        exercise.energyTotal?.let {
            laTotalEnergyValue.text = formatUtils.caloriesToString(it)
        }
        // fill odometer data (if available, e.g. not on Polar S410 or S610)
        exercise.odometer?.let {
            laOdometerValue.text = formatUtils.distanceToString(it.toDouble(), 2)
        }

        // fill device and type data
        if (!StringUtils.isNullOrEmpty(exercise.deviceName)) {
            laDeviceValue.text = exercise.deviceName
        }
        exercise.type?.let {
            laTypeValue.text = it
        }

        // fill time data
        exercise.dateTime?.let {
            laDateTimeValue.text = it.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
        }

        exercise.duration?.let {
            laDurationValue.text = TimeUtils.tenthSeconds2TimeString(it)
        }

        // fill heartrate data
        exercise.heartRateAVG?.let {
            laHeartrateAvgValue.text = formatUtils.heartRateToString(it.toInt())
        }
        exercise.heartRateMax?.let {
            laHeartrateMaxValue.text = formatUtils.heartRateToString(it.toInt())
        }

        // fill recording mode data
        laModeHeartrateValue.text = boolean2EnabledString(exercise.recordingMode.isHeartRate)
        laModeSpeedValue.text = boolean2EnabledString(exercise.recordingMode.isSpeed)
        laModeAltitudeValue.text = boolean2EnabledString(exercise.recordingMode.isAltitude)
        laModeCadendeValue.text = boolean2EnabledString(exercise.recordingMode.isCadence)
        laModePowerValue.text = boolean2EnabledString(exercise.recordingMode.isPower)
    }

    /**
     * Fills the heartrate range selection choicebox with the ranges stored in the exercise and selects
     * the first one. When no ranges are available, the selection will be disabled.
     */
    private fun setupHeartrateRangeSelection() {

        cbHeartrateRanges.addEventHandler(ActionEvent.ACTION) { _ -> updateHeartRateRangeTimes() }

        cbHeartrateRanges.converter = object : StringConverter<HeartRateLimit>() {
            override fun toString(limit: HeartRateLimit): String {
                val unitName = if (limit.isAbsoluteRange) "bpm" else "%"
                return "${limit.lowerHeartRate} - ${limit.upperHeartRate} $unitName"
            }

            override fun fromString(string: String): HeartRateLimit =
                throw UnsupportedOperationException()
        }

        val exercise = document.exercise
        if (!exercise.heartRateLimits.isEmpty()) {
            cbHeartrateRanges.items.addAll(exercise.heartRateLimits)
            cbHeartrateRanges.selectionModel.select(0)
        } else {
            cbHeartrateRanges.setDisable(true)
        }
    }

    /**
     * Updates the heartrate time value labels with the values of the current selected range.
     */
    private fun updateHeartRateRangeTimes() {

        val limit = cbHeartrateRanges.value

        // calculate percentages of times below, within and above
        document.exercise.duration?.let { duration ->
            displayHeartRateRangeTime(limit.timeBelow, duration, laTimeBelowValue)
            displayHeartRateRangeTime(limit.timeWithin, duration, laTimeWithinValue)
            displayHeartRateRangeTime(limit.timeAbove, duration, laTimeAboveValue)
        }

        // update heartrate range in diagram
        diagramPanelController.displayDiagramForHeartrateRange(limit)
    }

    private fun displayHeartRateRangeTime(time: Int?, duration: Int, label: Label) {

        if (time != null) {
            val percents = Math.round(time / duration.toDouble() * 10.0 * 100.0).toInt()
            label.text = TimeUtils.seconds2TimeString(time) + "   ($percents %)"
        } else {
            label.text = context.resources.getString("common.n_a_")
        }
    }

    private fun boolean2EnabledString(enabled: Boolean) =
        context.resources.getString(if (enabled) "common.enabled" else "common.disabled")
}
