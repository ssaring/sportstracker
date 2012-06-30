package de.saring.util.data;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests of class IdObject.
 * 
 * @author Stefan Saring
 */
public class IdObjectTest {

    /**
     * Test of equals method, of class IdObject.
     */
    @Test
    public void equals () {
        SubClass1 subClass1_1_1 = new SubClass1 (1);
        SubClass1 subClass1_1_2 = new SubClass1 (1);
        SubClass1 subClass1_3_1 = new SubClass1 (3);
        SubClass2 subClass2_1_1 = new SubClass2 (1);
        
        assertTrue (subClass1_1_1.equals (subClass1_1_2));
        assertTrue (subClass1_1_1.equals (subClass1_1_1));
        assertFalse (subClass1_1_1.equals (null));
        assertFalse (subClass1_1_1.equals (subClass1_3_1));
        assertFalse (subClass1_1_1.equals (subClass2_1_1));
    }

    // Subclasses of abstract class IdObject for testing.
    class SubClass1 extends IdObject {
        public SubClass1 (int id) {
            super (id);
        }
    }

    class SubClass2 extends IdObject {
        public SubClass2 (int id) {
            super (id);
        }
    }
}