package de.saring.util.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests of class IdDateObjectList.
 * 
 * @author Stefan Saring
 */
public class IdDateObjectListTest {

    /** The instance to test. */
    private IdDateObjectList<DateNameObject> list;

    @Before
    public void setUp () {
        list = new IdDateObjectList<> ();
        list.set (new DateNameObject (1, createDate (2009, 01, 05, 21, 30, 0), "one"));
        list.set (new DateNameObject (2, createDate (2008, 11, 11, 20, 30, 0), "two"));
        list.set (new DateNameObject (3, createDate (2009, 01, 07, 11, 40, 0), "three"));

        checkDateOrder ();
    }

    /**
     * Test of set(): must add a new object to list at position 2.
     */
    @Test
    public void testSetAdd () {
        list.set (new  DateNameObject (4, createDate (2008, 12, 25, 21, 30, 0), "four"));
        assertEquals (list.size (), 4);
        assertEquals ("four", list.getAt (1).getName ());
        checkDateOrder ();
    }

    /**
     * Test of set(): must replace the object in list at position 3 with the 
     * new one with the allready used ID.
     */
    @Test
    public void testSetReplaceNew () {
        assertEquals ("three", list.getAt (2).getName ());
        list.set (new  DateNameObject (3, createDate (2009, 01, 07, 11, 40, 0), "three-new"));
        assertEquals (list.size (), 3);
        assertEquals ("three-new", list.getAt (2).getName ());
        checkDateOrder ();
    }

    /**
     * Test of set(): must store the modified object in list from position 3
     * at the proper list position for the modified date.
     */
    @Test
    public void testSetReplaceModified () {
        DateNameObject no3 = list.getAt (2);
        assertEquals ("three", no3.getName ());
        no3.setDate (createDate (2008, 06, 12, 12, 45, 0));
        no3.setName ("three-new");

        list.set (no3);
        assertEquals (list.size (), 3);
        assertEquals ("three-new", list.getAt (0).getName ());
        checkDateOrder ();
    }

    /**
     * Test of set(): must fail when the entry is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSetNull () {
        list.set (null);
    }

    /**
     * Test of set(): must fail when date is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testSetDateNull () {
        list.set (new  DateNameObject (4, null, "four"));
    }

    /**
     * Test of getEntriesInTimeRange(): must find all entries, the time range 
     * includes the earliest and latest entry.
     */
    @Test
    public void testGetEntriesInTimeRangeFindAll () {
        List<DateNameObject> lFound = list.getEntriesInTimeRange (
            createDate (2008, 11, 11, 20, 30, 0),
            createDate (2009, 01, 07, 11, 40, 0));
        
        assertEquals (3, lFound.size ());
        assertEquals ("two", lFound.get (0).getName ());
        assertEquals ("one", lFound.get (1).getName ());
        assertEquals ("three", lFound.get (2).getName ());
    }

    /**
     * Test of getEntriesInTimeRange(): must find only one entries, the time range 
     * does not include the earliest and latest entry.
     */
    @Test
    public void testGetEntriesInTimeRangeFindOne () {
        List<DateNameObject> lFound = list.getEntriesInTimeRange (
            createDate (2008, 11, 11, 20, 30, 1),
            createDate (2009, 01, 07, 11, 39, 59));
        
        assertEquals (1, lFound.size ());
        assertEquals ("one", lFound.get (0).getName ());
    }
    
    /**
     * Test of getEntriesInTimeRange(): must fail when one of the dates is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGetEntriesInTimeRangeNull () {
        list.getEntriesInTimeRange (new Date (), null);
    }

    /**
     * Test of getEntriesInTimeRange(): must fail when one of the dates is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGetEntriesInTimeRangeInvalidRange () {
        list.getEntriesInTimeRange (
            createDate (2009, 11, 11, 20, 30, 0),
            createDate (2009, 10, 11, 20, 30, 0));
    }
    
    private Date createDate (int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance ();
        calendar.clear ();
        calendar.set (year, month, day, hour, minute, second);
        return calendar.getTime ();
    }

    /**
     * This helper method makes sure that all IdDateObjects in the list are in
     * correct chronological order (the date of the previous exercise is never
     * bigger then the date of the current exercise).
     */
    private void checkDateOrder () {
        Date previousDate = createDate (1970, 1, 1, 0, 0, 0);

        for (IdDateObject temp : list) {
            assertTrue (previousDate.before (temp.getDate ()));
            previousDate = temp.getDate ();
        }
    }

    
    /** 
     * Subclass of abstract class IdDateObject for testing.
     */
    static class DateNameObject extends IdDateObject {
        private String name;

        public DateNameObject (int id, Date date, String name) {
            super (id);
            setDate (date);
            this.name = name;
        }

        public String getName () {
            return name;
        }

        public void setName (String name) {
            this.name = name;
        }
    }
}