package de.saring.sportstracker.gui;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.util.unitcalc.FormatUtils;

/**
 * This is the GUI context of the SportsTracker application, it enhances the context
 * of the ExerciseViewer sub-application. It provides access to the main UI components
 * (e.g. application window) and common helper methods (e.g. displaying dialogs).
 *
 * @author Stefan Saring
 */
public interface STContext extends EVContext {

    /**
     * Sets the format utils class for the current unit system which needs to be used
     * by the entire application.
     *
     * @param formatUtils the FormatUtils instance to set
     */
    void setFormatUtils(FormatUtils formatUtils);
}
