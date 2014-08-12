package de.saring.sportstracker.gui;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.util.AppResources;
import de.saring.util.unitcalc.FormatUtils;
import javafx.stage.Stage;

/**
 * This is the GUI context of the SportsTracker application, it enhances the context
 * of the ExerciseViewer sub-application. It contains the ApplicationContext of the Swing
 * Application Framework and some helper methods, e.g. displaying dialogs and for
 * accessing the main frame.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public interface STContext extends EVContext {

    /**
     * Sets the format utils class for the current unit system which needs to be used
     * by the entire application.
     *
     * @param formatUtils the FormatUtils instance to set
     */
    void setFormatUtils(FormatUtils formatUtils);

    /**
     * Returns the primary stage (main window) of the JavaFX application.
     *
     * @return Stage
     */
    Stage getPrimaryStage();

    /**
     * Returns the provider of application text resources for the JavaFX based UI.
     *
     * @return AppResources
     */
    AppResources getFxResources();
}
