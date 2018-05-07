package de.saring.sportstracker.gui.dialogs;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import org.controlsfx.validation.ValidationSupport;

import de.saring.sportstracker.gui.STContext;
import de.saring.util.SystemUtils;
import de.saring.util.gui.javafx.FxmlLoader;

/**
 * Base class for the dialog controllers (MVC). It supports Information and Edit type dialogs.
 *
 * @author Stefan Saring
 */
public abstract class AbstractDialogController {

    protected STContext context;

    /** ValidationSupport of tis dialog, is null in Info dialogs. */
    protected ValidationSupport validationSupport;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     */
    public AbstractDialogController(final STContext context) {
        this.context = context;
    }

    /**
     * Displays the information dialog for the specified FXML file. The dialog contains only a Close button.
     *
     * @param fxmlFilename FXML filename of the dialog content
     * @param parent parent window of the dialog
     * @param title dialog title comment
     */
    protected void showInfoDialog(final String fxmlFilename, final Window parent, final String title) {

        final Parent root = loadDialogContent(fxmlFilename);
        setupDialogControls();

        // create and show dialog
        final Dialog<ButtonType> dlg = createDialog(parent, title, root);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        addCustomButtons(dlg.getDialogPane());
        dlg.showAndWait();
        triggerGC();
    }

    /**
     * Displays the edit dialog for the specified FXML file. The dialog contains an OK and a Cancel button.
     * The OK button is only enabled, when there are no errors found by the configured ValidationSupport.
     *
     * @param fxmlFilename FXML filename of the dialog content
     * @param parent parent window of the dialog
     * @param title dialog title comment
     */
    protected void showEditDialog(final String fxmlFilename, final Window parent, final String title) {

        this.validationSupport = new ValidationSupport();

        final Parent root = loadDialogContent(fxmlFilename);
        setupDialogControls();

        // create and setup dialog
        final Dialog<ButtonType> dlg = createDialog(parent, title, root);
        final DialogPane dlgPane = dlg.getDialogPane();

        dlgPane.getButtonTypes().add(ButtonType.OK);
        dlgPane.getButtonTypes().add(ButtonType.CANCEL);
        addCustomButtons(dlgPane);

        // bind validation to OK button, must only be enabled when there are no errors
        final Button btOk = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        btOk.disableProperty().bind(validationSupport.invalidProperty());

        // set action event filter for custom dialog validation and storing the result
        // => don't close the dialog on errors
        btOk.addEventFilter(ActionEvent.ACTION, (event) -> {
            if (!validateAndStore()) {
                event.consume();
            }
        });

        // show dialog
        dlg.showAndWait();
        triggerGC();
    }

    /**
     * Setups the dialog content controls before the dialog will be displayed. Usually the binding
     * between the model and the UI controls and the validation is defined here.
     */
    protected abstract void setupDialogControls();

    /**
     * Adds custom button bar buttons to the dialog. The default implementation does nothing, subclasses
     * can add custom behaviour here.
     *
     * @param dialogPane DialogPane
     */
    protected void addCustomButtons(final DialogPane dialogPane) {
    }

    /**
     * For edit dialogs only: Validates the inputs. On input errors an error message needs to be displayed.
     * If everything is OK, the values must be stored in the domain object passed to the dialog.<br/>
     * The default implementation does nothing.
     *
     * @return true when there were no validation errors and the inputs were stored
     */
    protected boolean validateAndStore() {
        return true;
    }

    /**
     * Returns the window of the specified control.
     *
     * @param control control
     * @return Window
     */
    protected Window getWindow(final Control control) {
        final Scene scene = control.getScene();
        return scene == null ? null : scene.getWindow();
    }

    /**
     * Focuses the specified control at dialog start. This will be done asynchronously, otherwise
     * the focusing fails. If the control is a TextField, then the cursor will be placed to the end.
     *
     * @param control control to focus
     */
    protected void focusInitialControl(final Control control) {
        Platform.runLater(() -> {
            control.requestFocus();
            if (control instanceof TextField) {
                ((TextField) control).selectEnd();
            }
        });
    }

    private Dialog<ButtonType> createDialog(final Window parent, final String title, final Parent root) {
        final Dialog<ButtonType> dlg = new Dialog<>();
        dlg.initOwner(parent);

        dlg.setTitle(title);
        dlg.getDialogPane().setContent(root);
        dlg.setResizable(false);
        return dlg;
    }

    private Parent loadDialogContent(final String fxmlFilename) {
        try {
            return FxmlLoader.load(AbstractDialogController.class.getResource(fxmlFilename), context.getResources()
                    .getResourceBundle(), this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the dialog FXML resource '" + fxmlFilename + "'!", e);
        }
    }

    /**
     * Triggers a delayed, asynchronous garbage collection after the dialog has been closed
     * to avoid allocation of additional heap space.
     */
    private void triggerGC() {
        SystemUtils.triggerGC();
    }
}