package de.saring.sportstracker.gui.dialogs;

import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.ValidationUtils;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
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

    /** ViewModel of the edited Note. */
    private NoteViewModel noteViewModel;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
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
        this.noteViewModel = new NoteViewModel(note);

        final String dlgTitleKey = note.getText() == null ? "st.dlg.note.title.add" : "st.dlg.note.title";
        final String dlgTitle = context.getFxResources().getString(dlgTitleKey);

        showEditDialog("/fxml/NoteDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(noteViewModel.date);
        tfHour.textProperty().bindBidirectional(noteViewModel.hour, new NumberStringConverter("00"));
        tfMinute.textProperty().bindBidirectional(noteViewModel.minute, new NumberStringConverter("00"));
        taText.textProperty().bindBidirectional(noteViewModel.text);

        // setup validation of the UI controls
        validationSupport.registerValidator(dpDate,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.note.error.date")));
        validationSupport.registerValidator(tfHour, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfHour, context.getFxResources().getString("st.dlg.note.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 23)));
        validationSupport.registerValidator(tfMinute, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfMinute, context.getFxResources().getString("st.dlg.note.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 59)));
        validationSupport.registerValidator(taText,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.note.error.no_text")));
    }

    @Override
    protected boolean validateAndStore() {

        // store the new Note, no further validation needed
        final Note newNote = noteViewModel.getNote();
        document.getNoteList().set(newNote);
        return true;
    }
}
