package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.*;
import de.saring.util.gui.javafx.ColorUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * This class contains all unit tests for the XMLStorage class.
 *
 * @author Stefan Saring
 */
public class XMLStorageTest {

    private static final String EXERCISES_WRITETEST_XML = "misc/testdata/exercises-writetest.xml";
    private static final String SPORTTYPES_WRITETEST_XML = "misc/testdata/sport-types-writetest.xml";
    private static final String NOTES_WRITETEST_XML = "misc/testdata/notes-writetest.xml";
    private static final String WEIGHTS_WRITETEST_XML = "misc/testdata/weights-writetest.xml";

    // the class instance to be tested
    private XMLStorage storage;

    /**
     * This method initializes the environment for testing.
     */
    @Before
    public void setUp() {
        storage = new XMLStorage();
    }

    /**
     * This method removes all temporary files after all tests were done.
     */
    @AfterClass
    public static void cleanUpFinal() {
        deleteFileIfExists(EXERCISES_WRITETEST_XML);
        deleteFileIfExists(SPORTTYPES_WRITETEST_XML);
        deleteFileIfExists(NOTES_WRITETEST_XML);
        deleteFileIfExists(WEIGHTS_WRITETEST_XML);
    }

    private static void deleteFileIfExists(String filename) {
        new File(filename).delete();
    }

    /**
     * This helper methods checks the content of the specified sport type list.
     * It has to be exactly same as in "misc/testdata/sport-types-valid.xml".
     *
     * @param sportTypes sport type list to check
     */
    private void checkSportTypeListContent(SportTypeList sportTypes) {

        assertNotNull(sportTypes);
        assertEquals(sportTypes.size(), 2);

        // check sporttype 1
        SportType type1 = sportTypes.getByID(1);
        assertEquals(type1.getId(), 1);
        assertEquals(type1.getName(), "Cycling");
        assertTrue(type1.isRecordDistance());
        assertEquals(type1.getIcon(), "cycling.png");
        java.awt.Color type1AwtColor = ColorUtils.toAwtColor(type1.getColor());
        assertEquals(type1AwtColor.getRed(), 30);
        assertEquals(type1AwtColor.getGreen(), 50);
        assertEquals(type1AwtColor.getBlue(), 180);
        assertEquals(type1.getSportSubTypeList().size(), 4);
        assertEquals(type1.getEquipmentList().size(), 2);

        // check subtypes of sporttype 1            
        SportSubType subType1_1 = type1.getSportSubTypeList().getByID(1);
        assertEquals(subType1_1.getId(), 1);
        assertEquals(subType1_1.getName(), "MTB tour");
        SportSubType subType1_2 = type1.getSportSubTypeList().getByID(2);
        assertEquals(subType1_2.getId(), 2);
        assertEquals(subType1_2.getName(), "MTB race");
        SportSubType subType1_3 = type1.getSportSubTypeList().getByID(3);
        assertEquals(subType1_3.getId(), 3);
        assertEquals(subType1_3.getName(), "Road tour");
        SportSubType subType1_4 = type1.getSportSubTypeList().getByID(4);
        assertEquals(subType1_4.getId(), 4);
        assertEquals(subType1_4.getName(), "Road race");

        // check equipment of sporttype 1            
        Equipment equipment1_1 = type1.getEquipmentList().getByID(1);
        assertEquals(equipment1_1.getId(), 1);
        assertEquals(equipment1_1.getName(), "Cannondale Jekyll");
        Equipment equipment1_2 = type1.getEquipmentList().getByID(2);
        assertEquals(equipment1_2.getId(), 2);
        assertEquals(equipment1_2.getName(), "Cannondale R800");

        // check sporttype 2
        SportType type2 = sportTypes.getByID(2);
        assertEquals(type2.getId(), 2);
        assertEquals(type2.getName(), "Running");
        assertFalse(type2.isRecordDistance());
        assertEquals(type2.getIcon(), "running.png");
        java.awt.Color type2AwtColor = ColorUtils.toAwtColor(type2.getColor());
        assertEquals(type2AwtColor.getRed(), 210);
        assertEquals(type2AwtColor.getGreen(), 60);
        assertEquals(type2AwtColor.getBlue(), 0);
        assertEquals(type2.getSportSubTypeList().size(), 2);
        assertEquals(type2.getEquipmentList().size(), 0);

        // check subtypes of sporttype 2            
        SportSubType subType2_1 = type2.getSportSubTypeList().getByID(1);
        assertEquals(subType2_1.getId(), 1);
        assertEquals(subType2_1.getName(), "Jogging");
        SportSubType subType2_2 = type2.getSportSubTypeList().getByID(2);
        assertEquals(subType2_2.getId(), 2);
        assertEquals(subType2_2.getName(), "Competition");
    }

