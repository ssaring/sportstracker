package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.gui.STContext;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Controller (MVC) class of the Overview dialog of the SportsTracker application.
 * This dialog contains a diagram which displays all the exercises or weight entries in
 * various diagram graph types. The user can select the displayed time range (e.g. all
 * months of the selected year or the last 10 years until the selected year).
 *
 * @author Stefan Saring
 */
@Singleton
public class OverviewDialogController extends AbstractDialogController {

    // TODO use generic classes
    @FXML
    private ChoiceBox<?> cbTimeRange;
    @FXML
    private ChoiceBox<?> cbDisplay;
    @FXML
    private ChoiceBox<?> cbSportTypeMode;

    @FXML
    private Pane pDiagram;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public OverviewDialogController(final STContext context, final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
    }

    /**
     * Displays the Overview dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {
        showInfoDialog("/fxml/OverviewDialog.fxml", parent, context.getFxResources().getString("st.dlg.overview.title"));
    }

    @Override
    protected void setupDialogControls() {
        // TODO setup combo boxes and chart
    }
}
