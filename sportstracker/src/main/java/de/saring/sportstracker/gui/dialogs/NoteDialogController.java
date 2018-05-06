package de.saring.sportstracker.gui.dialogs;

import java.time.LocalTime;

import de.saring.util.gui.javafx.FxWorkarounds;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;

import javax.inject.Inject;

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

    private final STDocument document;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField tfTime;

    @FXML
    private TextArea taText;

    /** ViewModel of the edited Note. */
    private NoteViewModel noteViewModel;

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
    }

    /**
     * Displays the Note dialog for the passed Note instance.
     *
     * @param parent parent window of the dialog
     * @param note Note to be edited
     */
    public void show(final Window parent, final Note note) {
        this.noteViewModel = new NoteViewModel(note);

        final String dlgTitleKey = note.getComment() == null ? "st.dlg.note.title.add" : "st.dlg.note.title";
        final String dlgTitle = context.getResources().getString(dlgTitleKey);

        showEditDialog("/fxml/dialogs/NoteDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(noteViewModel.date);

        // use text formatter for time values => makes sure that the value is also valid
        final TextFormatter<LocalTime> timeTextFormatter = new TextFormatter<>(new TimeToStringConverter());
        timeTextFormatter.valueProperty().bindBidirectional(noteViewModel.time);
        tfTime.setTextFormatter(timeTextFormatter);

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
        final Note newNote = noteViewModel.getNote();
        document.getNoteList().set(newNote);
        return true;
    }
}