    /**
     * Tests of the appropriate method.
     */
    @Test
    public void testReadSportTypeList() throws STException {

        // read valid sporttype list from XML and check content
        SportTypeList sportTypes = storage.readSportTypeList("misc/testdata/sport-types-valid.xml");
        checkSportTypeListContent(sportTypes);

        // read sporttype list from not existing XML 
        // => an empty List must be returned (same as first application startup)
        SportTypeList sportTypesEmpty = storage.readSportTypeList("misc/testdata/sport-types-xyz.xml");
        assertEquals(0, sportTypesEmpty.size());

        // read invalid sporttype list from XML
        // => an STException needs to be thrown
        try {
            storage.readSportTypeList("misc/testdata/sport-types-invalid.xml");
            fail();
        } catch (STException se) {
        }
    }

    /**
     * Tests of the appropriate method.
     */
    @Test
    public void testStoreSportTypeList() throws STException {

        // read valid sporttype list from XML (has been checked in previous test)
        SportTypeList sportTypes = storage.readSportTypeList("misc/testdata/sport-types-valid.xml");

        // store the read sport type list to a new file
        storage.storeSportTypeList(sportTypes, SPORTTYPES_WRITETEST_XML);

        // read the list from the created file again
        // => compare the content, needs to be same
        SportTypeList sportTypesNew = storage.readSportTypeList(SPORTTYPES_WRITETEST_XML);
        checkSportTypeListContent(sportTypesNew);
    }

    /**
     * This helper methods checks the content of the specified exercise list.
     * It has to be exactly same as in "misc/testdata/exercises-valid.xml".
     *
     * @param exercises exercise list to check
     */
    private void checkExerciseListContent(ExerciseList exercises) {

        assertNotNull(exercises);
        assertEquals(exercises.size(), 3);

        // check exercise 1
        Exercise exe1 = exercises.getByID(1);
        assertEquals(exe1.getId(), 1);
        assertEquals(exe1.getSportType().getId(), 1);
        assertEquals(exe1.getSportSubType().getId(), 3);
        assertEquals(exe1.getDateTime(), LocalDateTime.of(2003, 07, 24, 8, 23, 45));
        assertEquals(exe1.getDuration(), 11340);
        assertEquals(exe1.getIntensity(), Exercise.IntensityType.LOW);
        assertEquals(exe1.getDistance(), 76.5f, 0.01f);
        assertEquals(exe1.getAvgSpeed(), 26.3f, 0.01f);
        assertEquals(exe1.getAvgHeartRate(), 132);
        assertEquals(exe1.getAscent(), 670);
        assertEquals(exe1.getCalories(), 1280);
        assertEquals(exe1.getHrmFile(), "20030724082345.srd");
        assertEquals(exe1.getEquipment().getId(), 2);
        assertEquals(exe1.getEquipment().getName(), "Cannondale R800");
        assertEquals(exe1.getComment(), "Rennrad-Tour DD-Stolpen-Radeberg-DD.");

        // check exercise 2
        Exercise exe2 = exercises.getByID(2);
        assertEquals(exe2.getId(), 2);
        assertEquals(exe2.getSportType().getId(), 2);
        assertEquals(exe2.getSportSubType().getId(), 1);
        assertEquals(exe2.getDateTime(), LocalDateTime.of(2003, 07, 26, 14, 13, 22));
        assertEquals(exe2.getDuration(), 4082);
        assertEquals(exe2.getIntensity(), Exercise.IntensityType.INTERVALS);
        assertEquals(exe2.getDistance(), 9.5f, 0.01f);
        assertEquals(exe2.getAvgSpeed(), 8.5f, 0.01f);
        assertEquals(exe2.getAvgHeartRate(), 143);
        assertEquals(exe2.getAscent(), 70);
        assertEquals(exe2.getCalories(), 943);
        assertEquals(exe2.getHrmFile(), "20030726141322.srd");
        assertNull(exe2.getEquipment());
        assertEquals(exe2.getComment(), "Laufen in der Heide.");

        // check exercise 3
        Exercise exe3 = exercises.getByID(3);
        assertEquals(exe3.getId(), 3);
        assertEquals(exe3.getSportType().getId(), 1);
        assertEquals(exe3.getSportSubType().getId(), 1);
        assertEquals(exe3.getDateTime(), LocalDateTime.of(2003, 07, 27, 13, 20, 0));
        assertEquals(exe3.getDuration(), 12640);
        assertEquals(exe3.getIntensity(), Exercise.IntensityType.NORMAL);
        assertEquals(exe3.getDistance(), 61.5f, 0.01f);
        assertEquals(exe3.getAvgSpeed(), 21.3f, 0.01f);
        assertEquals(exe3.getAvgHeartRate(), 0);
        assertEquals(exe3.getAscent(), 0);
        assertEquals(exe3.getCalories(), 0);
        assertNull(exe3.getHrmFile());
        assertNull(exe3.getEquipment());
        assertNull(exe3.getComment());
    }

