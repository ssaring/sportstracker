package de.saring.exerciseviewer.gui.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.saring.leafletmap.ColorMarker;
import de.saring.leafletmap.ControlPosition;
import de.saring.leafletmap.LatLong;
import de.saring.leafletmap.LeafletMapView;
import de.saring.leafletmap.MapConfig;
import de.saring.leafletmap.MapLayer;
import de.saring.leafletmap.ScaleControlConfig;
import de.saring.leafletmap.ZoomControlConfig;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.Position;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Controller (MVC) class of the "Track" panel, which displays the recorded location data of the exercise (if
 * available) in a map.<br/>
 * The map component is LeafletMap which is based on the Leaflet Javascript library, the data provider is OpenStreetMap.
 *
 * @author Stefan Saring
 */
public class TrackPanelController extends AbstractPanelController {

    private static final Logger LOGGER = Logger.getLogger(TrackPanelController.class.getName());

    private static final int TRACKPOINT_TOOLTIP_DISTANCE_BUFFER = 4;

    @FXML
    private StackPane spTrackPanel;

    @FXML
    private VBox vbTrackViewer;

    @FXML
    private StackPane spMapViewer;

    @FXML
    private Slider slPosition;

    private LeafletMapView mapView;
    private MapConfig mapConfig;

    private Tooltip spMapViewerTooltip;
    private String positionMarkerName;

    /** Flag whether the exercise track has already been shown. */
    private boolean showTrackExecuted = false;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer document / model
     */
    public TrackPanelController(final EVContext context, final EVDocument document) {
        super(context, document);
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/panels/TrackPanel.fxml";
    }

    @Override
    protected void setupPanel() {

        // setup the map viewer if track data is available
        final EVExercise exercise = getDocument().getExercise();
        if (exercise.getRecordingMode().isLocation()) {
            setupMapView();
            setupMapViewerTooltip();
            setupTrackPositionSlider();
        } else {
            // remove the track viewer VBox, the StackPane now displays the label "No track data available")
            spTrackPanel.getChildren().remove(vbTrackViewer);
        }
    }

    private void setupMapView() {
        mapView = new LeafletMapView();
        spMapViewer.getChildren().add(mapView);

        final boolean metric = getDocument().getOptions().getUnitSystem() == FormatUtils.UnitSystem.Metric;

        mapConfig = new MapConfig(
                Arrays.asList(MapLayer.OPENSTREETMAP, MapLayer.OPENCYCLEMAP, MapLayer.HIKE_BIKE_MAP, MapLayer.MTB_MAP),
                new ZoomControlConfig(true, ControlPosition.BOTTOM_LEFT),
                new ScaleControlConfig(true, ControlPosition.BOTTOM_LEFT, metric));
    }

    private void setupMapViewerTooltip() {
        spMapViewerTooltip = new Tooltip();
        spMapViewerTooltip.setAutoHide(true);
    }

