package de.saring.sportstracker.gui.dialogs;

import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.*;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.Date310Utils;
import de.saring.util.ResourceReader;
import de.saring.util.data.IdObjectList;
import de.saring.util.gui.DialogUtils;
import de.saring.util.gui.jfreechart.ChartUtils;
import de.saring.util.gui.jfreechart.StackedRenderer;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils;
import de.saring.util.unitcalc.FormatUtils.SpeedView;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;
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

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is the "Overview diagram" dialog of the SportsTracker application,
 * which displays all the exercises or weight entries in various diagram graph types.
 * The user can select the displayed time range (e.g. all months of the selected year
 * or the last 10 years until the selected year).
 *
 * @author Stefan Saring, Kai Pastor
 * @version 3.2
 */
public class OverviewDialog extends JDialog {

    /**
     * This is the list of possible time ranges displayed in diagram.
     */
    private enum TimeRangeType {
        MONTHS_OF_YEAR,
        WEEKS_OF_YEAR,
        LAST_10_YEARS,
        /**
         * In total 13 months: current month and last 12 before (good
         * for compare current month and the one from year before).
         */
        LAST_12_MONTHS
    }

    /**
     * This is the list of possible value types displayed in diagram.
     */
    private enum ValueType {
        DISTANCE, DURATION, ASCENT, CALORIES, EXERCISES, AVG_SPEED, SPORTSUBTYPE, EQUIPMENT, WEIGHT
    }

    private final STContext context;
    private final STDocument document;

    /**
     * The panel containing the current chart.
     */
    private final ChartPanel chartPanel;

    /**
     * Constants for action and property names.
     */
    private static final String ACTION_CLOSE = "st.dlg.overview.close";

    /**
     * Creates new OverviewDialog instance.
     *
     * @param context the SportsTracker context
     * @param document the applications document component
     */
    @Inject
    public OverviewDialog(STContext context, STDocument document) {
        super(context.getMainFrame(), true);
        this.context = context;
        this.document = document;
        OverviewType.setResReader(context.getResReader());

        initComponents();
        setLocationRelativeTo(getParent());
        this.getRootPane().setDefaultButton(btClose);

        // add chart panel
        chartPanel = new ChartPanel(null);
        pDiagram.add(chartPanel, BorderLayout.CENTER);

        // setup actions
        ActionMap actionMap = context.getSAFContext().getActionMap(getClass(), this);
        javax.swing.Action aClose = actionMap.get(ACTION_CLOSE);
        btClose.setAction(aClose);
        DialogUtils.setDialogEscapeKeyAction(this, aClose);

        setInitialValues();
        updateDiagram();

        // add listener for selection changes => diagram must be updated
        ActionListener alUpdate = e -> updateDiagram();
        cbTimeRange.addActionListener(alUpdate);
        cbDisplay.addActionListener(alUpdate);
        cbSportTypeMode.addActionListener(alUpdate);
        cbSportTypeList.addActionListener(alUpdate);

        spYear.addChangeListener(e -> updateDiagram());
    }

    /**
     * Sets the dialog title (must be done here, otherwise the AppFramework overwrites it).
     *
     * @param title the title
     */
    @Override
    public void setTitle(String title) {

        // display in title when exercise filter is beeing used
        if (document.isFilterEnabled()) {
            super.setTitle(title + " " +
                    context.getResReader().getString("st.dlg.overview.title.filter"));
        } else {
            super.setTitle(title);
        }
    }

    /**
     * Sets the initial exercise values for all controls.
     */
    private void setInitialValues() {

        // fill selection comboboxes
        cbTimeRange.removeAllItems();
        cbTimeRange.addItem(context.getResReader().getString("st.dlg.overview.time_range.last_12_months.text"));
        cbTimeRange.addItem(context.getResReader().getString("st.dlg.overview.time_range.months_of_year.text"));
        cbTimeRange.addItem(context.getResReader().getString("st.dlg.overview.time_range.weeks_of_year.text"));
        cbTimeRange.addItem(context.getResReader().getString("st.dlg.overview.time_range.ten_years.text"));

        cbDisplay.removeAllItems();
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.distance_sum.text"));
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.duration_sum.text"));
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.ascent_sum.text"));
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.calorie_sum.text"));
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.exercise_count.text"));
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.avg_speed.text"));
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.sportsubtype_distance.text"));
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.equipment_distance.text"));
        cbDisplay.addItem(context.getResReader().getString("st.dlg.overview.display.weight.text"));

        cbSportTypeMode.removeAllItems();
        cbSportTypeMode.addItem(OverviewType.EACH_SPLITTED);
        cbSportTypeMode.addItem(OverviewType.EACH_STACKED);
        cbSportTypeMode.addItem(OverviewType.ALL_SUMMARY);

        cbSportTypeList.removeAllItems();
        for (SportType sportType : document.getSportTypeList()) {
            cbSportTypeList.addItem(sportType.getName());
        }

        // the spinner must not use number grouping (otherwise e.g. year "2.007")
        ((JSpinner.NumberEditor) spYear.getEditor()).getFormat().setGroupingUsed(false);
        spYear.setValue(LocalDate.now().getYear());
    }

