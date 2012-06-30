package de.saring.sportstracker.data;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains all unit tests for the Equipment class.
 *
 * @author Stefan Saring
 */
public class EquipmentTest {

    /**
     * Tests of method clone().
     */
    @Test
    public void testClone () {
        Equipment eOrg = new Equipment (123);
        eOrg.setName ("Equip");

        Equipment eClone = eOrg.clone ();
        assertFalse (eOrg == eClone);
        assertEquals (123, eClone.getId ());
        assertEquals ("Equip", eClone.getName ());
    }
}
