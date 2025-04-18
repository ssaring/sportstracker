package de.saring.sportstracker.gui.dialogs;

import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.SportType;
import de.saring.util.gui.javafx.FxWorkarounds;
import de.saring.util.gui.javafx.NameableStringConverter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;

import jakarta.inject.Inject;

import org.controlsfx.validation.Validator;

import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.TimeToStringConverter;

/**
 * Controller (MVC) class of the Note dialog for editing / adding Note entries.
 *
 * @author Stefan Saring
 */
public class NoteDialogController extends AbstractDialogController {

    private static final Logger LOGGER = Logger.getLogger(NoteDialogController.class.getName());

    private final STDocument document;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField tfTime;

    @FXML
    private ChoiceBox<SportType> cbSportType;

    @FXML
    private ChoiceBox<Equipment> cbEquipment;

    @FXML
    private TextArea taText;

    /** ViewModel of the edited Note. */
    private NoteViewModel noteViewModel;

    /** SportType for selection "none". */
    private final SportType sportTypeNone;
    /** Equipment for selection "none", same for all sport types. */
    private final Equipment equipmentNone;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     */
    @Inject
    public NoteDialogController(final STContext context, final STDocument document) {
        super(context);
        this.document = document;

        sportTypeNone = new SportType(Long.MAX_VALUE);
        sportTypeNone.setName(context.getResources().getString("st.dlg.note.sport_type.none.text"));
        equipmentNone = new Equipment(Long.MAX_VALUE);
        equipmentNone.setName(context.getResources().getString("st.dlg.note.equipment.none.text"));
    }

    /**
     * Displays the Note dialog for the passed Note instance.
     *
     * @param parent parent window of the dialog
     * @param note Note to be edited
     */
    public void show(final Window parent, final Note note) {
        this.noteViewModel = new NoteViewModel(note);

        final String dlgTitleKey = note.getId() == null ? "st.dlg.note.title.add" : "st.dlg.note.title";
        final String dlgTitle = context.getResources().getString(dlgTitleKey);

        showEditDialog("/fxml/dialogs/NoteDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        setupChoiceBoxes();
        fillSportTypeDependentControls();

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(noteViewModel.date);

        // use text formatter for time values => makes sure that the value is also valid
        final TextFormatter<LocalTime> timeTextFormatter = new TextFormatter<>(new TimeToStringConverter());
        timeTextFormatter.valueProperty().bindBidirectional(noteViewModel.time);
        tfTime.setTextFormatter(timeTextFormatter);

        cbSportType.valueProperty().bindBidirectional(noteViewModel.sportType);
        cbEquipment.valueProperty().bindBidirectional(noteViewModel.equipment);
        taText.textProperty().bindBidirectional(noteViewModel.comment);

        FxWorkarounds.fixDatePickerTextEntry(dpDate);

        // setup validation of the UI controls
        // => due to a ControlsFX bug the validation setup must be executed after the dialog has been shown
        // (see https://bitbucket.org/controlsfx/controlsfx/issues/539/multiple-dialog-fields-with-validation )
        Platform.runLater(() -> {
            validationSupport.registerValidator(dpDate, //
                    Validator.createEmptyValidator(context.getResources().getString("st.dlg.note.error.date")));
            validationSupport.registerValidator(tfTime, //
                    Validator.createEmptyValidator(context.getResources().getString("st.dlg.note.error.time")));
            validationSupport.registerValidator(taText, //
                    Validator.createEmptyValidator(context.getResources().getString("st.dlg.note.error.no_text")));
        });
    }

    @Override
    protected boolean validateAndStore() {
        // store the new Note, no further validation needed
        Note newNote = noteViewModel.getNote();

        // check for "none" sport type and equipment selection => replace this dummy by null
        if (sportTypeNone.equals(newNote.getSportType())) {
            newNote.setSportType(null);
        }
        if (equipmentNone.equals(newNote.getEquipment())) {
            newNote.setEquipment(null);
        }

        try {
            if (newNote.getId() == null) {
                newNote = document.getStorage().getNoteRepository().create(newNote);
            } else {
                document.getStorage().getNoteRepository().update(newNote);
            }
            document.updateApplicationData(newNote);
            return true;
        } catch (STException e) {
            LOGGER.log(Level.SEVERE, "Failed to store Note '" + newNote.getId() + "'!", e);
            return false;
        }
    }

    /**
     * Initializes all ChoiceBoxes by defining the String converters. SportType ChoiceBox with fixed values
     *  will be filled with possible values.
     */
    private void setupChoiceBoxes() {
        cbSportType.setConverter(new NameableStringConverter<>());
        cbEquipment.setConverter(new NameableStringConverter<>());

        cbSportType.getItems().add(sportTypeNone);
        document.getSportTypeList().forEach(sportType -> cbSportType.getItems().add(sportType));

        // select sport type "none" when note contains no sport type
        if (noteViewModel.sportType.get() == null) {
            noteViewModel.sportType.set(sportTypeNone);
        }

        // update the sport type dependent controls on each sport type selection change
        cbSportType.addEventHandler(ActionEvent.ACTION, event -> fillSportTypeDependentControls());
    }

    /**
     * Fills equipment controls with values dependent on the selected sport type.
     */
    private void fillSportTypeDependentControls() {
        cbEquipment.getItems().clear();
        cbEquipment.getItems().add(equipmentNone);

        final SportType selectedSportType = cbSportType.getValue();
        if (selectedSportType != null) {

            // add all active equipments or if the equipment is set in the note (even if notInUse has been set)
            Equipment selectedEquipment = noteViewModel.equipment.get();
            cbEquipment.getItems().addAll(selectedSportType.getEquipmentList().stream()
                    .filter(equipment -> !equipment.isNotInUse() || equipment.equals(selectedEquipment))
                    .toList());
        }

        // select equipment "none" when note contains no equipment
        if (noteViewModel.equipment.get() == null) {
            noteViewModel.equipment.set(equipmentNone);
        }

        // disable equipment choice box when no sport type selected
        cbEquipment.setDisable(selectedSportType == null || sportTypeNone.equals(selectedSportType));
    }
}
