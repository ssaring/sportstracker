package de.saring.sportstracker.gui.dialogs;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.saring.util.AppResources;
import de.saring.util.unitcalc.FormatUtils.SpeedMode;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import javax.inject.Inject;

import org.controlsfx.validation.Validator;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.gui.javafx.NameableListCell;

/**
 * Controller (MVC) class of the Sport Type dialog for editing / adding SportType entries.
 *
 * @author Stefan Saring
 */
public class SportTypeDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private TextField tfName;

    @FXML
    private CheckBox cbRecordDistance;

    @FXML
    private ComboBox<SpeedModeItem> cbSpeedMode;

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
     */
    @Inject
    public SportTypeDialogController(final STContext context, final STDocument document) {
        super(context);
        this.document = document;
        SpeedModeItem.appResources = context.getResources();
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
        final String dlgTitle = context.getResources().getString(dlgTitleKey);

        showEditDialog("/fxml/dialogs/SportTypeDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {
        cbSpeedMode.getItems().addAll(List.of(SpeedModeItem.values()));
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

        // start Equipment edit dialog on double clicks in list
        liEquipments.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                onEditEquipment(null);
            }
        });
    }

    /**
     * Setup of the binding between the view model and the UI controls.
     */
    private void setupBinding() {
        tfName.textProperty().bindBidirectional(sportTypeViewModel.name);
        cbRecordDistance.selectedProperty().bindBidirectional(sportTypeViewModel.recordDistance);
        cbSpeedMode.valueProperty().bindBidirectional(sportTypeViewModel.speedMode);
        cpColor.valueProperty().bindBidirectional(sportTypeViewModel.color);

        // the record distance mode can only be changed, when no exercises exists for
        // this sport type => disable checkbox, when such exercises were found
        Optional<Exercise> oExercise = document.getExerciseList().stream()
                .filter(exercise -> exercise.getSportType().getId() == sportTypeViewModel.id)
                .findFirst();
        cbRecordDistance.setDisable(oExercise.isPresent());

        // speed mode can only be configured when distance is being recorded
        cbSpeedMode.disableProperty().bind(cbRecordDistance.selectedProperty().not());

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
                Validator.createEmptyValidator(context.getResources().getString("st.dlg.sporttype.error.no_name")));
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
            context.showMessageDialog(getWindow(tfName), Alert.AlertType.ERROR,
                    "common.error", "st.dlg.sporttype.error.name_in_use");
            tfName.requestFocus();
            return false;
        }

        // make sure that there's at least one sport subtype
        if (editedSportType.getSportSubTypeList().size() == 0) {
            context.showMessageDialog(getWindow(liSportSubtypes), Alert.AlertType.ERROR,
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
        final SportSubType newSubType = new SportSubType(sportTypeViewModel.sportSubtypes.getNewId());
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
        final Optional<ButtonType> resultDeleteSportSubtype = context.showConfirmationDialog(getWindow(liSportSubtypes),
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
            final Optional<ButtonType> resultDeleteExistingExercises = context.showConfirmationDialog(
                    getWindow(liSportSubtypes), "st.dlg.sporttype.confirm.delete_subtype.title",
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
            final Optional<String> oResult = context.showTextInputDialog(
                    getWindow(liSportSubtypes), dlgTitleKey, "st.dlg.sportsubtype.name", strName);

            // exit when user has pressed Cancel button
            if (!oResult.isPresent()) {
                return;
            }
            strName = StringUtils.getTrimmedTextOrNull(oResult.get());

            // check the entered name => display error messages on problems
            if (strName == null) {
                // no name was entered
                context.showMessageDialog(getWindow(liSportSubtypes), Alert.AlertType.ERROR,
                        "common.error", "st.dlg.sportsubtype.error.no_name");
            } else {
                // make sure that the entered name is not in use by other sport subtypes yet
                final String enteredName = strName;
                Optional<SportSubType> oSportSubtypeConflict = sportTypeViewModel.sportSubtypes.stream()
                        .filter(sstTemp -> sstTemp.getId() != subType.getId() && sstTemp.getName().equals(enteredName))
                        .findFirst();

                if (oSportSubtypeConflict.isPresent()) {
                    context.showMessageDialog(getWindow(liSportSubtypes), Alert.AlertType.ERROR,
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

    /**
     * Action for adding a new equipment.
     */
    @FXML
    private void onAddEquipment(final ActionEvent event) {

        // create a new Equipment object and display in the edit dialog
        Equipment newEquipment = new Equipment(sportTypeViewModel.equipments.getNewId());
        editEquipment(newEquipment);
    }

    /**
     * Action for editing the selected equipment.
     */
    @FXML
    private void onEditEquipment(final ActionEvent event) {

        // display edit dialog for selected equipment
        final Equipment selectedEquipment = liEquipments.getSelectionModel().getSelectedItem();
        if (selectedEquipment!= null) {
            editEquipment(selectedEquipment);
        }
    }

    /**
     * Action for deleting the selected equipment.
     */
    @FXML
    private void onDeleteEquipment(final ActionEvent event) {

        // display confirmation dialog
        final Optional<ButtonType> resultDeleteEquipment = context.showConfirmationDialog(getWindow(liEquipments),
                "st.dlg.sporttype.confirm.delete_equipment.title", "st.dlg.sporttype.confirm.delete_equipment.text");
        if (!resultDeleteEquipment.isPresent() || resultDeleteEquipment.get() != ButtonType.OK) {
            return;
        }

        // are there any existing exercises for this equipment?
        final Equipment selectedEquipment = liEquipments.getSelectionModel().getSelectedItem();

        List<Exercise> lRefExercises = document.getExerciseList().stream()
                .filter(exercise -> exercise.getSportType().getId() == sportTypeViewModel.id
                        && exercise.getEquipment() != null && exercise.getEquipment().equals(selectedEquipment))
                .collect(Collectors.toList());

        // when there are referenced exercises => the equipment must be deleted in those too
        if (lRefExercises.size() > 0) {

            // show confirmation message box again
            final Optional<ButtonType> resultDeleteEqInExercises = context.showConfirmationDialog(
                    getWindow(liEquipments), "st.dlg.sporttype.confirm.delete_equipment.title",
                    "st.dlg.sporttype.confirm.delete_equipment_existing.text");
            if (!resultDeleteEqInExercises.isPresent() || resultDeleteEqInExercises.get() != ButtonType.OK) {
                return;
            }

            // delete equipment in all exercises which use it
            lRefExercises.forEach(exercise -> exercise.setEquipment(null));
        }

        // finally delete the equipment
        sportTypeViewModel.equipments.removeByID(selectedEquipment.getId());
        updateEquipmentList();
    }

    /**
     * Displays the add/edit dialog for the specified equipment name (includes
     * error checking and dialog redisplay). The modified equipment will be
     * stored in the sport type.
     *
     * @param equipment the equipment to be edited
     */
    private void editEquipment(final Equipment equipment) {

        // start with current subtype name
        String strName = equipment.getName();

        // title text depends on editing a new or an existing equipment
        final String dlgTitleKey = strName == null ? "st.dlg.equipment.add.title" : "st.dlg.equipment.edit.title";

        while (true) {
            // display text input dialog for equipment name
            final Optional<String> oResult = context.showTextInputDialog(
                    getWindow(liEquipments), dlgTitleKey, "st.dlg.equipment.name", strName);

            // exit when user has pressed Cancel button
            if (!oResult.isPresent()) {
                return;
            }
            strName = StringUtils.getTrimmedTextOrNull(oResult.get());

            // check the entered name => display error messages on problems
            if (strName == null) {
                // no name was entered
                context.showMessageDialog(getWindow(liEquipments), Alert.AlertType.ERROR,
                        "common.error", "st.dlg.equipment.error.no_name");
            } else {
                // make sure that the entered name is not in use by other equipment's yet
                final String enteredName = strName;
                Optional<Equipment> oEquipmnentConflict = sportTypeViewModel.equipments.stream()
                        .filter(eqTemp -> eqTemp.getId() != equipment.getId() && eqTemp.getName().equals(enteredName))
                        .findFirst();

                if (oEquipmnentConflict.isPresent()) {
                    context.showMessageDialog(getWindow(liEquipments), Alert.AlertType.ERROR,
                            "common.error", "st.dlg.equipment.error.in_use");
                } else {
                    // the name is OK, store the modified equipment and update the list
                    equipment.setName(strName);
                    sportTypeViewModel.equipments.set(equipment);
                    updateEquipmentList();
                    return;
                }
            }
        }
    }

    /**
     * This is the list of possible speed modes of a sport type.
     * This enum also provides the localized displayed enum names.
     */
    enum SpeedModeItem {
        SPEED(SpeedMode.SPEED, "st.dlg.sporttype.speed_mode_speed.text"), //
        PACE(SpeedMode.PACE, "st.dlg.sporttype.speed_mode_pace.text");

        private static AppResources appResources;

        private SpeedMode speedMode;
        private String resourceKey;

        SpeedModeItem(final SpeedMode speedMode, final String resourceKey) {
            this.speedMode = speedMode;
            this.resourceKey = resourceKey;
        }

        public SpeedMode getSpeedMode() {
            return speedMode;
        }

        @Override
        public String toString() {
            return appResources.getString(resourceKey);
        }

        /**
         * Returns the appropriate SpeedModeItem for the specified SpeedMode enum value.
         *
         * @param speedMode speed mode
         * @return SpeedModeItem
         */
        public static SpeedModeItem findBySpeedMode(SpeedMode speedMode) {
            return Stream.of(SpeedModeItem.values())
                    .filter(speedModeItem -> speedMode == speedModeItem.getSpeedMode())
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid speedMode '" + speedMode + "'!"));
        }
    }
}
