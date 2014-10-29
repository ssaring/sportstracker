package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Controller (MVC) class of the Sport Type List dialog of the SportsTracker application.
 *
 * @author Stefan Saring
 */
@Singleton
public class SportTypeListDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private ListView<SportType> liSportTypes;

    @FXML
    private Button btEdit;
    @FXML
    private Button btDelete;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public SportTypeListDialogController(final STContext context, final STDocument document,
                                         final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
    }

    /**
     * Displays the Sport Type List dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {
        showInfoDialog("/fxml/SportTypeListDialog.fxml", parent,
                context.getFxResources().getString("st.dlg.sporttype_list.title"));
    }

    @Override
    protected void setupDialogControls() {
        // TODO setup binding, disable edit and delete buttons when no selection
    }
}
