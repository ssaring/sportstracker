package de.saring.sportstracker.data;

import de.saring.util.data.IdDateObjectList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.PatternSyntaxException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

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
    @Before
    public void setUp () {

        // create a sport type list with 2 sport types with 2 sport subtypes in each
        sportTypeList = new SportTypeList ();

        SportType type1 = new SportType (1);
        type1.setName ("SportType 1");
        SportSubType subType11 = new SportSubType (11);
        subType11.setName ("SportSubType 11");
        type1.getSportSubTypeList ().set (subType11);
        SportSubType subType12 = new SportSubType (12);
        subType12.setName ("SportSubType 12");
        type1.getSportSubTypeList ().set (subType12);
        sportTypeList.set (type1);

        SportType type2 = new SportType (2);
        type2.setName ("SportType 2");
        SportSubType subType21 = new SportSubType (21);
        subType21.setName ("SportSubType 21");
        type2.getSportSubTypeList ().set (subType21);
        SportSubType subType22 = new SportSubType (22);
        subType22.setName ("SportSubType 22");
        type2.getSportSubTypeList ().set (subType22);
        sportTypeList.set (type2);
        
        // add two equipment's to sport type 2
        Equipment eq21 = new Equipment (21);
        eq21.setName ("Equipment 21");
        type2.getEquipmentList ().set (eq21);
        Equipment eq22 = new Equipment (22);
        eq22.setName ("Equipment 22");
        type2.getEquipmentList ().set (eq22);

        // create a new list with some test content
        list = new ExerciseList ();
        Calendar calendar = Calendar.getInstance ();
        calendar.clear ();

        Exercise exe1 = new Exercise (1);
        exe1.setSportType (type1);
        exe1.setSportSubType (subType12);
        exe1.setDate (createDate (2003, 8, 2, 0, 0, 0));
        exe1.setComment ("DummyExercise 1");
        exe1.setIntensity (Exercise.IntensityType.LOW);
        list.set (exe1);

        Exercise exe2 = new Exercise (2);
        exe2.setSportType (type1);
        exe2.setSportSubType (subType11);
        exe2.setDate (createDate (2003, 7, 20, 0, 0, 0));
        exe2.setComment ("DummyExercise 2");
        exe2.setIntensity (Exercise.IntensityType.HIGH);
        list.set (exe2);

        Exercise exe3 = new Exercise (3);
        exe3.setSportType (type2);
        exe3.setSportSubType (subType22);
        exe3.setEquipment (eq22);
        exe3.setDate (createDate (2003, 8, 6, 0, 0, 0));
        exe3.setComment ("DummyExercise 3");
        exe3.setIntensity (Exercise.IntensityType.LOW);
        exe3.setEquipment (eq22);
        list.set (exe3); 
    }

    /**
     * Creates a Date instance for the specified date.
     */
    private Date createDate (int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance ();
        calendar.clear ();
        calendar.set (year, month, day, hour, minute, second);
        return calendar.getTime ();
    }

    /** 
     * Tests the appropriate method. 
     */
    @Test
    public void testUpdateSportTypes ()
    {
        // get exercise 3 for tests and check its sport type and subtype
        Exercise exercise3 = list.getByID (3);
        assertEquals (3, exercise3.getId ());
        assertEquals ("SportType 2", exercise3.getSportType ().getName ());
        assertEquals ("SportSubType 22", exercise3.getSportSubType ().getName ());

        SportType sportType2 = sportTypeList.getByID (2);
        SportSubType sportSubType22 = sportType2.getSportSubTypeList ().getByID (22);
        assertNotNull (sportType2);
        assertNotNull (sportSubType22);

        // clone, edit and store the SportSubType 22 (the way the GUI editor works)
        SportSubType sportSubType22New = (SportSubType) sportSubType22.clone ();
        sportSubType22New.setName ("SportSubType 22 - New");
        sportType2.getSportSubTypeList ().set (sportSubType22New);

        // the exercise will still have the old sport subtype
        // => after UpdateSportTypes() it neesds to reference to the new subtype
        assertEquals ("SportSubType 22", exercise3.getSportSubType ().getName ());
        list.updateSportTypes (sportTypeList);
        assertEquals ("SportSubType 22 - New", exercise3.getSportSubType ().getName ());

        // clone, edit and store the SportType 2 (the way the GUI editor works)
        SportType sportType2New = (SportType) sportType2.clone ();
        sportType2New.setName ("SportType 2 - New");
        sportTypeList.set (sportType2New);

        // the exercise will still have the old sport type
        // => after UpdateSportTypes() it needs to reference to the new sport type
        assertEquals ("SportType 2", exercise3.getSportType ().getName ());
        list.updateSportTypes (sportTypeList);
        assertEquals ("SportType 2 - New", exercise3.getSportType ().getName ());

        // clone, edit and store the Equipment 22 (the way the GUI editor works)
        Equipment equipment22Old = sportTypeList.getByID (2).getEquipmentList ().getByID (22);
        assertEquals ("Equipment 22", equipment22Old.getName ());
        Equipment equipment22New = (Equipment) equipment22Old.clone ();
        equipment22New.setName ("Equipment 22 - New");
        sportTypeList.getByID (2).getEquipmentList ().set (equipment22New);

        // the exercise will still have the old equipment
        // => after UpdateSportTypes() it needs to reference to the new equipment
        assertEquals ("Equipment 22", exercise3.getEquipment ().getName ());
        list.updateSportTypes (sportTypeList);
        assertEquals ("Equipment 22 - New", exercise3.getEquipment ().getName ());
    }

    /** Tests the appropriate method. */
    @Test
    public void testgetExercisesForFilter () {
        
        // all 3 exercises should be found
        ExerciseFilter filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (null);
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        IdDateObjectList<Exercise> exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 3);

        // no exercises should be found (no exercises in time span)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 04, 30, 23, 59, 59));
        filter.setSportType (null);
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 0);

        // no exercises should be found (sport type does not exists)
        SportType sportTypeUnknown = new SportType (4);

        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeUnknown);
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 0);

        // no exercises should be found (sport subtype does not exists)
        SportSubType sportSubTypeUnknown = new SportSubType (7);

        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (1));
        filter.setSportSubType (sportSubTypeUnknown);
        filter.setIntensity (null);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 0);

        // no exercises should be found (no exercise with intensity NORMAL)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (null);
        filter.setSportSubType (null);
        filter.setIntensity ( Exercise.IntensityType.NORMAL);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 0);

        // no exercises should be found (in specified time span and with intensity LOW and sport type ID 1)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 7, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (1));
        filter.setSportSubType (null);
        filter.setIntensity ( Exercise.IntensityType.LOW);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 0);

        // 2 exercises should be found (in the specified time span)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 8, 2, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 8, 7, 23, 59, 59));
        filter.setSportType (null);
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 2);

        // 2 exercises should be found (with sport type ID 1)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (1));
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 2);

        // 1 exercises should be found (with sport type ID 1 and sport subtype ID 2)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (1));
        filter.setSportSubType (filter.getSportType ().getSportSubTypeList ().getByID (12));
        filter.setIntensity (null);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 1);

        // 2 exercises should be found (with intensity LOW)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (null);
        filter.setSportSubType (null);
        filter.setIntensity ( Exercise.IntensityType.LOW);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 2);

        // 1 exercises should be found (with intensity LOW and sport type ID 1)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (1));
        filter.setSportSubType (null);
        filter.setIntensity ( Exercise.IntensityType.LOW);
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 1);
        
        // 1 exercise (ID 3) should be found for equipment with ID 22
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (2));
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setEquipment (filter.getSportType ().getEquipmentList ().getByID (22));
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 1);
        assertEquals (3, exeList.getAt (0).getId ());

        // no exercise should be found for equipment with ID 21
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (2));
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setEquipment (filter.getSportType ().getEquipmentList ().getByID (21));
        filter.setCommentSubString ("");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 0);

        // 2 exercises should be found (with comment substring "EXERCISE" and sport type ID 1)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (1));
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("EXERCISE");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 2);

        // 1 exercise should be found (with comment substring "CISE 2" and sport type ID 1)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (1));
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString (" CISE 2 ");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 1);

        // 0 exercises should be found (with comment substring "NotInThere" and sport type ID 1)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (sportTypeList.getByID (1));
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("NotInThere");
        filter.setRegularExpressionMode (false);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 0);

        // 2 exercises should be found (with comment regular expression substring "cise [0-2]")
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (null);
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("cise [0-2]");
        filter.setRegularExpressionMode (true);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 2);

        // 3 exercises should be found (with comment regular expression substring for 4 small characters)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (null);
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("[a-z]{4}");
        filter.setRegularExpressionMode (true);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 3);

        // 0 exercises should be found (with comment regular expression substring for 8 small characters)
        filter = new ExerciseFilter ();
        filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
        filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
        filter.setSportType (null);
        filter.setSportSubType (null);
        filter.setIntensity (null);
        filter.setCommentSubString ("[a-z]{8}");
        filter.setRegularExpressionMode (true);

        exeList = list.getExercisesForFilter (filter);
        assertEquals (exeList.size (), 0);

        // use of regular expression "cise [0-2" with syntax error => ArgumentException needs to be thrown
        try {
            filter = new ExerciseFilter ();
            filter.setDateStart (createDate (2003, 1, 1, 0, 0, 0));
            filter.setDateEnd (createDate (2003, 12, 31, 23, 59, 59));
            filter.setSportType (null);
            filter.setSportSubType (null);
            filter.setIntensity (null);
            filter.setCommentSubString ("cise [0-2");
            filter.setRegularExpressionMode (true);

            exeList = list.getExercisesForFilter (filter);
            fail ("The expected System.ArgumentException was not thown!");	                
        }
        catch (PatternSyntaxException pse) {
        }
        catch (Exception e) {
            fail ("The expected System.ArgumentException was not thown!");
        }
    }
}
