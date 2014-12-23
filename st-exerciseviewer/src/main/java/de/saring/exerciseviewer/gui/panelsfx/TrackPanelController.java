package de.saring.exerciseviewer.gui.panelsfx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.painter.Painter;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.Position;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Controller (MVC) class of the "Track" panel, which displays the recorded location data of
 * the exercise (if available) in a map.<br/>
 * The map component is JXMapKit from the SwingLabs project, the data provider is OpenStreetMap.
 *
 * TODO use a JavaFX based map viewer component instead of JXMapKit!
 *
 * @author Stefan Saring
 */
public class TrackPanelController extends AbstractPanelController {

    private static final Color COLOR_START = new Color(180, 255, 180);
    private static final Color COLOR_END = new Color(255, 180, 180);
    private static final Color COLOR_LAP = Color.WHITE;
    private static final Color COLOR_TRACK = Color.RED;

    private static final int TRACKPOINT_TOOLTIP_DISTANCE_BUFFER = 4;

    @FXML
    private SwingNode snMapViewer;

    private JXMapKit mapKit;

    /** Flag whether the exercise track has already been shown. */
    private boolean showTrackExecuted = false;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer model/document
     */
    public TrackPanelController(final EVContext context, final EVDocument document) {
        super(context, document);
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/TrackPanel.fxml";
    }

    @Override
    protected void setupPanel() {

        // setup the map viewer (if track data is available).
        final EVExercise exercise = getDocument().getExercise();
        if (exercise.getRecordingMode().isLocation()) {
            SwingUtilities.invokeLater(() -> {
                setupMapViewer();
            });
        } else {
            // display the label "No track data available" behind the map viewer
            snMapViewer.setVisible(false);
        }
    }

    private void setupMapViewer() {
        mapKit = new JXMapKit();
        mapKit.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);

