package de.saring.sportstracker.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains all unit tests for the ExerciseList class.
 *
 * @author Stefan Saring
 */
public class ExerciseListTest {

    private ExerciseList list;
    private SportTypeList sportTypeList;

    /**
     * This method initializes the environment for testing.
     */
    @BeforeEach
    public void setUp() {

        // create a sport type list with 2 sport types with 2 sport subtypes in each
        sportTypeList = new SportTypeList();

        SportType type1 = new SportType(1L);
        type1.setName("SportType 1");
        SportSubType subType11 = new SportSubType(11L);
        subType11.setName("SportSubType 11");
        type1.getSportSubTypeList().set(subType11);
        SportSubType subType12 = new SportSubType(12L);
        subType12.setName("SportSubType 12");
        type1.getSportSubTypeList().set(subType12);
        sportTypeList.set(type1);

        SportType type2 = new SportType(2L);
        type2.setName("SportType 2");
        SportSubType subType21 = new SportSubType(21L);
        subType21.setName("SportSubType 21");
        type2.getSportSubTypeList().set(subType21);
        SportSubType subType22 = new SportSubType(22L);
        subType22.setName("SportSubType 22");
        type2.getSportSubTypeList().set(subType22);
        sportTypeList.set(type2);

        // add two equipment's to sport type 2
        Equipment eq21 = new Equipment(21L);
        eq21.setName("Equipment 21");
        type2.getEquipmentList().set(eq21);
        Equipment eq22 = new Equipment(22L);
        eq22.setName("Equipment 22");
        type2.getEquipmentList().set(eq22);

        // create a new list with some test content
        list = new ExerciseList();

        Exercise exe1 = new Exercise(1L);
        exe1.setSportType(type1);
        exe1.setSportSubType(subType12);
        exe1.setDateTime(LocalDateTime.of(2003, 9, 2, 0, 0, 0));
        exe1.setComment("DummyExercise 1");
        exe1.setIntensity(Exercise.IntensityType.LOW);
        list.set(exe1);

        Exercise exe2 = new Exercise(2L);
        exe2.setSportType(type1);
        exe2.setSportSubType(subType11);
        exe2.setDateTime(LocalDateTime.of(2003, 8, 20, 0, 0, 0));
        exe2.setComment("DummyExercise 2");
        exe2.setIntensity(Exercise.IntensityType.HIGH);
        list.set(exe2);

        Exercise exe3 = new Exercise(3L);
        exe3.setSportType(type2);
        exe3.setSportSubType(subType22);
        exe3.setEquipment(eq22);
        exe3.setDateTime(LocalDateTime.of(2003, 9, 6, 0, 0, 0));
        exe3.setComment("DummyExercise 3");
        exe3.setIntensity(Exercise.IntensityType.LOW);
        exe3.setEquipment(eq22);
        list.set(exe3);
    }

    /**
     * Tests the appropriate method.
     */
    @Test
    public void testUpdateSportTypes() {
        // get exercise 3 for tests and check its sport type and subtype
        Exercise exercise3 = list.getByID(3);
        assertEquals(3, exercise3.getId());
        assertEquals("SportType 2", exercise3.getSportType().getName());
        assertEquals("SportSubType 22", exercise3.getSportSubType().getName());

        SportType sportType2 = sportTypeList.getByID(2);
        SportSubType sportSubType22 = sportType2.getSportSubTypeList().getByID(22);
        assertNotNull(sportType2);
        assertNotNull(sportSubType22);

        // clone, edit and store the SportSubType 22 (the way the GUI editor works)
        SportSubType sportSubType22New = (SportSubType) sportSubType22.clone();
        sportSubType22New.setName("SportSubType 22 - New");
        sportType2.getSportSubTypeList().set(sportSubType22New);

        // the exercise will still have the old sport subtype
        // => after UpdateSportTypes() it neesds to reference to the new subtype
        assertEquals("SportSubType 22", exercise3.getSportSubType().getName());
        list.updateSportTypes(sportTypeList);
        assertEquals("SportSubType 22 - New", exercise3.getSportSubType().getName());

        // clone, edit and store the SportType 2 (the way the GUI editor works)
        SportType sportType2New = (SportType) sportType2.clone();
        sportType2New.setName("SportType 2 - New");
        sportTypeList.set(sportType2New);

        // the exercise will still have the old sport type
        // => after UpdateSportTypes() it needs to reference to the new sport type
        assertEquals("SportType 2", exercise3.getSportType().getName());
        list.updateSportTypes(sportTypeList);
        assertEquals("SportType 2 - New", exercise3.getSportType().getName());

        // clone, edit and store the Equipment 22 (the way the GUI editor works)
        Equipment equipment22Old = sportTypeList.getByID(2).getEquipmentList().getByID(22);
        assertEquals("Equipment 22", equipment22Old.getName());
        Equipment equipment22New = (Equipment) equipment22Old.clone();
        equipment22New.setName("Equipment 22 - New");
        sportTypeList.getByID(2).getEquipmentList().set(equipment22New);

        // the exercise will still have the old equipment
        // => after UpdateSportTypes() it needs to reference to the new equipment
        assertEquals("Equipment 22", exercise3.getEquipment().getName());
        list.updateSportTypes(sportTypeList);
        assertEquals("Equipment 22 - New", exercise3.getEquipment().getName());
    }

