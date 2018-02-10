package de.saring.sportstracker.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class contains all unit tests for the WeightList class.
 *
 * @author Stefan Saring
 */
public class WeightListTest {

    private WeightList list;

    /**
     * Setup of test data.
     */
    @BeforeEach
    public void setUp() {

        // create a new list with some test content
        list = new WeightList();

        Weight weight1 = new Weight(1);
        weight1.setDateTime(LocalDateTime.of(2003, 9, 2, 0, 0, 0));
        weight1.setComment("Dummy weight 1");
        list.set(weight1);

        Weight weight2 = new Weight(2);
        weight2.setDateTime(LocalDateTime.of(2003, 8, 20, 0, 0, 0));
        weight2.setComment("Dummy weight 2");
        list.set(weight2);

        Weight weight3 = new Weight(3);
        weight3.setDateTime(LocalDateTime.of(2003, 9, 6, 0, 0, 0));
        weight3.setComment("Dummy weight 3");
        list.set(weight3);
    }

    /**
     * Tests for getEntriesForFilter(): all 3 weights should be found.
     */
    @Test
    public void testGetEntriesForFilter1() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 2, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(3, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): no weights should be found (no weights in time span).
     */
    @Test
    public void testGetEntriesForFilter2() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 04, 30));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(0, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): all weights should be found (no weights in time span, 
     * but filter is set to type NOTE).
     */
    @Test
    public void testGetEntriesForFilter3() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 04, 30));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(3, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 2 weights should be found (in the specified time span).
     */
    @Test
    public void testGetEntriesForFilter4() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 9, 2));
        filter.setDateEnd(LocalDate.of(2003, 9, 6));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(2, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 3 weights should be found (with comment substring "NOTE").
     */
    @Test
    public void testGetEntriesForFilter5() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("WEIGHT");
        filter.setRegularExpressionMode(false);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(3, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 1 weight should be found (with comment substring "EIGHT 2").
     */
    @Test
    public void testGetEntriesForFilter6() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString(" EIGHT 2 ");
        filter.setRegularExpressionMode(false);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(1, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 0 weights should be found (with comment substring "NotInThere").
     */
    @Test
    public void testGetEntriesForFilter7() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("NotInThere");
        filter.setRegularExpressionMode(false);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(0, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 2 weights should be found (with comment regular expression substring "ght [0-2]").
     */
    @Test
    public void testGetEntriesForFilter8() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("ght [0-2]");
        filter.setRegularExpressionMode(true);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(2, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 3 weights should be found (with comment regular expression substring for 3 
     * small characters).
     */
    @Test
    public void testGetEntriesForFilter9() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("[a-z]{3}");
        filter.setRegularExpressionMode(true);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(3, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): 0 weights should be found (with comment regular expression substring for 8 
     * small characters).
     */
    @Test
    public void testGetEntriesForFilter10() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("[a-z]{8}");
        filter.setRegularExpressionMode(true);

        EntryList<Weight> entryList = list.getEntriesForFilter(filter);
        assertEquals(0, entryList.size());
    }

    /**
     * Tests for getEntriesForFilter(): use of regular expression "ote [0-2" with syntax error => PatternSyntaxException
     * needs to be thrown.
     */
    @Test
    public void testGetEntriesForFilter11() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setEntryType(EntryFilter.EntryType.WEIGHT);
        filter.setCommentSubString("cise [0-2");
        filter.setRegularExpressionMode(true);

        assertThrows(PatternSyntaxException.class, () ->
            list.getEntriesForFilter(filter));
    }
}
