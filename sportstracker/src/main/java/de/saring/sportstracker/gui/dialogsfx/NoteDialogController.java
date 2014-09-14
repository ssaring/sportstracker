package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.util.InputValidators;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Controller (MVC) class of the Note dialog for editing / adding Note entries.
 *
 * @author Stefan Saring
 */
@Singleton
public class NoteDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private DatePicker dpDate;

    // TODO use formatted TextField, will be introduced in JavaFX 8u40
    @FXML
    private TextField tfHour;

    // TODO use formatted TextField, will be introduced in JavaFX 8u40
    @FXML
    private TextField tfMinute;

    @FXML
    private TextArea taText;

    /** Model (MVC) of the edited Note. */
    private NoteModel noteModel;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public NoteDialogController(final STContext context, final STDocument document,
                                final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
    }

    /**
     * Displays the Note dialog for the passed Note instance.
     *
     * @param parent parent window of the dialog
     * @param note Note to be edited
     */
    public void show(final Window parent, final Note note) {
        this.noteModel = new NoteModel(note);

        final String dlgTitleKey = note.getText() == null ? "st.dlg.note.title.add" : "st.dlg.note.title";
        final String dlgTitle = context.getFxResources().getString(dlgTitleKey);

        showEditDialog("/fxml/NoteDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(noteModel.date);
        tfHour.textProperty().bindBidirectional(noteModel.hour, new NumberStringConverter("00"));
        tfMinute.textProperty().bindBidirectional(noteModel.minute, new NumberStringConverter("00"));
        taText.textProperty().bindBidirectional(noteModel.text);

        // setup validation of the UI controls
        validationSupport.registerValidator(dpDate,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.note.error.date")));
        validationSupport.registerValidator(tfHour, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfHour, context.getFxResources().getString("st.dlg.note.error.time"),
                        !isValueIntegerBetween(newValue, 0, 23)));
        validationSupport.registerValidator(tfMinute, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfMinute, context.getFxResources().getString("st.dlg.note.error.time"),
                        !isValueIntegerBetween(newValue, 0, 59)));
        validationSupport.registerValidator(taText,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.note.error.no_text")));
    }

    // TODO move to a util class and create unit tests
    private boolean isValueIntegerBetween(final String value, final int minValue, final int maxValue) {
        try {
            final int intValue = Integer.parseInt(value);
            return intValue >= minValue && intValue <= maxValue;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected boolean validateAndStore() {

        // TODO use frameworks such as JideFX or ControlsFX for validation

        // check date input
        if (noteModel.date.getValue() == null) {
            context.showFxErrorDialog(dpDate.getScene().getWindow(),
                    "common.error", "st.dlg.note.error.date");
            dpDate.requestFocus();
            return false;
        }

        // check time inputs
        if (InputValidators.getRequiredTextControlIntegerValue(context, tfHour, 0, 23,
                "common.error", "st.dlg.note.error.time") == null) {
            return false;
        }

        if (InputValidators.getRequiredTextControlIntegerValue(context, tfMinute, 0, 59,
                "common.error", "st.dlg.note.error.time") == null) {
            return false;
        }

        // get note text
        if (InputValidators.getRequiredTextControlValue(context, taText,
                "common.error", "st.dlg.note.error.no_text") == null) {
            return false;
        }

        // finally store the new Note
        final Note newNote = noteModel.getNote();
        document.getNoteList().set(newNote);
        return true;
    }

    // TODO move class to a separate file?

    /**
     * This ViewModel class provides JavaFX properties of all Note attributes to be edited in the dialog.
     * So they can be bound to the appropriate dialog view controls.
     */
    private static final class NoteModel {

        private final int id;
        private final ObjectProperty<LocalDate> date;
        private final IntegerProperty hour;
        private final IntegerProperty minute;
        private final StringProperty text;

        /**
         * Creates the NoteModel with JavaFX properties for the passed Note object.
         *
         * @param note Note to be edited
         */
        public NoteModel(final Note note) {
            this.id = note.getId();
            this.date = new SimpleObjectProperty(note.getDateTime().toLocalDate());
            this.hour = new SimpleIntegerProperty(note.getDateTime().getHour());
            this.minute = new SimpleIntegerProperty(note.getDateTime().getMinute());
            this.text = new SimpleStringProperty(note.getText());
        }

        /**
         * Creates a new Note domain object from the edited JavaFX properties.
         *
         * @return Note
         */
        public Note getNote() {
            final Note note = new Note(id);
            note.setDateTime(LocalDateTime.of(date.get(), LocalTime.of(hour.getValue(), minute.getValue())));
            note.setText(text.getValue().trim());
            return note;
        }
    }
}