        // add MouseMotionListener to the map for nearby sample lookup and tooltip creation
        mapKit.getMainMap().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                lookupNearbySampleAndCreateToolTip(e);
            }
        });

        snMapViewer.setContent(mapKit);
    }

    /**
     * Displays the track of the current exercise, if available. This method will be executed only
     * once and should be called when the user wants to see the track (to prevent long startup delays).
     */
    public void showTrack() {
        if (!showTrackExecuted) {
            showTrackExecuted = true;

            System.out.println("Init!");

            EVExercise exercise = getDocument().getExercise();
            if (exercise.getRecordingMode().isLocation()) {

                SwingUtilities.invokeLater(() -> {
                    List<GeoPosition> sampleGeoPositions = createSampleGeoPositionList(exercise);
                    List<GeoPosition> lapGeoPositions = createLapGeoPositionList(exercise);

                    if (!sampleGeoPositions.isEmpty()) {
                        // setup map zoom and position
                        setupZoomAndCenterPosition(sampleGeoPositions);
                        // display track
                        setupTrackPainter(sampleGeoPositions, lapGeoPositions);
                    }
                });
            }
        }
    }

    /**
     * Sets the zoom level and map center position. The full track will be visible
     * with as much details as possible.
     * This implementations is a workaround for a bug in JXMapViewer.calculateZoomFrom(),
     * which should do the same.
     *
     * @param positions list of positions of the route
     */
    private void setupZoomAndCenterPosition(List<GeoPosition> positions) {

        // calculate and set center position of the track
        Rectangle2D gpRectangle = createGeoPositionRectangle(positions);
        GeoPosition gpCenter = new GeoPosition(gpRectangle.getCenterX(), gpRectangle.getCenterY());
        mapKit.setCenterPosition(gpCenter);

        // calculate mapKit dimensions based on the SwingNode dimensions (with a little offset)
        // (there's a bug in JXMapKit.getWidth/getHeight)
        Bounds mapViewerBounds = snMapViewer.getLayoutBounds();
        int mapKitWidth = (int) mapViewerBounds.getWidth() - 30;
        int mapKitHeight = (int) mapViewerBounds.getHeight() - 30;

        // start with zoom level for maximum details
        boolean fullTrackVisible = false;
        int currentZoom = 0;
        int maxZoom = mapKit.getMainMap().getTileFactory().getInfo().getMaximumZoomLevel();

        // stop when the track is completely visible or when the max zoom level has been reached
        while (!fullTrackVisible && currentZoom < maxZoom) {
            currentZoom++;
            mapKit.setZoom(currentZoom);

            // calculate pixel positions of top left and bottom right in the track rectangle
            Point2D ptTopLeft = convertGeoPosToPixelPos(new GeoPosition(gpRectangle.getX(), gpRectangle.getY()));
            Point2D ptBottomRight = convertGeoPosToPixelPos(new GeoPosition(
                    gpRectangle.getX() + gpRectangle.getWidth(), gpRectangle.getY() + gpRectangle.getHeight()));

            // calculate current track width and height in pixels (can be negative)
            int trackPixelWidth = Math.abs((int) (ptBottomRight.getX() - ptTopLeft.getX()));
            int trackPixelHeight = Math.abs((int) (ptBottomRight.getY() - ptTopLeft.getY()));

            // track is completely visible when track dimensions are smaller than map viewer dimensions
            fullTrackVisible = trackPixelWidth < mapKitWidth && trackPixelHeight < mapKitHeight;
        }
    }

    /**
     * Creates a rectangle of minimal size which contains all specified GeoPositions.
     *
     * @param positions list of positions of the route
     * @return the created Rectangle
     */
    private Rectangle2D createGeoPositionRectangle(List<GeoPosition> positions) {
        Rectangle2D rect = new Rectangle2D.Double(positions.get(0).getLatitude(), positions.get(0).getLongitude(), 0, 0);

        for (GeoPosition pos : positions) {
            rect.add(new Point2D.Double(pos.getLatitude(), pos.getLongitude()));
        }
        return rect;
    }

    /**
     * Creates a custom painter which draws the track.
     *
     * @param sampleGeoPositions list of GeoPosition objects of all samples of this track
     * @param lapGeoPositions list of GeoPosition objects of all lap splits this track
     */
    private void setupTrackPainter(final List<GeoPosition> sampleGeoPositions, final List<GeoPosition> lapGeoPositions) {

        Painter<JXMapViewer> lineOverlay = new Painter<JXMapViewer>() {
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {

                g = (Graphics2D) g.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // convert from viewport to world bitmap
                Rectangle rect = mapKit.getMainMap().getViewportBounds();
                g.translate(-rect.x, -rect.y);

                // draw track line
                drawTrackLine(g, sampleGeoPositions);

                // draw waypoints for all lap split positions
                for (int i = 0; i < lapGeoPositions.size(); i++) {
                    GeoPosition geoPosition = lapGeoPositions.get(i);
                    drawWaypoint(g, geoPosition, String.valueOf(i + 1), COLOR_LAP);
                }

                // draw waypoints for start and end position
                drawWaypoint(g, sampleGeoPositions.get(0), "S", COLOR_START);
                drawWaypoint(g, sampleGeoPositions.get(sampleGeoPositions.size() - 1), "E", COLOR_END);

                g.dispose();
            }
        };
        mapKit.getMainMap().setOverlayPainter(lineOverlay);
    }

    /**
     * Draws a red line which connects all GeoPosition of the track.
     *
     * @param g the Graphics2D context
     * @param geoPositions list of GeoPosition objects of this track
     */
    private void drawTrackLine(Graphics2D g, List<GeoPosition> geoPositions) {
        g.setColor(COLOR_TRACK);
        g.setStroke(new BasicStroke(2));

        int lastX = -1;
        int lastY = -1;

        for (GeoPosition geoPosition : geoPositions) {
            Point2D pt = convertGeoPosToPixelPos(geoPosition);
            if (lastX != -1 && lastY != -1) {
                g.drawLine(lastX, lastY, round(pt.getX()), round(pt.getY()));
            }
            lastX = round(pt.getX());
            lastY = round(pt.getY());
        }
    }

    private int round(double value) {
        return (int) Math.round(value);
    }

    /**
     * Draws a waypoint circle and a description text at the specified GeoPosition.
     *
     * @param g the Graphics2D context
     * @param geoPosition position of the waypoint
     * @param text the description text (right to the circle)
     * @param color the color of the circle
     */
    private void drawWaypoint(Graphics2D g, GeoPosition geoPosition, String text, Color color) {
        final int RADIUS = 5;

        Point2D pt = convertGeoPosToPixelPos(geoPosition);

        // draw an outer gray circle, so it's better visible on backgrounds with same color
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(3));
        g.draw(new Ellipse2D.Double(pt.getX() - (RADIUS + 1), pt.getY() - (RADIUS + 1), (RADIUS * 2) + 2,
                (RADIUS * 2) + 2));

        g.setColor(color);
        g.setStroke(new BasicStroke(3));
        g.draw(new Ellipse2D.Double(pt.getX() - RADIUS, pt.getY() - RADIUS, RADIUS * 2, RADIUS * 2));

        // draw the text right from the circle with a gray shadow
        int textPosX = round(pt.getX() + RADIUS * 2.2);
        int textPosY = round(pt.getY() + 3);

        g.setFont(new Font("Dialog.bold", Font.BOLD, 12));

        g.setColor(Color.DARK_GRAY);
        g.drawString(text, textPosX + 1, textPosY + 1);
        g.setColor(color);
        g.drawString(text, textPosX, textPosY);
    }

    private List<GeoPosition> createSampleGeoPositionList(EVExercise exercise) {
        ArrayList<GeoPosition> geoPositions = new ArrayList<>();

        for (ExerciseSample exerciseSample : exercise.getSampleList()) {
            Position pos = exerciseSample.getPosition();
            if (pos != null) {
                geoPositions.add(new GeoPosition(pos.getLatitude(), pos.getLongitude()));
            }
        }
        return geoPositions;
    }

    private List<GeoPosition> createLapGeoPositionList(EVExercise exercise) {
        ArrayList<GeoPosition> geoPositions = new ArrayList<>();

        // ignore last lap split position, it's the exercise end position
        for (int i = 0; i < exercise.getLapList().length - 1; i++) {
            Lap lap = exercise.getLapList()[i];
            Position pos = lap.getPositionSplit();
            if (pos != null) {
                geoPositions.add(new GeoPosition(pos.getLatitude(), pos.getLongitude()));
            }
        }
        return geoPositions;
    }

    private Point2D convertGeoPosToPixelPos(GeoPosition geoPosition) {
        return mapKit.getMainMap().getTileFactory().geoToPixel(geoPosition, mapKit.getMainMap().getZoom());
    }

    private GeoPosition convertPixelPosToGeoPos(Point2D point) {
        return mapKit.getMainMap().getTileFactory().pixelToGeo(point, mapKit.getMainMap().getZoom());
    }

    /**
     * This method must be called on every mouse movement. It searches for an exercise samples
     * nearby the mouse position. If a sample was found, then a tooltip with all the sample
     * details will be shown.
     *
     * @param e the MouseEvent
     */
    private void lookupNearbySampleAndCreateToolTip(MouseEvent e) {

        // get mouse position in the map component (translation needed)
        // => the offset of 1 pixel is needed for proper centered detection of nearby trackpoints
        Rectangle rect = mapKit.getMainMap().getViewportBounds();
        Point mousePos = e.getPoint();
        mousePos.translate(rect.x - 1, rect.y - 1);
        GeoPosition mouseGeoPos = convertPixelPosToGeoPos(mousePos);

        // compute the latitude and longitude distance buffer for searching a nearby sample
        Point bufferPos = new Point(mousePos.x + TRACKPOINT_TOOLTIP_DISTANCE_BUFFER, mousePos.y
                - TRACKPOINT_TOOLTIP_DISTANCE_BUFFER);
        GeoPosition bufferGeoPos = convertPixelPosToGeoPos(bufferPos);

        double latitudeBuffer = Math.abs(bufferGeoPos.getLatitude() - mouseGeoPos.getLatitude());
        double longitudeBuffer = Math.abs(bufferGeoPos.getLongitude() - mouseGeoPos.getLongitude());

        // lookup a nearby sample and show tooltip text when found (or delete tooltip if not found)
        String toolTipText = null;

        int nearBySampleIndex = getSampleIndexNearbyGeoPos(mouseGeoPos, latitudeBuffer, longitudeBuffer);
        if (nearBySampleIndex >= 0) {
            toolTipText = createToolTipText(nearBySampleIndex);
        }

        mapKit.getMainMap().setToolTipText(toolTipText);
    }

    /**
     * Searches for the exercise sample with the position nearby the specified position.
     *
     * @param geoPos the position to search for a nearby exercise sample
     * @param latitudeBuffer longitude distance buffer, the exercise sample must be located closer
     * @param longitudeBuffer longitude distance buffer, the exercise sample must be located closer
     * @return the index of the found exercise sample or -1 when no sample found
     */
    private int getSampleIndexNearbyGeoPos(GeoPosition geoPos, double latitudeBuffer, double longitudeBuffer) {
        EVExercise exercise = getDocument().getExercise();

        for (int i = 0; i < exercise.getSampleList().length; i++) {
            ExerciseSample sample = exercise.getSampleList()[i];
            Position samplePos = sample.getPosition();

            if (samplePos != null && Math.abs(samplePos.getLatitude() - geoPos.getLatitude()) < latitudeBuffer
                    && Math.abs(samplePos.getLongitude() - geoPos.getLongitude()) < longitudeBuffer) {
                return i;
            }
        }
        return -1;
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
        sb.append("<html>");

        appendToolTipLine(sb, "pv.track.tooltip.trackpoint", String.valueOf(sampleIndex + 1));
        appendToolTipLine(sb, "pv.track.tooltip.time",
                formatUtils.seconds2TimeString((int) (sample.getTimestamp() / 1000)));
        appendToolTipLine(sb, "pv.track.tooltip.distance",
                formatUtils.distanceToString(sample.getDistance() / 1000f, 3));
        if (exercise.getRecordingMode().isAltitude()) {
            appendToolTipLine(sb, "pv.track.tooltip.altitude", formatUtils.heightToString(sample.getAltitude()));
        }
        appendToolTipLine(sb, "pv.track.tooltip.heartrate", formatUtils.heartRateToString(sample.getHeartRate()));
        if (exercise.getRecordingMode().isSpeed()) {
            appendToolTipLine(sb, "pv.track.tooltip.speed", formatUtils.speedToString(sample.getSpeed(), 2));
        }
        if (exercise.getRecordingMode().isTemperature()) {
            appendToolTipLine(sb, "pv.track.tooltip.temperature",
                    formatUtils.temperatureToString(sample.getTemperature()));
        }

        sb.append("</html>");
        return sb.toString();
    }

    private void appendToolTipLine(StringBuilder sb, String resourceKey, String value) {
        sb.append(getContext().getFxResources().getString(resourceKey));
        sb.append(": ").append(value).append("<br/>");
    }
}
