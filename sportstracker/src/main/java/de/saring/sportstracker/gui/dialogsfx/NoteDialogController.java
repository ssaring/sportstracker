package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.NumberFormat;
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

    @FXML
    private TextField tfHour;

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
        taText.setText(note.getText() == null ? "" : note.getText());
    }

    @Override
    protected boolean validateAndStore() {

        // create a new Note, because user can cancel after validation errors
        // => so we don't modify the original Note
        Note newNote = new Note(note.getId());

        // TODO extract the InputValidators to a util class?

        // check date input
        if (dpDate.getValue() == null) {
            context.showFxErrorDialog(dpDate.getScene().getWindow(),
                    "common.error", "st.dlg.note.error.date");
            dpDate.requestFocus();
            return false;
        }

        // check hour input
        int hour;
        try {
            hour = NumberFormat.getInstance().parse(tfHour.getText()).intValue();
            if (hour < 0 || hour > 23) {
                throw new Exception("The hour value must be in range 0..23!");
            }
        } catch (Exception e) {
            context.showFxErrorDialog(tfHour.getScene().getWindow(),
                    "common.error", "st.dlg.note.error.time");
            tfHour.selectAll();
            tfHour.requestFocus();
            return false;
        }

        // check minute input
        int minute;
        try {
            minute = NumberFormat.getInstance().parse(tfMinute.getText()).intValue();
            if (minute < 0 || minute > 59) {
                throw new Exception("The minute value must be in range 0..59!");
            }
        } catch (Exception e) {
            context.showFxErrorDialog(tfMinute.getScene().getWindow(),
                    "common.error", "st.dlg.note.error.time");
            tfMinute.selectAll();
            tfMinute.requestFocus();
            return false;
        }

        // store date and time of the note
        LocalDateTime newDateTime = dpDate.getValue().atTime(hour, minute);
        newNote.setDateTime(newDateTime);

        // get note text
        String strText = taText.getText().trim();
        if (strText.length() == 0) {
            context.showFxErrorDialog(taText.getScene().getWindow(),
                    "common.error", "st.dlg.note.error.no_text");
            taText.requestFocus();
            return false;
        }
        newNote.setText(strText);

        // finally store the new Note and close dialog
        document.getNoteList().set(newNote);
        return true;
    }
}
