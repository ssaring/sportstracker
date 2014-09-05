package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.gui.STContext;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

        executeOnJavaFXThread(() -> {
            final Parent root = loadDialogContent(fxmlFilename);
            showInitialValues();

            // show dialog
            final Dialog dlg = createDialog(parent, title, root);
            dlg.getActions().addAll(Dialog.Actions.CLOSE);
            applyJavaFXToSwingMigrationWorkarounds(dlg);
            dlg.show();
        });
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
        // TODO to be executed on JavaFX thread!

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
        applyJavaFXToSwingMigrationWorkarounds(dlg);
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

    private void executeOnJavaFXThread(Runnable task) {
        // TODO: remove this function when the main window has been migrated to JavaFX
        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    private void applyJavaFXToSwingMigrationWorkarounds(final Dialog dialog) {
        // TODO: remove these functions when the main window has been migrated to JavaFX
        centerDialogOnSwingFrame();
        fakeModalDialog(dialog);
    }

    /**
     * Places the Dialog to be shown in the center of the main Swing window (JFrame). This is done
     * by placing the transparent dummy JavaFX main window in the center of the Swing window.
     * The JavaFX dialog uses the dummy window as parent, so it's placed in the center.
     */
    private void centerDialogOnSwingFrame() {
        final java.awt.Rectangle swingFrameBounds = context.getMainFrame().getBounds();
        final Stage fxStage = context.getPrimaryStage();
        fxStage.setX(swingFrameBounds.getCenterX());
        fxStage.setY(swingFrameBounds.getCenterY());

        // problem: the new Stage position is not being used for the dialog
        // workaround: hide and show the dummy stage (is invisible anyway), then it works
        fxStage.hide();
        fxStage.show();
    }

    /**
     * Unfortunately it's not possible to show a modal JavaFX dialog on top of a Swing main window (JFrame).
     * The workaround is to disable main Swing window and it's menu when the dialog is shown. And enable
     * them again when the dialog has been closed.
     *
     * @param dialog Dialog
     */
    private void fakeModalDialog(final Dialog dialog) {

        // disable main Swing window and it's menu when the dialog is shown
        final javax.swing.JFrame swingMainFrame = context.getMainFrame();
        dialog.getWindow().setOnShown(e -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                swingMainFrame.setEnabled(false);
                swingMainFrame.getJMenuBar().setEnabled(false);
            });
        });

        // enable main Swing window and it's menu when the dialog has been closed
        dialog.getWindow().setOnHidden(e -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                swingMainFrame.setEnabled(true);
                swingMainFrame.getJMenuBar().setEnabled(true);
                swingMainFrame.toFront();
            });
        });
    }
}