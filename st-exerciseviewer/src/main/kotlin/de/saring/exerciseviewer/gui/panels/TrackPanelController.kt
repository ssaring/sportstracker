package de.saring.exerciseviewer.gui.panels

import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.data.ExerciseSample
import de.saring.exerciseviewer.gui.EVContext
import de.saring.exerciseviewer.gui.EVDocument
import de.saring.leafletmap.ColorMarker
import de.saring.leafletmap.ControlPosition
import de.saring.leafletmap.LatLong
import de.saring.leafletmap.LeafletMapView
import de.saring.leafletmap.MapConfig
import de.saring.leafletmap.MapLayer
import de.saring.leafletmap.ScaleControlConfig
import de.saring.leafletmap.ZoomControlConfig
import de.saring.util.gui.jfreechart.ChartUtils
import de.saring.util.gui.jfreechart.FixedRangeNumberAxis
import de.saring.util.unitcalc.ConvertUtils
import de.saring.util.unitcalc.TimeUtils
import de.saring.util.unitcalc.UnitSystem
import javafx.concurrent.Worker
import javafx.fxml.FXML
import javafx.geometry.Point2D
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.jfree.chart.ChartFactory
import org.jfree.chart.fx.ChartViewer
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.ValueMarker
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYAreaRenderer
import org.jfree.data.Range
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.util.logging.Level
import java.util.logging.Logger


/**
 * Controller (MVC) class of the "Track" panel, which displays the recorded location data of the exercise (if
 * available) in a map.<br/>
 * The map component is LeafletMap which is based on the Leaflet Javascript library, the data provider is OpenStreetMap.
 *
 * @constructor constructor for dependency injection
 * @param context the ExerciseViewer UI context
 * @param document the ExerciseViewer document / model
 *
 * @author Stefan Saring
 */
