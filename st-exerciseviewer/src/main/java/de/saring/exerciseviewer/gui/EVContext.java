package de.saring.exerciseviewer.gui;

import de.saring.util.ResourceReader;
import de.saring.util.unitcalc.FormatUtils;
import javafx.stage.*;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * This is the GUI context of the ExerciseViewer sub-application. It contains the
 * ApplicationContext of the Swing Application Framework and some helper methods.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public interface EVContext {

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
     * Displays the specified dialog. This will be delegated to SingleFrameApplication.show(),
     * so the benefits of the Swing Application Framework can be used.
     *
     * @param dlg the dialog to display
     */
    void showDialog(JDialog dlg);

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
     * Displays a JavaFX modal message dialog of type Error with the specified message title and message
     * for the specified parent window.
     *
     * @param parent the parent component of the message dialog
     * @param titleKey the resource key for the dialog title text
     * @param messageKey the resource key for the message text
     */
    void showFxErrorDialog(javafx.stage.Window parent, String titleKey, String messageKey);

    /**
     * Returns the helper class for reading resources from the applications properties
     * files for the current locale.
     *
     * @return the helper class for reading resources
     */
    ResourceReader getResReader();

    /**
     * Returns the format utils class for the current unit system.
     *
     * @return the current FormatUtils instance
     */
    FormatUtils getFormatUtils();
}
