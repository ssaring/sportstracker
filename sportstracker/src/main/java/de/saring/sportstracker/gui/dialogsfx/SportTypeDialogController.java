package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import de.saring.util.gui.javafx.NameableListCell;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.controlsfx.validation.Validator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

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

        // setup binding between view model and the UI controls
        tfName.textProperty().bindBidirectional(sportTypeViewModel.name);
        cbRecordDistance.selectedProperty().bindBidirectional(sportTypeViewModel.recordDistance);
        cpColor.valueProperty().bindBidirectional(sportTypeViewModel.color);
        liSportSubtypes.setItems(sportTypeViewModel.sportSubTypes);
        liEquipments.setItems(sportTypeViewModel.equipments);

        // setup validation of the UI controls
        validationSupport.registerValidator(tfName,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.sporttype.error.no_name")));
        validationSupport.registerValidator(liSportSubtypes,
                Validator.createPredicateValidator(
                        (List<SportSubType> sportSubTypes) -> !sportSubTypes.isEmpty(),
                        context.getFxResources().getString("st.dlg.sporttype.error.no_subtype")));

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

    @Override
    protected boolean validateAndStore() {

        /* TODO store the new SportType, no further validation needed
        final Note newNote = noteViewModel.getNote();
        document.getNoteList().set(newNote); */
        return true;
    }
}
