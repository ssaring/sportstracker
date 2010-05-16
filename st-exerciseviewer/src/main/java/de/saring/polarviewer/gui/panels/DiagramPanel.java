package de.saring.polarviewer.gui.panels;

import com.google.inject.Inject;
import de.saring.polarviewer.data.ExerciseSample;
import de.saring.polarviewer.data.HeartRateLimit;
import de.saring.polarviewer.data.Lap;
import de.saring.polarviewer.data.PVExercise;
import de.saring.polarviewer.gui.PVContext;
import de.saring.util.ResourceReader;
import de.saring.util.gui.jfreechart.ChartUtils;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ActionMap;
import org.jdesktop.application.Action;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 * This class is the implementation of the "Diagram" panel, which displays
 * the exercise graphically (heartrate, altitude, speed and cadence).
 *
 * @author  Stefan Saring, Jacob Ilsoe Christensen (parts of C# version)
 * @version 1.0
 */
public class DiagramPanel extends BasePanel {
    
    /** The panel containing the current chart. */
    private ChartPanel chartPanel;
    
    /** Flag for ignoring actions for updating the diagram. */
    private boolean fIgnoreUpdateDiagramActions = false;
    
    /** The index of the exercise heartrate range to be highlighted. */
    private int highlightHeartrateRange = -1;

    /** The colors of the chart. */
    private static final Color COLOR_AXIS_LEFT = Color.RED;
    private static final Color COLOR_AXIS_RIGHT = Color.BLUE;
    private static final Color COLOR_MARKER_LAP = new Color (0f, 0.73f, 0f);
    private static final Color COLOR_MARKER_HEARTRATE = Color.LIGHT_GRAY;
    
    private static final String ACTION_UPDATE_DIAGRAM = "pv.diagram.update";
    
    
    /**
     * Standard c'tor.
     * @param context the PolarViewer context
     */
    @Inject
    public DiagramPanel (PVContext context) {
        super (context);
        initComponents ();
        
        // set static enumeration dependencies
        AxisType.setResReader (getContext ().getResReader ());
        AxisType.setFormatUtils (getContext ().getFormatUtils ());
        
        // add chart panel
        chartPanel = new ChartPanel (null);
        pDiagram.add (chartPanel, BorderLayout.CENTER);
        // set chart panel max size to screen size, otherwise the chart will 
        // be just scaled and not redrawn when screen size changes
        Dimension dimScreen = Toolkit.getDefaultToolkit ().getScreenSize ();
        chartPanel.setMaximumDrawWidth ((int) dimScreen.getWidth ());
        chartPanel.setMaximumDrawHeight ((int) dimScreen.getHeight ());

        // setup event handling
        ActionMap actionMap = getContext ().getSAFContext ().getActionMap (getClass (), this);
        javax.swing.Action aUpdate = actionMap.get (ACTION_UPDATE_DIAGRAM);
        cbLeft.setAction (aUpdate);
        cbRight.setAction (aUpdate);
        cbBottom.setAction (aUpdate);
    }

    /** {@inheritDoc} */
    @Override
    public void displayExercise () {
        PVExercise exercise = getDocument ().getExercise ();
        
        // fill axis comboboxes with possible axis types 
        // (disable event handling while filling)
        fIgnoreUpdateDiagramActions = true;
        cbLeft.addItem (AxisType.Heartrate);        
        cbRight.addItem (AxisType.Nothing);
        cbRight.addItem (AxisType.Heartrate);
        cbBottom.addItem (AxisType.Time);
        
        // add altitude item if recorded
        if (exercise.getRecordingMode ().isAltitude ()) {
            cbLeft.addItem (AxisType.Altitude);
            cbRight.addItem (AxisType.Altitude);
        }
        
        // add speed and distance item if recorded
        if (exercise.getRecordingMode ().isSpeed ()) {
            cbLeft.addItem (AxisType.Speed);
            cbRight.addItem (AxisType.Speed);
            cbBottom.addItem (AxisType.Distance);
        }
        
        // add cadence item if recorded
        if (exercise.getRecordingMode ().isCadence ()) {
            cbLeft.addItem (AxisType.Cadence);
            cbRight.addItem (AxisType.Cadence);
        }
        
        // add temperature item if recorded
        if (exercise.getRecordingMode ().isTemperature ()) {
            cbLeft.addItem (AxisType.Temperature);
            cbRight.addItem (AxisType.Temperature);
        }
        
        // do we need to display the second diagram too?
        if (getDocument ().getOptions ().isDisplaySecondDiagram ()) {
            // it's only possible when additional data is available (first 2 entries
            // are nothing and heartrate, which is allready displayed)
            if (cbRight.getItemCount () > 2) {
                cbRight.setSelectedIndex (2);
            }
        }

        fIgnoreUpdateDiagramActions = false;
        updateDiagram ();
    }
    
    /**
     * Update the diagram and highlight the specified heartrate range.
     * @param rangeIndex index of heartrate range to highlight
     */
    public void displayDiagramForHeartrateRange (int rangeIndex) {        
        highlightHeartrateRange = rangeIndex;
        
        // don't update the diagram when this panel was not initialized yet
        if (cbLeft.getItemCount () > 0) {            
            updateDiagram ();
        }
    }
    
    /**
     * Draws the diagram according to the current display settings.
     */
    @Action(name=ACTION_UPDATE_DIAGRAM)
    public void updateDiagram ()
    {
        if (fIgnoreUpdateDiagramActions) {
            return;
        }
        
        PVExercise exercise = getDocument ().getExercise ();
        
        AxisType axisTypeLeft = (AxisType) cbLeft.getSelectedItem ();
        AxisType axisTypeRight = (AxisType) cbRight.getSelectedItem ();
        AxisType axisTypeBottom = (AxisType) cbBottom.getSelectedItem ();
        boolean fDomainAxisTime = axisTypeBottom == AxisType.Time;
        
        // create and fill data series according to axis type
        // (right axis only when user selected a different axis type)
        Series sLeft = createSeries (fDomainAxisTime, "left");
        Series sRight = null;
        if ((axisTypeRight != AxisType.Nothing) && (axisTypeRight != axisTypeLeft)) {
            sRight = createSeries (fDomainAxisTime, "right");
        }

        
        // fill data series with all recorded exercise samples
        if (exercise.getSampleList () != null) {
            for (int i = 0; i < exercise.getSampleList ().length; i++) {

                ExerciseSample sample = exercise.getSampleList ()[i];
                Number valueLeft = getSampleValue (axisTypeLeft, sample);
                Number valueRight = getSampleValue (axisTypeRight, sample);

                if (fDomainAxisTime) {                
                    // calculate current second 
                    int timeSeconds = i * exercise.getRecordingInterval ();
                    Second second = createJFreeChartSecond (timeSeconds);
                    fillDataInTimeSeries ((TimeSeries) sLeft, (TimeSeries) sRight, second, valueLeft, valueRight);
                }
                else {
                    // get current distance of this sample
                    double fDistance = sample.getDistance () / 1000f;
                    if (getContext ().getFormatUtils ().getUnitSystem () != FormatUtils.UnitSystem.Metric) {
                        fDistance = ConvertUtils.convertKilometer2Miles (fDistance, false);
                    }
                    fillDataInXYSeries ((XYSeries) sLeft, (XYSeries) sRight, fDistance, valueLeft, valueRight);
                }
            }
        }
        // some Polar models only record lap data. no samples (e.g. RS200SD)
        else if (exercise.getLapList () != null)
        {
            // data starts with first lap => add 0 values (otherwise not displayed)
            if (fDomainAxisTime) {                
                fillDataInTimeSeries ((TimeSeries) sLeft, (TimeSeries) sRight, createJFreeChartSecond (0), 0, 0);
            }
            else {
                fillDataInXYSeries ((XYSeries) sLeft, (XYSeries) sRight, 0, 0, 0);
            }
                    
            // fill data series with all recorded exercise laps
            for (int i = 0; i < exercise.getLapList ().length; i++) {

                Lap lap = exercise.getLapList ()[i];
                Number valueLeft = getLapValue (axisTypeLeft, lap);
                Number valueRight = getLapValue (axisTypeRight, lap);

                if (fDomainAxisTime) {                
                    // calculate current second 
                    int timeSeconds = Math.round (lap.getTimeSplit () / 10f);
                    Second second = createJFreeChartSecond (timeSeconds);
                    fillDataInTimeSeries ((TimeSeries) sLeft, (TimeSeries) sRight, second, valueLeft, valueRight);
                }
                else {
                    // get current distance of this sample
                    double fDistance = lap.getSpeed ().getDistance () / 1000f;
                    if (getContext ().getFormatUtils ().getUnitSystem () != FormatUtils.UnitSystem.Metric) {
                        fDistance = ConvertUtils.convertKilometer2Miles (fDistance, false);
                    }
                    fillDataInXYSeries ((XYSeries) sLeft, (XYSeries) sRight, fDistance, valueLeft, valueRight);
                }
            }
        }
        
        XYDataset dataset = createDataSet (fDomainAxisTime, sLeft);
        
        // create chart depending on domain axis type
        JFreeChart chart = null;
        if (fDomainAxisTime) {
            chart = ChartFactory.createTimeSeriesChart (
                null,                       // Title
                axisTypeBottom.toString (), // Y-axis label
                axisTypeLeft.toString (),   // X-axis label
                dataset,                    // primary dataset
                false,                      // display legend
                true,                       // display tooltips
                false);                     // URLs
        }
        else {
            chart = ChartFactory.createXYLineChart (
                null,                       // Title
                axisTypeBottom.toString (), // Y-axis label
                axisTypeLeft.toString (),   // X-axis label
                dataset,                    // primary dataset
                PlotOrientation.VERTICAL,   // plot orientation
                false,                      // display legend
                true,                       // display tooltips
                false);                     // URLs
        }
        
        ChartUtils.customizeChart (chart, chartPanel);

        // set format of time domain axis (if active)
        XYPlot plot = (XYPlot) chart.getPlot ();
        
        // setup left axis
        ValueAxis axisLeft = plot.getRangeAxis (0);
        axisLeft.setLabelPaint (COLOR_AXIS_LEFT);
        axisLeft.setTickLabelPaint (COLOR_AXIS_LEFT);
        XYItemRenderer rendererLeft = plot.getRenderer (0);
        rendererLeft.setSeriesPaint (0, COLOR_AXIS_LEFT);
        setTooltipGenerator (rendererLeft, axisTypeBottom, axisTypeLeft);
        
        // setup right axis (when selected)
        if (sRight != null) {
            
            NumberAxis axisRight = new NumberAxis (axisTypeRight.toString ());
            axisRight.setAutoRangeIncludesZero (false);
            plot.setRangeAxis (1, axisRight);
            plot.setRangeAxisLocation (1, AxisLocation.BOTTOM_OR_RIGHT);
            axisRight.setLabelPaint (COLOR_AXIS_RIGHT);
            axisRight.setTickLabelPaint (COLOR_AXIS_RIGHT);
            
            // create dataset for right axis
            XYDataset datasetRight = createDataSet (fDomainAxisTime, sRight);
            plot.setDataset (1, datasetRight);
            plot.mapDatasetToRangeAxis (1, 1);
            
            // set custom renderer
            StandardXYItemRenderer rendererRight = new StandardXYItemRenderer ();
            rendererRight.setSeriesPaint (0, COLOR_AXIS_RIGHT);
            plot.setRenderer (1, rendererRight);
            setTooltipGenerator (rendererRight, axisTypeBottom, axisTypeRight);
        }
        
        // highlight current selected (if presdent) heartrate range when displayed on left axis
        if ((highlightHeartrateRange >= 0) && (axisTypeLeft == AxisType.Heartrate)) {            
            HeartRateLimit currentLimit = exercise.getHeartRateLimits ()[highlightHeartrateRange];
            
            // don't highlight percentual ranges (is not possible, the values 
            // are absolute and the maximum heartrate is unknown)
            if (currentLimit.isAbsoluteRange ()) {
                Marker hrRangeMarker = new IntervalMarker (currentLimit.getLowerHeartRate (), currentLimit.getUpperHeartRate ());
                hrRangeMarker.setPaint (COLOR_MARKER_HEARTRATE);
                hrRangeMarker.setAlpha (0.3f);
                plot.addRangeMarker (hrRangeMarker);
            }
        }
        
        // draw a vertical marker line for each lap (not for the last)
        if (exercise.getLapList ().length > 0) {
            
            for (int i = 0; i < exercise.getLapList ().length - 1; i++) {
                Lap lap = exercise.getLapList ()[i];
                double lapSplitValue;
                
                // compute lap split value (different for time or distance mode)
                // (the value must be milliseconds for time domain axis)
                if (fDomainAxisTime) {
                    int lapSplitSeconds = lap.getTimeSplit () / 10;
                    lapSplitValue = createJFreeChartSecond (lapSplitSeconds).getFirstMillisecond ();
                } 
                else {
                    lapSplitValue = lap.getSpeed ().getDistance () / 1000D;
                    if (getContext ().getFormatUtils ().getUnitSystem () == FormatUtils.UnitSystem.English) {
                        lapSplitValue = ConvertUtils.convertKilometer2Miles (lapSplitValue, false);
                    }
                }
                
                // create domain marker 
                Marker lapMarker = new ValueMarker (lapSplitValue);
                lapMarker.setPaint (COLOR_MARKER_LAP);
                lapMarker.setStroke (new BasicStroke (1.5f));
                lapMarker.setLabel (getContext ().getResReader ().getString ("pv.diagram.lap", i+1));
                lapMarker.setLabelAnchor (RectangleAnchor.TOP_LEFT);
                lapMarker.setLabelTextAnchor (TextAnchor.TOP_RIGHT);
                plot.addDomainMarker (lapMarker);
            }
        }

        // add chart to panel
        chartPanel.setChart (chart);
    }
    
    /**
     * Creates the JFreeChart Second instance for the specified number of seconds.
     * One hour must be substracted, otherwise the range starts with 01:00.
     * @param seconds the number of seconds
     * @return the created Second instance
     */
    private Second createJFreeChartSecond (int seconds) {
        return new Second (new Date ((seconds * 1000) - (60*60*1000)));
    }
    
    /**
     * Creates a data series object for the specified domain axis type.
     * @param fDomainAxisTime true when domain axis is time of false when distance
     * @param name name of the data series
     * @return the created data series
     */
    private Series createSeries (boolean fDomainAxisTime, String name) {
        if (fDomainAxisTime) {
            return new TimeSeries (name);
        }
        else {
            return new XYSeries (name);
        }
    }
    
    /**
     * Creates a dataset for the specified series and the domain axis type.
     * @param fDomainAxisTime true when domain axis is time of false when distance
     * @param series the series to be addet to the dataset
     * @return the created dataset
     */
    private XYDataset createDataSet (boolean fDomainAxisTime, Series series) {
        if (fDomainAxisTime) {
            return new TimeSeriesCollection ((TimeSeries) series);
        }
        else {
            return new XYSeriesCollection ((XYSeries) series);
        }
    }
    
    /**
     * Fills the specified data to the left and right time series.
     * @param sLeft the left TimeSeries
     * @param sRight the right TimeSeries (optional, can be null)
     * @param second the second for the current values
     * @param valueLeft the value of the left time series
     * @param valueRight the value of the right time series
     */
    private void fillDataInTimeSeries (TimeSeries sLeft, TimeSeries sRight, Second second, Number valueLeft, Number valueRight) {
        // don't add the data when the specified second was allready added
        if (sLeft.getValue (second) == null) {
            sLeft.add (second, valueLeft);                
            if (sRight != null) {
                sRight.add (second, valueRight);
            }
        }
    } 
        
    /**
     * Fills the specified data to the left and right XY series.
     * @param sLeft the left XYSeries
     * @param sRight the right XYSeries (optional, can be null)
     * @param valueBottom the value of the bottom domain axis
     * @param valueLeft the value of the left time series
     * @param valueRight the value of the right time series
     */
    private void fillDataInXYSeries (XYSeries sLeft, XYSeries sRight, double valueBottom, Number valueLeft, Number valueRight) {
        sLeft.add (valueBottom, valueLeft);                
        if (sRight != null) {
            sRight.add (valueBottom, valueRight);
        }
    } 
        
    /**
     * Sets the tooltip generator for the specified renderer.
     * @param renderer the renderer for the tooltip
     * @param domainAxis type of the domain axis
     * @param valueAxis type of the value axis
     */
    private void setTooltipGenerator (XYItemRenderer renderer, AxisType domainAxis, AxisType valueAxis) {        
        String format = "" + domainAxis + ": {1}, " + valueAxis + ": {2}";
        
        if (domainAxis == AxisType.Time) {
            renderer.setBaseToolTipGenerator (new StandardXYToolTipGenerator (format,
                new SimpleDateFormat ("HH:mm"), new DecimalFormat ()));
        }
        else {
            renderer.setBaseToolTipGenerator (new StandardXYToolTipGenerator (format,
                new DecimalFormat (), new DecimalFormat ()));
        }
    }
    
    /**
     * Returns the value specified by the axis type of the exercise sample. It
     * also converts the value to the current unit system and speed view.
     * @param axisType the axis type to be displayed
     * @param sample the exercise sample to display
     * @return the requested value
     */
    private Number getSampleValue (AxisType axisType, ExerciseSample sample) {
        
        FormatUtils formatUtils = getContext ().getFormatUtils ();

        switch (axisType) {
            case Heartrate:
                return sample.getHeartRate ();
            case Altitude:
                if (formatUtils.getUnitSystem () == FormatUtils.UnitSystem.Metric) {
                    return sample.getAltitude ();
                }
                else {
                    return ConvertUtils.convertMeter2Feet (sample.getAltitude ());
                }
            case Speed:
                float speed = sample.getSpeed ();
                if (formatUtils.getUnitSystem () != FormatUtils.UnitSystem.Metric) {
                    speed = (float) ConvertUtils.convertKilometer2Miles (speed, false);
                }
                if (formatUtils.getSpeedView () == FormatUtils.SpeedView.MinutesPerDistance) {
                    // convert speed to minutes per distance
                    if (speed != 0f) {
                        speed = 60 / speed;
                    }
                }
                return speed;
            case Cadence:
                return sample.getCadence ();
            case Temperature:
                if (formatUtils.getUnitSystem () == FormatUtils.UnitSystem.Metric) {
                    return sample.getTemperature ();
                }
                else {
                    return ConvertUtils.convertCelsius2Fahrenheit (sample.getTemperature ());
                }
            default:
                return 0;
        }
    }
    
    /**
     * Returns the value specified by the axis type of the exercise lap. It
     * also converts the value to the current unit system and speed view.
     * @param axisType the axis type to be displayed
     * @param lap the exercise lap to display
     * @return the requested value
     */
    private Number getLapValue (AxisType axisType, Lap lap) {
        
        FormatUtils formatUtils = getContext ().getFormatUtils ();

        switch (axisType) {
            case Heartrate:
                return lap.getHeartRateAVG ();
            case Speed:
                float speed = lap.getSpeed ().getSpeedAVG ();
                if (formatUtils.getUnitSystem () != FormatUtils.UnitSystem.Metric) {
                    speed = (float) ConvertUtils.convertKilometer2Miles (speed, false);
                }
                if (formatUtils.getSpeedView () == FormatUtils.SpeedView.MinutesPerDistance) {
                    // convert speed to minutes per distance
                    if (speed != 0f) {
                        speed = 60 / speed;
                    }
                }
                return speed;
            default:
                return 0;
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pDiagram = new javax.swing.JPanel();
        pButtons = new javax.swing.JPanel();
        laAxis = new javax.swing.JLabel();
        laLeft = new javax.swing.JLabel();
        cbLeft = new javax.swing.JComboBox();
        laRight = new javax.swing.JLabel();
        cbRight = new javax.swing.JComboBox();
        laBottom = new javax.swing.JLabel();
        cbBottom = new javax.swing.JComboBox();

        pDiagram.setLayout(new java.awt.BorderLayout());

        laAxis.setFont(laAxis.getFont().deriveFont(laAxis.getFont().getStyle() | java.awt.Font.BOLD));
        laAxis.setText("_Axis Usage");
        laAxis.setName("pv.diagram.axis_usage");

        laLeft.setText("_Left:");
        laLeft.setName("pv.diagram.left");

        laRight.setText("_Right:");
        laRight.setName("pv.diagram.right");

        laBottom.setText("_Bottom:");
        laBottom.setName("pv.diagram.bottom");

        javax.swing.GroupLayout pButtonsLayout = new javax.swing.GroupLayout(pButtons);
        pButtons.setLayout(pButtonsLayout);
        pButtonsLayout.setHorizontalGroup(
            pButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pButtonsLayout.createSequentialGroup()
                .addGroup(pButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laAxis)
                    .addGroup(pButtonsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(laLeft)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(laRight)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(laBottom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(204, Short.MAX_VALUE))
        );
        pButtonsLayout.setVerticalGroup(
            pButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(laAxis)
                .addGap(15, 15, 15)
                .addGroup(pButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laLeft)
                    .addComponent(cbLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(laRight)
                    .addComponent(cbRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(laBottom)
                    .addComponent(cbBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pDiagram, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                    .addComponent(pButtons, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pDiagram, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbBottom;
    private javax.swing.JComboBox cbLeft;
    private javax.swing.JComboBox cbRight;
    private javax.swing.JLabel laAxis;
    private javax.swing.JLabel laBottom;
    private javax.swing.JLabel laLeft;
    private javax.swing.JLabel laRight;
    private javax.swing.JPanel pButtons;
    private javax.swing.JPanel pDiagram;
    // End of variables declaration//GEN-END:variables
    
    
    /** This is the list of possible exercise value types to draw in diagram. */
    private enum AxisType {
        Nothing, Heartrate, Altitude, Speed, Cadence, Temperature, Time, Distance;
        
        /** Static resource reader is needed for string creation. */
        private static ResourceReader resReader;
        /** Static format utils is needed for string creation. */
        private static FormatUtils formatUtils;

        public static void setResReader (ResourceReader resReader) {
            AxisType.resReader = resReader;
        }

        public static void setFormatUtils (FormatUtils formatUtils) {
            AxisType.formatUtils = formatUtils;
        }
        
        /** 
         * Returns the translated name (to be displayed) of this axis type. 
         * @return name of this axis type
         */
        @Override
        public String toString () {
            
            switch (this) {
                case Nothing: 
                    return AxisType.resReader.getString ("pv.diagram.axis.nothing");
                case Heartrate: 
                    return AxisType.resReader.getString ("pv.diagram.axis.heartrate");
                case Altitude: 
                    return AxisType.resReader.getString ("pv.diagram.axis.altitude", formatUtils.getAltitudeUnitName ());
                case Speed: 
                    return AxisType.resReader.getString ("pv.diagram.axis.speed", formatUtils.getSpeedUnitName ());
                case Cadence: 
                    return AxisType.resReader.getString ("pv.diagram.axis.cadence");
                case Temperature: 
                    return AxisType.resReader.getString ("pv.diagram.axis.temperature", formatUtils.getTemperatureUnitName ());
                case Time: 
                    return AxisType.resReader.getString ("pv.diagram.axis.time");
                case Distance: 
                    return AxisType.resReader.getString ("pv.diagram.axis.distance", formatUtils.getDistanceUnitName ());
                default: 
                    return "???";
            }
        }
    }
}
