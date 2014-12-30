package de.saring.sportstracker.gui;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.util.ResourceReader;
import de.saring.util.unitcalc.FormatUtils;
import org.jdesktop.application.ApplicationContext;

import javax.swing.JFrame;
import java.awt.Component;

/**
 * This is the GUI context of the SportsTracker application, it enhances the context
 * of the ExerciseViewer sub-application. It contains the ApplicationContext of the Swing
 * Application Framework and some helper methods, e.g. displaying dialogs and for
 * accessing the main frame.
 *
 * @author Stefan Saring
 */
public interface STContext extends EVContext {

    /**
     * Returns the ApplicationContext of the Swing Application Framework.
     *
     * @return the ApplicationContext
     */
    ApplicationContext getSAFContext();

    /**
     * Returns the main frame of the application (e.g. needed for dialog creation).
     *
     * @return the main frame of the application
     */
    JFrame getMainFrame();

    /**
     * Displays a modal message dialog with the specified message text, title and
     * message type for the specified parent component.
     *
     * @param parent the parent component of the message dialog
     * @param msgType message type of dialog (use constants of JOptionPane)
     * @param titleKey the resource key for the dialog title text
     * @param messageKey the resource key for the message text
     * @param arguments list of objects which needs to be inserted in the message text (optional)
     */
    void showMessageDialog(Component parent, int msgType, String titleKey, String messageKey, Object... arguments);

    /**
     * Displays a modal confirmation dialog with the specified message text and title
     * for the specified parent component.
     *
     * @param parent the parent component of the message dialog
     * @param titleKey the resource key for the dialog title text
     * @param messageKey the resource key for the message text
     * @return an int indicating the option selected by the user
     */
    int showConfirmDialog(Component parent, String titleKey, String messageKey);

    /**
     * Returns the helper class for reading resources from the applications properties
     * files for the current locale.
     *
     * @return the helper class for reading resources
     */
    ResourceReader getResReader();

    /**
     * Sets the format utils class for the current unit system which needs to be used
     * by the entire application.
     *
     * @param formatUtils the FormatUtils instance to set
     */
    void setFormatUtils(FormatUtils formatUtils);
}
