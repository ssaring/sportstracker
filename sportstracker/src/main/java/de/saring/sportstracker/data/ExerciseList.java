package de.saring.sportstracker.data;

import de.saring.util.data.IdDateObjectList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This class contains a list of all exercises of the user and provides access 
 * methods to them.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public final class ExerciseList extends IdDateObjectList<Exercise> {
    
    /** 
     * This method updates the sport type, the subtype and the equipment objects 
     * for all exercises. This is necessary when the sport type objects have been 
     * edited, e.g. the name of a sport type has changed. The new sport type will
     * be a new object and the exercise object which uses it needs to get the 
     * reference to this new object (references the old object before).
     * 
     * @param sportTypeList the sport type list to be used for update
     */
    public void updateSportTypes (SportTypeList sportTypeList) {
        
        // process all exercises
        for (Exercise exercise : this) {
            
            // get and store the new SportType object with the same ID
            SportType newSportType = sportTypeList.getByID (exercise.getSportType ().getId ());
            exercise.setSportType (newSportType);

            // get and store the new SportSubType object with the same ID
            SportSubType newSportSubType = newSportType.getSportSubTypeList ().getByID (exercise.getSportSubType ().getId ());
            exercise.setSportSubType (newSportSubType);
            
            // get and store the new Equipment object with the same ID (is optional)
            if (exercise.getEquipment () != null) {
                Equipment newEquipment = newSportType.getEquipmentList ().getByID (exercise.getEquipment ().getId ());
                exercise.setEquipment (newEquipment);
            }
        }
    }

    /** 
     * This method searches through the whole exercise list and returns an list of
     * all exercises which are fullfilling all the specified filter criterias.
     * The filters for sport type, subtype and intensity and comment searching
     * are optional.
     * The filtering by a comment substring is only case sensitive in regualar
     * expression mode.
     *
     * @param filter the exercise filter criterias
     * @return List of Exercise objects which are valid for the specified filters
     * @throws PatternSyntaxException thrown on parsing problems of the regular expression for comment searching
     */
    public IdDateObjectList<Exercise> getExercisesForFilter (ExerciseFilter filter) throws PatternSyntaxException {

        IdDateObjectList<Exercise> foundExercises = new IdDateObjectList<Exercise> ();

        // do we need to search in comments ?
        boolean fCommentStringSearch = (filter.getCommentSubString () != null) && (filter.getCommentSubString ().length () > 0);
        String strCommentSubString = null;
        Pattern ptnCommentSubString = null;

        // prepare searchstring
        if (fCommentStringSearch) {
            strCommentSubString = filter.getCommentSubString ().trim ();

            if (!filter.isRegularExpressionMode ()) {
                // searching is not case sensitive when regular expression mode is disabled
                strCommentSubString = strCommentSubString.toLowerCase ();
            }
            else {
                // create regular expression pattern
                ptnCommentSubString = Pattern.compile (strCommentSubString);
            }
        }

        // process all exercises
        for (Exercise tempExercise : this) {

            // make sure that the exercise is in the specified time period
            if ((!filter.getDateStart ().after (tempExercise.getDate ())) && 
                (!filter.getDateEnd ().before (tempExercise.getDate ())))
            {
                // if a sport type filter is specified => make sure that exercise has the same sport type
                if ((filter.getSportType () != null) && (filter.getSportType ().getId () != tempExercise.getSportType ().getId ())) {
                    continue;
                }

                // if a sport subtype filter is specified => make sure that exercise has the same sport subtype
                if ((filter.getSportSubType () != null) && (filter.getSportSubType ().getId () != tempExercise.getSportSubType ().getId ())) {
                    continue;
                }

                // if an intensity is specified => make sure that exercise has the same intensity
                if ((filter.getIntensity () != null) && (filter.getIntensity () != tempExercise.getIntensity ())) {
                    continue;
                }

                // if an equipment filter is specified => make sure that exercise has the same equipment (is optional)
                if ((filter.getEquipment () != null) &&
                    ((tempExercise.getEquipment () == null) ||
                     (filter.getEquipment ().getId () != tempExercise.getEquipment ().getId ()))) {
                    continue;
                }

                // search for comment substring (if enabled)
                if (fCommentStringSearch)
                {
                    // ignore this exercise, when it has no comment
                    if ((tempExercise.getComment () == null) || (tempExercise.getComment ().length () == 0)) {
                        continue;
                    }

                    if (!filter.isRegularExpressionMode ())
                    {
                        // normal searching for substring (is not case sensitive !)
                        String strExerciseComment = tempExercise.getComment ().toLowerCase ();
                        if (strExerciseComment.indexOf (strCommentSubString) == -1) {
                            continue;
                        }
                    }
                    else {
                        // regular expression searching for substring (is case sensitive !)
                        Matcher matcher = ptnCommentSubString.matcher (tempExercise.getComment ());
                        if (!matcher.find ()) {
                            continue;
                        }
                    }
                }

                // exercise passed all filters => add
                foundExercises.set (tempExercise);
            }
        }

        return foundExercises;
    }
}