    /**
     * This helper methods creates the correct SportTypeList needed by the ExerciseList
     * reader and writer test methods.
     *
     * @return the created SportTypeList
     */
    private SportTypeList createSportTypeList() {

        SportTypeList sportTypeList = new SportTypeList();

        // create cycling sport type
        SportType sportType = new SportType(1);
        sportType.setName("Cycling");
        sportTypeList.set(sportType);

        SportSubType subType = new SportSubType(1);
        subType.setName("MTB tour");
        sportType.getSportSubTypeList().set(subType);

        subType = new SportSubType(2);
        subType.setName("MTB race");
        sportType.getSportSubTypeList().set(subType);

        subType = new SportSubType(3);
        subType.setName("Road tour");
        sportType.getSportSubTypeList().set(subType);

        subType = new SportSubType(4);
        subType.setName("Road race");
        sportType.getSportSubTypeList().set(subType);

        Equipment equipment = new Equipment(1);
        equipment.setName("Cannondale Jekyll");
        sportType.getEquipmentList().set(equipment);

        equipment = new Equipment(2);
        equipment.setName("Cannondale R800");
        sportType.getEquipmentList().set(equipment);

        // create running sport type
        sportType = new SportType(2);
        sportType.setName("Running");
        sportTypeList.set(sportType);

        subType = new SportSubType(1);
        subType.setName("Jogging");
        sportType.getSportSubTypeList().set(subType);

        subType = new SportSubType(2);
        subType.setName("Competition");
        sportType.getSportSubTypeList().set(subType);

        return sportTypeList;
    }

    /**
     * Tests of the appropriate method.
     */
    @Test
    public void testReadExerciseList() throws STException {

        // create sport type list for mapping in exercises
        SportTypeList sportTypeList = createSportTypeList();

        // read valid exercise list from XML and check content
        ExerciseList exercises = storage.readExerciseList("misc/testdata/exercises-valid.xml", sportTypeList);
        checkExerciseListContent(exercises);

        // read exercise list from not existing XML
        // => an empty List must be returned (same as first application startup)
        ExerciseList exercisesEmpty = storage.readExerciseList("misc/testdata/exercises-xyz.xml", sportTypeList);
        assertEquals(0, exercisesEmpty.size());

        // read invalid exercise list from XML
        // => an STException needs to be thrown
        try {
            storage.readExerciseList("misc/testdata/exercises-invalid.xml", sportTypeList);
            fail();
        } catch (STException se) {
        }

        // read valid exercise list from XML but remove the referenced equipment
        // from the sport type configuration before
        // => an STException needs to be thrown
        sportTypeList.getByID(1).getEquipmentList().removeByID(2);
        try {
            storage.readExerciseList("misc/testdata/exercises-valid.xml", sportTypeList);
            fail();
        } catch (STException se) {
        }
    }

    /**
     * Tests of the appropriate method.
     */
    @Test
    public void testStoreExerciseList() throws STException {

        // create sport type list for mapping in exercises
        SportTypeList sportTypeList = createSportTypeList();

        // read valid exercise list from XML (has been checked in previous test)
        ExerciseList exercises = storage.readExerciseList("misc/testdata/exercises-valid.xml", sportTypeList);

        // store the read exercise list to a new file
        storage.storeExerciseList(exercises, EXERCISES_WRITETEST_XML);

        // read the list from the created file again
        // => compare the content, needs to be same
        ExerciseList exercisesNew = storage.readExerciseList(EXERCISES_WRITETEST_XML, sportTypeList);
        checkExerciseListContent(exercisesNew);
    }

    /**
     * Tests of the appropriate method.
     */
    @Test
    public void testReadNoteList() throws STException {

        // read valid note list from XML and check content
        NoteList noteList = storage.readNoteList("misc/testdata/notes-valid.xml");
        checkNoteListContent(noteList);

        // read note list from not existing XML
        // => an empty List must be returned (same as first application startup)
        NoteList noteListEmpty = storage.readNoteList("misc/testdata/notes-xyz.xml");
        assertEquals(0, noteListEmpty.size());

        // read invalid note list from XML
        // => an STException needs to be thrown
        try {
            storage.readNoteList("misc/testdata/notes-invalid.xml");
            fail();
        } catch (STException se) {
        }
    }

