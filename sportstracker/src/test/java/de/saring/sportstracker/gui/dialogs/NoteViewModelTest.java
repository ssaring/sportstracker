package de.saring.sportstracker.gui.dialogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.SportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Unit tests of class NoteViewModel.
 *
 * @author Stefan Saring
 */
public class NoteViewModelTest {

    private Note note;

    @BeforeEach
    public void setUp() {
        note = new Note(123L);
        note.setDateTime(LocalDateTime.of(2014, 10, 20, 7, 30, 0));
        note.setSportType(new SportType(234L));
        note.setEquipment(new Equipment(345L));
        note.setComment("Foo Bar");
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
        assertEquals(note.getSportType(), unmodifiedNote.getSportType());
        assertEquals(note.getEquipment(), unmodifiedNote.getEquipment());
        assertEquals(note.getComment(), unmodifiedNote.getComment());

        // test after modifications
        viewModel.time.set(LocalTime.of(14, 45));
        viewModel.sportType.set(null);
        viewModel.equipment.set(null);
        viewModel.comment.set("  Bar Foo  ");

        Note modifiedNote = viewModel.getNote();
        assertEquals(LocalDateTime.of(2014, 10, 20, 14, 45, 0), modifiedNote.getDateTime());
        assertNull(modifiedNote.getSportType());
        assertNull(modifiedNote.getEquipment());
        assertEquals("Bar Foo", modifiedNote.getComment());
    }
}
