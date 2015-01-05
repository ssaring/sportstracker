package de.saring.sportstracker.gui.dialogs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import javax.inject.Inject;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.fx.ChartViewer;
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
import de.saring.util.AppResources;
import de.saring.util.Date310Utils;
import de.saring.util.data.IdObjectList;
import de.saring.util.gui.javafx.NameableStringConverter;
import de.saring.util.gui.jfreechart.ChartUtils;
import de.saring.util.gui.jfreechart.StackedRenderer;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Controller (MVC) class of the Overview dialog of the SportsTracker application.
 * This dialog contains a diagram which displays all the exercises or weight entries in
 * various diagram graph types. The user can select the displayed time range (e.g. all
 * months of the selected year or the last 10 years until the selected year).
 *
 * @author Stefan Saring
 */
public class OverviewDialogController extends AbstractDialogController {

    private final STDocument document;

    /** The viewer for the chart. */
    private ChartViewer chartViewer;

    @FXML
    private ChoiceBox<TimeRangeType> cbTimeRange;
    @FXML
    private ChoiceBox<Integer> cbYear;
    @FXML
    private ChoiceBox<ValueType> cbDisplay;
    @FXML
    private ChoiceBox<OverviewType> cbSportTypeMode;
    @FXML
    private ChoiceBox<SportType> cbSportTypeList;

    @FXML
    private StackPane spDiagram;

    @FXML
    private HBox hBoxOptions;
    @FXML
    private HBox hBoxSportTypeMode;
    @FXML
    private HBox hBoxSportTypeList;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     */
    @Inject
    public OverviewDialogController(final STContext context, final STDocument document) {
        super(context);
        this.document = document;

        TimeRangeType.appResources = context.getResources();
        ValueType.appResources = context.getResources();
        OverviewType.appResources = context.getResources();
    }

    /**
     * Displays the Overview dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {

        // display in title when exercise filter is being used
        String dlgTitle = context.getResources().getString("st.dlg.overview.title");
        if (document.isFilterEnabled()) {
            dlgTitle += " " + context.getResources().getString("st.dlg.overview.title.filter");
        }

        showInfoDialog("/fxml/OverviewDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {
        setupChoiceBoxes();
        updateDiagram();
    }

    private void setupChoiceBoxes() {

        // fill choice boxes
        cbTimeRange.getItems().addAll(Arrays.asList(TimeRangeType.values()));
        cbTimeRange.getSelectionModel().select(TimeRangeType.LAST_12_MONTHS);

        cbDisplay.getItems().addAll(Arrays.asList(ValueType.values()));
        cbDisplay.getSelectionModel().select(ValueType.DISTANCE);

        cbSportTypeMode.getItems().addAll(Arrays.asList(OverviewType.values()));
        cbSportTypeMode.getSelectionModel().select(OverviewType.EACH_SPLITTED);

        cbSportTypeList.setConverter(new NameableStringConverter<>());
        document.getSportTypeList().forEach(sportType -> cbSportTypeList.getItems().add(sportType));
        cbSportTypeList.getSelectionModel().select(0);

        // init choice box for year selection, must not be visible for time range type "last 12 months"
        // TODO use spinner control, will be available in JavaFX 9
        for (int i = 1950; i <= 2070; i++) {
            cbYear.getItems().addAll(i);
        }
        cbYear.getSelectionModel().select(Integer.valueOf(LocalDate.now().getYear()));
        cbYear.visibleProperty().bind(Bindings.notEqual(cbTimeRange.valueProperty(), TimeRangeType.LAST_12_MONTHS));

        // set listeners for updating the diagram on selection changes
        cbTimeRange.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbYear.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbDisplay.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbSportTypeMode.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbSportTypeList.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
    }

    /**
     * Draws the Overview diagram according to the current selections.
     */
    private void updateDiagram() {
        updateOptionControls();

        // get selected time range and value type and its name to display
        TimeRangeType timeType = cbTimeRange.getValue();
        ValueType vType = cbDisplay.getValue();
        int year = cbYear.getValue();

        // create a table of all time series (graphs) and the appropriate colors
        TimeTableXYDataset dataset = new TimeTableXYDataset();
        java.util.List<java.awt.Color> lGraphColors = new ArrayList<>();

        // setup TimeSeries in the diagram (done in different ways for all the value types)
        if (vType == ValueType.SPORTSUBTYPE) {
            setupSportSubTypeDiagram(dataset, lGraphColors);
        } else if (vType == ValueType.EQUIPMENT) {
            setupEquipmentDiagram(dataset, lGraphColors);
        } else if (vType == ValueType.WEIGHT) {
            setupWeightDiagram(dataset, lGraphColors);
        } else {
            setupExerciseDiagram(dataset, lGraphColors);
        }

        // create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(null, // Title
                null, // Y-axis label
                vType.toString(), // X-axis label
                dataset, // primary dataset
                true, // display legend
                true, // display tooltips
                false); // URLs

