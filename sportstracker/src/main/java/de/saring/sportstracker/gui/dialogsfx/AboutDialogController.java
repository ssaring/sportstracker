package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.gui.STContext;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Controller (MVC) class of the About dialog window.
 *
 * @author Stefan Saring
 */
@Singleton
public class AboutDialogController extends AbstractDialogController {

    @FXML
    private TextArea taAuthors;
    @FXML
    private TextArea taTranslators;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public AboutDialogController(final STContext context, final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
    }

    /**
     * Displays the About dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {
        showInfoDialog("/fxml/AboutDialog.fxml", parent, context.getFxResources().getString("st.dlg.about.title"));
    }

    @Override
    protected void setupDialogControls() {

        // Workaround: always show vertical scroll bars (otherwise they are displayed when the TextArea gets the focus)
        Platform.runLater(() -> {
            taAuthors.setWrapText(true);
            taTranslators.setWrapText(true);
        });
    }
}