class TrackPanelController(
        context: EVContext,
        document: EVDocument) : AbstractPanelController(context, document) {

    private val logger = Logger.getLogger(TrackPanelController::class.java.name)

    @FXML
    private lateinit var spTrackPanel: StackPane

    @FXML
    private lateinit var vbTrackViewer: VBox

    @FXML
    private lateinit var spMapViewer: StackPane

    @FXML
    private lateinit var spDiagram: StackPane

    @FXML
    private lateinit var slPosition: Slider

    private var mapView: LeafletMapView? = null
    private var mapConfig: MapConfig? = null

    private var spMapViewerTooltip: Tooltip? = null
    private var positionMarkerName: String? = null

    private val colorAltitudeAxis = java.awt.Color(255, 30, 30)
    private val colorAltitudePlot = java.awt.Color(255, 30, 30, 128)

    private var altitudeGraphMarker: ValueMarker? = null
    private val colorAltitudeGraphMarker = java.awt.Color(110, 110, 120)
    private val strokeAltitudeGraphMarker = java.awt.BasicStroke(1.5f)

    /** Flag whether the exercise track has already been shown.  */
    private var showTrackExecuted = false

    override val fxmlFilename: String = "/fxml/panels/TrackPanel.fxml"

    override fun setupPanel() {

        // setup the map viewer if track data is available
        val exercise = document.exercise
        if (exercise.recordingMode.isLocation) {
            setupAltitudeChart()
            setupMapView()
            setupMapViewerTooltip()
            setupTrackPositionSlider()
        } else {
            // remove the track viewer VBox, the StackPane now displays the label "No track data available")
            spTrackPanel.children.remove(vbTrackViewer)
        }
    }

    private fun setupMapView() {
        mapView = LeafletMapView()
        spMapViewer.children.add(mapView)

        val metric = document.options.unitSystem == UnitSystem.METRIC

        mapConfig = MapConfig(
                listOf(MapLayer.OPENSTREETMAP, MapLayer.OPENCYCLEMAP, MapLayer.HIKE_BIKE_MAP, MapLayer.MTB_MAP),
                ZoomControlConfig(true, ControlPosition.BOTTOM_LEFT),
                ScaleControlConfig(true, ControlPosition.BOTTOM_LEFT, metric))
    }

    private fun setupMapViewerTooltip() {
        spMapViewerTooltip = Tooltip()
        spMapViewerTooltip!!.isAutoHide = true
    }

    private fun setupAltitudeChart() {
        if (document.exercise.recordingMode.isAltitude) {

            val sAltitude = createAltitudeXYSeries()
            val dsAltitude = XYSeriesCollection(sAltitude)

            val chartAltitude = ChartFactory.createXYLineChart(null, // Title
                    null, // Y-axis label
                    null, // X-axis label will be set later
                    dsAltitude, // primary dataset
                    PlotOrientation.VERTICAL, // plot orientation
                    false, // display legend
                    false, // display tooltips
                    false) // URLs

            val plotAltitude = chartAltitude.plot as XYPlot

            // use custom axis (both) with fixed ranges to avoid altitude to start with 0 and to avoid empty space on end of the distance axis
            // (use a buffer of 10 on both range ends for avoiding display errors for exercises with flat altitude)
            val axisAltitude = FixedRangeNumberAxis(
                    getAltitudeAxisTitle(), Range(sAltitude.minY - 10.0, sAltitude.maxY + 10.0), true)
            plotAltitude.rangeAxis = axisAltitude
            plotAltitude.domainAxis = FixedRangeNumberAxis(null, Range(0.0, sAltitude.maxX), false)

            // setup altitude axis and custom area renderer
            axisAltitude.labelPaint = colorAltitudeAxis
            axisAltitude.tickLabelPaint = colorAltitudeAxis

            plotAltitude.setRenderer(0, XYAreaRenderer().apply {
                setSeriesPaint(0, colorAltitudePlot)
            })

            addAltitudeGraphMarker(plotAltitude)

            ChartUtils.customizeChart(chartAltitude)
            val chartViewer = ChartViewer(chartAltitude)
            spDiagram.children.addAll(chartViewer)
        }
        else {
            // hide diagram pane when no altitude data present
            vbTrackViewer.children.remove(spDiagram)
        }
    }

    private fun getAltitudeAxisTitle() = context.resources.getString(
            "pv.track.axis.altitude", context.formatUtils.getAltitudeUnitName())

    private fun setupTrackPositionSlider() {
        // on position slider changes: update position marker in the map viewer and display tooltip with details
        // (slider uses a double value, make sure the int value has changed)
        slPosition.valueProperty().addListener { _, oldValue, newValue ->
            if (oldValue.toInt() != newValue.toInt()) {
                movePositionMarker(newValue.toInt())
            }
        }
    }

    private fun getConvertedDistanceForAltitudeGraph(sample: ExerciseSample): Double? {
        return sample.distance?.let { sampleDistanceInMeters ->
            val isEnglishUnitSystem = document.options.unitSystem == UnitSystem.ENGLISH
            val sampleDistanceInCurrentUnit = if (isEnglishUnitSystem)
                ConvertUtils.convertKilometer2Miles(sampleDistanceInMeters) else sampleDistanceInMeters
            return sampleDistanceInCurrentUnit / 1000.0
        }
    }

    private fun createAltitudeXYSeries(): XYSeries {
        val isEnglishUnitSystem = document.options.unitSystem == UnitSystem.ENGLISH
        val sAltitude = XYSeries("altitude")

        document.exercise.sampleList.forEach { sample ->
            val altitudeInMeters = sample.altitude?.toInt() ?: 0
            val altitudeInCurrentUnit = if (isEnglishUnitSystem)
                ConvertUtils.convertMeter2Feet(altitudeInMeters) else altitudeInMeters
            val distanceInCurrentUnit = getConvertedDistanceForAltitudeGraph(sample) ?: 0.0

            sAltitude.add(distanceInCurrentUnit, altitudeInCurrentUnit)
        }
        return sAltitude
    }

    private fun addAltitudeGraphMarker(plotAltitude: XYPlot) {
        altitudeGraphMarker = ValueMarker(0.0).apply {
            paint = colorAltitudeGraphMarker
            stroke = strokeAltitudeGraphMarker
        }
        plotAltitude.addDomainMarker(altitudeGraphMarker)
    }

    private fun movePositionMarker(positionIndex: Int) {
        val samplePosition = document.exercise.sampleList[positionIndex].position

        // some samples could have no position
        samplePosition?.let {
            val position = LatLong(it.latitude, it.longitude)

            if (positionMarkerName == null) {
                positionMarkerName = mapView!!.addMarker(position, "", ColorMarker.BLUE_MARKER, 0)
            } else {
                mapView!!.moveMarker(positionMarkerName!!, position)
            }

            val tooltipText = createToolTipText(positionIndex)
            spMapViewerTooltip!!.text = tooltipText

            // display position tooltip in the upper left corner of the map viewer container
            var tooltipPos = spMapViewer.localToScene(8.0, 8.0)
            tooltipPos = tooltipPos.add(getMapViewerScreenPosition())
            spMapViewerTooltip!!.show(spMapViewer, tooltipPos.x, tooltipPos.y)
        }

        // move the vertical position marker in the altitude graph to the new track position
        altitudeGraphMarker?.let { marker ->
            getConvertedDistanceForAltitudeGraph(document.exercise.sampleList[positionIndex])?.let { distance ->
                marker.value = distance
            }
        }
    }

    /**
     * Displays map and the track of the current exercise, if available. This method will be executed only once and
     * should be called when the user wants to see the track (to prevent long startup delays).
     */
    fun showMapAndTrack() {
        if (!showTrackExecuted) {
            showTrackExecuted = true

            val exercise = document.exercise
            if (exercise.recordingMode.isLocation) {

                // display map, on success display the track and laps
                mapView!!.displayMap(mapConfig!!).whenComplete { workerState, throwable ->

                    if (workerState == Worker.State.SUCCEEDED) {
                        showTrackAndLaps()
                        // enable position slider by setting max. sample count
                        slPosition.max = (exercise.sampleList.size - 1).toDouble()
                    } else if (throwable != null) {
                        logger.log(Level.SEVERE, "Failed to display map!", throwable)
                    }
                }
            }
        }
    }

    private fun showTrackAndLaps() {
        val exercise = document.exercise
        val samplePositions = createSamplePositionList(exercise)

        if (!samplePositions.isEmpty()) {
            mapView!!.addTrack(samplePositions)

            // display lap markers first, start and end needs to be displayed on top
            val lapPositions = createLapPositionList(exercise)
            for (i in lapPositions.indices) {
                mapView!!.addMarker(lapPositions[i],
                        context.resources.getString("pv.track.maptooltip.lap", i + 1),
                        ColorMarker.GREY_MARKER, 0)
            }

            mapView!!.addMarker(samplePositions.first(),
                    context.resources.getString("pv.track.maptooltip.start"),
                    ColorMarker.GREEN_MARKER, 1000)
            mapView!!.addMarker(samplePositions.last(),
                    context.resources.getString("pv.track.maptooltip.end"),
                    ColorMarker.RED_MARKER, 2000)
        }
    }

    private fun createSamplePositionList(exercise: EVExercise): List<LatLong> =
            exercise.sampleList
                    .filter { it.position != null }
                    .map { LatLong(it.position!!.latitude, it.position!!.longitude) }
                    .toList()

    private fun createLapPositionList(exercise: EVExercise): List<LatLong> {
        val lapPositions = mutableListOf<LatLong>()

        // ignore last lap split position, it's the exercise end position
        for (i in 0..exercise.lapList.size - 1 - 1) {
            val position = exercise.lapList[i].positionSplit
            if (position != null) {
                lapPositions.add(LatLong(position.latitude, position.longitude))
            }
        }
        return lapPositions
    }

    private fun getMapViewerScreenPosition(): Point2D {
        val scene = spMapViewer.scene
        val window = scene.window
        return Point2D(scene.x + window.x, scene.y + window.y)
    }

    /**
     * Creates the tool tip text for the specified exercise sample to be shown on the map.
     *
     * @param sampleIndex index of the exercise sample
     * @return text
     */
    private fun createToolTipText(sampleIndex: Int): String {

        val exercise = document.exercise
        val sample = exercise.sampleList[sampleIndex]
        val formatUtils = context.formatUtils

        val sb = StringBuilder()
        appendToolTipLine(sb, "pv.track.tooltip.trackpoint", (sampleIndex + 1).toString())

        sample.timestamp?.let {
            appendToolTipLine(sb, "pv.track.tooltip.time", TimeUtils.seconds2TimeString((it / 1000).toInt()))
        }
        sample.distance?.let {
            appendToolTipLine(sb, "pv.track.tooltip.distance", formatUtils.distanceToString((it / 1000f).toDouble(), 3))
        }
        sample.altitude?.let {
            appendToolTipLine(sb, "pv.track.tooltip.altitude", formatUtils.heightToString(it.toInt()))
        }
        sample.heartRate?.let {
            appendToolTipLine(sb, "pv.track.tooltip.heartrate", formatUtils.heartRateToString(it.toInt()))
        }
        sample.speed?.let {
            appendToolTipLine(sb, "pv.track.tooltip.speed", formatUtils.speedToString(it, 2, document.speedMode))
        }
        sample.power?.let {
            appendToolTipLine(sb, "pv.track.tooltip.power", formatUtils.powerToString(it))
        }
        sample.temperature?.let {
            appendToolTipLine(sb, "pv.track.tooltip.temperature", formatUtils.temperatureToString(it))
        }
        return sb.toString()
    }

    private fun appendToolTipLine(sb: StringBuilder, resourceKey: String, value: String) =
            sb.append("${context.resources.getString(resourceKey)}: $value\n")

}