    /**
     * Draws the overview diagram according to the current selections.
     */
    private void updateDiagram() {
        updateSelectionControlsState();

        // get selected time range and value type and its name to display
        TimeRangeType timeType = getCurrentTimeRangeType();
        ValueType vType = getCurrentValueType();
        String vTypeName = getCurrentValueTypeName();
        int year = getSelectedYear();

        // create a table of all time series (graphs) and the appropriate colors
        TimeTableXYDataset dataset = new TimeTableXYDataset();
        List<Color> lGraphColors = new ArrayList<>();

        // setup TimeSeries in the diagram (done in different ways for all the value types)
        if (getCurrentValueType() == ValueType.SPORTSUBTYPE) {
            setupSportSubTypeDiagram(dataset, lGraphColors);
        } else if (getCurrentValueType() == ValueType.EQUIPMENT) {
            setupEquipmentDiagram(dataset, lGraphColors);
        } else if (getCurrentValueType() == ValueType.WEIGHT) {
            setupWeightDiagram(dataset, lGraphColors);
        } else {
            setupExerciseDiagram(dataset, lGraphColors);
        }

        // create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                null,          // Title
                null,          // Y-axis label
                vTypeName,     // X-axis label
                dataset,       // primary dataset
                true,          // display legend
                true,          // display tooltips
                false);        // URLs

        ChartUtils.customizeChart(chart, chartPanel);

