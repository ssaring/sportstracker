package de.saring.util.data;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests of class IdObjectList.
 * 
 * @author Stefan Saring
 */
public class IdObjectListTest {

    /** The instance to test. */
    private IdObjectList<NameObject> list;

    @Before
    public void setUp () {
        
        // create a new list with some content
        list = new IdObjectList<> ();
        list.set (new NameObject (1, "one"));
        list.set (new NameObject (2, "two"));
        list.set (new NameObject (3, "three"));
    }

    /**
     * Test of getByID method, of class IdObjectList.
     */
    @Test
    public void getByID () {
        assertNull (list.getByID (0));
        assertNull (list.getByID (1000));
        assertEquals ("one", list.getByID (1).getName ());
        assertEquals ("three", list.getByID (3).getName ());
    }

    /**
     * Test of getAt method, of class IdObjectList.
     */
    @Test
    public void getAt () {
        assertEquals ("one", list.getAt (0).getName ());
        assertEquals ("three", list.getAt (2).getName ());
        
        try {
            list.getAt (3);
            fail ("Must fail, index is not valid...");
        }
        catch (IndexOutOfBoundsException e) {}
    }
    
    /**
     * Test of indexOf method, of class IdObjectList.
     */
    @Test
    public void indexOf () {
        assertEquals (1, list.indexOf (new NameObject (2, "two")));
    }

    /**
     * Test of set method set() of class IdObjectList.
     */
    @Test
    public void set () {
        
        // this should add an element to list
        list.set (new NameObject (4, "four"));
        assertEquals (4, list.size ());
        assertEquals ("four", list.getAt (3).getName ());

        // this should replace a list-element
        list.set (new NameObject (2, "zwei"));
        assertEquals (4, list.size ());
        assertEquals ("zwei", list.getByID (2).getName ());
    }

    /**
     * Test of set method set() of class IdObjectList: must fail on null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void setNull () {
        list.set (null);
    }

    /**
     * Test of set method set() of class IdObjectList: must fail when ID = 0.
     */
    @Test(expected=IllegalArgumentException.class)
    public void setIdZero () {
        list.set (new NameObject (0, "Null"));
    }

    /**
     * Test of removeByID method, of class IdObjectList.
     */
    @Test
    public void removeByID () {
        
        // this delete must work
        assertTrue (list.removeByID (3));
        assertEquals (2, list.size ());
        assertNull (list.getByID (3));

        // this delete can't work
        assertFalse (list.removeByID (3));
        assertEquals (2, list.size ());
    }

    /**
     * Test of getNewID method, of class IdObjectList.
     */
    @Test
    public void getNewID () {
        
        // ID's 1-3 in use => next needs to be 4
        assertEquals (list.getNewID (), 4);

        // add ID's 4 and 6 to list => next needs to be 5
        list.set (new NameObject (4, "four"));
        list.set (new NameObject (6, "six"));
        assertEquals (5, list.getNewID ());

        // remove ID 2 from list => next needs to be 2
        list.removeByID (2);
        assertEquals (2, list.getNewID ());
    }

    /** 
     * Subclass of abstract class IdObject for testing.
     */
    class NameObject extends IdObject {
        private String name;

        public NameObject (int id, String name) {
            super (id);
            this.name = name;
        }

        public String getName () {
            return name;
        }
    }
}