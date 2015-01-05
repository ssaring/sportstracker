package de.saring.sportstracker.gui.dialogs;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Provider;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.ColorConverter;
import de.saring.util.gui.javafx.NameableListCell;

/**
 * Controller (MVC) class of the Sport Type List dialog of the SportsTracker application.
 *
 * @author Stefan Saring
 */
public class SportTypeListDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private ListView<SportType> liSportTypes;

    @FXML
    private Button btEdit;
    @FXML
    private Button btDelete;

    @Inject
    private Provider<SportTypeDialogController> prSportTypeDialogController;


    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     */
    @Inject
    public SportTypeListDialogController(final STContext context, final STDocument document) {
        super(context);
        this.document = document;
    }

    /**
     * Displays the Sport Type List dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {
        showInfoDialog("/fxml/SportTypeListDialog.fxml", parent,
                context.getResources().getString("st.dlg.sporttype_list.title"));
    }

    @Override
    protected void setupDialogControls() {

        // setup SportType list
        liSportTypes.setCellFactory(list -> new SportTypeListCell());
        updateSportTypeList();

        // start SportType edit dialog on double clicks in list
        liSportTypes.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                onEditSportType(null);
            }
        });

        // Edit and Delete buttons must be disabled when there is no selection in list
        final BooleanBinding selected = Bindings.isNull(liSportTypes.getSelectionModel().selectedItemProperty());
        btEdit.disableProperty().bind(selected);
        btDelete.disableProperty().bind(selected);
    }

    private void updateSportTypeList() {
        final ObservableList<SportType> olSportTypes = FXCollections.observableArrayList();
        liSportTypes.getItems().clear();
        document.getSportTypeList().forEach(sportType -> olSportTypes.add(sportType));
        liSportTypes.setItems(olSportTypes);
    }

    /**
     * Action for adding a new sport type.
     */
    @FXML
    private void onAddSportType(final ActionEvent event) {

        // start SportType dialog for a new created SportType object
        final SportType newSportType = new SportType(document.getSportTypeList().getNewID());
        prSportTypeDialogController.get().show(getWindow(liSportTypes), newSportType);
        updateSportTypeList();
    }

    /**
     * Action for editing the selected sport type.
     */
    @FXML
    private void onEditSportType(final ActionEvent event) {

        // start SportType dialog for the selected sport type (can be null due to double clicks)
        final SportType selectedSportType = liSportTypes.getSelectionModel().getSelectedItem();
        if (selectedSportType != null) {
            prSportTypeDialogController.get().show(getWindow(liSportTypes), selectedSportType);
            updateSportTypeList();
        }
    }

    /**
     * Action for deleting the selected sport type.
     */
    @FXML
    private void onDeleteSportType(final ActionEvent event) {

        // display confirmation dialog
        final Optional<ButtonType> resultDeleteSportType = context.showMessageDialog(
                getWindow(liSportTypes), Alert.AlertType.CONFIRMATION,
                "st.dlg.sporttype_list.confirm.delete.title", "st.dlg.sporttype_list.confirm.delete.text");
        if (!resultDeleteSportType.isPresent() || resultDeleteSportType.get() != ButtonType.OK) {
            return;
        }

        // are there any existing exercises for this sport type?
        final SportType sportType = liSportTypes.getSelectionModel().getSelectedItem();
        final List<Exercise> lRefExercises = document.getExerciseList().stream()
                .filter(exercise -> exercise.getSportType().equals(sportType))
                .collect(Collectors.toList());

        // when there are referenced exercises => these exercises needs to be deleted too
        if (!lRefExercises.isEmpty()) {

            // show confirmation dialog for deleting exercises
            final Optional<ButtonType> resultDeleteExercises = context.showMessageDialog(
                    getWindow(liSportTypes), Alert.AlertType.CONFIRMATION,
                    "st.dlg.sporttype_list.confirm.delete.title", "st.dlg.sporttype_list.confirm.delete_existing.text");
            if (!resultDeleteExercises.isPresent() || resultDeleteExercises.get() != ButtonType.OK) {
                return;
            }

            // delete reference exercises
            lRefExercises.forEach(exercise -> document.getExerciseList().removeByID(exercise.getId()));
        }

        // finally delete the sport type
        document.getSportTypeList().removeByID(sportType.getId());
        updateSportTypeList();
    }

    /**
     * Custom ListCell implementation for displaying the sport types in the ListView by using its name and color.
     */
    private static class SportTypeListCell extends NameableListCell<SportType> {

        @Override
        protected void updateItem(final SportType item, final boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setTextFill(ColorConverter.toFxColor(item.getColor()));
            }
        }

        @Override
        public void updateSelected(final boolean selected) {
            super.updateSelected(selected);

            // use white text color for selected sport types, updateItem() is not called on selection changes
            if (getItem() != null) {
                setTextFill(selected ? Color.WHITE : ColorConverter.toFxColor(getItem().getColor()));
            }
        }
    }
}
