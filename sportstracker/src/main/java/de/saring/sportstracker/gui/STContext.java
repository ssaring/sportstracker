package de.saring.sportstracker.gui;

import com.google.inject.ImplementedBy;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.util.unitcalc.FormatUtils;

/**
 * This is the GUI context of the SportsTracker application, it enhances the context
 * of the ExerciseViewer sub-application. It contains the ApplicationContext of the Swing 
 * Application Framework and some helper methods, e.g. displaying dialogs and for 
 * accessing the main frame.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
@ImplementedBy(STContextImpl.class)
public interface STContext extends EVContext {

    /**
     * Sets the format utils class for the current unit system which needs to be used
     * by the entire application.
     * @param formatUtils the FormatUtils instance to set
     */
    void setFormatUtils (FormatUtils formatUtils);
}
