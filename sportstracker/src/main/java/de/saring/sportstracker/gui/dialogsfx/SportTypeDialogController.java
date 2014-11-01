package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import de.saring.util.gui.javafx.NameableListCell;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;
import org.controlsfx.validation.Validator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller (MVC) class of the Sport Type dialog for editing / adding SportType entries.
 *
 * @author Stefan Saring
 */
@Singleton
public class SportTypeDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private TextField tfName;

    @FXML
    private CheckBox cbRecordDistance;

    @FXML
    private ColorPicker cpColor;

    @FXML
    private ListView<SportSubType> liSportSubtypes;

    @FXML
    private ListView<Equipment> liEquipments;

    @FXML
    private Button btSportSubtypeEdit;
    @FXML
    private Button btSportSubtypeDelete;

    @FXML
    private Button btEquipmentEdit;
    @FXML
    private Button btEquipmentDelete;


    /** ViewModel of the edited SportType. */
    private SportTypeViewModel sportTypeViewModel;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public SportTypeDialogController(final STContext context, final STDocument document,
                                     final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
        setParentIsSwingWindow(false);
    }

    /**
     * Displays the Sport Type dialog for the passed SportType instance.
     *
     * @param parent parent window of the dialog
     * @param sportType sport type to be edited
     */
    public void show(final Window parent, final SportType sportType) {

        // use a copy of the SportType to be edited
        // => prevents source object modification when dialog is closed using the "Cancel" action
        this.sportTypeViewModel = new SportTypeViewModel(sportType.clone());

        final String dlgTitleKey = sportType.getName() == null ?
                "st.dlg.sporttype.title.add" : "st.dlg.sporttype.title";
        final String dlgTitle = context.getFxResources().getString(dlgTitleKey);

        showEditDialog("/fxml/SportTypeDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {
        liSportSubtypes.setCellFactory(list -> new NameableListCell<>());
        liEquipments.setCellFactory(list -> new NameableListCell<>());

        setupBinding();
        setupValidation();

        updateSportSubtypeList();
        updateEquipmentList();

        // start Sport Subtype edit dialog on double clicks in list
        liSportSubtypes.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                onEditSportSubtype(null);
            }
        });

        // TODO add double click for equipment list
    }

    /**
     * Setup of the binding between the view model and the UI controls.
     */
    private void setupBinding() {
        tfName.textProperty().bindBidirectional(sportTypeViewModel.name);
        cbRecordDistance.selectedProperty().bindBidirectional(sportTypeViewModel.recordDistance);
        cpColor.valueProperty().bindBidirectional(sportTypeViewModel.color);

        // the record distance mode can only be changed, when no exercises exists for
        // this sport type => disable checkbox, when such exercises were found
        Optional<Exercise> oExercise = document.getExerciseList().stream()
                .filter(exercise -> exercise.getSportType().getId() == sportTypeViewModel.id)
                .findFirst();
        cbRecordDistance.setDisable(oExercise.isPresent());

        // Edit and Delete buttons must be disabled when there is no selection in the appropriate list
        final BooleanBinding sportSubtypeSelected = Bindings.isNull(
                liSportSubtypes.getSelectionModel().selectedItemProperty());
        btSportSubtypeEdit.disableProperty().bind(sportSubtypeSelected);
        btSportSubtypeDelete.disableProperty().bind(sportSubtypeSelected);

        final BooleanBinding equipmentSelected = Bindings.isNull(
                liEquipments.getSelectionModel().selectedItemProperty());
        btEquipmentEdit.disableProperty().bind(equipmentSelected);
        btEquipmentDelete.disableProperty().bind(equipmentSelected);
    }

    /**
     * Setup of the validation of the UI controls.
     */
    private void setupValidation() {
        validationSupport.registerValidator(tfName,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.sporttype.error.no_name")));
    }

    @Override
    protected boolean validateAndStore() {

        // make sure that the entered name is not in use by other sport types yet
        final SportType editedSportType = sportTypeViewModel.getSportType();
        Optional<SportType> oSportTypeSameName = document.getSportTypeList().stream()
                .filter(stTemp -> stTemp.getId() != sportTypeViewModel.id
                        && stTemp.getName().equals(editedSportType.getName()))
                .findFirst();

        if (oSportTypeSameName.isPresent()) {
            tfName.selectAll();
            context.showFxMessageDialog(getWindow(tfName), Alert.AlertType.ERROR,
                    "common.error", "st.dlg.sporttype.error.name_in_use");
            tfName.requestFocus();
            return false;
        }

        // make sure that there's at least one sport subtype
        if (editedSportType.getSportSubTypeList().size() == 0) {
            context.showFxMessageDialog(getWindow(liSportSubtypes), Alert.AlertType.ERROR,
                    "common.error", "st.dlg.sporttype.error.no_subtype");
            return false;
        }

        // store the edited SportType in the documents list
        document.getSportTypeList().set(editedSportType);
        return true;
    }

    private void updateSportSubtypeList() {
        final ObservableList<SportSubType> olSportSubtypes = FXCollections.observableArrayList();
        liSportSubtypes.getItems().clear();
        sportTypeViewModel.sportSubtypes.forEach(sportType -> olSportSubtypes.add(sportType));
        liSportSubtypes.setItems(olSportSubtypes);
    }

    private void updateEquipmentList() {
        final ObservableList<Equipment> olEquipments = FXCollections.observableArrayList();
        liEquipments.getItems().clear();
        sportTypeViewModel.equipments.forEach(equipment -> olEquipments.add(equipment));
        liEquipments.setItems(olEquipments);
    }

    /**
     * Action for adding a new sport subtype.
     */
    @FXML
    private void onAddSportSubtype(final ActionEvent event) {

        // create a new SportSubType object and display in the edit dialog
        final SportSubType newSubType = new SportSubType(sportTypeViewModel.sportSubtypes.getNewID());
        editSportSubType(newSubType);
    }

    /**
     * Action for editing the selected sport subtype.
     */
    @FXML
    private void onEditSportSubtype(final ActionEvent event) {

        // display edit dialog for selected sport subtype
        final SportSubType selectedSportSubtype = liSportSubtypes.getSelectionModel().getSelectedItem();
        if (selectedSportSubtype != null) {
            editSportSubType(selectedSportSubtype);
        }
    }

    /**
     * Action for deleting the selected sport subtype.
     */
    @FXML
    private void onDeleteSportSubtype(final ActionEvent event) {

        // display confirmation dialog
        final Optional<ButtonType> resultDeleteSportSubtype = context.showFxMessageDialog(
                getWindow(liSportSubtypes), Alert.AlertType.CONFIRMATION,
                "st.dlg.sporttype.confirm.delete_subtype.title", "st.dlg.sporttype.confirm.delete_subtype.text");
        if (!resultDeleteSportSubtype.isPresent() || resultDeleteSportSubtype.get() != ButtonType.OK) {
            return;
        }

        // are there any existing exercises for this sport subtype?
        final SportSubType selectedSportSubtype = liSportSubtypes.getSelectionModel().getSelectedItem();

        final List<Exercise> lRefExercises = document.getExerciseList().stream()
                .filter(exercise -> exercise.getSportType().getId() == sportTypeViewModel.id
                        && exercise.getSportSubType().equals(selectedSportSubtype))
                .collect(Collectors.toList());

        // when there are referenced exercises => these exercises needs to be deleted too
        if (!lRefExercises.isEmpty()) {

            // show confirmation message box again
            final Optional<ButtonType> resultDeleteExistingExercises = context.showFxMessageDialog(
                    getWindow(liSportSubtypes), Alert.AlertType.CONFIRMATION,
                    "st.dlg.sporttype.confirm.delete_subtype.title",
                    "st.dlg.sporttype.confirm.delete_subtype_existing.text");
            if (!resultDeleteExistingExercises.isPresent() || resultDeleteExistingExercises.get() != ButtonType.OK) {
                return;
            }

            // delete reference exercises
            lRefExercises.forEach(exercise -> document.getExerciseList().removeByID(exercise.getId()));
        }

        // finally delete the sport subtype
        sportTypeViewModel.sportSubtypes.removeByID(selectedSportSubtype.getId());
        updateSportSubtypeList();
    }

    /**
     * Displays the add/edit dialog for the specified sport subtype name (includes
     * error checking and dialog redisplay). The modified sport subtype will be
     * stored in the sport type.
     *
     * @param subType the sport subtype to be edited
     */
    private void editSportSubType(final SportSubType subType) {

        // start with current subtype name
        String strName = subType.getName();

        // title text depends on editing a new or an existing subtype
        final String dlgTitleKey = strName == null ? "st.dlg.sportsubtype.add.title" : "st.dlg.sportsubtype.edit.title";

        while (true) {
            // display text input dialog for sport subtype name
            final TextInputDialog inputDlg = new TextInputDialog(strName == null ? "" : strName);
            inputDlg.initOwner(getWindow(liSportSubtypes));
            inputDlg.setTitle(context.getFxResources().getString(dlgTitleKey));
            inputDlg.setContentText(context.getFxResources().getString("st.dlg.sportsubtype.name"));
            inputDlg.setHeaderText(null);
            // TODO remove when fixed in OpenJFX-Dialogs
            // workaround for disabling bigger font size of custom dialog content
            inputDlg.getDialogPane().setStyle("-fx-font-size: 1em;");

            // show dialog and exit when user has pressed Cancel button
            final Optional<String> oResult = inputDlg.showAndWait();
            if (!oResult.isPresent()) {
                return;
            }
            strName = StringUtils.getTrimmedTextOrNull(oResult.get());

            // check the entered name => display error messages on problems
            if (strName == null) {
                // no name was entered
                context.showFxMessageDialog(getWindow(liSportSubtypes), Alert.AlertType.ERROR,
                        "common.error", "st.dlg.sportsubtype.error.no_name");
            } else {
                // make sure that the entered name is not in use by other sport subtypes yet
                final String enteredName = strName;
                Optional<SportSubType> oSportSubtypeConflict = sportTypeViewModel.sportSubtypes.stream()
                        .filter(sstTemp -> sstTemp.getId() != subType.getId() && sstTemp.getName().equals(enteredName))
                        .findFirst();

                if (oSportSubtypeConflict.isPresent()) {
                    context.showFxMessageDialog(getWindow(liSportSubtypes), Alert.AlertType.ERROR,
                            "common.error", "st.dlg.sportsubtype.error.in_use");
                } else {
                    // the name is OK, store the modified subtype and update the list
                    subType.setName(strName);
                    sportTypeViewModel.sportSubtypes.set(subType);
                    updateSportSubtypeList();
                    return;
                }
            }
        }
    }
}
