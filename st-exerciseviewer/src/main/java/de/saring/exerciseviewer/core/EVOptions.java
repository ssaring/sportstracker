package de.saring.exerciseviewer.core;

import de.saring.util.unitcalc.FormatUtils.SpeedView;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;

/**
 * This interface defines all required options by the ExerciseViewer application.
 * The implementation must be done by the application which uses ExerciseViewer.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public interface EVOptions {

    /**
     * Returns the unit system to be used in the GUI.
     *
     * @return the unit system to be used in the GUI
     */
    public UnitSystem getUnitSystem();

    /**
     * The returned flag is true, when the second chart should always be displayed in the diagram panel (data must be
     * available).
     *
     * @return the flag for displaying the second chart
     */
    public boolean isDisplaySecondChart();

    /**
     * Returns the flag for display smoothed charts for all value types (heartrate, speed, cadence) in the Exercise
     * Viewer diagram panel. If set to true then a average filter will be used to smooth the charts and make the diagram
     * more readable.
     *
     * @return the flag for displaying smoothed charts
     */
    public boolean isDisplaySmoothedCharts();

    /**
     * Returns the speed view system to be used in GUI.
     *
     * @return the speed view system to be used in the GUI
     */
    public SpeedView getSpeedView();
}

