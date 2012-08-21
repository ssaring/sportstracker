package de.saring.sportstracker.gui.dialogs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.application.Action;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.ResourceReader;
import de.saring.util.data.IdObjectList;
import de.saring.util.gui.DialogUtils;
import de.saring.util.gui.jfreechart.ChartUtils;
import de.saring.util.gui.jfreechart.StackedRenderer;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils;
import de.saring.util.unitcalc.FormatUtils.SpeedView;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;

/**
 * This class is the "Overview diagram" dialog of the SportsTracker application,
 * which displays all the exercises or weight entries in various diagram graph types.
 * The user can select the displayed time range (e.g. all months of the selected year 
 * or the last 10 years until the selected year).
 *
 * @author  Stefan Saring, Kai Pastor
 * @version 3.2
 */
public class OverviewDialog extends JDialog {

    /** This is the list of possible time ranges displayed in diagram. */
    private enum TimeRangeType { 
    	MONTHS_OF_YEAR, 
    	WEEKS_OF_YEAR, 
    	LAST_10_YEARS, 
    	/**
    	 *  In total 13 months: current month and last 12 before (good 
    	 *  for compare current month and the one from year before).
    	 */
    	LAST_12_MONTHS 
	}

    /** This is the list of possible value types displayed in diagram. */
    private enum ValueType { DISTANCE, DURATION, ASCENT, CALORIES, EXERCISES, AVG_SPEED, SPORTSUBTYPE, EQUIPMENT, WEIGHT  }

    private STContext context;
    private STDocument document;
    
    /** The panel containing the current chart. */
    private ChartPanel chartPanel;
    
    /** Constants for action and property names. */
    private static final String ACTION_CLOSE = "st.dlg.overview.close";

