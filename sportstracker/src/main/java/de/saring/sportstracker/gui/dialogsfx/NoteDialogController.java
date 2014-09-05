package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.util.InputValidators;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDateTime;

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

    // TODO use formatted TextField, will be introduced in JavaFX future
    @FXML
    private TextField tfHour;

    // TODO use formatted TextField, will be introduced in JavaFX future
    @FXML
    private TextField tfMinute;

    @FXML
    private TextArea taText;

    /** This is the Note object edited in this dialog. */
    private Note note;

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
        this.note = note;

        final String dlgTitleKey = note.getText() == null ? "st.dlg.note.title.add" : "st.dlg.note.title";
        final String dlgTitle = context.getFxResources().getString(dlgTitleKey);

        showEditDialog("/fxml/NoteDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setInitialValues() {

        // TODO use JavaFX Binding?
        dpDate.setValue(note.getDateTime().toLocalDate());
        tfHour.setText(String.format("%02d", note.getDateTime().getHour()));
        tfMinute.setText(String.format("%02d", note.getDateTime().getMinute()));
        taText.setText(note.getText());
    }

    @Override
    protected boolean validateAndStore() {

        // create a new Note, because user can cancel after validation errors
        // => so we don't modify the original Note
        Note newNote = new Note(note.getId());

        // check date input
        if (dpDate.getValue() == null) {
            context.showFxErrorDialog(dpDate.getScene().getWindow(),
                    "common.error", "st.dlg.note.error.date");
            dpDate.requestFocus();
            return false;
        }

        // check time inputs
        Integer hour = InputValidators.getRequiredTextControlIntegerValue(context, tfHour, 0, 23,
                "common.error", "st.dlg.note.error.time");
        if (hour == null) {
            return false;
        }

        Integer minute = InputValidators.getRequiredTextControlIntegerValue(context, tfMinute, 0, 59,
                "common.error", "st.dlg.note.error.time");
        if (minute == null) {
            return false;
        }

        // store date and time of the note
        LocalDateTime newDateTime = dpDate.getValue().atTime(hour, minute);
        newNote.setDateTime(newDateTime);

        // get note text
        String strText = InputValidators.getRequiredTextControlValue(context, taText,
                "common.error", "st.dlg.note.error.no_text");
        if (strText == null) {
            return false;
        }
        newNote.setText(strText);

        // finally store the new Note
        document.getNoteList().set(newNote);
        return true;
    }
}
