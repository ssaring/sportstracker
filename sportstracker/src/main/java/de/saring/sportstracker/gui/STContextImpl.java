package de.saring.sportstracker.gui;

import de.saring.util.ResourceReader;
import de.saring.util.unitcalc.FormatUtils;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.SingleFrameApplication;

/**
 * Implementation of the GUI context of the SportsTracker application. It contains 
 * the ApplicationContext of the Swing Application Framework and some helper methods,
 * e.g. displaying dialogs and for accessing the main frame.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class STContextImpl implements STContext {

    private ApplicationContext appContext;

    /** The helper class for reading application resources. */
    private ResourceReader resReader;
    
    /** The format utils for the current unit system. */
    private FormatUtils formatUtils;

    /**
     * Standard c'tor.
     * @param appContext the ApplicationContext of the Swing Application Framework
     */
    public STContextImpl (ApplicationContext appContext) {
        this.appContext = appContext;

        // initialize the I18N helper class
        this.resReader = new ResourceReader (appContext.getResourceMap ());
    }

    /** {@inheritDoc} */
    public ApplicationContext getSAFContext () {
        return appContext;
    }

    /** {@inheritDoc} */
    public JFrame getMainFrame () {
        return getApplication ().getMainFrame ();
    }
    
    /** {@inheritDoc} */
    public void showDialog (JDialog dlg) {
        getApplication ().show (dlg);
    }
    
    /** {@inheritDoc} */
    public void showMessageDialog (Component parent, int msgType, String titleKey, String messageKey, Object... arguments) {
        JOptionPane.showMessageDialog (parent, resReader.getString (messageKey, arguments), 
            resReader.getString (titleKey), msgType);
    }
    
    /** {@inheritDoc} */
    public int showConfirmDialog (Component parent, String titleKey, String messageKey) {
        return JOptionPane.showConfirmDialog (parent, resReader.getString (messageKey), 
            resReader.getString (titleKey), JOptionPane.YES_NO_OPTION);
    }
    
    /** {@inheritDoc} */
    public ResourceReader getResReader () {
        return resReader;
    }
    
    /** {@inheritDoc} */
    public FormatUtils getFormatUtils () {
        return formatUtils;
    }

    /** {@inheritDoc} */
    public void setFormatUtils (FormatUtils formatUtils) {
        this.formatUtils = formatUtils;
    }

    /**
     * This helper methods returns the SingleFrameApplication instance, this is 
     * needed e.g. for displaying dialogs.
     * @return the SingleFrameApplication instance
     */
    private SingleFrameApplication getApplication () {
        return (SingleFrameApplication) appContext.getApplication ();
    }
}