        // render unique filled shapes for each graph
        XYPlot plot = (XYPlot) chart.getPlot();

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);
        // set color for sport type series
        for (int i = 0; i < lGraphColors.size(); i++) {
            Color tempColor = lGraphColors.get(i);
            renderer.setSeriesPaint(i, tempColor);
        }

        // setup date format for tooltips and time (bottom) axis
        String dateFormatTooltip;
        String dateFormatAxis;
        DateTickUnit dateTickUnit;

        switch (timeType) {
            case LAST_12_MONTHS:
                dateFormatTooltip = "MMMMM yyyy";
                dateFormatAxis = "MMM";
                dateTickUnit = new DateTickUnit(DateTickUnitType.MONTH, 1);
                break;
            case MONTHS_OF_YEAR:
                dateFormatTooltip = "MMMMM";
                dateFormatAxis = "MMM";
                dateTickUnit = new DateTickUnit(DateTickUnitType.MONTH, 1);
                break;
            case WEEKS_OF_YEAR:
                // Workaround for a JFreeChart formating problem: years are used
                // instead of weeks for the bottom axis, otherwise there will be
                // format problems on the axis (the first week is often "52")
                dateFormatTooltip = "yy";
                dateFormatAxis = "yy";
                dateTickUnit = new DateTickUnit(DateTickUnitType.YEAR, 2);
                break;
            default: // LAST_10_YEARS
                dateFormatTooltip = "yyyy";
                dateFormatAxis = "yyyy";
                dateTickUnit = new DateTickUnit(DateTickUnitType.YEAR, 1);
                break;
        }

        // setup tooltips: must display month, week or year and the value only
        String toolTipFormat = "{1}: {2}";
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(toolTipFormat,
                new SimpleDateFormat(dateFormatTooltip), new DecimalFormat()) {
            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                return dataset.getSeriesKey(series) + ", " + super.generateToolTip(dataset, series, item);
            }
        });

        // special handling for overview type EACH_STACKED, it uses a special renderer
        // (only for exercises based value types, stacked mode can be selected only there)
        if (cbSportTypeMode.isVisible() && cbSportTypeMode.getSelectedItem() == OverviewType.EACH_STACKED) {

            renderer.setSeriesLinesVisible(0, false);
            renderer.setSeriesShapesVisible(0, false);
            renderer.setSeriesVisibleInLegend(0, false);

            // actual dataset
            dataset = new TimeTableXYDataset();
            lGraphColors = new ArrayList<>();

            // create a separate graph for each sport type
            for (SportType sportType : document.getSportTypeList()) {
                addExerciseTimeSeries(dataset, timeType, year, vType, sportType);
                lGraphColors.add(sportType.getColor());
            }
            plot.setDataset(1, dataset);

            // actual stacked renderer
            StackedRenderer stackedRenderer = new StackedRenderer();
            // set color for sport type series
            for (int i = 0; i < lGraphColors.size(); i++) {
                Color tempColor = lGraphColors.get(i);
                stackedRenderer.setSeriesPaint(i, tempColor);
            }
            stackedRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(toolTipFormat,
                    new SimpleDateFormat(dateFormatTooltip), new DecimalFormat()) {
                @Override
                public String generateToolTip(XYDataset dataset, int series, int item) {
                    return dataset.getSeriesKey(series) + ", " + super.generateToolTip(dataset, series, item);
                }
            });
            plot.setRenderer(1, stackedRenderer);
        }

        // set date format of time (bottom) axis
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat(dateFormatAxis));
        if (dateTickUnit != null) {
            axis.setTickUnit(dateTickUnit);
        }

        // set value range from 0 to 10 when there are no values
        // (otherwise the range is double minimum to double maximum)
        if (plot.getRangeAxis().getRange().getCentralValue() == 0d) {
            plot.getRangeAxis().setRange(new Range(0, 10));
        }

        // add vertical year break marker when displaying the last 12 months
        if (timeType == TimeRangeType.LAST_12_MONTHS) {
            LocalDate firstDayOfYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
            LocalDate middleOfDecemberOfLastYear = firstDayOfYear.minusDays(15);
            Date dateMiddleOfDecemberOfLastYear = Date310Utils.localDateToDate(middleOfDecemberOfLastYear);
            ValueMarker newYearMarker = new ValueMarker(dateMiddleOfDecemberOfLastYear.getTime());
            newYearMarker.setPaint(context.getResReader().getColor("st.dlg.overview.graph_color.year_break"));
            newYearMarker.setStroke(new BasicStroke(0.8f));
            newYearMarker.setLabel(String.valueOf(firstDayOfYear.getYear()));
            newYearMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            newYearMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            plot.addDomainMarker(newYearMarker);
        }

        // display legend next to the diagram on the right side
        chart.getLegend().setPosition(RectangleEdge.RIGHT);

        // add chart to panel
        chartPanel.setChart(chart);
    }

    /**
     * Updates the state of the selection widgets for the diagram content.
     */
    private void updateSelectionControlsState() {
        // hide year selection when displaying last 12 months
        spYear.setVisible(getCurrentTimeRangeType() != TimeRangeType.LAST_12_MONTHS);

        // the sport type selection is only visible for the ValueType WEIGHT
        boolean sssEnabled = getCurrentValueType() != ValueType.WEIGHT;
        laFor.setVisible(sssEnabled);
        cbSportTypeMode.setVisible(sssEnabled);
        cbSportTypeList.setVisible(sssEnabled);

        // display sport type mode (for exercise mode) or sport type list (for sport subtype and equipment mode) combobox only
        if (sssEnabled) {
            boolean showSportTypeModes = getCurrentValueType() != ValueType.SPORTSUBTYPE
                    && getCurrentValueType() != ValueType.EQUIPMENT;
            laFor.setText(context.getResReader().getString(showSportTypeModes ?
                    "st.dlg.overview.for.text" : "st.dlg.overview.for_sport_type.text"));
            cbSportTypeMode.setVisible(showSportTypeModes);
            cbSportTypeList.setVisible(!showSportTypeModes);
        }
    }

    /**
     * Returns the current selected time range type to display.
     *
     * @return current TimeRangeType
     */
    private TimeRangeType getCurrentTimeRangeType() {
        switch (cbTimeRange.getSelectedIndex()) {
            case 0:
                return TimeRangeType.LAST_12_MONTHS;
            case 1:
                return TimeRangeType.MONTHS_OF_YEAR;
            case 2:
                return TimeRangeType.WEEKS_OF_YEAR;
            case 3:
                return TimeRangeType.LAST_10_YEARS;
            default:
                throw new IllegalArgumentException("Invalid time range type type selection!");
        }
    }

    /**
     * Returns the selected year for the time range to be shown.
     *
     * @return year
     */
    private int getSelectedYear() {
        return ((Number) spYear.getValue()).intValue();
    }

    /**
     * Returns the current selected value type.
     *
     * @return current ValueType
     */
    private ValueType getCurrentValueType() {
        switch (cbDisplay.getSelectedIndex()) {
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
                throw new IllegalArgumentException("Invalid display type selection!");
        }
    }

    /**
     * Returns the name to be displayed for the current selected value type.
     *
     * @return name of the current ValueType
     */
    private String getCurrentValueTypeName() {
        FormatUtils formatUtils = context.getFormatUtils();

        switch (getCurrentValueType()) {
            case DISTANCE:
                return context.getResReader().getString(
                        "st.dlg.overview.value_type.distance_sum", formatUtils.getDistanceUnitName());
            case DURATION:
                return context.getResReader().getString("st.dlg.overview.value_type.duration_sum");
            case ASCENT:
                return context.getResReader().getString("st.dlg.overview.value_type.ascent_sum",
                        formatUtils.getAltitudeUnitName());
            case CALORIES:
                return context.getResReader().getString("st.dlg.overview.value_type.calories_sum");
            case EXERCISES:
                return context.getResReader().getString("st.dlg.overview.value_type.exercise_count");
            case AVG_SPEED:
                return context.getResReader().getString(
                        "st.dlg.overview.value_type.avg_speed", formatUtils.getSpeedUnitName());
            case SPORTSUBTYPE:
                return context.getResReader().getString(
                        "st.dlg.overview.value_type.sportsubtype_distance", formatUtils.getDistanceUnitName());
            case EQUIPMENT:
                return context.getResReader().getString(
                        "st.dlg.overview.value_type.equipment_distance", formatUtils.getDistanceUnitName());
            case WEIGHT:
                return context.getResReader().getString(
                        "st.dlg.overview.value_type.weight", formatUtils.getWeightUnitName());
            default:
                throw new IllegalArgumentException("Invalid value type!");
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
        TimeRangeType timeType = getCurrentTimeRangeType();
        int year = getSelectedYear();

        ValueType vType = getCurrentValueType();
        OverviewType overviewType = (OverviewType) cbSportTypeMode.getSelectedItem();

        // which sport type mode is selected by user ?
        if (overviewType != OverviewType.EACH_SPLITTED) {
            // create one graph for sum of all sport types
            addExerciseTimeSeries(dataset, timeType, year, vType, null);
            graphColors.add(context.getResReader().getColor("st.dlg.overview.graph_color.all_types"));
        } else {
            // create a separate graph for each sport type
            for (SportType sportType : document.getSportTypeList()) {
                addExerciseTimeSeries(dataset, timeType, year, vType, sportType);
                graphColors.add(sportType.getColor());
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
    private void addExerciseTimeSeries(TimeTableXYDataset dataset, TimeRangeType timeType, int year, ValueType valueType, SportType sportType) {

        // create the time series for specified time range and sport type
        String seriesName = sportType != null ?
                sportType.getName() :
                context.getResReader().getString("st.dlg.overview.graph.all_types");

        // process value calculation for each step of time range
        int timeStepCount = getTimeStepCount(timeType, year);
        for (int timeStep = 0; timeStep < timeStepCount; timeStep++) {

            // create time period for current time step
            RegularTimePeriod timePeriod = createTimePeriodForTimeStep(timeType, year, timeStep);

            // create the ExerciseFilter for the time range of the current time step
            ExerciseFilter filter = createExerciseFilterForTimeStep(timeType, year, timeStep);
            filter.setSportType(sportType);
            mergeExerciseFilterIfEnabled(filter);

            // get exercises for defined filter
            // (add value 0 and skip to next time step when no exercises found)
            IdObjectList<Exercise> lExercises = document.getExerciseList().getExercisesForFilter(filter);
            if (lExercises.size() == 0) {
                dataset.add(timePeriod, 0, seriesName);
                continue;
            }

            // create sums of all exercises
            double sumDistance = 0d;
            double sumDuration = 0d;
            double sumAscent = 0d;
            double sumCalories = 0d;

            for (Exercise tempExercise : lExercises) {
                sumDistance += tempExercise.getDistance();
                sumDuration += tempExercise.getDuration();
                sumAscent += tempExercise.getAscent();
                sumCalories += tempExercise.getCalories();
            }

            // set value of time step depending on value type
            // (convert to english unit mode when enabled)
            STOptions options = document.getOptions();
            switch (valueType) {

                case DISTANCE:
                    if (options.getUnitSystem() != UnitSystem.Metric) {
                        sumDistance = ConvertUtils.convertKilometer2Miles(sumDistance, false);
                    }
                    dataset.add(timePeriod, sumDistance, seriesName);
                    break;

                case DURATION:
                    // calculate duration in hours
                    dataset.add(timePeriod, sumDuration / 3600d, seriesName);
                    break;

                case ASCENT:
                    if (options.getUnitSystem() != UnitSystem.Metric) {
                        sumAscent = ConvertUtils.convertMeter2Feet((int) sumAscent);
                    }
                    dataset.add(timePeriod, sumAscent, seriesName);
                    break;

                case CALORIES:
                    // set calorie consumption
                    dataset.add(timePeriod, sumCalories, seriesName);
                    break;

                case EXERCISES:
                    // set number of exercises
                    dataset.add(timePeriod, lExercises.size(), seriesName);
                    break;

                case AVG_SPEED:
                    // calculate AVG speed of all exercises of time step
                    if (options.getUnitSystem() != UnitSystem.Metric) {
                        sumDistance = ConvertUtils.convertKilometer2Miles(sumDistance, false);
                    }

                    double averageSpeed = sumDistance / (sumDuration / 3600d);

                    // calculate the speed value depending on current speed unit view
                    if (options.getSpeedView() == SpeedView.MinutesPerDistance) {
                        if (averageSpeed == 0) {
                            dataset.add(timePeriod, 0, seriesName);
                        } else {
                            dataset.add(timePeriod, 60 / averageSpeed, seriesName);
                        }
                    } else {
                        dataset.add(timePeriod, averageSpeed, seriesName);
                    }
                    break;
                default:
                    dataset.add(timePeriod, 0, seriesName);
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

        // get time range to display
        TimeRangeType timeType = getCurrentTimeRangeType();
        int year = getSelectedYear();

        // get selected sport type
        SportType sportType = document.getSportTypeList().getAt(cbSportTypeList.getSelectedIndex());

        // display a graph for each sport subtype
        for (SportSubType sportSubType : sportType.getSportSubTypeList()) {
            addSportSubTypeTimeSeries(dataset, timeType, year, sportType, sportSubType);
        }

        addCustomGraphColors(graphColors);
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
    private void addSportSubTypeTimeSeries(TimeTableXYDataset dataset, TimeRangeType timeType, int year,
                                           SportType sportType, SportSubType sportSubType) {

        String seriesName = sportSubType.getName();

        // process value calculation for each step of time range
        int timeStepCount = getTimeStepCount(timeType, year);
        for (int timeStep = 0; timeStep < timeStepCount; timeStep++) {

            // create time period for current time step
            RegularTimePeriod timePeriod = createTimePeriodForTimeStep(timeType, year, timeStep);

            // create the ExerciseFilter for the time range of the current time step
            ExerciseFilter filter = createExerciseFilterForTimeStep(timeType, year, timeStep);
            filter.setSportType(sportType);
            filter.setSportSubType(sportSubType);
            mergeExerciseFilterIfEnabled(filter);

            // get exercises for defined filter
            IdObjectList<Exercise> lExercises = document.getExerciseList().getExercisesForFilter(filter);

            // create distance sum of all found exercises
            double sumDistance = 0d;
            for (Exercise tempExercise : lExercises) {
                // sum only exercises with same sport subtype (otherwise conflicts with the merged filter set in the view)
                if (sportSubType.equals(tempExercise.getSportSubType())) {
                    sumDistance += tempExercise.getDistance();
                }
            }

            // convert to english unit mode when enabled
            if (document.getOptions().getUnitSystem() != UnitSystem.Metric) {
                sumDistance = ConvertUtils.convertKilometer2Miles(sumDistance, false);
            }

            // set distance value of time step
            dataset.add(timePeriod, sumDistance, seriesName);
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
        TimeRangeType timeType = getCurrentTimeRangeType();
        int year = getSelectedYear();

        // get selected sport type
        SportType sportType = document.getSportTypeList().getAt(cbSportTypeList.getSelectedIndex());

        // display a graph for each equipment and one for not specified equipment
        for (Equipment equipment : sportType.getEquipmentList()) {
            addEquipmentTimeSeries(dataset, timeType, year, sportType, equipment);
        }
        addEquipmentTimeSeries(dataset, timeType, year, sportType, null);

        addCustomGraphColors(graphColors);
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
    private void addEquipmentTimeSeries(TimeTableXYDataset dataset, TimeRangeType timeType, int year,
                                        SportType sportType, Equipment equipment) {

        String seriesName = equipment != null ?
                equipment.getName() : context.getResReader().getString("st.dlg.overview.equipment.not_specified");

        // process value calculation for each step of time range
        int timeStepCount = getTimeStepCount(timeType, year);
        for (int timeStep = 0; timeStep < timeStepCount; timeStep++) {

            // create time period for current time step
            RegularTimePeriod timePeriod = createTimePeriodForTimeStep(timeType, year, timeStep);

            // create the ExerciseFilter for the time range of the current time step
            ExerciseFilter filter = createExerciseFilterForTimeStep(timeType, year, timeStep);
            filter.setSportType(sportType);
            filter.setEquipment(equipment);
            mergeExerciseFilterIfEnabled(filter);

            // get exercises for defined filter
            IdObjectList<Exercise> lExercises = document.getExerciseList().getExercisesForFilter(filter);

            // create distance sum of all found exercises
            double sumDistance = 0d;
            for (Exercise tempExercise : lExercises) {
                // when displaying series for no equipment assigned then skip exercises with assigned equipment
                if (equipment == null && tempExercise.getEquipment() != null) {
                    continue;
                }
                sumDistance += tempExercise.getDistance();
            }

            // convert to english unit mode when enabled
            if (document.getOptions().getUnitSystem() != UnitSystem.Metric) {
                sumDistance = ConvertUtils.convertKilometer2Miles(sumDistance, false);
            }

            // set distance value of time step
            dataset.add(timePeriod, sumDistance, seriesName);
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
        TimeRangeType timeType = getCurrentTimeRangeType();
        int year = getSelectedYear();

        addWeightTimeSeries(dataset, timeType, year);
        graphColors.add(context.getResReader().getColor("st.dlg.overview.graph_color.all_types"));
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
    private void addWeightTimeSeries(TimeTableXYDataset dataset, TimeRangeType timeType, int year) {

        String seriesName = context.getResReader().getString("st.dlg.overview.display.weight.text");

        // process value calculation for each step of time range
        int timeStepCount = getTimeStepCount(timeType, year);
        for (int timeStep = 0; timeStep < timeStepCount; timeStep++) {

            // create time period for current time step
            RegularTimePeriod timePeriod = createTimePeriodForTimeStep(timeType, year, timeStep);

            // create the ExerciseFilter for the time range of the current time step
            // (ExerciseFilter was not made for Weight, but it's also handy to use here :-)
            ExerciseFilter filter = createExerciseFilterForTimeStep(timeType, year, timeStep);

            // get average weight for the time range of this step
            double avgWeight = getAverageWeightInTimeRange(filter);
            if (document.getOptions().getUnitSystem() != UnitSystem.Metric) {
                avgWeight = ConvertUtils.convertKilogram2Lbs(avgWeight);
            }

            // add computed value to time series (if the weight value is available)
            if (avgWeight > 0) {
                dataset.add(timePeriod, avgWeight, seriesName, true);
            }
            // when there is no weight value, add at least the first and last dataset item
            // to make sure the full time range is shown
            else if (timeStep == 0 || timeStep == timeStepCount - 1) {
                dataset.add(timePeriod, (Number) null, seriesName, true);
            }
        }
    }

    /**
     * Returns the number of displayed time steps in the specified time range type.
     *
     * @param timeType the time range type to be displayed
     * @param year the year to be displayed
     * @return number of time steps
     */
    private int getTimeStepCount(TimeRangeType timeType, int year) {

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
                LocalDate dateinYear = LocalDate.of(year, 1, 15);
                WeekFields weekField = getWeekFieldsForWeekStart();
                return (int) dateinYear.range(weekField.weekOfYear()).getMaximum();
            case LAST_10_YEARS:
                return 10;
            default:
                throw new IllegalArgumentException("Unknow TimeRangeType!");
        }
    }

    /**
     * Creates the TimePeriod to be displayed in the TimeSeries graph for the
     * specified time step.
     *
     * @param timeType the time range type to be displayed
     * @param year the year to be displayed
     * @param timeStep the current time step in the graph
     * @return the created TimePeriod for the current time step
     */
    private RegularTimePeriod createTimePeriodForTimeStep(
            TimeRangeType timeType, int year, int timeStep) {

        switch (timeType) {
            case LAST_12_MONTHS:
                LocalDate now = LocalDate.now();
                int tempMonth = now.getMonthValue() + timeStep - 1;
                int tempYear = now.getYear() - 1 + tempMonth / 12;
                return new Month(tempMonth % 12 + 1, tempYear);
            case MONTHS_OF_YEAR:
                return new Month(timeStep + 1, year);
            case WEEKS_OF_YEAR:
                // Workaround for a JFreeChart problem: use the Year instead
                // of Week class for the botton axis, otherwise there will be
                // format problems on the axis (the first week is often "52")
                int tempWeekNr = timeStep + 1;
                return new Year(1900 + tempWeekNr);
            case LAST_10_YEARS:
                return new Year(year - 9 + timeStep);
            default:
                throw new IllegalArgumentException("Unknow TimeRangeType!");
        }
    }

    /**
     * Creates the ExerciseFilter with the time range (dateStart/dateEnd)
     * to be shown in the TimeSeries graph for the specified time step.
     * All the other ExerciseFilter attributes are not set yet.
     *
     * @param timeType the time range type to be displayed
     * @param year the year to be displayed
     * @param timeStep the current time step in the graph
     * @return the created ExerciseFilter for the current time step
     */
    private ExerciseFilter createExerciseFilterForTimeStep(
            TimeRangeType timeType, int year, int timeStep) {

        ExerciseFilter filter = new ExerciseFilter();
        LocalDate now = LocalDate.now();

        LocalDate dateRangeStart;
        LocalDate dateRangeEnd;

        // setup time range of filter depending on the specified time type
        switch (timeType) {
            case LAST_12_MONTHS:
                int tempMonth = now.getMonthValue() + timeStep - 1;
                int tempYear = now.getYear() - 1 + tempMonth / 12;
                dateRangeStart = LocalDate.of(tempYear, tempMonth % 12 + 1, 1);
                dateRangeEnd = dateRangeStart.plusMonths(1).minusDays(1);
                break;

            case MONTHS_OF_YEAR:
                tempMonth = timeStep;
                dateRangeStart = LocalDate.of(year, tempMonth + 1, 1);
                dateRangeEnd = dateRangeStart.plusMonths(1).minusDays(1);
                break;

            case WEEKS_OF_YEAR:
                int tempWeekNr = timeStep + 1;
                dateRangeStart = getStartDateForWeekOfYear(year, tempWeekNr);
                dateRangeEnd = dateRangeStart.plusDays(6);
                break;

            case LAST_10_YEARS:
                tempYear = year - 9 + timeStep;
                dateRangeStart = LocalDate.of(tempYear, 1, 1);
                dateRangeEnd = dateRangeStart.plusYears(1).minusDays(1);
                break;

            default:
                throw new IllegalArgumentException("Unknow TimeRangeType!");
        }

        filter.setDateStart(dateRangeStart);
        filter.setDateEnd(dateRangeEnd);
        return filter;
    }

    /**
     * Adds custom colors for all the diagram graphs, because some color presets are not usable or
     * readable (if more colors are needed, then presets will be used).
     */
    private void addCustomGraphColors(List<Color> graphColors) {
        for (int i = 1; i <= 8; i++) {
            graphColors.add(context.getResReader().getColor("st.dlg.overview.graph_color.graph" + i));
        }
    }

    private LocalDate getStartDateForWeekOfYear(int year, int weekNr) {
        WeekFields weekField = getWeekFieldsForWeekStart();

        // create date for some day in the specified year
        LocalDate date = LocalDate.of(year, 2, 1);
        // set the first weekday (values 1 - 7, value 1 is Sunday or Monday)
        date = date.with(weekField.dayOfWeek(), 1);
        // set the specified week number
        return date.with(weekField.weekOfYear(), weekNr);
    }

    private WeekFields getWeekFieldsForWeekStart() {
        // use ISO (week start at monday) or SUNDAY_START WeekFields depending on configuration
        return document.getOptions().isWeekStartSunday() ? WeekFields.SUNDAY_START : WeekFields.ISO;
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
        if (document.isFilterEnabled()) {
            ExerciseFilter currentFilter = document.getCurrentFilter();

            // merge filter date
            if (currentFilter.getDateStart().isAfter(filter.getDateStart())) {
                filter.setDateStart(currentFilter.getDateStart());
            }
            if (currentFilter.getDateEnd().isBefore(filter.getDateEnd())) {
                filter.setDateEnd(currentFilter.getDateEnd());
            }

            // merge sport type and subtype filter
            if (currentFilter.getSportType() != null) {

                if (filter.getSportType() != null &&
                        !currentFilter.getSportType().equals(filter.getSportType())) {
                    // filters have different sport types => add a not existing sport type, so nothing will be found 
                    filter.setSportType(new SportType(Integer.MIN_VALUE));
                } else {
                    filter.setSportType(currentFilter.getSportType());

                    if (currentFilter.getSportSubType() != null) {
                        filter.setSportSubType(currentFilter.getSportSubType());
                    }
                }
            }

            // merge intensity filter
            if (currentFilter.getIntensity() != null) {
                filter.setIntensity(currentFilter.getIntensity());
            }

            // merge equipment filter
            if (currentFilter.getEquipment() != null) {
                if (filter.getEquipment() != null &&
                        !currentFilter.getEquipment().equals(filter.getEquipment())) {
                    // filters have different equipments => add a not existing equipment, so nothing will be found 
                    filter.setEquipment(new Equipment(Integer.MIN_VALUE));
                } else {
                    filter.setEquipment(currentFilter.getEquipment());
                }
            }

            // merge comment filter
            if (currentFilter.getCommentSubString() != null) {
                filter.setCommentSubString(currentFilter.getCommentSubString());
                filter.setRegularExpressionMode(currentFilter.isRegularExpressionMode());
            }
        }
    }

    /**
     * Returns the average weight value for all Weight entries in the time range
     * of the specified ExerciseFilter.
     *
     * @param filter the ExerciseFilter with the time range to be used
     * @return the average weight value or 0 when no Weight entries found
     */
    private double getAverageWeightInTimeRange(ExerciseFilter filter) {
        List<Weight> weightsInTimeRange =
                document.getWeightList().getEntriesInDateRange(
                        filter.getDateStart(), filter.getDateEnd());

        if (weightsInTimeRange.isEmpty()) {
            return 0;
        }

        double weightSum = 0;
        for (Weight weight : weightsInTimeRange) {
            weightSum += weight.getValue();
        }
        return weightSum / (double) weightsInTimeRange.size();
    }

    /**
     * Action for closing this dialog.
     */
    @Action(name = ACTION_CLOSE)
    public void close() {
        this.dispose();
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
        cbTimeRange = new javax.swing.JComboBox<>();
        spYear = new javax.swing.JSpinner();
        pDiagram = new javax.swing.JPanel();
        laOptions = new javax.swing.JLabel();
        laDisplay = new javax.swing.JLabel();
        cbDisplay = new javax.swing.JComboBox<>();
        laFor = new javax.swing.JLabel();
        cbSportTypeMode = new javax.swing.JComboBox<>();
        separator = new javax.swing.JSeparator();
        cbSportTypeList = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("st.dlg.overview"); // NOI18N
        setResizable(false);

        btClose.setText("_Close");
        btClose.setName("btClose"); // NOI18N

        laTimeRange.setFont(laTimeRange.getFont().deriveFont(laTimeRange.getFont().getStyle() | java.awt.Font.BOLD));
        laTimeRange.setText("_Displayed Time Range");
        laTimeRange.setName("st.dlg.overview.time_range"); // NOI18N

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

        cbDisplay.setName("cbDisplay"); // NOI18N

        laFor.setText("_for");
        laFor.setName("st.dlg.overview.for"); // NOI18N

        cbSportTypeMode.setName("cbSportTypeMode"); // NOI18N

        separator.setName("separator"); // NOI18N

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

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[]{cbTimeRange, spYear});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClose;
    private javax.swing.JComboBox<String> cbDisplay;
    private javax.swing.JComboBox<String> cbSportTypeList;
    private javax.swing.JComboBox<OverviewType> cbSportTypeMode;
    private javax.swing.JComboBox<String> cbTimeRange;
    private javax.swing.JLabel laDisplay;
    private javax.swing.JLabel laFor;
    private javax.swing.JLabel laOptions;
    private javax.swing.JLabel laTimeRange;
    private javax.swing.JPanel pDiagram;
    private javax.swing.JSeparator separator;
    private javax.swing.JSpinner spYear;
    // End of variables declaration//GEN-END:variables

    /**
     * This is the list of possible overview types to draw in diagram.
     */
    private enum OverviewType {
        EACH_SPLITTED, EACH_STACKED, ALL_SUMMARY;

        /**
         * Static resource reader is needed for string creation.
         */
        private static ResourceReader resReader;

        public static void setResReader(ResourceReader resReader) {
            OverviewType.resReader = resReader;
        }

        /**
         * Returns the translated name (to be displayed) of this overview type.
         *
         * @return name of this axis type
         */
        @Override
        public String toString() {
            switch (this) {
                case EACH_STACKED:
                    return OverviewType.resReader.getString("st.dlg.overview.sport_type.each_stacked.text");
                case EACH_SPLITTED:
                    return OverviewType.resReader.getString("st.dlg.overview.sport_type.each_splitted.text");
                case ALL_SUMMARY:
                    return OverviewType.resReader.getString("st.dlg.overview.sport_type.all_summary.text");
                default:
                    return "???";
            }
        }
    }
}