        // render unique filled shapes for each graph
        XYPlot plot = (XYPlot) chart.getPlot();

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);
        // set color for sport type series
        for (int i = 0; i < lGraphColors.size(); i++) {
            java.awt.Color tempColor = lGraphColors.get(i);
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
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(toolTipFormat, new SimpleDateFormat(
                dateFormatTooltip), new DecimalFormat()) {
            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                return dataset.getSeriesKey(series) + ", " + super.generateToolTip(dataset, series, item);
            }
        });

        // special handling for overview type EACH_STACKED, it uses a special renderer
        // (only for exercises based value types, stacked mode can be selected only there)
        if (cbSportTypeMode.isVisible() && cbSportTypeMode.getValue() == OverviewType.EACH_STACKED) {

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
                java.awt.Color tempColor = lGraphColors.get(i);
                stackedRenderer.setSeriesPaint(i, tempColor);
            }
            stackedRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(toolTipFormat, new SimpleDateFormat(
                    dateFormatTooltip), new DecimalFormat()) {
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
            newYearMarker.setPaint(new java.awt.Color(0x00b000));
            newYearMarker.setStroke(new java.awt.BasicStroke(0.8f));
            newYearMarker.setLabel(String.valueOf(firstDayOfYear.getYear()));
            newYearMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            newYearMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            plot.addDomainMarker(newYearMarker);
        }

        // display legend next to the diagram on the right side
        chart.getLegend().setPosition(RectangleEdge.RIGHT);

        ChartUtils.customizeChart(chart);

        // display chart in viewer (chart viewer will be initialized lazily)
        if (chartViewer == null) {
            chartViewer = new ChartViewer(chart);
            spDiagram.getChildren().addAll(chartViewer);
        } else {
            chartViewer.setChart(chart);
        }
    }

    private void updateOptionControls() {
        final ValueType selectedValueType = cbDisplay.getValue();

        // the sport type mode selection must not be visible for the ValueType SPORTSUBTYPE, EQUIPMENT and WEIGHT
        final boolean sportTypeModeVisible = selectedValueType != ValueType.SPORTSUBTYPE
                && selectedValueType != ValueType.EQUIPMENT && selectedValueType != ValueType.WEIGHT;

        // the sport type list selection must only be visible for the ValueType SPORTSUBTYPE and EQUIPMENT
        final boolean sportTypeListVisible = selectedValueType == ValueType.SPORTSUBTYPE
                || selectedValueType == ValueType.EQUIPMENT;

        // add or remove sport type mode selection depending on visibility and current state
        if (sportTypeModeVisible && !hBoxOptions.getChildren().contains(hBoxSportTypeMode)) {
            hBoxOptions.getChildren().add(hBoxSportTypeMode);
        }
        if (!sportTypeModeVisible && hBoxOptions.getChildren().contains(hBoxSportTypeMode)) {
            hBoxOptions.getChildren().remove(hBoxSportTypeMode);
        }

        // add or remove sport type list selection depending on visibility and current state
        if (sportTypeListVisible && !hBoxOptions.getChildren().contains(hBoxSportTypeList)) {
            hBoxOptions.getChildren().add(hBoxSportTypeList);
        }
        if (!sportTypeListVisible && hBoxOptions.getChildren().contains(hBoxSportTypeList)) {
            hBoxOptions.getChildren().remove(hBoxSportTypeList);
        }
    }

    /**
     * Sets up the diagram for exercise data.
     *
     * @param dataset the XY dataset to be filled
     * @param graphColors list of graph colors, can be filled with preferred colors
     */
    private void setupExerciseDiagram(TimeTableXYDataset dataset, java.util.List<java.awt.Color> graphColors) {

        // get time range and value type to display
        TimeRangeType timeType = cbTimeRange.getValue();
        int year = cbYear.getValue();
        ValueType vType = cbDisplay.getValue();
        OverviewType overviewType = cbSportTypeMode.getValue();

        // which sport type mode is selected by user ?
        if (overviewType != OverviewType.EACH_SPLITTED) {
            // create one graph for sum of all sport types
            addExerciseTimeSeries(dataset, timeType, year, vType, null);
            graphColors.add(new java.awt.Color(0xff0000));
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
    private void addExerciseTimeSeries(TimeTableXYDataset dataset, TimeRangeType timeType, int year,
            ValueType valueType, SportType sportType) {

        // create the time series for specified time range and sport type
        String seriesName = sportType != null ? sportType.getName() : context.getResources().getString(
                "st.dlg.overview.graph.all_types");

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
                    if (options.getUnitSystem() != FormatUtils.UnitSystem.Metric) {
                        sumDistance = ConvertUtils.convertKilometer2Miles(sumDistance, false);
                    }
                    dataset.add(timePeriod, sumDistance, seriesName);
                    break;

                case DURATION:
                    // calculate duration in hours
                    dataset.add(timePeriod, sumDuration / 3600d, seriesName);
                    break;

                case ASCENT:
                    if (options.getUnitSystem() != FormatUtils.UnitSystem.Metric) {
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
                    if (options.getUnitSystem() != FormatUtils.UnitSystem.Metric) {
                        sumDistance = ConvertUtils.convertKilometer2Miles(sumDistance, false);
                    }

                    double averageSpeed = sumDistance / (sumDuration / 3600d);

                    // calculate the speed value depending on current speed unit view
                    if (options.getSpeedView() == FormatUtils.SpeedView.MinutesPerDistance) {
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
    private void setupSportSubTypeDiagram(TimeTableXYDataset dataset, java.util.List<java.awt.Color> graphColors) {

        // get time range to display
        TimeRangeType timeType = cbTimeRange.getValue();
        int year = cbYear.getValue();

        // get selected sport type
        SportType sportType = cbSportTypeList.getValue();

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
                // sum only exercises with same sport subtype (otherwise conflicts with the merged filter set in the
                // view)
                if (sportSubType.equals(tempExercise.getSportSubType())) {
                    sumDistance += tempExercise.getDistance();
                }
            }

            // convert to english unit mode when enabled
            if (document.getOptions().getUnitSystem() != FormatUtils.UnitSystem.Metric) {
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
    private void setupEquipmentDiagram(TimeTableXYDataset dataset, java.util.List<java.awt.Color> graphColors) {

        // get time range to display
        TimeRangeType timeType = cbTimeRange.getValue();
        int year = cbYear.getValue();

        // get selected sport type
        SportType sportType = cbSportTypeList.getValue();

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
     * @param equipment the equipment to be shown in this series (when null, then calculate exercises with no equipment
     *            assigned only)
     */
    private void addEquipmentTimeSeries(TimeTableXYDataset dataset, TimeRangeType timeType, int year,
            SportType sportType, Equipment equipment) {

        String seriesName = equipment != null ? equipment.getName() : context.getResources().getString(
                "st.dlg.overview.equipment.not_specified");

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
            if (document.getOptions().getUnitSystem() != FormatUtils.UnitSystem.Metric) {
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
    private void setupWeightDiagram(TimeTableXYDataset dataset, java.util.List<java.awt.Color> graphColors) {

        // get time range to display
        TimeRangeType timeType = cbTimeRange.getValue();
        int year = cbYear.getValue();

        addWeightTimeSeries(dataset, timeType, year);
        graphColors.add(new java.awt.Color(0xff0000));
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

        String seriesName = context.getResources().getString("st.dlg.overview.display.weight.text");

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
            if (document.getOptions().getUnitSystem() != FormatUtils.UnitSystem.Metric) {
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
                throw new IllegalArgumentException("Unknown TimeRangeType!");
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
    private RegularTimePeriod createTimePeriodForTimeStep(TimeRangeType timeType, int year, int timeStep) {

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
    private ExerciseFilter createExerciseFilterForTimeStep(TimeRangeType timeType, int year, int timeStep) {

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
    private void addCustomGraphColors(java.util.List<java.awt.Color> graphColors) {
        graphColors.add(new java.awt.Color(0xff5555));
        graphColors.add(new java.awt.Color(0x5555ff));
        graphColors.add(new java.awt.Color(0x3acc2e));
        graphColors.add(new java.awt.Color(0xff8000));
        graphColors.add(new java.awt.Color(0xff55ff));
        graphColors.add(new java.awt.Color(0x31d5d5));
        graphColors.add(new java.awt.Color(0xdc8686));
        graphColors.add(new java.awt.Color(0x808080));
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

                if (filter.getSportType() != null && !currentFilter.getSportType().equals(filter.getSportType())) {
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
                if (filter.getEquipment() != null && !currentFilter.getEquipment().equals(filter.getEquipment())) {
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
        java.util.List<Weight> weightsInTimeRange = document.getWeightList().getEntriesInDateRange(
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
     * This is the list of possible time ranges displayed in diagram.
     * This enum also provides the localized displayed enum names.
     */
    private enum TimeRangeType {
        /**
         * In total 13 months: current month and last 12 before (good
         * for compare current month and the one from year before).
         */
        LAST_12_MONTHS("st.dlg.overview.time_range.last_12_months.text"), MONTHS_OF_YEAR(
                "st.dlg.overview.time_range.months_of_year.text"), WEEKS_OF_YEAR(
                "st.dlg.overview.time_range.weeks_of_year.text"), LAST_10_YEARS(
                "st.dlg.overview.time_range.ten_years.text");

        private static AppResources appResources;

        private String resourceKey;

        private TimeRangeType(final String resourceKey) {
            this.resourceKey = resourceKey;
        }

        @Override
        public String toString() {
            return appResources.getString(resourceKey);
        }
    }

    /**
     * This is the list of possible value types displayed in diagram.
     * This enum also provides the localized displayed enum names.
     */
    private enum ValueType {
        DISTANCE("st.dlg.overview.display.distance_sum.text"), DURATION("st.dlg.overview.display.duration_sum.text"), ASCENT(
                "st.dlg.overview.display.ascent_sum.text"), CALORIES("st.dlg.overview.display.calorie_sum.text"), EXERCISES(
                "st.dlg.overview.display.exercise_count.text"), AVG_SPEED("st.dlg.overview.display.avg_speed.text"), SPORTSUBTYPE(
                "st.dlg.overview.display.sportsubtype_distance.text"), EQUIPMENT(
                "st.dlg.overview.display.equipment_distance.text"), WEIGHT("st.dlg.overview.display.weight.text");

        private static AppResources appResources;

        private String resourceKey;

        private ValueType(final String resourceKey) {
            this.resourceKey = resourceKey;
        }

        @Override
        public String toString() {
            return appResources.getString(resourceKey);
        }
    }

    /**
     * This is the list of possible overview types to draw in diagram.
     * This enum also provides the localized displayed enum names.
     */
    private enum OverviewType {
        EACH_SPLITTED("st.dlg.overview.sport_type.each_splitted.text"), EACH_STACKED(
                "st.dlg.overview.sport_type.each_stacked.text"), ALL_SUMMARY(
                "st.dlg.overview.sport_type.all_summary.text");

        private static AppResources appResources;

        private String resourceKey;

        private OverviewType(final String resourceKey) {
            this.resourceKey = resourceKey;
        }

        @Override
        public String toString() {
            return appResources.getString(resourceKey);
        }
    }
}