    private void setupTrackPositionSlider() {
        // on position slider changes: update position marker in the map viewer and display tooltip with details
        // (slider uses a double value, make sure the int value has changed)
        slPosition.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() != newValue.intValue()) {
                movePositionMarker(newValue.intValue());
            }
        });
    }

    private void movePositionMarker(final int positionIndex) {
        final ExerciseSample sample = getDocument().getExercise().getSampleList()[positionIndex];

        // some samples could have no position
        if (sample.getPosition() != null) {
            final LatLong position = new LatLong(sample.getPosition().getLatitude(), sample.getPosition().getLongitude());

            if (positionMarkerName == null) {
                positionMarkerName = mapView.addMarker(position, "", ColorMarker.BLUE_MARKER, 0);
            } else {
                mapView.moveMarker(positionMarkerName, position);
            }

            final String tooltipText = createToolTipText(positionIndex);
            spMapViewerTooltip.setText(tooltipText);

            // display position tooltip in the upper left corner of the map viewer container
            Point2D tooltipPos = spMapViewer.localToScene(8d, 8d);
            tooltipPos = tooltipPos.add(getMapViewerScreenPosition());
            spMapViewerTooltip.show(spMapViewer, tooltipPos.getX(), tooltipPos.getY());
        }
    }

    /**
     * Displays map and the track of the current exercise, if available. This method will be executed only once and
     * should be called when the user wants to see the track (to prevent long startup delays).
     */
    public void showMapAndTrack() {
        if (!showTrackExecuted) {
            showTrackExecuted = true;

            EVExercise exercise = getDocument().getExercise();
            if (exercise.getRecordingMode().isLocation()) {

                // display map, on success display the track and laps
                mapView.displayMap(mapConfig).whenComplete((workerState, throwable) -> {

                    if (workerState == Worker.State.SUCCEEDED) {
                        showTrackAndLaps();
                        // enable position slider by setting max. sample count
                        slPosition.setMax(exercise.getSampleList().length - 1);
                    } else if (throwable != null) {
                        LOGGER.log(Level.SEVERE, "Failed to display map!", throwable);
                    }
                });
            }
        }
    }

    public void showTrackAndLaps() {
        EVExercise exercise = getDocument().getExercise();
        List<LatLong> samplePositions = createSamplePositionList(exercise);

        if (!samplePositions.isEmpty()) {
            mapView.addTrack(samplePositions);

            // display lap markers first, start and end needs to be displayed on top
            List<LatLong> lapPositions = createLapPositionList(exercise);
            for (int i = 0; i < lapPositions.size(); i++) {
                mapView.addMarker(lapPositions.get(i),
                        getContext().getResources().getString("pv.track.maptooltip.lap", i + 1),
                        ColorMarker.GREY_MARKER, 0);
            }

            mapView.addMarker(samplePositions.get(0),
                    getContext().getResources().getString("pv.track.maptooltip.start"),
                    ColorMarker.GREEN_MARKER, 1000);
            mapView.addMarker(samplePositions.get(samplePositions.size() - 1),
                    getContext().getResources().getString("pv.track.maptooltip.end"),
                    ColorMarker.RED_MARKER, 2000);
        }
    }

    private List<LatLong> createSamplePositionList(final EVExercise exercise) {
        final List<LatLong> positions = new ArrayList<>();

        for (ExerciseSample sample : exercise.getSampleList()) {
            final Position pos = sample.getPosition();
            if (pos != null) {
                positions.add(new LatLong(pos.getLatitude(), pos.getLongitude()));
            }
        }
        return positions;
    }

    private List<LatLong> createLapPositionList(final EVExercise exercise) {
        final List<LatLong> lapPositions = new ArrayList<>();

        // ignore last lap split position, it's the exercise end position
        for (int i = 0; i < exercise.getLapList().length - 1; i++) {
            final Lap lap = exercise.getLapList()[i];
            final Position pos = lap.getPositionSplit();
            if (pos != null) {
                lapPositions.add(new LatLong(pos.getLatitude(), pos.getLongitude()));
            }
        }
        return lapPositions;
    }

    private Point2D getMapViewerScreenPosition() {
        final Scene scene = spMapViewer.getScene();
        final Window window = scene.getWindow();
        return new Point2D(scene.getX() + window.getX(), scene.getY() + window.getY());
    }

    /**
     * Creates the tool tip text for the specified exercise sample to be shown on the map.
     *
     * @param sampleIndex index of the exercise sample
     * @return text
     */
    private String createToolTipText(int sampleIndex) {

        EVExercise exercise = getDocument().getExercise();
        ExerciseSample sample = exercise.getSampleList()[sampleIndex];
        FormatUtils formatUtils = getContext().getFormatUtils();

        StringBuilder sb = new StringBuilder();
        appendToolTipLine(sb, "pv.track.tooltip.trackpoint", String.valueOf(sampleIndex + 1));
        appendToolTipLine(sb, "pv.track.tooltip.time",
                formatUtils.seconds2TimeString((int) (sample.getTimestamp() / 1000)));
        appendToolTipLine(sb, "pv.track.tooltip.distance",
                formatUtils.distanceToString(sample.getDistance() / 1000f, 3));
        if (exercise.getRecordingMode().isAltitude()) {
            appendToolTipLine(sb, "pv.track.tooltip.altitude", //
                    formatUtils.heightToString(sample.getAltitude()));
        }
        appendToolTipLine(sb, "pv.track.tooltip.heartrate", //
                formatUtils.heartRateToString(sample.getHeartRate()));
        if (exercise.getRecordingMode().isSpeed()) {
            appendToolTipLine(sb, "pv.track.tooltip.speed", //
                    formatUtils.speedToString(sample.getSpeed(), 2));
        }
        if (exercise.getRecordingMode().isTemperature()) {
            appendToolTipLine(sb, "pv.track.tooltip.temperature", //
                    formatUtils.temperatureToString(sample.getTemperature()));
        }
        return sb.toString();
    }

    private void appendToolTipLine(StringBuilder sb, String resourceKey, String value) {
        sb.append(getContext().getResources().getString(resourceKey));
        sb.append(": ").append(value).append("\n");
    }
}
