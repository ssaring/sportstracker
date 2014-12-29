package de.saring.exerciseviewer.gui.panelsfx;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.saring.util.gui.jfreechart.ChartUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
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

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.HeartRateLimit;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.exerciseviewer.gui.EVDocument;
import de.saring.util.AppResources;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Controller (MVC) class of the "Samples" panel, which displays the exercise graphically
 * (heartrate, altitude, speed and cadence).
 *
 * @author Stefan Saring
 */
public class DiagramPanelController extends AbstractPanelController {

    // The colors of the chart.
    private static final java.awt.Color COLOR_AXIS_LEFT = java.awt.Color.RED;
    private static final java.awt.Color COLOR_AXIS_RIGHT = java.awt.Color.BLUE;
    private static final java.awt.Color COLOR_MARKER_LAP = new java.awt.Color(0f, 0.73f, 0f);
    private static final java.awt.Color COLOR_MARKER_HEARTRATE = new java.awt.Color(0.8f, 0.8f, 0.8f, 0.3f);

    private final AxisTypeStringConverter axisTypeStringConverter;

    /** The viewer for the chart. */
    private ChartViewer chartViewer;

    /** The exercise heartrate range to be highlighted (null for no highlighting). */
    private HeartRateLimit highlightHeartrateRange = null;

    @FXML
    private StackPane spDiagram;

    @FXML
    private ChoiceBox<AxisType> cbLeftAxis;
    @FXML
    private ChoiceBox<AxisType> cbRightAxis;
    @FXML
    private ChoiceBox<AxisType> cbBottomAxis;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer model/document
     */
    public DiagramPanelController(final EVContext context, final EVDocument document) {
        super(context, document);

        axisTypeStringConverter = new AxisTypeStringConverter(getContext().getFxResources(),
                getContext().getFormatUtils());
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/DiagramPanel.fxml";
    }

    /**
     * Updates the diagram and highlights the specified heartrate range.
     *
     * @param heartrateRange heartrate range to highlight
     */
    public void displayDiagramForHeartrateRange(final HeartRateLimit heartrateRange) {
        highlightHeartrateRange = heartrateRange;

        // don't update the diagram when this panel was not initialized yet
        if (chartViewer != null) {
            updateDiagram();
        }
    }

    @Override
    protected void setupPanel() {
        setupAxisChoiceBoxes();
        updateDiagram();
    }

    private void setupAxisChoiceBoxes() {
        EVExercise exercise = getDocument().getExercise();

        // setup axis type name converter
        cbLeftAxis.setConverter(axisTypeStringConverter);
        cbRightAxis.setConverter(axisTypeStringConverter);
        cbBottomAxis.setConverter(axisTypeStringConverter);

        // fill axes with all possible types depending on the exercise recording mode
        cbLeftAxis.getItems().add(AxisType.HEARTRATE);
        cbRightAxis.getItems().addAll(AxisType.NOTHING, AxisType.HEARTRATE);
        cbBottomAxis.getItems().add(AxisType.TIME);

        cbLeftAxis.getSelectionModel().select(0);
        cbRightAxis.getSelectionModel().select(0);
        cbBottomAxis.getSelectionModel().select(0);

        // add altitude items if recorded
        if (exercise.getRecordingMode().isAltitude()) {
            cbLeftAxis.getItems().addAll(AxisType.ALTITUDE);
            cbRightAxis.getItems().add(AxisType.ALTITUDE);
        }

        // add speed and distance items if recorded
        if (exercise.getRecordingMode().isSpeed()) {
            cbLeftAxis.getItems().add(AxisType.SPEED);
            cbRightAxis.getItems().add(AxisType.SPEED);
            cbBottomAxis.getItems().add(AxisType.DISTANCE);
        }

        // add cadence items if recorded
        if (exercise.getRecordingMode().isCadence()) {
            cbLeftAxis.getItems().add(AxisType.CADENCE);
            cbRightAxis.getItems().add(AxisType.CADENCE);
        }

        // add temperature items if recorded
        if (exercise.getRecordingMode().isTemperature()) {
            cbLeftAxis.getItems().add(AxisType.TEMPERATURE);
            cbRightAxis.getItems().add(AxisType.TEMPERATURE);
        }

        // do we need to display the second diagram too?
        if (getDocument().getOptions().isDisplaySecondDiagram()) {
            // it's only possible when additional data is available (first 2 entries
            // are nothing and heartrate, which is already displayed)
            if (cbRightAxis.getItems().size() > 2) {
                cbRightAxis.getSelectionModel().select(2);
            }
        }

        // set listeners for updating the diagram on selection changes
        cbLeftAxis.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbRightAxis.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbBottomAxis.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
    }

