package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
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

        // setup SportType list
        liSportTypes.setCellFactory(list -> new SportTypeListCell());
        updateSportTypeList();

        // Edit and Delete buttons must be disabled when there is no selection in list
        final BooleanBinding selected = Bindings.isNull(liSportTypes.getSelectionModel().selectedItemProperty());
        btEdit.disableProperty().bind(selected);
        btDelete.disableProperty().bind(selected);
    }

    private void updateSportTypeList() {
        final ObservableList<SportType> olSportTypes = FXCollections.observableArrayList();
        document.getSportTypeList().forEach(sportType -> olSportTypes.add(sportType));
        liSportTypes.setItems(olSportTypes);
    }

    /**
     * Custom ListCell implementation for displaying the sport types in the ListView by using its name and color.
     */
    private static class SportTypeListCell extends ListCell<SportType> {

        @Override
        protected void updateItem(final SportType item, final boolean empty) {
            super.updateItem(item, empty);

            setText(item == null ? null : item.getName());

            if (item != null) {
                setTextFill(getJavaFXColorForSportType(item));
            }
        }

        @Override
        public void updateSelected(final boolean selected) {
            super.updateSelected(selected);

            // use white text color for selected sport types, updateItem() is not called on selection changes
            if (getItem() != null) {
                setTextFill(selected ? Color.WHITE : getJavaFXColorForSportType(getItem()));
            }
        }

        private Color getJavaFXColorForSportType(final SportType sportType) {
            final java.awt.Color awtColor = sportType.getColor();
            return new Color(awtColor.getRed() / 255f, awtColor.getGreen() / 255f, awtColor.getBlue() / 255f, 1);
        }
    }
}
