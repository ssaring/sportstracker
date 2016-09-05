package de.saring.sportstracker.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.PatternSyntaxException;

import org.junit.Before;
import org.junit.Test;

/**
 * This class contains all unit tests for the NoteList class.
 *
 * @author Stefan Saring
 */
public class NoteListTest {

    private NoteList list;

    /**
     * Setup of test data.
     */
    @Before
    public void setUp() {

        // create a new list with some test content
        list = new NoteList();

        Note note1 = new Note(1);
        note1.setDateTime(LocalDateTime.of(2003, 9, 2, 0, 0, 0));
        note1.setComment("Dummy note 1");
        list.set(note1);

        Note note2 = new Note(2);
        note2.setDateTime(LocalDateTime.of(2003, 8, 20, 0, 0, 0));
        note2.setComment("Dummy note 2");
        list.set(note2);

        Note note3 = new Note(3);
        note3.setDateTime(LocalDateTime.of(2003, 9, 6, 0, 0, 0));
        note3.setComment("Dummy note 3");
        list.set(note3);
    }

    /**
     * Tests for getEntriesForFilter().
     */
    @Test
    public void testGetEntriesForFilter1() {

        // all 3 notes should be found
        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 2, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(3, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): no notes should be found (no notes in time span).
     */
    @Test
    public void testGetEntriesForFilter2() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 04, 30));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(0, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): all notes should be found (no notes in time span, but filter is set to type 
     * NOTE).
     */
    @Test
    public void testGetEntriesForFilter3() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 04, 30));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(3, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 2 notes should be found (in the specified time span).
     */
    @Test
    public void testGetEntriesForFilter4() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 9, 2));
        filter.setDateEnd(LocalDate.of(2003, 9, 6));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(2, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 3 notes should be found (with comment substring "NOTE").
     */
    @Test
    public void testGetEntriesForFilter5() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("NOTE");
        filter.setRegularExpressionMode(false);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(3, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 1 note should be found (with comment substring "OTE 2").
     */
    @Test
    public void testGetEntriesForFilter6() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString(" OTE 2 ");
        filter.setRegularExpressionMode(false);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(1, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 0 notes should be found (with comment substring "NotInThere").
     */
    @Test
    public void testGetEntriesForFilter7() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("NotInThere");
        filter.setRegularExpressionMode(false);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(0, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 2 notes should be found (with comment regular expression substring "ote [0-2]").
     */
    @Test
    public void testGetEntriesForFilter8() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("ote [0-2]");
        filter.setRegularExpressionMode(true);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(2, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 3 notes should be found (with comment regular expression substring for 3 small 
     * characters).
     */
    @Test
    public void testGetEntriesForFilter9() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("[a-z]{3}");
        filter.setRegularExpressionMode(true);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(3, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 0 notes should be found (with comment regular expression substring for 8 small
     * characters).
     */
    @Test
    public void testGetEntriesForFilter10() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("[a-z]{8}");
        filter.setRegularExpressionMode(true);

        EntryList<Note> entryList = list.getEntriesForFilter(filter);
        assertEquals(0, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): use of regular expression "ote [0-2" with syntax error => ArgumentException 
     * needs to be thrown.
     */
    @Test
    public void testGetEntriesForFilter11() {

        try {
            EntryFilter filter = new EntryFilter();
            filter.setDateStart(LocalDate.of(2003, 1, 1));
            filter.setDateEnd(LocalDate.of(2003, 12, 31));
            filter.setEntryType(EntryFilter.EntryType.NOTE);
            filter.setCommentSubString("cise [0-2");
            filter.setRegularExpressionMode(true);

            list.getEntriesForFilter(filter);
            fail("The expected System.ArgumentException was not thown!");
        } catch (PatternSyntaxException pse) {
        } catch (Exception e) {
            fail("The expected System.ArgumentException was not thown!");
        }
    }
}