    /**
     * Test of getEntriesForFilter(): all 3 exercises should be found.
     */
    @Test
    public void testGetEntriesForFilter1() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 2, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = list.getEntriesForFilter(filter);
        assertEquals(3, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): no exercises should be found (no exercises in time span).
     */
    @Test
    public void testGetEntriesForFilter2() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 04, 30));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(0, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): all exercises should be found (no exercises in time span, but filter is set to
     * type NOTE).
     */
    @Test
    public void testGetEntriesForFilter3() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 04, 30));
        filter.setEntryType(EntryFilter.EntryType.NOTE);
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(3, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): no exercises should be found (sport type does not exists).
     */
    @Test
    public void testGetEntriesForFilter4() {

        SportType sportTypeUnknown = new SportType(4L);

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeUnknown);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(0, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): no exercises should be found (sport subtype does not exists).
     */
    @Test
    public void testGetEntriesForFilter5() {

        SportSubType sportSubTypeUnknown = new SportSubType(7L);

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(1));
        filter.setSportSubType(sportSubTypeUnknown);
        filter.setIntensity(null);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(0, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): no exercises should be found (no exercise with intensity NORMAL).
     */
    @Test
    public void testGetEntriesForFilter6() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(Exercise.IntensityType.NORMAL);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(0, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): no exercises should be found (in specified time span and with intensity LOW and sport type ID 1).
     */
    @Test
    public void testGetEntriesForFilter7() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 7, 31));
        filter.setSportType(sportTypeList.getByID(1));
        filter.setSportSubType(null);
        filter.setIntensity(Exercise.IntensityType.LOW);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(0, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 2 exercises should be found (in the specified time span).
     */
    @Test
    public void testGetEntriesForFilter8() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 9, 2));
        filter.setDateEnd(LocalDate.of(2003, 9, 6));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(2, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 2 exercises should be found (with sport type ID 1).
     */
    @Test
    public void testGetEntriesForFilter9() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(1));
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(2, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 1 exercises should be found (with sport type ID 1 and sport subtype ID 2).
     */
    @Test
    public void testGetEntriesForFilter10() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(1));
        filter.setSportSubType(filter.getSportType().getSportSubTypeList().getByID(12));
        filter.setIntensity(null);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(1, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 2 exercises should be found (with intensity LOW).
     */
    @Test
    public void testGetEntriesForFilter11() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(Exercise.IntensityType.LOW);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(2, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 1 exercises should be found (with intensity LOW and sport type ID 1).
     */
    @Test
    public void testGetEntriesForFilter12() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(1));
        filter.setSportSubType(null);
        filter.setIntensity(Exercise.IntensityType.LOW);
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(1, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 1 exercise (ID 3) should be found for equipment with ID 22.
     */
    @Test
    public void testGetEntriesForFilter13() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(2));
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setEquipment(filter.getSportType().getEquipmentList().getByID(22));
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(1, exeList.size());
        assertEquals(3, exeList.getAt(0).getId());
    }

    /**
     * Test of getEntriesForFilter(): no exercise should be found for equipment with ID 21.
     */
    @Test
    public void testGetEntriesForFilter14() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(2));
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setEquipment(filter.getSportType().getEquipmentList().getByID(21));
        filter.setCommentSubString("");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(0, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 2 exercises should be found (with comment substring "EXERCISE" and sport type ID
     * 1).
     */
    @Test
    public void testGetEntriesForFilter15() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(1));
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("EXERCISE");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(2, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 1 exercise should be found (with comment substring "CISE 2" and sport type ID 1).
     */
    @Test
    public void testGetEntriesForFilter16() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(1));
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString(" CISE 2 ");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(1, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 0 exercises should be found (with comment substring "NotInThere" and sport type
     * ID 1).
     */
    @Test
    public void testGetEntriesForFilter17() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(sportTypeList.getByID(1));
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("NotInThere");
        filter.setRegularExpressionMode(false);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(0, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 2 exercises should be found (with comment regular expression substring
     * "cise [0-2]").
     */
    @Test
    public void testGetEntriesForFilter18() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("cise [0-2]");
        filter.setRegularExpressionMode(true);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(2, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 3 exercises should be found (with comment regular expression substring for 4 small
     * characters).
     */
    @Test
    public void testGetEntriesForFilter19() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("[a-z]{4}");
        filter.setRegularExpressionMode(true);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(3, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): 0 exercises should be found (with comment regular expression substring for 8 small
     * characters).
     */
    @Test
    public void testGetEntriesForFilter20() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("[a-z]{8}");
        filter.setRegularExpressionMode(true);

        EntryList<Exercise> exeList = exeList = list.getEntriesForFilter(filter);
        assertEquals(0, exeList.size());
    }

    /**
     * Test of getEntriesForFilter(): use of regular expression "cise [0-2" with syntax error => PatternSyntaxException
     * needs to be thrown.
     */
    @Test
    public void testGetEntriesForFilter21() {

        EntryFilter filter = new EntryFilter();
        filter.setDateStart(LocalDate.of(2003, 1, 1));
        filter.setDateEnd(LocalDate.of(2003, 12, 31));
        filter.setSportType(null);
        filter.setSportSubType(null);
        filter.setIntensity(null);
        filter.setCommentSubString("cise [0-2");
        filter.setRegularExpressionMode(true);

        assertThrows(PatternSyntaxException.class, () ->
            list.getEntriesForFilter(filter));
    }
}
