package de.saring.exerciseviewer.gui.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.JXMapKit.DefaultProviders;
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
        
        // create map viewer later, only when location data is available
    }
    
    /** {@inheritDoc} */
    @Override
    public void displayExercise () {

        // show track in mapviewer if data is available 
        final EVExercise exercise = getDocument ().getExercise ();
        if (exercise.getRecordingMode().isLocation()) {
            setupMapViewer();
            showTrack(exercise);
        }
        else {
            // TODO: show "no track data available" or a disabled map component
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

        // show start and end position of the track
        Position startPosition = exercise.getSampleList()[0].getPosition();
        // Position endPosition = exercise.getSampleList()[exercise.getSampleList().length - 1].getPosition();

        // TODO: only one OverlayPainter can be active - how to display start/end and track?
//        HashSet<Waypoint> waypoints = new HashSet<Waypoint>();
//        waypoints.add(new Waypoint(startPosition.getLatitude(), startPosition.getLongitude()));
//        waypoints.add(new Waypoint(endPosition.getLatitude(), endPosition.getLongitude()));
//        WaypointPainter<JXMapViewer> painter = new WaypointPainter<JXMapViewer>();
//        painter.setWaypoints(waypoints);
//        mapKit.getMainMap().setOverlayPainter(painter);

        // TODO: how to set proper center position and zoom factor? 
        mapKit.setCenterPosition(new GeoPosition(startPosition.getLatitude(), startPosition.getLongitude()));  
        mapKit.setZoom(5);  
        
        // display track
        setupTrackPainter(exercise.getSampleList());
    }

    // TODO: isn't there a predefined painter?
    // This code is based on the example from 
    // http://www.naxos-software.de/blog/index.php?/archives/92-TracknMash-Openstreetmap-Karten-in-JavaSwing-mit-JXMapViewer.html
    private void setupTrackPainter(final ExerciseSample[] exerciseSamples) {

        Painter<JXMapViewer> lineOverlay = new Painter<JXMapViewer>() {
            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                g = (Graphics2D) g.create();

                // convert from viewport to world bitmap
                Rectangle rect = mapKit.getMainMap().getViewportBounds();
                g.translate(-rect.x, -rect.y);

                // do the drawing
                g.setColor(Color.RED);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setStroke(new BasicStroke(2));

                int lastX = -1;
                int lastY = -1;
                for (ExerciseSample exerciseSample : exerciseSamples) {

                    Position pos = exerciseSample.getPosition();
                    if (pos != null) {
                        // convert geo to world bitmap pixel
                        GeoPosition geoPosition = new GeoPosition(pos.getLatitude(), pos.getLongitude());
                        Point2D pt = mapKit.getMainMap().getTileFactory().geoToPixel(geoPosition, mapKit.getMainMap().getZoom());
                        if (lastX != -1 && lastY != -1) {
                            g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
                        }
                        lastX = (int) pt.getX();
                        lastY = (int) pt.getY();
                    }
                }

                g.dispose();
            }
        };
        mapKit.getMainMap().setOverlayPainter(lineOverlay);
    }
}
