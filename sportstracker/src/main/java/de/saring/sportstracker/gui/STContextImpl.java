package de.saring.sportstracker.gui;

import de.saring.util.AppResources;
import de.saring.util.ResourceReader;
import de.saring.util.unitcalc.FormatUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.SingleFrameApplication;

import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Implementation of the GUI context of the SportsTracker application. It contains
 * the ApplicationContext of the Swing Application Framework and some helper methods,
 * e.g. displaying dialogs and for accessing the main frame.
 *
 * @author Stefan Saring
 * @version 1.0
 */
@Singleton
public class STContextImpl implements STContext {

    private final ApplicationContext appContext;

    private final STFXApplication fxApplication;

    /** The helper class for reading application resources (for the Swing Application Framework UI). */
    private final ResourceReader resReader;

    /** The provider of application text resources for the JavaFX based UI. */
    private AppResources fxResources;

    /**
     * The format utils for the current unit system.
     */
    private FormatUtils formatUtils;

    /**
     * Standard c'tor.
     *
     * @param appContext the ApplicationContext of the Swing Application Framework
     * @param fxApplication the JavaFX Application instance
     */
    public STContextImpl(ApplicationContext appContext, STFXApplication fxApplication) {
        this.appContext = appContext;
        this.fxApplication = fxApplication;

        // initialize the I18N helper classes
        this.resReader = new ResourceReader(appContext.getResourceMap());
        this.fxResources = new AppResources("de.saring.sportstracker.gui.resources.SportsTracker");
    }

    @Override
    public ApplicationContext getSAFContext() {
        return appContext;
    }

    @Override
    public JFrame getMainFrame() {
        return getApplication().getMainFrame();
    }

    @Override
    public void showMessageDialog(Component parent, int msgType, String titleKey, String messageKey, Object... arguments) {
        JOptionPane.showMessageDialog(parent, resReader.getString(messageKey, arguments),
                resReader.getString(titleKey), msgType);
    }

    @Override
    public int showConfirmDialog(Component parent, String titleKey, String messageKey) {
        return JOptionPane.showConfirmDialog(parent, resReader.getString(messageKey),
                resReader.getString(titleKey), JOptionPane.YES_NO_OPTION);
    }

    @Override
    public Optional<ButtonType> showFxMessageDialog(final javafx.stage.Window parent, final Alert.AlertType alertType,
            final String titleKey, final String messageKey, final Object... arguments) {

        final String message = fxResources.getString(messageKey, arguments);
        final Alert alert = new Alert(alertType, message);
        alert.initOwner(parent);
        alert.setTitle(fxResources.getString(titleKey));
        alert.setHeaderText(null);
        // TODO remove when fixed in OpenJFX-Dialogs
        alert.setResizable(true);
        // workaround for disabling bigger font size of custom dialog content
        alert.getDialogPane().setStyle("-fx-font-size: 1em;");
        return alert.showAndWait();
    }

    @Override
    public Optional<String> showFxTextInputDialog(final javafx.stage.Window parent, final String titleKey,
            final String messageKey, final String initialValue) {

        final TextInputDialog inputDlg = new TextInputDialog(initialValue == null ? "" : initialValue);
        inputDlg.initOwner(parent);
        inputDlg.setTitle(getFxResources().getString(titleKey));
        inputDlg.setContentText(getFxResources().getString(messageKey));
        inputDlg.setHeaderText(null);
        // TODO remove when fixed in OpenJFX-Dialogs
        // workaround for disabling bigger font size of custom dialog content
        inputDlg.getDialogPane().setStyle("-fx-font-size: 1em;");
        return inputDlg.showAndWait();
    }

    @Override
    public ResourceReader getResReader() {
        return resReader;
    }

    @Override
    public FormatUtils getFormatUtils() {
        return formatUtils;
    }

    @Override
    public void setFormatUtils(FormatUtils formatUtils) {
        this.formatUtils = formatUtils;
    }

    @Override
    public Stage getPrimaryStage() {
        return fxApplication.getPrimaryStage();
    }

    @Override
    public AppResources getFxResources() {
        return fxResources;
    }

    /**
     * This helper methods returns the SingleFrameApplication instance, this is
     * needed e.g. for displaying dialogs.
     *
     * @return the SingleFrameApplication instance
     */
    private SingleFrameApplication getApplication() {
        return (SingleFrameApplication) appContext.getApplication();
    }
}
