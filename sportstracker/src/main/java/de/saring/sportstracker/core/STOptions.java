package de.saring.sportstracker.core;

import de.saring.exerciseviewer.core.EVOptions;
import de.saring.util.unitcalc.FormatUtils.SpeedView;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;

import java.io.Serializable;

/**
 * This class contains all options of the SportsTracker application, it also implements the
 * ExerciseViewer options interface. These options will be persisted by using XmlBeanStorage
 * (which uses the Java Beans Persistence classes XMLEncoder and XMLDecoder).
 *
 * @author Stefan Saring
 */
public class STOptions implements EVOptions, Serializable {

    private static final long serialVersionUID = 6756725362338738970L;

    /**  This is the list of possible views. */
    public enum View {
        Calendar, List
    }

    /** This is the list of possible automatic calculation values. */
    public enum AutoCalculation {
        Distance, AvgSpeed, Duration
    }

    /** This is the initial view of the GUI (at startup). */
    private View initialView;

    /** This is the unit system used in GUI. */
    private UnitSystem unitSystem;

    /** This is the speed view system used in GUI. */
    private SpeedView speedView;

    /** This is the value which will be calculated automatically by default. */
    private AutoCalculation defaultAutoCalcuation;

    /** If this flag is true, then the data will be saved automatically on exit (no confirmation dialog).
     */
    private boolean saveOnExit;

    /**
     * If this flag is true, then the second chart will always be displayed in the ExerciseViewer diagram panel (data
     * must be available).
     */
    private boolean displaySecondChart;

    /**
     * Flag for display smoothed charts for all value types (heartrate, speed, cadence) in the ExerciseViewer diagram
     * panel. If set to true then a average filter will be used to smooth the charts and make the diagram more readable.
     */
    private boolean displaySmoothedCharts;

    /** If this flag is true, then the week in the calendar starts with sunday, otherwise monday. */
    private boolean weekStartSunday;

    /** The list view shows the average heartrate when this flag is true. */
    private boolean listViewShowAvgHeartrate;

    /** The list view shows the ascent when this flag is true. */
    private boolean listViewShowAscent;

    /** The list view shows the descent when this flag is true. */
    private boolean listViewShowDescent;

    /** The list view shows the consumed energy when this flag is true. */
    private boolean listViewShowEnergy;

    /** The list view shows the equipment when this flag is true. */
    private boolean listViewShowEquipment;

    /** The list view shows the comment when this flag is true. */
    private boolean listViewShowComment;

    /**
     * The directory of the previous opened HRM exercise file, so the user must not go to this directory every time
     * again (optional, can be null).
     */
    private String previousExerciseDirectory;

    /**
     * Creates an instance of STOptions filled with default values.
     */
    public STOptions() {
        this.initialView = View.Calendar;
        this.unitSystem = UnitSystem.Metric;
        this.speedView = SpeedView.DistancePerHour;
        this.defaultAutoCalcuation = AutoCalculation.Duration;
        this.saveOnExit = false;
        this.displaySecondChart = false;
        this.displaySmoothedCharts = true;
        this.weekStartSunday = false;
        this.listViewShowAvgHeartrate = true;
        this.listViewShowAscent = true;
        this.listViewShowDescent = false;
        this.listViewShowEnergy = false;
        this.listViewShowEquipment = false;
        this.listViewShowComment = false;
        this.previousExerciseDirectory = null;
    }

    public View getInitialView() {
        return initialView;
    }

    public void setInitialView(View initialView) {
        this.initialView = initialView;
    }

    public UnitSystem getUnitSystem() {
        return unitSystem;
    }

    public void setUnitSystem(UnitSystem unitSystem) {
        this.unitSystem = unitSystem;
    }

    public SpeedView getSpeedView() {
        return speedView;
    }

    public void setSpeedView(SpeedView speedView) {
        this.speedView = speedView;
    }

    public AutoCalculation getDefaultAutoCalcuation() {
        // set to 'duration' when not set yet (can happen when updating from previous version)
        if (defaultAutoCalcuation == null) {
            defaultAutoCalcuation = AutoCalculation.Duration;
        }
        return defaultAutoCalcuation;
    }

    public void setDefaultAutoCalcuation(AutoCalculation defaultAutoCalcuation) {
        this.defaultAutoCalcuation = defaultAutoCalcuation;
    }

    public boolean isSaveOnExit() {
        return saveOnExit;
    }

    public void setSaveOnExit(boolean saveOnExit) {
        this.saveOnExit = saveOnExit;
    }

    public boolean isDisplaySecondChart() {
        return displaySecondChart;
    }

    public void setDisplaySecondChart(boolean displaySecondChart) {
        this.displaySecondChart = displaySecondChart;
    }

    public boolean isDisplaySmoothedCharts() {
        return displaySmoothedCharts;
    }

    public void setDisplaySmoothedCharts(boolean displaySmoothedCharts) {
        this.displaySmoothedCharts = displaySmoothedCharts;
    }

    public boolean isWeekStartSunday() {
        return weekStartSunday;
    }

    public void setWeekStartSunday(boolean weekStartSunday) {
        this.weekStartSunday = weekStartSunday;
    }

    public boolean isListViewShowAvgHeartrate() {
        return listViewShowAvgHeartrate;
    }

    public void setListViewShowAvgHeartrate(boolean listViewShowAvgHeartrate) {
        this.listViewShowAvgHeartrate = listViewShowAvgHeartrate;
    }

    public boolean isListViewShowAscent() {
        return listViewShowAscent;
    }

    public void setListViewShowAscent(boolean listViewShowAscent) {
        this.listViewShowAscent = listViewShowAscent;
    }

    public boolean isListViewShowDescent() {
        return listViewShowDescent;
    }

    public void setListViewShowDescent(boolean listViewShowDescent) {
        this.listViewShowDescent = listViewShowDescent;
    }

    public boolean isListViewShowEnergy() {
        return listViewShowEnergy;
    }

    public void setListViewShowEnergy(boolean listViewShowEnergy) {
        this.listViewShowEnergy = listViewShowEnergy;
    }

    public boolean isListViewShowEquipment() {
        return listViewShowEquipment;
    }

    public void setListViewShowEquipment(boolean listViewShowEquipment) {
        this.listViewShowEquipment = listViewShowEquipment;
    }

    public boolean isListViewShowComment() {
        return listViewShowComment;
    }

    public void setListViewShowComment(boolean listViewShowComment) {
        this.listViewShowComment = listViewShowComment;
    }

    public String getPreviousExerciseDirectory() {
        return previousExerciseDirectory;
    }

    public void setPreviousExerciseDirectory(String previousExerciseDirectory) {
        this.previousExerciseDirectory = previousExerciseDirectory;
    }
}