    /**
     * Draws the diagram according to the current axis type selection and configuration settings.
     */
    private void updateDiagram() {
        final EVExercise exercise = getDocument().getExercise();

        final AxisType axisTypeLeft = cbLeftAxis.getValue();
        final AxisType axisTypeRight = cbRightAxis.getValue();
        final AxisType axisTypeBottom = cbBottomAxis.getValue();
        final boolean fDomainAxisTime = axisTypeBottom == AxisType.TIME;

        // create and fill data series according to axis type
        // (right axis only when user selected a different axis type)
        final Series sLeft = createSeries(fDomainAxisTime, "left");
        Series sRight = null;
        if ((axisTypeRight != AxisType.NOTHING) && (axisTypeRight != axisTypeLeft)) {
            sRight = createSeries(fDomainAxisTime, "right");
        }

        // fill data series with all recorded exercise samples
        if (exercise.getSampleList() != null) {
            for (int i = 0; i < exercise.getSampleList().length; i++) {

                final ExerciseSample sample = exercise.getSampleList()[i];
                final Number valueLeft = getSampleValue(axisTypeLeft, sample);
                final Number valueRight = getSampleValue(axisTypeRight, sample);

                if (fDomainAxisTime) {
                    // calculate current second
                    final int timeSeconds = (int) (sample.getTimestamp() / 1000);
                    final Second second = createJFreeChartSecond(timeSeconds);
                    fillDataInTimeSeries((TimeSeries) sLeft, (TimeSeries) sRight, second, valueLeft, valueRight);
                } else {
                    // get current distance of this sample
                    double fDistance = sample.getDistance() / 1000f;
                    if (getContext().getFormatUtils().getUnitSystem() != FormatUtils.UnitSystem.Metric) {
                        fDistance = ConvertUtils.convertKilometer2Miles(fDistance, false);
                    }
                    fillDataInXYSeries((XYSeries) sLeft, (XYSeries) sRight, fDistance, valueLeft, valueRight);
                }
            }
        }
        // some Polar models only record lap data. no samples (e.g. RS200SD)
        else if (exercise.getLapList() != null) {
            // data starts with first lap => add 0 values (otherwise not displayed)
            if (fDomainAxisTime) {
                fillDataInTimeSeries((TimeSeries) sLeft, (TimeSeries) sRight, createJFreeChartSecond(0), 0, 0);
            } else {
                fillDataInXYSeries((XYSeries) sLeft, (XYSeries) sRight, 0, 0, 0);
            }

            // fill data series with all recorded exercise laps
            for (int i = 0; i < exercise.getLapList().length; i++) {

                final Lap lap = exercise.getLapList()[i];
                final Number valueLeft = getLapValue(axisTypeLeft, lap);
                final Number valueRight = getLapValue(axisTypeRight, lap);

                if (fDomainAxisTime) {
                    // calculate current second
                    final int timeSeconds = Math.round(lap.getTimeSplit() / 10f);
                    final Second second = createJFreeChartSecond(timeSeconds);
                    fillDataInTimeSeries((TimeSeries) sLeft, (TimeSeries) sRight, second, valueLeft, valueRight);
                } else {
                    // get current distance of this sample
                    double fDistance = lap.getSpeed().getDistance() / 1000f;
                    if (getContext().getFormatUtils().getUnitSystem() != FormatUtils.UnitSystem.Metric) {
                        fDistance = ConvertUtils.convertKilometer2Miles(fDistance, false);
                    }
                    fillDataInXYSeries((XYSeries) sLeft, (XYSeries) sRight, fDistance, valueLeft, valueRight);
                }
            }
        }

        final XYDataset dataset = createDataSet(fDomainAxisTime, sLeft);

        // create chart depending on domain axis type
        JFreeChart chart = null;
        if (fDomainAxisTime) {
            chart = ChartFactory.createTimeSeriesChart(null, // Title
                    axisTypeStringConverter.toString(axisTypeBottom), // Y-axis label
                    axisTypeStringConverter.toString(axisTypeLeft), // X-axis label
                    dataset, // primary dataset
                    false, // display legend
                    true, // display tooltips
                    false); // URLs
        } else {
            chart = ChartFactory.createXYLineChart(null, // Title
                    axisTypeStringConverter.toString(axisTypeBottom), // Y-axis label
                    axisTypeStringConverter.toString(axisTypeLeft), // X-axis label
                    dataset, // primary dataset
                    PlotOrientation.VERTICAL, // plot orientation
                    false, // display legend
                    true, // display tooltips
                    false); // URLs
        }

        // set format of time domain axis (if active)
        final XYPlot plot = (XYPlot) chart.getPlot();

        // setup left axis
        final ValueAxis axisLeft = plot.getRangeAxis(0);
        axisLeft.setLabelPaint(COLOR_AXIS_LEFT);
        axisLeft.setTickLabelPaint(COLOR_AXIS_LEFT);
        final XYItemRenderer rendererLeft = plot.getRenderer(0);
        rendererLeft.setSeriesPaint(0, COLOR_AXIS_LEFT);
        setTooltipGenerator(rendererLeft, axisTypeBottom, axisTypeLeft);

        // setup right axis (when selected)
        if (sRight != null) {

            final NumberAxis axisRight = new NumberAxis(axisTypeStringConverter.toString(axisTypeRight));
            axisRight.setAutoRangeIncludesZero(false);
            plot.setRangeAxis(1, axisRight);
            plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
            axisRight.setLabelPaint(COLOR_AXIS_RIGHT);
            axisRight.setTickLabelPaint(COLOR_AXIS_RIGHT);

            // create dataset for right axis
            final XYDataset datasetRight = createDataSet(fDomainAxisTime, sRight);
            plot.setDataset(1, datasetRight);
            plot.mapDatasetToRangeAxis(1, 1);

            // set custom renderer
            final StandardXYItemRenderer rendererRight = new StandardXYItemRenderer();
            rendererRight.setSeriesPaint(0, COLOR_AXIS_RIGHT);
            plot.setRenderer(1, rendererRight);
            setTooltipGenerator(rendererRight, axisTypeBottom, axisTypeRight);
        }

        // highlight current selected (if set) heartrate range when displayed on left axis
        if (highlightHeartrateRange != null && axisTypeLeft == AxisType.HEARTRATE) {

            // don't highlight percentual ranges (is not possible, the values
            // are absolute and the maximum heartrate is unknown)
            if (highlightHeartrateRange.isAbsoluteRange()) {
                final Marker hrRangeMarker = new IntervalMarker(highlightHeartrateRange.getLowerHeartRate(),
                        highlightHeartrateRange.getUpperHeartRate());
                hrRangeMarker.setPaint(COLOR_MARKER_HEARTRATE);
                plot.addRangeMarker(hrRangeMarker);
            }
        }

        // draw a vertical marker line for each lap (not for the last)
        if (exercise.getLapList().length > 0) {

            for (int i = 0; i < exercise.getLapList().length - 1; i++) {
                final Lap lap = exercise.getLapList()[i];
                double lapSplitValue;

                // compute lap split value (different for time or distance mode)
                // (the value must be milliseconds for time domain axis)
                if (fDomainAxisTime) {
                    final int lapSplitSeconds = lap.getTimeSplit() / 10;
                    lapSplitValue = createJFreeChartSecond(lapSplitSeconds).getFirstMillisecond();
                } else {
                    lapSplitValue = lap.getSpeed().getDistance() / 1000D;
                    if (getContext().getFormatUtils().getUnitSystem() == FormatUtils.UnitSystem.English) {
                        lapSplitValue = ConvertUtils.convertKilometer2Miles(lapSplitValue, false);
                    }
                }

                // create domain marker
                Marker lapMarker = new ValueMarker(lapSplitValue);
                lapMarker.setPaint(COLOR_MARKER_LAP);
                lapMarker.setStroke(new java.awt.BasicStroke(1.5f));
                lapMarker.setLabel(getContext().getResReader().getString("pv.diagram.lap", i + 1));
                lapMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                lapMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                plot.addDomainMarker(lapMarker);
            }
        }

        ChartUtils.customizeChart(chart, spDiagram);

        // display chart in viewer (chart viewer will be initialized lazily)
        if (chartViewer == null) {
            chartViewer = new ChartViewer(chart);
            spDiagram.getChildren().addAll(chartViewer);
        } else {
            chartViewer.setChart(chart);
        }
    }

