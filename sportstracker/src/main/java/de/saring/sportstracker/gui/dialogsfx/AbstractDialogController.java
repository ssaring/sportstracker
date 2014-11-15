package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.gui.STContext;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
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
import javafx.stage.Stage;
import javafx.stage.Window;

import org.controlsfx.validation.ValidationSupport;

import java.io.IOException;

/**
 * Base class for the dialog controllers (MVC). It supports Information and Edit type dialogs.
 *
 * @author Stefan Saring
 */
public abstract class AbstractDialogController {

    protected STContext context;
    protected GuiceFxmlLoader guiceFxmlLoader;

    /** ValidationSupport of tis dialog, is null in Info dialogs. */
    protected ValidationSupport validationSupport;

    // TODO: remove this flag when the main window has been migrated to JavaFX
    private boolean parentIsSwingWindow = true;

    private Runnable afterCloseBehavior;

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
     * Sets the flag which defines whether the parent window of this dialog is a Swing window (default) or not.
     * Swing parents need extra workarounds. This method needs to be called before the show...() methods.
     *
     * @param parentIsSwingWindow flag for parent window type
     */
    public void setParentIsSwingWindow(final boolean parentIsSwingWindow) {
        this.parentIsSwingWindow = parentIsSwingWindow;
    }

    /**
     * Sets the function/runnable which needs to be called after the dialog has been closed (no matter
     * which action has been used).<br/>
     * BEWARE: this runnable will be called on the JavaFX UI thread!
     *
     * @param afterCloseBehavior function to be called
     */
    public void setAfterCloseBehavior(final Runnable afterCloseBehavior) {
        this.afterCloseBehavior = afterCloseBehavior;
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
            setupDialogControls();

            // create and show dialog
            final Dialog<ButtonType> dlg = createDialog(parent, title, root);
            dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            addCustomButtons(dlg.getDialogPane());
            applyJavaFXToSwingMigrationWorkarounds(dlg);
            dlg.showAndWait();

            if (afterCloseBehavior != null) {
                afterCloseBehavior.run();
            }
        });
    }

    /**
     * Displays the edit dialog for the specified FXML file. The dialog contains an OK and a Cancel button.
     * The OK button is only enabled, when there are no errors found by the configured ValidationSupport.
     *
     * @param fxmlFilename FXML filename of the dialog content
     * @param parent parent window of the dialog
     * @param title dialog title text
     */
    protected void showEditDialog(final String fxmlFilename, final Window parent, final String title) {

        executeOnJavaFXThread(() -> {
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

            applyJavaFXToSwingMigrationWorkarounds(dlg);

            // show dialog
            dlg.showAndWait();

            if (afterCloseBehavior != null) {
                afterCloseBehavior.run();
            }
        });
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
        Platform.runLater(()-> {
            control.requestFocus();
            if (control instanceof TextField) {
                ((TextField) control).selectEnd();
            }
        });
    }

    private Dialog<ButtonType> createDialog(final Window parent, final String title, final Parent root) {
        final Dialog<ButtonType> dlg = new Dialog();
        dlg.initOwner(parent);

        // TODO remove when fixed in OpenJFX-Dialogs
        // workaround for disabling bigger font size of custom dialog content
        dlg.getDialogPane().setStyle("-fx-font-size: 1em;");

        dlg.setTitle(title);
        dlg.getDialogPane().setContent(root);
        dlg.setResizable(false);
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

    /**
     * Invokes the passed task on the Swing UI thread (asynchronously).
     *
     * @param task task to execute
     */
    protected void executeOnSwingThread(Runnable task) {
        // TODO: remove this function when the main window has been migrated to JavaFX
        javax.swing.SwingUtilities.invokeLater(() -> task.run());
    }

    /**
     * Invokes the passed task on the JavaFX UI thread (asynchronously).
     *
     * @param task task to execute
     */
    protected void executeOnJavaFXThread(Runnable task) {
        // TODO: remove this function when the main window has been migrated to JavaFX
        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    private void applyJavaFXToSwingMigrationWorkarounds(final Dialog dialog) {
        // TODO: remove these functions when the main window has been migrated to JavaFX
        if (parentIsSwingWindow) {
            centerDialogOnSwingFrame();
            fakeModalDialog(dialog);
        }
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
        dialog.setOnShown(e -> {
            executeOnSwingThread(() -> {
                swingMainFrame.setEnabled(false);
                swingMainFrame.getJMenuBar().setEnabled(false);
            });
        });

        // enable main Swing window and it's menu when the dialog has been closed
        dialog.setOnHidden(e -> {
            executeOnSwingThread(() -> {
                swingMainFrame.setEnabled(true);
                swingMainFrame.getJMenuBar().setEnabled(true);
                swingMainFrame.toFront();
            });
        });
    }
}