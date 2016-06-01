package de.saring.sportstracker.gui;

import java.util.Optional;

import javafx.application.HostServices;
import javafx.event.EventDispatcher;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.util.AppResources;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Implementation of the GUI context of the SportsTracker application.
 *
 * @author Stefan Saring
 */
@Singleton
public class STContextImpl implements STContext {

    private final STApplication application;

    /** The provider of application text resources. */
    private AppResources fxResources;

    /** The format utils for the current unit system. */
    private FormatUtils formatUtils;

    /** The default Stage event dispatcher (is needed for unblocking). */
    private EventDispatcher primaryStageEventDispatcher = null;

    /** Event dispatches implementation which blocks all events from processing. */
    private final EventDispatcher blockingEventDispatcher = (event, tail) -> {
        event.consume();
        return null;
    };

    /**
     * Standard c'tor.
     *
     * @param application the JavaFX Application instance
     */
    @Inject
    public STContextImpl(STApplication application) {
        this.application = application;

        // initialize the I18N helper classes
        this.fxResources = new AppResources("i18n.SportsTracker");
    }

    @Override
    public void showMessageDialog(final Window parent, final Alert.AlertType alertType, final String titleKey,
            final String messageKey, final Object... arguments) {

        final String message = fxResources.getString(messageKey, arguments);
        final Alert alert = new Alert(alertType, message);
        alert.initOwner(parent);
        alert.setTitle(fxResources.getString(titleKey));
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @Override
    public Optional<ButtonType> showConfirmationDialog(final Window parent, final String titleKey,
            final String messageKey, final ButtonType... buttonTypes) {

        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, fxResources.getString(messageKey));
        alert.initOwner(parent);
        alert.setTitle(fxResources.getString(titleKey));
        alert.setHeaderText(null);

        // add custom button types if specified
        if (buttonTypes.length > 0) {
            alert.getButtonTypes().setAll(buttonTypes);
        }

        return alert.showAndWait();
    }

    @Override
    public Optional<String> showTextInputDialog(final Window parent, final String titleKey, final String messageKey,
            final String initialValue) {

        final TextInputDialog inputDlg = new TextInputDialog(initialValue == null ? "" : initialValue);
        inputDlg.initOwner(parent);
        inputDlg.setTitle(getResources().getString(titleKey));
        inputDlg.setContentText(getResources().getString(messageKey));
        inputDlg.setHeaderText(null);
        return inputDlg.showAndWait();
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
        return application.getPrimaryStage();
    }

    @Override
    public AppResources getResources() {
        return fxResources;
    }

    @Override
    public HostServices getHostServices() {
        return application.getHostServices();
    }

    @Override
    public void blockMainWindow(final boolean blockWindow) {
        final Stage primaryStage = getPrimaryStage();
        final EventDispatcher currentEventDispatcher = primaryStage.getEventDispatcher();

        if (blockWindow) {
            if (currentEventDispatcher != blockingEventDispatcher) {
                primaryStageEventDispatcher = currentEventDispatcher;
                primaryStage.setEventDispatcher(blockingEventDispatcher);
            }
        } else {
            if (currentEventDispatcher == blockingEventDispatcher) {
                primaryStage.setEventDispatcher(primaryStageEventDispatcher);
            }
        }

        primaryStage.getScene().setCursor(blockWindow ? Cursor.WAIT : Cursor.DEFAULT);
    }
}
