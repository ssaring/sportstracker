package de.saring.exerciseviewer.core

import de.saring.util.unitcalc.FormatUtils.UnitSystem

/**
 * This interface defines all required options by the ExerciseViewer application.
 * The implementation must be done by the application which uses ExerciseViewer.
 *
 * @author Stefan Saring
 */
interface EVOptions {

    /**
     * The unit system to be used in the GUI.
     */
    val unitSystem: UnitSystem

    /**
     * This flag is true, when the second chart should always be displayed in the diagram panel (data must be available).
     */
    val isDisplaySecondChart: Boolean

    /**
     * The flag for display smoothed charts for all value types (heartrate, speed, cadence) in the Exercise Viewer
     * diagram panel. If set to true then a average filter will be used to smooth the charts and make the diagram
     * more readable.
     */
    val isDisplaySmoothedCharts: Boolean
}
