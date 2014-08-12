package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.gui.STContext;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;

import java.io.IOException;

/**
 * Base class for the dialog controllers (MVC). It supports Information and Edit type dialogs.
 *
 * @author Stefan Saring
 */
public abstract class AbstractDialogController {

    protected STContext context;
    protected GuiceFxmlLoader guiceFxmlLoader;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param guiceFxmlLoader the Guice FXML loader
     */
    public AbstractDialogController(final STContext context, final GuiceFxmlLoader guiceFxmlLoader) {
        this.context = context;
        this.guiceFxmlLoader = guiceFxmlLoader;
    }

    /**
     * Displays the information dialog for the specified FXML file. The dialog contains only a Close button.
     *
     * @param fxmlFilename FXML filename of the dialog content
     * @param parent parent window of the dialog
     * @param title dialog title text
     */
    protected void showInfoDialog(final String fxmlFilename, final Window parent, final String title) {

        Parent root = loadDialogContent(fxmlFilename);
        showInitialValues();

        // show dialog
        Dialog dlg = createDialog(parent, title, root);
        dlg.getActions().addAll(Dialog.Actions.CLOSE);
        dlg.show();
    }

    /**
     * Displays the edit dialog for the specified FXML file. The dialog contains an OK and a Cancel button,
     * the OK button text can be changed via {@link #getOkButtonText()}.
     *
     * @param fxmlFilename FXML filename of the dialog content
     * @param parent parent window of the dialog
     * @param title dialog title text
     *
     * @return true if the dialog has been closed successfully with OK (no validation errors)
     */
    protected boolean showEditDialog(final String fxmlFilename, final Window parent, final String title) {

        Parent root = loadDialogContent(fxmlFilename);
        showInitialValues();

        // define the action when user presses the OK button (default)
        Action actionOk = new AbstractAction(getOkButtonText()) {
            public void handle(final ActionEvent event) {
                onOk(event);
            }
        };
        ButtonBar.setType(actionOk, ButtonBar.ButtonType.OK_DONE);

        // show dialog
        Dialog dlg = createDialog(parent, title, root);
        dlg.getActions().addAll(actionOk, Dialog.Actions.CANCEL);
        return dlg.show() == actionOk;
    }

    /**
     * Returns the text to be shown in the OK action button of the dialog.
     *
     * @return button text
     */
    protected String getOkButtonText() {
        return context.getFxResources().getString("dialog.ok");
    }

    /**
     * Setups the dialog content controls and shows the initial values of the domain object to be edited.
     */
    protected abstract void showInitialValues();

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
     * Focuses the specified control at dialog start. This will be done asynchronously, otherwise
     * the focusing fails. If the control is a TextField, then the cursor will be placed to the end.
     *
     * @param control control to focus
     */
    protected void focusInitialControl(final Control control) {
        Platform.runLater(()-> {
            control.requestFocus();
            if (control instanceof TextField) {
                ((TextField) control).selectEnd();
            }
        });
    }


    private Dialog createDialog(final Window parent, final String title, final Parent root) {
        Dialog dlg = new Dialog(parent, title, false, DialogStyle.NATIVE);
        dlg.setResizable(false);
        dlg.setIconifiable(false);
        dlg.setContent(root);
        return dlg;
    }

    private Parent loadDialogContent(final String fxmlFilename) {
        try {
            return guiceFxmlLoader.load(AbstractDialogController.class.getResource(fxmlFilename),
                    context.getFxResources().getResourceBundle());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the dialog FXML resource '" + fxmlFilename + "'!", e);
        }
    }

    private void onOk(final ActionEvent event) {
        if (validateAndStore()) {
            Dialog dlg = (Dialog) event.getSource();
            dlg.hide();
        }
    }
}