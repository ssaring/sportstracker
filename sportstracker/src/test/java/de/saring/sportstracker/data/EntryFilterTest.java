package de.saring.sportstracker.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * This class contains all unit tests for the EntryFilter class.
 *
 * @author Stefan Saring
 */
public class EntryFilterTest {

    private SportTypeList sportTypeList;

    /**
     * This method initializes the environment for testing.
     */
    @BeforeEach
    public void setUp() {

        // create a sport type list with 2 sport types with 2 sport subtypes in each
        sportTypeList = new SportTypeList();

        SportType type1 = new SportType(1);
        type1.setName("SportType 1");
        SportSubType subType11 = new SportSubType(11);
        subType11.setName("SportSubType 11");
        type1.getSportSubTypeList().set(subType11);
        SportSubType subType12 = new SportSubType(12);
        subType12.setName("SportSubType 12");
        type1.getSportSubTypeList().set(subType12);
        sportTypeList.set(type1);

        SportType type2 = new SportType(2);
        type2.setName("SportType 2");
        SportSubType subType21 = new SportSubType(21);
        subType21.setName("SportSubType 21");
        type2.getSportSubTypeList().set(subType21);
        SportSubType subType22 = new SportSubType(22);
        subType22.setName("SportSubType 22");
        type2.getSportSubTypeList().set(subType22);
        sportTypeList.set(type2);
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testUpdateSportTypes() {

        EntryFilter filter = EntryFilter.createDefaultExerciseFilter();

        // nothing happens when the filter has no sport type and subtype
        filter.updateSportTypes(sportTypeList);
        assertNull(filter.getSportType());
        assertNull(filter.getSportSubType());

        // no changes in sport type list => filter sport type and subtype keeps the same
        SportType type2 = sportTypeList.getByID(2);
        filter.setSportType(type2);
        SportSubType subType22 = type2.getSportSubTypeList().getByID(22);
        filter.setSportSubType(subType22);
        filter.updateSportTypes(sportTypeList);
        assertEquals(2, filter.getSportType().getId());
        assertEquals(22, filter.getSportSubType().getId());

        // the subtypes gets removed => must alse be removed in the filter
        type2.getSportSubTypeList().removeByID(22);
        filter.updateSportTypes(sportTypeList);
        assertEquals(2, filter.getSportType().getId());
        assertNull(filter.getSportSubType());

        // the sport type gets removed => must alse be removed in the filter
        sportTypeList.removeByID(2);
        filter.updateSportTypes(sportTypeList);
        assertNull(filter.getSportType());
        assertNull(filter.getSportSubType());
    }
}