    /** 
     * Creates new OverviewDialog instance.
     * @param context the SportsTracker context
     * @param document the applications document component
     */
    @Inject
    public OverviewDialog (STContext context, STDocument document) {
        super (context.getMainFrame (), true);
        this.context = context;
        this.document = document;
        OverviewType.setResReader (context.getResReader ());
        
        initComponents ();
        setLocationRelativeTo(getParent());
        this.getRootPane ().setDefaultButton (btClose);
        
        // add chart panel
        chartPanel = new ChartPanel (null);
        pDiagram.add (chartPanel, BorderLayout.CENTER);
        
        // setup actions
        ActionMap actionMap = context.getSAFContext ().getActionMap (getClass (), this);
        javax.swing.Action aClose = actionMap.get(ACTION_CLOSE);
        btClose.setAction(aClose);
        DialogUtils.setDialogEscapeKeyAction(this, aClose);
        
        setInitialValues ();
        updateDiagram ();
        
        // add listener for selection changes => diagram must be updated
        ActionListener alUpdate = new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                updateDiagram ();
            }
        };
        
        cbTimeRange.addActionListener (alUpdate);
        cbDisplay.addActionListener (alUpdate);
        cbSportTypeMode.addActionListener (alUpdate);
        cbSportTypeList.addActionListener (alUpdate);        

        spYear.addChangeListener (new ChangeListener () {
            public void stateChanged (ChangeEvent e) {
                updateDiagram ();
            }
        });
    }
    
    /**
     * Sets the dialog title (must be done here, otherwise the AppFramework overwrites it).
     * @param title the title
     */
    @Override
    public void setTitle (String title) {

        // display in title when exercise filter is beeing used
        if (document.isFilterEnabled ()) {
            super.setTitle (title + " " +
                context.getResReader ().getString ("st.dlg.overview.title.filter"));
        } 
        else {
            super.setTitle (title);
        }
    }

    /**
     * Sets the initial exercise values for all controls.
     */
    private void setInitialValues () {

        // fill selection comboboxes
        cbTimeRange.removeAllItems ();
        cbTimeRange.addItem (context.getResReader ().getString ("st.dlg.overview.time_range.last_12_months.text"));
        cbTimeRange.addItem (context.getResReader ().getString ("st.dlg.overview.time_range.months_of_year.text"));
        cbTimeRange.addItem (context.getResReader ().getString ("st.dlg.overview.time_range.weeks_of_year.text"));
        cbTimeRange.addItem (context.getResReader ().getString ("st.dlg.overview.time_range.ten_years.text"));

        cbDisplay.removeAllItems ();
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.distance_sum.text"));
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.duration_sum.text"));
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.ascent_sum.text"));
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.calorie_sum.text"));
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.exercise_count.text"));
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.avg_speed.text"));
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.sportsubtype_distance.text"));
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.equipment_distance.text"));
        cbDisplay.addItem (context.getResReader ().getString ("st.dlg.overview.display.weight.text"));

        cbSportTypeMode.removeAllItems ();
        cbSportTypeMode.addItem (OverviewType.EACH_SPLITTED);
        cbSportTypeMode.addItem (OverviewType.EACH_STACKED);
        cbSportTypeMode.addItem (OverviewType.ALL_SUMMARY);
        
        cbSportTypeList.removeAllItems();
        for (SportType sportType : document.getSportTypeList()) {
            cbSportTypeList.addItem(sportType.getName());
        }

        // the spinner must not use number grouping (otherwise e.g. year "2.007")
        ((JSpinner.NumberEditor) spYear.getEditor ()).getFormat ().setGroupingUsed (false);
        spYear.setValue (Calendar.getInstance ().get (Calendar.YEAR));    
    }
    
    /**
     * Draws the overview diagram according to the current selections.
     */
    private void updateDiagram () {
        updateSelectionControlsState ();
        
        // get selected time range and value type and its name to display
        TimeRangeType timeType = getCurrentTimeRangeType ();
        ValueType vType = getCurrentValueType ();
        String vTypeName = getCurrentValueTypeName ();
        int year = getSelectedYear();

        // create a table of all time series (graphs) and the appropriate colors
        TimeTableXYDataset dataset = new TimeTableXYDataset ();
        List<Color> lGraphColors = new ArrayList<> ();
        
        // setup TimeSeries in the diagram (done in different ways for all the value types)
        if (getCurrentValueType () == ValueType.SPORTSUBTYPE) {
            setupSportSubTypeDiagram(dataset, lGraphColors);
        }
        else if (getCurrentValueType () == ValueType.EQUIPMENT) {
            setupEquipmentDiagram(dataset, lGraphColors);
        }
        else if (getCurrentValueType () == ValueType.WEIGHT) {
            setupWeightDiagram(dataset, lGraphColors);
        }
        else {
            setupExerciseDiagram(dataset, lGraphColors);
        }
        
        // create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart (
            null,          // Title
            null,          // Y-axis label
            vTypeName,     // X-axis label
            dataset,       // primary dataset
            true,          // display legend
            true,          // display tooltips
            false);        // URLs
        
        ChartUtils.customizeChart (chart, chartPanel);
        
        // render unique filled shapes for each graph
        XYPlot plot = (XYPlot) chart.getPlot ();
        
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer ();
        renderer.setBaseShapesVisible (true);
        renderer.setBaseShapesFilled (true);
        // set color for sport type series
        for (int i = 0; i < lGraphColors.size (); i++) {
            Color tempColor = lGraphColors.get (i);
            renderer.setSeriesPaint (i, tempColor);
        }
        
        // setup date format for tooltips and time (bottom) axis
        String dateFormatTooltip = null;
        String dateFormatAxis = null;
        DateTickUnit dateTickUnit = null;
        
        switch (timeType) {
            case LAST_12_MONTHS:
                dateFormatTooltip = "MMMMM yyyy";
                dateFormatAxis = "MMM";
                dateTickUnit = new DateTickUnit (DateTickUnitType.MONTH, 1);
                break;
            case MONTHS_OF_YEAR:
                dateFormatTooltip = "MMMMM";
                dateFormatAxis = "MMM";
                dateTickUnit = new DateTickUnit (DateTickUnitType.MONTH, 1);
                break;
            case WEEKS_OF_YEAR:
                // Workaround for a JFreeChart formating problem: years are used
                // instead of weeks for the bottom axis, otherwise there will be
                // format problems on the axis (the first week is often "52")
                dateFormatTooltip = "yy";
                dateFormatAxis = "yy";
                dateTickUnit = new DateTickUnit (DateTickUnitType.YEAR, 2);
                break;
            default: // LAST_10_YEARS
                dateFormatTooltip = "yyyy";
                dateFormatAxis = "yyyy";
                dateTickUnit = new DateTickUnit (DateTickUnitType.YEAR, 1);
                break;
        }        
        
        // setup tooltips: must display month, week or year and the value only
        String toolTipFormat = "{1}: {2}";
        renderer.setBaseToolTipGenerator (new StandardXYToolTipGenerator (toolTipFormat,
            new SimpleDateFormat (dateFormatTooltip), new DecimalFormat ()) {
                @Override
                public String generateToolTip (XYDataset dataset, int series, int item) {
                    return dataset.getSeriesKey (series) + ", " + super.generateToolTip(dataset, series, item);
                }
        });
        
        // special handling for overview type EACH_STACKED, it uses a special renderer
        // (only for exercises based value types, stacked mode can be selected only there)
        if (cbSportTypeMode.isVisible() && cbSportTypeMode.getSelectedItem () == OverviewType.EACH_STACKED) {
            
            renderer.setSeriesLinesVisible (0, false);
            renderer.setSeriesShapesVisible (0, false);
            renderer.setSeriesVisibleInLegend (0, false);

            // actual dataset
            dataset = new TimeTableXYDataset ();
            lGraphColors = new ArrayList<> ();

            // create a separate graph for each sport type
            for (SportType sportType : document.getSportTypeList ()) {
                addExerciseTimeSeries (dataset, timeType, year, vType, sportType);
                lGraphColors.add (sportType.getColor ());
            }
            plot.setDataset (1, dataset);

            // actual stacked renderer
            StackedRenderer stackedRenderer = new StackedRenderer ();
            // set color for sport type series
            for (int i = 0; i < lGraphColors.size (); i++) {
                Color tempColor = lGraphColors.get (i);
                stackedRenderer.setSeriesPaint (i, tempColor);
            }
            stackedRenderer.setBaseToolTipGenerator (new StandardXYToolTipGenerator (toolTipFormat,
                new SimpleDateFormat (dateFormatTooltip), new DecimalFormat ()) {
                    @Override
                    public String generateToolTip (XYDataset dataset, int series, int item) {
                        return dataset.getSeriesKey (series) + ", " + super.generateToolTip (dataset, series, item);
                    }
            });
            plot.setRenderer (1, stackedRenderer);
        }

        // set date format of time (bottom) axis
        DateAxis axis = (DateAxis) plot.getDomainAxis ();
        axis.setDateFormatOverride (new SimpleDateFormat (dateFormatAxis));
        if (dateTickUnit != null) {
            axis.setTickUnit (dateTickUnit);
        }
        
        // set value range from 0 to 10 when there are no values
        // (otherwise the range is double minimum to double maximum)
        if (plot.getRangeAxis ().getRange ().getCentralValue () == 0d) {
            plot.getRangeAxis ().setRange (new Range (0, 10));
        }
        
        // add vertical year break marker when displaying the last 12 months
        if (timeType == TimeRangeType.LAST_12_MONTHS) {
            Calendar today = Calendar.getInstance ();
            Calendar newYear = createCalendarFor (today.get (Calendar.YEAR), 0, 1, true);
            newYear.add (Calendar.DAY_OF_MONTH, -15);
            ValueMarker newYearMarker = new ValueMarker (newYear.getTimeInMillis ());
            newYearMarker.setPaint (context.getResReader ().getColor ("st.dlg.overview.graph_color.year_break"));
            newYearMarker.setStroke (new BasicStroke (0.8f));
            newYearMarker.setLabel (" " + today.get (Calendar.YEAR));
            newYearMarker.setLabelAnchor (RectangleAnchor.TOP_RIGHT);
            newYearMarker.setLabelTextAnchor (TextAnchor.TOP_LEFT);
            plot.addDomainMarker (newYearMarker);
        }
            
        // display legend next to the diagram on the right side
        chart.getLegend ().setPosition (RectangleEdge.RIGHT);
        
        // add chart to panel
        chartPanel.setChart (chart);
    }

    /**
     * Updates the state of the selection widgets for the diagram content.
     */
    private void updateSelectionControlsState () {
        // hide year selection when displaying last 12 months
        spYear.setVisible (getCurrentTimeRangeType () != TimeRangeType.LAST_12_MONTHS);

        // the sport type selection is only visible for the ValueType WEIGHT
        boolean sssEnabled = getCurrentValueType () != ValueType.WEIGHT;
        laFor.setVisible(sssEnabled);
        cbSportTypeMode.setVisible(sssEnabled);
        cbSportTypeList.setVisible(sssEnabled);
        
        // display sport type mode (for exercise mode) or sport type list (for equipment mode) combobox only
        if (sssEnabled) {
            boolean equipmentSelected = getCurrentValueType() == ValueType.EQUIPMENT;
            laFor.setText(context.getResReader().getString(!equipmentSelected ? 
                    "st.dlg.overview.for.text" : "st.dlg.overview.for_sport_type.text"));
            cbSportTypeMode.setVisible(!equipmentSelected);
            cbSportTypeList.setVisible(equipmentSelected);
        }
    }

    /**
     * Returns the current selected time range type to display.
     * @return current TimeRangeType
     */
    private TimeRangeType getCurrentTimeRangeType () {
        switch (cbTimeRange.getSelectedIndex ()) {
            case 0: return TimeRangeType.LAST_12_MONTHS;
            case 1: return TimeRangeType.MONTHS_OF_YEAR;
            case 2: return TimeRangeType.WEEKS_OF_YEAR;
            case 3: return TimeRangeType.LAST_10_YEARS;
            default:
                throw new IllegalArgumentException ("Invalid time range type type selection!");
        }
    }

    /**
     * Returns the selected year for the time range to be shown.
     * @return year
     */
    private int getSelectedYear() {
        return ((Number) spYear.getValue ()).intValue ();
    }

    /**
     * Returns the current selected value type.
     * @return current ValueType
     */
    private ValueType getCurrentValueType () {
        switch (cbDisplay.getSelectedIndex ()) {
            case 0:
                return ValueType.DISTANCE;
            case 1:
                return ValueType.DURATION;
            case 2:
                return ValueType.ASCENT;
            case 3:
                return ValueType.CALORIES;
            case 4:
                return ValueType.EXERCISES;
            case 5:
                return ValueType.AVG_SPEED;
            case 6:
                return ValueType.SPORTSUBTYPE;
            case 7:
                return ValueType.EQUIPMENT;
            case 8:
                return ValueType.WEIGHT;
            default:
                throw new IllegalArgumentException ("Invalid display type selection!");
        }
    }

    /**
     * Returns the name to be displayed for the current selected value type.
     * @return name of the current ValueType
     */
    private String getCurrentValueTypeName () {
        FormatUtils formatUtils = context.getFormatUtils ();

        switch (getCurrentValueType ()) {
            case DISTANCE:
                return context.getResReader ().getString (
                    "st.dlg.overview.value_type.distance_sum", formatUtils.getDistanceUnitName ());
            case DURATION:
                return context.getResReader ().getString ("st.dlg.overview.value_type.duration_sum");
            case ASCENT:
                return context.getResReader ().getString ("st.dlg.overview.value_type.ascent_sum",
                    formatUtils.getAltitudeUnitName ());
            case CALORIES:
                return context.getResReader ().getString ("st.dlg.overview.value_type.calories_sum");
            case EXERCISES:
                return context.getResReader ().getString ("st.dlg.overview.value_type.exercise_count");
            case AVG_SPEED:
                return context.getResReader ().getString (
                    "st.dlg.overview.value_type.avg_speed", formatUtils.getSpeedUnitName ());
            case SPORTSUBTYPE:
                return context.getResReader ().getString (
                    "st.dlg.overview.value_type.sportsubtype_distance", formatUtils.getDistanceUnitName ());
            case EQUIPMENT:
                return context.getResReader ().getString (
                    "st.dlg.overview.value_type.equipment_distance", formatUtils.getDistanceUnitName ());
            case WEIGHT:
                return context.getResReader ().getString (
                    "st.dlg.overview.value_type.weight", formatUtils.getWeightUnitName ());
            default:
                throw new IllegalArgumentException ("Invalid value type!");
        }
    }
    
    /**
     * Sets up the diagram for exercise data.
     * 
     * @param dataset the XY dataset to be filled
     * @param graphColors list of graph colors, can be filled with preferred colors 
     */
    private void setupExerciseDiagram(TimeTableXYDataset dataset, List<Color> graphColors) {
        
        // get time range and value type to display
        TimeRangeType timeType = getCurrentTimeRangeType ();
        int year = getSelectedYear();

        ValueType vType = getCurrentValueType ();
        OverviewType overviewType = (OverviewType) cbSportTypeMode.getSelectedItem ();
        
        // which sport type mode is selected by user ?
        if (overviewType != OverviewType.EACH_SPLITTED) {
            // create one graph for sum of all sport types
            addExerciseTimeSeries (dataset, timeType, year, vType, null);
            graphColors.add (context.getResReader ().getColor ("st.dlg.overview.graph_color.all_types"));
        }
        else {
            // create a separate graph for each sport type
            for (SportType sportType : document.getSportTypeList ()) {
                addExerciseTimeSeries (dataset, timeType, year, vType, sportType);
                graphColors.add (sportType.getColor ());
            }
        }
    }    

    /**
     * This method calculates the specified exercise values (distance, duration, ascent,
     * avarage speed or calories concumption) and adds them to a TimeTableXYDataset. 
     * The calculation can be done for the exercises of all sport types (sum) or for a 
     * single sport type.
     * 
     * @param dataset the timetable dataset
     * @param timeType time range for calculated values
     * @param year the year for calculation
     * @param valueType the type of values needs to be calculated
     * @param sportType the specific sport type to be calculated or null for the sum of all sport types
     */
    private void addExerciseTimeSeries (TimeTableXYDataset dataset, TimeRangeType timeType, int year, ValueType valueType, SportType sportType) {
        
        // create the time series for specified time range and sport type
        String seriesName = sportType != null ?
            sportType.getName () :
            context.getResReader ().getString ("st.dlg.overview.graph.all_types");

        // process value calculation for each step of time range
        int timeStepCount = getTimeStepCount (timeType, year);
        for (int timeStep = 0; timeStep < timeStepCount; timeStep++) {
            
            // create time period for current time step
            RegularTimePeriod timePeriod = createTimePeriodForTimeStep (timeType, year, timeStep);

            // create the ExerciseFilter for the time range of the current time step
            ExerciseFilter filter = createExerciseFilterForTimeStep (timeType, year, timeStep);
            filter.setSportType (sportType);
            mergeExerciseFilterIfEnabled(filter);
            
            // get exercises for defined filter
            // (add value 0 and skip to next time step when no exercises found)
            IdObjectList<Exercise> lExercises = document.getExerciseList ().getExercisesForFilter (filter);
            if (lExercises.size () == 0) {
                dataset.add (timePeriod, 0, seriesName);
                continue;
            }

            // create sums of all exercises
            double sumDistance = 0d;
            double sumDuration = 0d;
            double sumAscent = 0d;
            double sumCalories = 0d;
				
            for (Exercise tempExercise : lExercises) {
                sumDistance += tempExercise.getDistance ();
                sumDuration += tempExercise.getDuration ();
                sumAscent += tempExercise.getAscent ();
                sumCalories += tempExercise.getCalories ();
            }
				
            // set value of time step depending on value type
            // (convert to english unit mode when enabled)
            STOptions options = document.getOptions ();
            switch (valueType) {
                
                case DISTANCE:
                    if (options.getUnitSystem () != UnitSystem.Metric) {
                        sumDistance = ConvertUtils.convertKilometer2Miles (sumDistance, false);
                    }
                    dataset.add (timePeriod, sumDistance, seriesName);
                    break;

                case DURATION:
                    // calculate duration in hours
                    dataset.add (timePeriod, sumDuration / 3600d, seriesName);
                    break;

                case ASCENT:
                    if (options.getUnitSystem () != UnitSystem.Metric) {
                        sumAscent = ConvertUtils.convertMeter2Feet ((int) sumAscent);
                    }
                    dataset.add (timePeriod, sumAscent, seriesName);
                    break;

                case CALORIES:
                    // set calorie consumption
                    dataset.add (timePeriod, sumCalories, seriesName);
                    break;						

                case EXERCISES:
                    // set number of exercises
                    dataset.add (timePeriod, lExercises.size (), seriesName);
                    break;

                case AVG_SPEED:
                    // calculate AVG speed of all exercises of time step
                    if (options.getUnitSystem () != UnitSystem.Metric) {
                        sumDistance = ConvertUtils.convertKilometer2Miles (sumDistance, false);
                    }

                    double averageSpeed = sumDistance / (sumDuration / 3600d); 

                    // calculate the speed value depending on current speed unit view
                    if (options.getSpeedView () == SpeedView.MinutesPerDistance) {
                        if (averageSpeed == 0) {
                            dataset.add (timePeriod, 0, seriesName);
                        }
                        else {
                            dataset.add (timePeriod, 60 / averageSpeed, seriesName);
                        }
                    }
                    else {
                        dataset.add (timePeriod, averageSpeed, seriesName);
                    }
                    break;
                default:
                    dataset.add (timePeriod, 0, seriesName);                    
            }
        }
    }

    /**
     * Sets up the diagram for sport subtype overview for the selected sport type.
     * 
     * @param dataset the XY dataset to be filled
     * @param graphColors list of graph colors, can be filled with preferred colors 
     */
    private void setupSportSubTypeDiagram(TimeTableXYDataset dataset, List<Color> graphColors) {
        
        // TODO: refactoring possible?
        
        // get time range to display
        TimeRangeType timeType = getCurrentTimeRangeType ();
        int year = getSelectedYear();
        
        // get selected sport type
        SportType sportType = document.getSportTypeList().getAt(cbSportTypeList.getSelectedIndex());
        
        // display a graph for each sport subtype
        for (SportSubType sportSubType : sportType.getSportSubTypeList()) {
            addSportSubTypeTimeSeries(dataset, timeType, year, sportType, sportSubType);
        }
        
        // add custom colors for the sport subtype graphs, some color presets are not usable
        // (if more colors are needed, then presets will be used)
        for (int i = 1; i <= 8; i++) {
            // TODO: rename properties
            graphColors.add(context.getResReader ().getColor ("st.dlg.overview.graph_color.equipment" + i));   
        }            
    }

    /**
     * This method calculates the distance per sport subtype values and adds them to a TimeTableXYDataset. 
     * The calculation is always done for the sport type selected by the user.
     * 
     * @param dataset the timetable dataset
     * @param timeType time range for calculated values
     * @param year the year for calculation
     * @param sportType the sport type to be shown
     * @param sportSubType the sport subtype to be shown in this series
     */
    private void addSportSubTypeTimeSeries (TimeTableXYDataset dataset, TimeRangeType timeType, int year, 
            SportType sportType, SportSubType sportSubType) {
        
        String seriesName = sportSubType.getName();
                
        // process value calculation for each step of time range
        int timeStepCount = getTimeStepCount (timeType, year);
        for (int timeStep = 0; timeStep < timeStepCount; timeStep++) {
            
            // create time period for current time step
            RegularTimePeriod timePeriod = createTimePeriodForTimeStep (timeType, year, timeStep);

            // create the ExerciseFilter for the time range of the current time step
            ExerciseFilter filter = createExerciseFilterForTimeStep (timeType, year, timeStep);
            filter.setSportType (sportType);
            filter.setSportSubType(sportSubType);
            mergeExerciseFilterIfEnabled(filter);
                        
            // get exercises for defined filter
            IdObjectList<Exercise> lExercises = document.getExerciseList ().getExercisesForFilter (filter);

            // create distance sum of all found exercises
            double sumDistance = 0d;                
            for (Exercise tempExercise : lExercises) {
                sumDistance += tempExercise.getDistance ();
            }
                
            // convert to english unit mode when enabled
            if (document.getOptions ().getUnitSystem () != UnitSystem.Metric) {
                sumDistance = ConvertUtils.convertKilometer2Miles (sumDistance, false);
            }

            // set distance value of time step
            dataset.add (timePeriod, sumDistance, seriesName);
        }
    }

    /**
     * Sets up the diagram for equipment usage for the selected sport type.
     * 
     * @param dataset the XY dataset to be filled
     * @param graphColors list of graph colors, can be filled with preferred colors 
     */
    private void setupEquipmentDiagram(TimeTableXYDataset dataset, List<Color> graphColors) {
        
        // get time range to display
        TimeRangeType timeType = getCurrentTimeRangeType ();
        int year = getSelectedYear();
        
        // get selected sport type
        SportType sportType = document.getSportTypeList().getAt(cbSportTypeList.getSelectedIndex());
        
        // display a graph for each equipment and one for not specified equipment
        for (Equipment equipment : sportType.getEquipmentList()) {
            addEquipmentTimeSeries(dataset, timeType, year, sportType, equipment);
        }
        addEquipmentTimeSeries(dataset, timeType, year, sportType, null);
        
        // add custom colors for the equipment graphs, some color presets are not usable
        // (if more colors are needed, then presets will be used)
        for (int i = 1; i <= 8; i++) {
            graphColors.add(context.getResReader ().getColor ("st.dlg.overview.graph_color.equipment" + i));   
        }            
    }

    /**
     * This method calculates the distance per equipment values and adds them to a TimeTableXYDataset. 
     * The calculation is always done for the sport type selected by the user.
     * 
     * @param dataset the timetable dataset
     * @param timeType time range for calculated values
     * @param year the year for calculation
     * @param sportType the sport type to be shown
     * @param equipment the equipment to be shown in this series (when null, then calculate exercises with no equipment assigned only)
     */
    private void addEquipmentTimeSeries (TimeTableXYDataset dataset, TimeRangeType timeType, int year, 
            SportType sportType, Equipment equipment) {
        
        String seriesName = equipment != null ? 
                equipment.getName() : context.getResReader().getString("st.dlg.overview.equipment.not_specified");
                
        // process value calculation for each step of time range
        int timeStepCount = getTimeStepCount (timeType, year);
        for (int timeStep = 0; timeStep < timeStepCount; timeStep++) {
            
            // create time period for current time step
            RegularTimePeriod timePeriod = createTimePeriodForTimeStep (timeType, year, timeStep);

            // create the ExerciseFilter for the time range of the current time step
            ExerciseFilter filter = createExerciseFilterForTimeStep (timeType, year, timeStep);
            filter.setSportType (sportType);
            filter.setEquipment(equipment);
            mergeExerciseFilterIfEnabled(filter);
                        
            // get exercises for defined filter
            IdObjectList<Exercise> lExercises = document.getExerciseList ().getExercisesForFilter (filter);

            // create distance sum of all found exercises
            double sumDistance = 0d;                
            for (Exercise tempExercise : lExercises) {
                // when displaying series for no equipment assigned then skip exercises with assigned equipment
                if (equipment == null && tempExercise.getEquipment() != null) {
                    continue;
                }
                sumDistance += tempExercise.getDistance ();
            }
                
            // convert to english unit mode when enabled
            if (document.getOptions ().getUnitSystem () != UnitSystem.Metric) {
                sumDistance = ConvertUtils.convertKilometer2Miles (sumDistance, false);
            }

            // set distance value of time step
            dataset.add (timePeriod, sumDistance, seriesName);
        }
    }

    /**
     * Sets up the diagram for weight data.
     * 
     * @param dataset the XY dataset to be filled
     * @param graphColors list of graph colors, can be filled with preferred colors 
     */
    private void setupWeightDiagram(TimeTableXYDataset dataset, List<Color> graphColors) {        
        
        // get time range to display
        TimeRangeType timeType = getCurrentTimeRangeType ();
        int year = getSelectedYear();
        
        addWeightTimeSeries(dataset, timeType, year);
        graphColors.add (context.getResReader ().getColor ("st.dlg.overview.graph_color.all_types"));        
    }
    
    /**
     * This method creates a TimeSeries graph which contains all Weight entries
     * for the current selected time range and adds them to the passed
     * TimeTableXYDataset.
     *
     * @param dataset the timetable dataset
     * @param timeType time range for calculated values
     * @param year the year for calculation
     */
    private void addWeightTimeSeries (TimeTableXYDataset dataset, TimeRangeType timeType, int year) {
        
        String seriesName = context.getResReader ().getString ("st.dlg.overview.display.weight.text");
        
        // process value calculation for each step of time range
        int timeStepCount = getTimeStepCount (timeType, year);
        for (int timeStep = 0; timeStep < timeStepCount; timeStep++) {
            
            // create time period for current time step
            RegularTimePeriod timePeriod = createTimePeriodForTimeStep (timeType, year, timeStep);

            // create the ExerciseFilter for the time range of the current time step
            // (ExerciseFilter was not made for Weight, but it's also handy to use here :-)
            ExerciseFilter filter = createExerciseFilterForTimeStep (timeType, year, timeStep);
            
            // get average weight for the time range of this step
            double avgWeight = getAverageWeightInTimeRange (filter);            
            if (document.getOptions ().getUnitSystem () != UnitSystem.Metric) {
                avgWeight = ConvertUtils.convertKilogram2Lbs (avgWeight);
            }
            
            // add computed value to time series (if the weight value is available)
            if (avgWeight > 0) {
                dataset.add (timePeriod, Double.valueOf(avgWeight), seriesName, true);
            }
            // when there is no weight value, add at least the first and last dataset item
            // to make sure the full time range is shown
            else if (timeStep == 0 || timeStep == timeStepCount - 1) {
                dataset.add (timePeriod, (Number) null, seriesName, true);
            }
        }
    }

    /**
     * Returns the number of displayed time steps in the specified time range type.
     * @param timeType the time range type to be displayed
     * @param year the year to be displayed
     * @return number of time steps
     */
    private int getTimeStepCount (TimeRangeType timeType, int year) {
        
        switch (timeType) {
            case LAST_12_MONTHS:
            	return 13;
            case MONTHS_OF_YEAR:
                return 12;                
            case WEEKS_OF_YEAR:
                // Workaround for a JFreeChart problem: use the Year instead
                // of Week class for the bottom axis, otherwise there will be
                // format problems on the axis (the first week is often "52")
                // => get number of weeks for the specified year (mostly 52, sometimes 53)
                Calendar calTemp = createCalendarForWeekOfYear (year, 3);
                return calTemp.getActualMaximum (Calendar.WEEK_OF_YEAR);                
            case LAST_10_YEARS:
                return 10;                
            default:
                throw new IllegalArgumentException ("Unknow TimeRangeType!");
        }        
    }

    /**
     * Creates the TimePeriod to be displayed in the TimeSeries graph for the
     * specified time step.
     * @param timeType the time range type to be displayed
     * @param year the year to be displayed
     * @param timeStep the current time step in the graph
     * @return the created TimePeriod for the current time step
     */
    private RegularTimePeriod createTimePeriodForTimeStep (
        TimeRangeType timeType, int year, int timeStep) {
        
        switch (timeType) {
            case LAST_12_MONTHS:
                Calendar today = Calendar.getInstance ();
                int tempMonth = today.get (Calendar.MONTH) + timeStep;
                int tempYear = today.get (Calendar.YEAR) - 1 + tempMonth / 12;
                return new Month (tempMonth % 12 + 1, tempYear);                
            case MONTHS_OF_YEAR:
                return new Month (timeStep + 1, year);                
            case WEEKS_OF_YEAR:                    
                // Workaround for a JFreeChart problem: use the Year instead
                // of Week class for the botton axis, otherwise there will be
                // format problems on the axis (the first week is often "52")
                int tempWeekNr = timeStep + 1;
                return new Year (1900 + tempWeekNr);                
            case LAST_10_YEARS:
                return new Year (year - 9 + timeStep);
            default:
                throw new IllegalArgumentException ("Unknow TimeRangeType!");
        }
    }

    /**
     * Creates the ExerciseFilter with the time range (dateStart/dateEnd) 
     * to be shown in the TimeSeries graph for the specified time step.
     * All the other ExerciseFilter attributes are not set yet.
     * @param timeType the time range type to be displayed
     * @param year the year to be displayed
     * @param timeStep the current time step in the graph
     * @return the created ExerciseFilter for the current time step
     */
    private ExerciseFilter createExerciseFilterForTimeStep (
        TimeRangeType timeType, int year, int timeStep) {
        
        ExerciseFilter filter = new ExerciseFilter ();

        // setup time range of filter depending on the specified time type
        switch (timeType) {
            case LAST_12_MONTHS:
                Calendar today = Calendar.getInstance ();
                int tempMonth = today.get (Calendar.MONTH) + timeStep;
                int tempYear = today.get (Calendar.YEAR) - 1 + tempMonth / 12;
                Calendar calMonth = createCalendarFor (tempYear, tempMonth % 12, 1, true);
                filter.setDateStart (calMonth.getTime ());
                calMonth.add (Calendar.MONTH, 1);
                calMonth.add (Calendar.SECOND, -1);              
                filter.setDateEnd (calMonth.getTime ());
                break;
                
            case MONTHS_OF_YEAR:
                tempMonth = timeStep;
                calMonth = createCalendarFor (year, tempMonth, 1, true);
                filter.setDateStart (calMonth.getTime ());
                calMonth.add (Calendar.MONTH, 1);
                calMonth.add (Calendar.SECOND, -1);              
                filter.setDateEnd (calMonth.getTime ());
                break;
                
            case WEEKS_OF_YEAR:                    
                int tempWeekNr = timeStep + 1;
                Calendar calWeek = createCalendarForWeekOfYear (year, tempWeekNr);
                filter.setDateStart (calWeek.getTime ());
                calWeek.add (Calendar.WEEK_OF_YEAR, 1);
                calWeek.add (Calendar.SECOND, -1);              
                filter.setDateEnd (calWeek.getTime ());
                break;
                
            case LAST_10_YEARS:
                tempYear = year - 9 + timeStep;
                Calendar calYear = createCalendarFor (tempYear, 1-1, 1, true);
                filter.setDateStart (calYear.getTime ());
                calYear.add (Calendar.YEAR, 1);
                calYear.add (Calendar.SECOND, -1);              
                filter.setDateEnd (calYear.getTime ());
                break;
                
            default:
                throw new IllegalArgumentException ("Unknow TimeRangeType!");
        }
        
        return filter;
    }    
    
    private Calendar createCalendarFor (int year, int month, int day, boolean startOfDay) {
        Calendar cal = Calendar.getInstance ();
        cal.clear ();
        if (startOfDay) {
            cal.set (year, month, day, 0, 0, 0);
        }
        else {
            cal.set (year, month, day, 23, 59, 59);
        }
        return cal;
    }
    
    private Calendar createCalendarForWeekOfYear (int year, int weekNr) {        
        int firstWeekDay = document.getOptions ().isWeekStartSunday () ?
            Calendar.SUNDAY : Calendar.MONDAY;
        Calendar cal = Calendar.getInstance ();
        cal.clear ();
        cal.setFirstDayOfWeek (firstWeekDay);                    
        cal.set (year, 0, 1, 0, 0, 0);
        cal.set (Calendar.WEEK_OF_YEAR, weekNr);
        cal.set (Calendar.DAY_OF_WEEK, firstWeekDay);
        return cal;
    }    
    
    /**
     * Merges the specified filter for time series creation with the existing filter
     * in the SportsTracker view (if it is enabled).
     * 
     * @param filter the filter used for time series creation
     */
    private void mergeExerciseFilterIfEnabled(ExerciseFilter filter) {
        // when the exercise filter is enabled in the GUI then we need to
        // merge these filter criterias into the created diagram filter            
        if (document.isFilterEnabled ()) {            
            ExerciseFilter currentFilter = document.getCurrentFilter ();

            // merge filter date
            if (currentFilter.getDateStart ().after (filter.getDateStart ())) {
                filter.setDateStart (currentFilter.getDateStart ());
            }
            if (currentFilter.getDateEnd ().before (filter.getDateEnd ())) {
                filter.setDateEnd (currentFilter.getDateEnd ());
            }

            // merge sport type and subtype filter
            if (currentFilter.getSportType() != null) {
                
                if (filter.getSportType() != null &&
                    !currentFilter.getSportType().equals(filter.getSportType())) {
                    // filters have different sport types => add a not existing sport type, so nothing will be found 
                    filter.setSportType(new SportType(Integer.MIN_VALUE));
                } else {
                    filter.setSportType (currentFilter.getSportType ());
    
                    if (currentFilter.getSportSubType () != null) {
                        filter.setSportSubType (currentFilter.getSportSubType ());
                    }
                }
            }

            // merge intensity filter
            if (currentFilter.getIntensity () != null) {
                filter.setIntensity (currentFilter.getIntensity ());
            }

            // merge equipment filter
            if (currentFilter.getEquipment () != null) {
                if (filter.getEquipment() != null &&
                    !currentFilter.getEquipment().equals(filter.getEquipment())) {
                    // filters have different equipments => add a not existing equipment, so nothing will be found 
                    filter.setEquipment(new Equipment(Integer.MIN_VALUE));
                } else {
                    filter.setEquipment (currentFilter.getEquipment ());
                }                
            }

            // merge comment filter
            if (currentFilter.getCommentSubString () != null) {
                filter.setCommentSubString (currentFilter.getCommentSubString ());
                filter.setRegularExpressionMode (currentFilter.isRegularExpressionMode ());
            }
        }        
    }

    /**
     * Returns the average weight value for all Weight entries in the time range
     * of the specified ExerciseFilter. 
     * @param filter the ExerciseFilter with the time range to be used
     * @return the average weight value or 0 when no Weight entries found
     */
    private double getAverageWeightInTimeRange (ExerciseFilter filter) {
        List<Weight> weightsInTimeRange = 
            document.getWeightList ().getEntriesInTimeRange (
                filter.getDateStart (), filter.getDateEnd ()); 
        
        if (weightsInTimeRange.isEmpty()) {
            return 0;
        }
        
        double weightSum = 0;
        for (Weight weight : weightsInTimeRange) {
            weightSum += weight.getValue ();
        }
        return weightSum / (double) weightsInTimeRange.size ();
    }
    
    /**
     * Action for closing this dialog.
     */
    @Action(name=ACTION_CLOSE)
    public void close () {
        this.dispose ();
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btClose = new javax.swing.JButton();
        laTimeRange = new javax.swing.JLabel();
        cbTimeRange = new javax.swing.JComboBox();
        spYear = new javax.swing.JSpinner();
        pDiagram = new javax.swing.JPanel();
        laOptions = new javax.swing.JLabel();
        laDisplay = new javax.swing.JLabel();
        cbDisplay = new javax.swing.JComboBox();
        laFor = new javax.swing.JLabel();
        cbSportTypeMode = new javax.swing.JComboBox();
        separator = new javax.swing.JSeparator();
        cbSportTypeList = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("st.dlg.overview"); // NOI18N
        setResizable(false);

        btClose.setText("_Close");
        btClose.setName("btClose"); // NOI18N

        laTimeRange.setFont(laTimeRange.getFont().deriveFont(laTimeRange.getFont().getStyle() | java.awt.Font.BOLD));
        laTimeRange.setText("_Displayed Time Range");
        laTimeRange.setName("st.dlg.overview.time_range"); // NOI18N

        cbTimeRange.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbTimeRange.setName("cbTimeRange"); // NOI18N

        spYear.setModel(new javax.swing.SpinnerNumberModel(2000, 1900, 2999, 1));
        spYear.setName("spYear"); // NOI18N

        pDiagram.setName("pDiagram"); // NOI18N
        pDiagram.setLayout(new java.awt.BorderLayout());

        laOptions.setFont(laOptions.getFont().deriveFont(laOptions.getFont().getStyle() | java.awt.Font.BOLD));
        laOptions.setText("_Display Options");
        laOptions.setName("st.dlg.overview.options"); // NOI18N

        laDisplay.setText("_Display");
        laDisplay.setName("st.dlg.overview.display"); // NOI18N

        cbDisplay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbDisplay.setName("cbDisplay"); // NOI18N

        laFor.setText("_for");
        laFor.setName("st.dlg.overview.for"); // NOI18N

        cbSportTypeMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbSportTypeMode.setName("cbSportTypeMode"); // NOI18N

        separator.setName("separator"); // NOI18N

        cbSportTypeList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbSportTypeList.setName("cbSportTypeList"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pDiagram, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                    .addComponent(laTimeRange)
                    .addComponent(laOptions)
                    .addComponent(btClose, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(laDisplay)
                                .addGap(18, 18, 18)
                                .addComponent(cbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(laFor)
                                .addGap(18, 18, 18)
                                .addComponent(cbSportTypeMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbSportTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cbTimeRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(spYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(8, 8, 8))
            .addComponent(separator, javax.swing.GroupLayout.DEFAULT_SIZE, 916, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(laTimeRange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbTimeRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pDiagram, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(laOptions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laDisplay)
                    .addComponent(cbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(laFor)
                    .addComponent(cbSportTypeMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbSportTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btClose)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cbTimeRange, spYear});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClose;
    private javax.swing.JComboBox cbDisplay;
    private javax.swing.JComboBox cbSportTypeList;
    private javax.swing.JComboBox cbSportTypeMode;
    private javax.swing.JComboBox cbTimeRange;
    private javax.swing.JLabel laDisplay;
    private javax.swing.JLabel laFor;
    private javax.swing.JLabel laOptions;
    private javax.swing.JLabel laTimeRange;
    private javax.swing.JPanel pDiagram;
    private javax.swing.JSeparator separator;
    private javax.swing.JSpinner spYear;
    // End of variables declaration//GEN-END:variables

    /** This is the list of possible overview types to draw in diagram. */
    private enum OverviewType {
        EACH_SPLITTED, EACH_STACKED, ALL_SUMMARY;
    
        /** Static resource reader is needed for string creation. */
        private static ResourceReader resReader;

        public static void setResReader (ResourceReader resReader) {
            OverviewType.resReader = resReader;
        }
        
        /** 
         * Returns the translated name (to be displayed) of this overview type. 
         * @return name of this axis type
         */
        @Override
        public String toString () {
            switch (this) {
                case EACH_STACKED:
                    return OverviewType.resReader.getString ("st.dlg.overview.sport_type.each_stacked.text");
                case EACH_SPLITTED:
                    return OverviewType.resReader.getString ("st.dlg.overview.sport_type.each_splitted.text");
                case ALL_SUMMARY:
                    return OverviewType.resReader.getString ("st.dlg.overview.sport_type.all_summary.text");
                default: 
                    return "???";
            }
        }
    }
}