    /**
     * Creates the JFreeChart Second instance for the specified number of seconds.
     * One hour must be substracted, otherwise the range starts with 01:00.
     *
     * @param seconds the number of seconds
     * @return the created Second instance
     */
    private Second createJFreeChartSecond(final int seconds) {
        return new Second(new Date((seconds * 1000) - (60 * 60 * 1000)));
    }

    /**
     * Creates a data series object for the specified domain axis type.
     *
     * @param fDomainAxisTime true when domain axis is time of false when distance
     * @param name name of the data series
     * @return the created data series
     */
    private Series createSeries(final boolean fDomainAxisTime, final String name) {
        if (fDomainAxisTime) {
            return new TimeSeries(name);
        } else {
            return new XYSeries(name);
        }
    }

    /**
     * Creates a dataset for the specified series and the domain axis type.
     *
     * @param fDomainAxisTime true when domain axis is time of false when distance
     * @param series the series to be addet to the dataset
     * @return the created dataset
     */
    private XYDataset createDataSet(final boolean fDomainAxisTime, final Series series) {
        if (fDomainAxisTime) {
            return new TimeSeriesCollection((TimeSeries) series);
        } else {
            return new XYSeriesCollection((XYSeries) series);
        }
    }

    /**
     * Fills the specified data to the left and right time series.
     *
     * @param sLeft the left TimeSeries
     * @param sRight the right TimeSeries (optional, can be null)
     * @param second the second for the current values
     * @param valueLeft the value of the left time series
     * @param valueRight the value of the right time series
     */
    private void fillDataInTimeSeries(final TimeSeries sLeft, final TimeSeries sRight, final Second second,
            final Number valueLeft, final Number valueRight) {

        // don't add the data when the specified second was allready added
        if (sLeft.getValue(second) == null) {
            sLeft.add(second, valueLeft);
            if (sRight != null) {
                sRight.add(second, valueRight);
            }
        }
    }

