package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Note;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This ViewModel class provides JavaFX properties of all Note attributes to be edited in the dialog.
 * So they can be bound to the appropriate dialog view controls.
 *
 * @author Stefan Saring
 */
public class NoteViewModel {

    private final int id;
    public final ObjectProperty<LocalDate> date;
    public final IntegerProperty hour;
    public final IntegerProperty minute;
    public final StringProperty text;

    /**
     * Creates the NoteViewModel with JavaFX properties for the passed Note object.
     *
     * @param note Note to be edited
     */
    public NoteViewModel(final Note note) {
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