    /**
     * Tests of the appropriate method.
     */
    @Test
    public void testStoreNoteList() throws STException {

        // read valid NoteList from XML (has been checked in previous test)
        NoteList noteList = storage.readNoteList("misc/testdata/notes-valid.xml");

        // store the read sport type list to a new file
        storage.storeNoteList(noteList, NOTES_WRITETEST_XML);

        // read the list from the created file again
        // => compare the content, needs to be same
        NoteList noteListNew = storage.readNoteList(NOTES_WRITETEST_XML);
        checkNoteListContent(noteListNew);
    }

    /**
     * This helper methods checks the content of the specified NoteList.
     * It has to be exactly same as in "misc/testdata/notes-valid.xml".
     *
     * @param noteList NoteList to check
     */
    private void checkNoteListContent(NoteList noteList) {
        assertNotNull(noteList);
        assertEquals(noteList.size(), 3);

        Note note1 = noteList.getByID(1);
        assertEquals(1, note1.getId());
        assertEquals(note1.getDateTime(), LocalDateTime.of(2009, 01, 02, 9, 10, 11));
        assertEquals("Note 1", note1.getText());

        Note note2 = noteList.getByID(2);
        assertEquals(2, note2.getId());
        assertEquals(note2.getDateTime(), LocalDateTime.of(2009, 01, 05, 13, 14, 15));
        assertEquals("Note 2", note2.getText());

        Note note3 = noteList.getByID(3);
        assertEquals(3, note3.getId());
        assertEquals(note3.getDateTime(), LocalDateTime.of(2009, 01, 07, 20, 21, 22));
        assertEquals("Note 3", note3.getText());
    }

    /**
     * Tests of the appropriate method.
     */
    @Test
    public void testReadWeightList() throws STException {

        // read valid weight list from XML and check content
        WeightList weightList = storage.readWeightList("misc/testdata/weights-valid.xml");
        checkWeightListContent(weightList);

        // read weight list from not existing XML
        // => an empty List must be returned (same as first application startup)
        WeightList weightListEmpty = storage.readWeightList("misc/testdata/weights-xyz.xml");
        assertEquals(0, weightListEmpty.size());

        // read invalid weight list from XML
        // => an STException needs to be thrown
        try {
            storage.readWeightList("misc/testdata/weights-invalid.xml");
            fail();
        } catch (STException se) {
        }
    }

    /**
     * Tests of the appropriate method.
     */
    @Test
    public void testStoreWeightList() throws STException {

        // read valid WeightList from XML (has been checked in previous test)
        WeightList weightList = storage.readWeightList("misc/testdata/weights-valid.xml");

        // store the read sport type list to a new file
        storage.storeWeightList(weightList, WEIGHTS_WRITETEST_XML);

        // read the list from the created file again
        // => compare the content, needs to be same
        WeightList weightListNew = storage.readWeightList(WEIGHTS_WRITETEST_XML);
        checkWeightListContent(weightListNew);
    }

    /**
     * This helper methods checks the content of the specified WeightList.
     * It has to be exactly same as in "misc/testdata/weights-valid.xml".
     *
     * @param weightList WeightList to check
     */
    private void checkWeightListContent(WeightList weightList) {
        assertNotNull(weightList);
        assertEquals(weightList.size(), 3);

        Weight weight1 = weightList.getByID(1);
        assertEquals(1, weight1.getId());
        assertEquals(weight1.getDateTime(), LocalDateTime.of(2009, 01, 02, 9, 10, 11));
        assertEquals(70.2, weight1.getValue(), 0.0001);
        assertEquals("Weight 1", weight1.getComment());

        Weight weight2 = weightList.getByID(2);
        assertEquals(2, weight2.getId());
        assertEquals(weight2.getDateTime(), LocalDateTime.of(2009, 01, 05, 13, 14, 15));
        assertEquals(72.3, weight2.getValue(), 0.0001);
        assertEquals("Weight 2", weight2.getComment());

        Weight weight3 = weightList.getByID(3);
        assertEquals(3, weight3.getId());
        assertEquals(weight3.getDateTime(), LocalDateTime.of(2009, 01, 07, 20, 21, 22));
        assertEquals(74.4, weight3.getValue(), 0.0001);
    }
}
