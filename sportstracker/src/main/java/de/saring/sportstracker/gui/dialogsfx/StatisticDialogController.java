package de.saring.sportstracker.gui.dialogsfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;

/**
 * Controller (MVC) class of the Statistic dialog.
 *
 * @author Stefan Saring
 */
@Singleton
public class StatisticDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private Label laTimespanValue;
    @FXML
    private Label laSportTypeValue;
    @FXML
    private Label laSportSubtypeValue;
    @FXML
    private Label laIntensityValue;
    @FXML
    private Label laEquipmentValue;
    @FXML
    private Label laCommentValue;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public StatisticDialogController(final STContext context, final STDocument document,
            final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
    }

    /**
     * Displays the Statistic dialog for the current Exercise filter.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {

        // TODO display calculate action button

        showInfoDialog("/fxml/StatisticDialog.fxml", parent,
                context.getFxResources().getString("st.dlg.statistic.title"));
    }

    @Override
    protected void setupDialogControls() {
        // TODO display filter values
    }

    /**
     * Action for changing the current statistic filter.
     */
    @FXML
    private void onChange(final ActionEvent event) {
        // TODO
    }
}
