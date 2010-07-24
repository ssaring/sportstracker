package de.saring.exerciseviewer.gui.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapKit.DefaultProviders;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.painter.Painter;

import com.google.inject.Inject;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.Position;
import de.saring.exerciseviewer.gui.EVContext;

/**
 * This class is the implementation of the "Track" panel, which displays the recorded location  
 * data of the exercise (if available) in a map.<br>
 * The map component is JXMapKit from the SwingLabs project, the data provider is OpenStreetMap.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class TrackPanel extends BasePanel {
    
    private JXMapKit mapKit;
    private boolean panelWasVisible = false;
    
    /**
     * Standard c'tor.
     * @param context the ExerciseViewer context
     */
    @Inject
    public TrackPanel (EVContext context) {
        super (context);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));
        
        // create initial label, that track data is not available
        // (map viewer will be created later)
        JLabel laNoTrackData = new JLabel();
        laNoTrackData.setHorizontalAlignment(JLabel.CENTER);
        laNoTrackData.setName("pv.track.no_track_data");
        add(laNoTrackData, java.awt.BorderLayout.CENTER);  
    }
    
    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        
        // display the track when this panel gets visible for the first time 
        // (because the needed map viewer dimensions are not set at panel creation)
        if (visible && !panelWasVisible) {
            displayTrack();
            panelWasVisible = true;
        }
        super.setVisible(visible);
    }
    
    /** {@inheritDoc} */
    @Override
    public void displayExercise () {
        // track will be displayed later
    }

    /**
     * Shows the exercise track in the map viewer (if track data is available). 
     */
    private void displayTrack() {
        final EVExercise exercise = getDocument ().getExercise ();
        
        if (exercise.getRecordingMode().isLocation()) {
            setupMapViewer();
            showTrack(exercise);
        }
    }
    
    private void setupMapViewer() {
        mapKit = new JXMapKit();
        mapKit.setDefaultProvider(DefaultProviders.OpenStreetMaps);           

        removeAll();
        add(mapKit, java.awt.BorderLayout.CENTER);  
    }

    /**
     * Displays the track of the specified exercise.
     * 
     * @param exercise the exercise with track data
     */
    private void showTrack(EVExercise exercise) {        
        List<GeoPosition> geoPositions = createGeoPositionList(exercise);
        
        if (!geoPositions.isEmpty()) {
            // setup map zoom and position
            setupZoomAndCenterPosition(geoPositions);            
            // display track
            setupTrackPainter(geoPositions);
        }
    }
    
    /**
     * Sets the zoom level and map center position. The full track will be visible
     * with as much details as possible.
     * This implementations is a workaround for a bug in JXMapViewer.calculateZoomFrom(),
     * which should do the same.
     * @param positions list of positions of the route
     */
    private void setupZoomAndCenterPosition(List<GeoPosition> positions) {

        // calculate and set center position of the track
        Rectangle2D gpRectangle = createGeoPositionRectangle(positions);
        GeoPosition gpCenter = new GeoPosition(gpRectangle.getCenterX(), gpRectangle.getCenterY());
        mapKit.setCenterPosition(gpCenter);
        
        // calculate mapKit dimensions based on panel dimensions (with a little offset)
        // (there's a bug in JXMapKit.getWidth/getHeight)
        int mapKitWidth = getWidth() - 30;
        int mapKitHeight = getHeight() - 30;

        // start with zoom level for maximum details
        boolean fullTrackVisible = false;
        int currentZoom = 0;
        int maxZoom = mapKit.getMainMap().getTileFactory().getInfo().getMaximumZoomLevel();

        // stop when the track is completely visible or when the max zoom level has been reached
        while (!fullTrackVisible && currentZoom < maxZoom) {
            currentZoom++;
            mapKit.setZoom(currentZoom);

            // calculate pixel positions of top left and bottom right in the track rectangle  
            Point2D ptTopLeft = convertGeoPosToPixelPos(new GeoPosition(
                    gpRectangle.getX(), gpRectangle.getY()));
            Point2D ptBottomRight = convertGeoPosToPixelPos(new GeoPosition(
                    gpRectangle.getX() + gpRectangle.getWidth(), 
                    gpRectangle.getY() + gpRectangle.getHeight()));

            // calculate current track width and height in pixels (can be negative) 
            int trackPixelWidth = Math.abs((int) (ptBottomRight.getX() - ptTopLeft.getX()));
            int trackPixelHeight = Math.abs((int) (ptBottomRight.getY() - ptTopLeft.getY()));
            
            // track is completely visible when track dimensions are smaller than map viewer dimensions
            fullTrackVisible = trackPixelWidth < mapKitWidth && trackPixelHeight < mapKitHeight;
        }        
    }
    
    /**
     * Creates a rectangle of minimal size which contains all specified GeoPositions.
     * @param positions list of positions of the route
     * @return the created Rectangle
     */
    private Rectangle2D createGeoPositionRectangle(List<GeoPosition> positions) {        
        Rectangle2D rect = new Rectangle2D.Double(
            positions.get(0).getLatitude(), positions.get(0).getLongitude(), 0, 0);
        
        for (GeoPosition pos : positions) {
            rect.add(new Point2D.Double(pos.getLatitude(), pos.getLongitude()));
        }
        return rect;
    }
    
    /**
     * Creates a custom painter which draws the track.
     * 
     * @param geoPositions list of GeoPosition objects of this track
     */
    private void setupTrackPainter(final List<GeoPosition> geoPositions) {
        
        Painter<JXMapViewer> lineOverlay = new Painter<JXMapViewer>() {
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                
                g = (Graphics2D) g.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // convert from viewport to world bitmap
                Rectangle rect = mapKit.getMainMap().getViewportBounds();
                g.translate(-rect.x, -rect.y);

                // draw track line and waypoints for start and end position
                drawTrackLine(g, geoPositions);
                drawWaypoint(g, geoPositions.get(0), Color.GREEN);
                drawWaypoint(g, geoPositions.get(geoPositions.size()-1), Color.BLUE);

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
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));

        int lastX = -1;
        int lastY = -1;
        
        for (GeoPosition geoPosition : geoPositions) {
            Point2D pt = convertGeoPosToPixelPos(geoPosition);
            if (lastX != -1 && lastY != -1) {
                g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
            }
            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }
    }

    /**
     * Draws a waypoint circle at the specified GeoPosition.
     * 
     * @param g the Graphics2D context
     * @param geoPosition position of the waypoint
     * @param color the color of the circle
     */
    private void drawWaypoint(Graphics2D g, GeoPosition geoPosition, Color color) {
    	final int RADIUS = 5;
    	
        Point2D pt = convertGeoPosToPixelPos(geoPosition);
        
        // draw an outer gray circle, so it's better visible on backgrounds with same color
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(3));
        g.draw(new Ellipse2D.Double(pt.getX() - (RADIUS+1), pt.getY() - (RADIUS+1), (RADIUS*2)+2, (RADIUS*2)+2));
        
        g.setColor(color);
        g.setStroke(new BasicStroke(3));
        g.draw(new Ellipse2D.Double(pt.getX() - RADIUS, pt.getY() - RADIUS, RADIUS*2, RADIUS*2));
    }
    
    private List<GeoPosition> createGeoPositionList(EVExercise exercise) {
        ArrayList<GeoPosition> geoPositions = new ArrayList<GeoPosition>();
        
        for (ExerciseSample exerciseSample : exercise.getSampleList()) {
            Position pos = exerciseSample.getPosition();
            if (pos != null) {
                geoPositions.add(new GeoPosition(pos.getLatitude(), pos.getLongitude()));
            }
        }
        return geoPositions;
    }

    private Point2D convertGeoPosToPixelPos(GeoPosition geoPosition) {
        return mapKit.getMainMap().getTileFactory().geoToPixel(geoPosition, mapKit.getMainMap().getZoom());
    }    
}
