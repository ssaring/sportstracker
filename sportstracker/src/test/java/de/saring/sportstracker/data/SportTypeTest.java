package de.saring.sportstracker.data;

import de.saring.util.unitcalc.SpeedMode;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains all unit tests for the SportType class.
 *
 * @author Stefan Saring
 */
public class SportTypeTest {

    private SportType type;

    /**
     * This method initializes the environment for testing.
     */
    @BeforeEach
    public void setUp() {

        // create a new sport type with some test content
        type = new SportType(1L);
        type.setName("Cycling");
        type.setSpeedMode(SpeedMode.SPEED);
        type.setIcon("cycling.png");
        type.setColor(Color.RED);
        type.setRecordDistance(true);

        SportSubType sub1 = new SportSubType(1L);
        sub1.setName("MTB");
        type.getSportSubTypeList().set(sub1);

        SportSubType sub2 = new SportSubType(2L);
        sub2.setName("Road");
        type.getSportSubTypeList().set(sub2);

        SportSubType sub3 = new SportSubType(3L);
        sub3.setName("City");
        type.getSportSubTypeList().set(sub3);

        Equipment equipment1 = new Equipment(1L);
        equipment1.setName("Cannondale Jekyll");
        type.getEquipmentList().set(equipment1);

        Equipment equipment2 = new Equipment(2L);
        equipment2.setName("Cannondale R800");
        type.getEquipmentList().set(equipment2);
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testClone() {
        SportType clone = type.clone();

        // compare original and cloned object => needs to be same
        assertNotNull(clone);
        assertFalse(type == clone);
        assertEquals(type.getId(), clone.getId());
        assertEquals(type.getName(), clone.getName());
        assertEquals(type.getSpeedMode(), clone.getSpeedMode());
        assertEquals(type.getIcon(), clone.getIcon());
        assertEquals(type.getColor(), clone.getColor());
        assertEquals(type.isRecordDistance(), clone.isRecordDistance());
        assertEquals(type.getSportSubTypeList().size(), clone.getSportSubTypeList().size());
        assertEquals(type.getSportSubTypeList().getByID(2).getName(), clone.getSportSubTypeList().getByID(2).getName());
        assertEquals(type.getSportSubTypeList().getByID(3).getName(), clone.getSportSubTypeList().getByID(3).getName());

        // modify original object only
        type.setName("Stuff");
        type.setSpeedMode(SpeedMode.PACE);
        type.setIcon("noicon.png");
        type.setColor(Color.PINK);

        type.getSportSubTypeList().removeByID(3);
        type.getSportSubTypeList().getByID(2).setName("Road123");

        type.getEquipmentList().removeByID(1);
        type.getEquipmentList().getByID(2).setName("Litespeed Icon");

        // compare original and cloned object => needs to be different
        assertFalse(type.getName().equals(clone.getName()));
        assertFalse(type.getSpeedMode().equals(clone.getSpeedMode()));
        assertFalse(type.getIcon().equals(clone.getIcon()));
        assertFalse(type.getColor().equals(clone.getColor()));

        assertTrue(type.getSportSubTypeList().size() != clone.getSportSubTypeList().size());
        assertFalse(type.getSportSubTypeList().getByID(2).getName().equals(clone.getSportSubTypeList().getByID(2).getName()));

        assertTrue(type.getEquipmentList().size() != clone.getEquipmentList().size());
        assertNull(type.getEquipmentList().getByID(1));
        assertNotNull(clone.getEquipmentList().getByID(1));
        assertFalse(type.getEquipmentList().getByID(2).getName().equals(clone.getEquipmentList().getByID(2).getName()));
    }
}
