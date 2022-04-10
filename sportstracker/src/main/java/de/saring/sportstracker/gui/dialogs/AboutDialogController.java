package de.saring.sportstracker.gui.dialogs;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import jakarta.inject.Inject;

import de.saring.sportstracker.gui.STContext;

/**
 * Controller (MVC) class of the About dialog window.
 *
 * @author Stefan Saring
 */
public class AboutDialogController extends AbstractDialogController {

    @FXML
    private TextArea taAuthors;
    @FXML
    private TextArea taTranslators;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     */
    @Inject
    public AboutDialogController(final STContext context) {
        super(context);
    }

    /**
     * Displays the About dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {
        showInfoDialog("/fxml/dialogs/AboutDialog.fxml", parent, context.getResources().getString("st.dlg.about.title"));
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
