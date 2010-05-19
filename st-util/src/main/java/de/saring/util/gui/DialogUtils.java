package de.saring.util.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;


/**
 * This utility class contains helper methods for typical dialog features.
 * 
 * @author Stefan Saring
 */
public final class DialogUtils {

    private DialogUtils () {
    }
    
    /**
     * Sets the action to be started when the user presses the Escape key in the specified dialog.
     *
     * @param dialog the dialog for the action
     * @param action the action when Escape key is pressed 
     */
    public static void setDialogEscapeKeyAction(JDialog dialog, ActionListener action) {
        dialog.getRootPane().registerKeyboardAction(action,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}
