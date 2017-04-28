package de.saring.leafletmapdemo

import de.saring.leafletmap.*
import javafx.concurrent.Worker
import javafx.fxml.FXML
import javafx.geometry.Point2D
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.StackPane

class Controller {

    private val track = Track.readFromJson("/demo-track.json")

    @FXML
    private lateinit var spMapView: StackPane

    @FXML
    private lateinit var slPosition: Slider

    private val mapView = LeafletMapView()

    private val positionTooltip = Tooltip()

    private var positionMarkerName: String? = null

    @FXML
    fun initialize() {
        spMapView.children.add(mapView)

        val cfMapLoadState = mapView.displayMap(MapConfig(
                layers = MapLayer.values().asList(),
                zoomControlConfig = ZoomControlConfig(true, ControlPosition.BOTTOM_LEFT),
                scaleControlConfig = ScaleControlConfig(true, ControlPosition.BOTTOM_LEFT, metric = true)))

        // display Berlin initially after map has been loaded
        cfMapLoadState.whenComplete { workerState, _ ->
            if (workerState == Worker.State.SUCCEEDED) {
                mapView.setView(LatLong(52.5172, 13.4040), 9)
            }
        }

        positionTooltip.setAutoHide(true)

        slPosition.valueProperty().addListener { _, oldValue, newValue ->
            if (oldValue.toInt() != newValue.toInt()) {
                movePositionMarker()
            }
        }
    }

    @FXML
    private fun onDisplayTrack() {

        // display lap markers first, start and end needs to be displayed on top
        for (i in 0 until track.lapsPositions.size) {
            mapView.addMarker(track.lapsPositions[i], "Lap ${i + 1}", ColorMarker.GREY_MARKER, 0)
        }

        mapView.addMarker(track.positions.first(), "Start", ColorMarker.GREEN_MARKER, 1000)
        mapView.addMarker(track.positions.last(), "End", ColorMarker.RED_MARKER, 2000)

        mapView.addTrack(track.positions)

        setupPositionSliderRange()
    }

    private fun setupPositionSliderRange() {
        slPosition.min = 0.0
        slPosition.max = track.positions.size - 1.0
        slPosition.value = 0.0
    }

    private fun movePositionMarker() {
        val positionIndex = slPosition.value.toInt()
        val position = track.positions[positionIndex]

        if (positionMarkerName == null) {
            positionMarkerName = mapView.addMarker(position, "", ColorMarker.BLUE_MARKER, 0)
        } else {
            mapView.moveMarker(positionMarkerName!!, position)
        }

        displayPositionTooltip(positionIndex)
    }

    private fun displayPositionTooltip(positionIndex: Int) {
        // display position tooltip in the upper left corner of the web view
        var tooltipPos: Point2D = mapView.localToScene(8.0, 8.0)
        tooltipPos = tooltipPos.add(getMapScreenPosition())

        positionTooltip.text = "Position: ${positionIndex + 1}"
        positionTooltip.show(mapView, tooltipPos.x, tooltipPos.y)
    }

    private fun getMapScreenPosition(): Point2D {
        val scene = mapView.scene
        val window = scene.window
        return Point2D(scene.x + window.x, scene.y + window.y)
    }
}