    /**
     * Fills the specified data to the left and right XY series.
     *
     * @param sLeft the left XYSeries
     * @param sRight the right XYSeries (optional, can be null)
     * @param valueBottom the value of the bottom domain axis
     * @param valueLeft the value of the left time series
     * @param valueRight the value of the right time series
     */
    private void fillDataInXYSeries(final XYSeries sLeft, final XYSeries sRight, final double valueBottom,
            final Number valueLeft, final Number valueRight) {

        sLeft.add(valueBottom, valueLeft);
        if (sRight != null) {
            sRight.add(valueBottom, valueRight);
        }
    }

    /**
     * Sets the tooltip generator for the specified renderer.
     *
     * @param renderer the renderer for the tooltip
     * @param domainAxis type of the domain axis
     * @param valueAxis type of the value axis
     */
    private void setTooltipGenerator(final XYItemRenderer renderer, final AxisType domainAxis, final AxisType valueAxis) {

        final String format = "" + axisTypeStringConverter.toString(domainAxis) + ": {1}, " +
                axisTypeStringConverter.toString(valueAxis) + ": {2}";

        if (domainAxis == AxisType.TIME) {
            renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(format, new SimpleDateFormat("HH:mm"),
                    new DecimalFormat()));
        } else {
            renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(format, new DecimalFormat(),
                    new DecimalFormat()));
        }
    }

    /**
     * Returns the value specified by the axis type of the exercise sample. It
     * also converts the value to the current unit system and speed view.
     *
     * @param axisType the axis type to be displayed
     * @param sample the exercise sample to display
     * @return the requested value
     */
    private Number getSampleValue(AxisType axisType, ExerciseSample sample) {

        final FormatUtils formatUtils = getContext().getFormatUtils();

        switch (axisType) {
            case HEARTRATE:
                return sample.getHeartRate();
            case ALTITUDE:
                if (formatUtils.getUnitSystem() == FormatUtils.UnitSystem.Metric) {
                    return sample.getAltitude();
                } else {
                    return ConvertUtils.convertMeter2Feet(sample.getAltitude());
                }
            case SPEED:
                float speed = sample.getSpeed();
                if (formatUtils.getUnitSystem() != FormatUtils.UnitSystem.Metric) {
                    speed = (float) ConvertUtils.convertKilometer2Miles(speed, false);
                }
                if (formatUtils.getSpeedView() == FormatUtils.SpeedView.MinutesPerDistance) {
                    // convert speed to minutes per distance
                    if (speed != 0f) {
                        speed = 60 / speed;
                    }
                }
                return speed;
            case CADENCE:
                return sample.getCadence();
            case TEMPERATURE:
                if (formatUtils.getUnitSystem() == FormatUtils.UnitSystem.Metric) {
                    return sample.getTemperature();
                } else {
                    return ConvertUtils.convertCelsius2Fahrenheit(sample.getTemperature());
                }
            default:
                return 0;
        }
    }

    /**
     * Returns the value specified by the axis type of the exercise lap. It
     * also converts the value to the current unit system and speed view.
     *
     * @param axisType the axis type to be displayed
     * @param lap the exercise lap to display
     * @return the requested value
     */
    private Number getLapValue(AxisType axisType, Lap lap) {

        final FormatUtils formatUtils = getContext().getFormatUtils();

        switch (axisType) {
            case HEARTRATE:
                return lap.getHeartRateAVG();
            case SPEED:
                float speed = lap.getSpeed().getSpeedAVG();
                if (formatUtils.getUnitSystem() != FormatUtils.UnitSystem.Metric) {
                    speed = (float) ConvertUtils.convertKilometer2Miles(speed, false);
                }
                if (formatUtils.getSpeedView() == FormatUtils.SpeedView.MinutesPerDistance) {
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
     * The list of possible value types to be shown on the diagram axes. This enum also provides the
     * the localized displayed enum names.
     */
    private enum AxisType {
        NOTHING, HEARTRATE, ALTITUDE, SPEED, CADENCE, TEMPERATURE, TIME, DISTANCE
    }

    /**
     * StringConverter for the axis type choice boxes. It returns the name to be displayed for all
     * the available axis types.
     */
    private static class AxisTypeStringConverter extends StringConverter<AxisType> {

        private AppResources appResources;
        private FormatUtils formatUtils;

        /**
         * Default c'tor.
         *
         * @param appResources application resources for I18N
         * @param formatUtils current format utils instance
         */
        public AxisTypeStringConverter(final AppResources appResources, final FormatUtils formatUtils) {
            this.appResources = appResources;
            this.formatUtils = formatUtils;
        }

        @Override
        public String toString(final AxisType axisType) {
            switch (axisType) {
                case NOTHING:
                    return appResources.getString("pv.diagram.axis.nothing");
                case HEARTRATE:
                    return appResources.getString("pv.diagram.axis.heartrate");
                case ALTITUDE:
                    return appResources.getString("pv.diagram.axis.altitude", formatUtils.getAltitudeUnitName());
                case SPEED:
                    return appResources.getString("pv.diagram.axis.speed", formatUtils.getSpeedUnitName());
                case CADENCE:
                    return appResources.getString("pv.diagram.axis.cadence");
                case TEMPERATURE:
                    return appResources.getString("pv.diagram.axis.temperature", formatUtils.getTemperatureUnitName());
                case TIME:
                    return appResources.getString("pv.diagram.axis.time");
                case DISTANCE:
                    return appResources.getString("pv.diagram.axis.distance", formatUtils.getDistanceUnitName());
                default:
                    throw new IllegalArgumentException("Invalid AxisType: '" + axisType + "'!");
            }
        }

        @Override
        public AxisType fromString(final String string) {
            throw new UnsupportedOperationException();
        }
    }
}
