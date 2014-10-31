package de.saring.sportstracker.gui.dialogsfx;

import static org.junit.Assert.assertEquals;

import de.saring.sportstracker.data.Note;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Unit tests of class NoteViewModel.
 *
 * @author Stefan Saring
 */
public class NoteViewModelTest {

    private Note note;

    @Before
    public void setUp() {
        note = new Note(123);
        note.setDateTime(LocalDateTime.of(2014, 10, 20, 7, 30, 0));
        note.setText("Foo Bar");
    }

    /**
     * Test of method getNote().
     */
    @Test
    public void testGetNote() {
        NoteViewModel viewModel = new NoteViewModel(note);

        // test without modifications
        Note unmodifiedNote = viewModel.getNote();
        assertEquals(note.getId(), unmodifiedNote.getId());
        assertEquals(note.getDateTime(), unmodifiedNote.getDateTime());
        assertEquals(note.getText(), unmodifiedNote.getText());

        // test after modifications
        viewModel.hour.set(14);
        viewModel.minute.set(45);
        viewModel.text.set("  Bar Foo  ");

        Note modifiedNote = viewModel.getNote();
        assertEquals(LocalDateTime.of(2014, 10, 20, 14, 45, 0), modifiedNote.getDateTime());
        assertEquals("Bar Foo", modifiedNote.getText());
    }
}
