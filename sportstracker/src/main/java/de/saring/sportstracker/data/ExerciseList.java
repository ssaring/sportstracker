package de.saring.sportstracker.data;

import java.util.regex.PatternSyntaxException;

/**
 * This class contains a list of all exercises of the user and provides access
 * methods to them.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class ExerciseList extends EntryList<Exercise> {

    /**
     * This method updates the sport type, the subtype and the equipment objects
     * for all exercises. This is necessary when the sport type objects have
     * been edited, e.g. the name of a sport type has changed. The new sport
     * type will be a new object and the exercise object which uses it needs to
     * get the reference to this new object (references the old object before).
     *
     * @param sportTypeList the sport type list to be used for update
     */
    public void updateSportTypes(SportTypeList sportTypeList) {

        // process all exercises
        this.forEach(exercise -> {

            // get and store the new SportType object with the same ID
            SportType newSportType = sportTypeList.getByID(exercise.getSportType().getId());
            exercise.setSportType(newSportType);

            // get and store the new SportSubType object with the same ID
            SportSubType newSportSubType = newSportType.getSportSubTypeList().getByID(exercise.getSportSubType().getId());
            exercise.setSportSubType(newSportSubType);

            // get and store the new Equipment object with the same ID (is optional)
            if (exercise.getEquipment() != null) {
                Equipment newEquipment = newSportType.getEquipmentList().getByID(exercise.getEquipment().getId());
                exercise.setEquipment(newEquipment);
            }
        });
    }

    /**
     * This method searches through the whole exercise list and returns an list
     * of all exercises which are fullfilling all the specified filter
     * criterias. The filters for sport type, subtype and intensity and comment
     * searching are optional. The filtering by a comment substring is only case
     * sensitive in regualar expression mode.
     *
     * @param filter the exercise filter criterias
     * @return List of Exercise objects which are valid for the specified
     *         filters
     * @throws PatternSyntaxException thrown on parsing problems of the regular
     * expression for comment searching
     */
    public EntryList<Exercise> getExercisesForFilter(EntryFilter filter) throws PatternSyntaxException {

        // ignore the filter when not set for exercises
        if (filter.getEntryType() != EntryFilter.EntryType.EXERCISE) {
            return this;
        }

        final EntryList<Exercise> foundExercises = new EntryList<>();
        stream().filter(exercise -> filterExercise(exercise, filter))
                .forEach(foundExercises::set);
        return foundExercises;
    }

    protected boolean filterExercise(Exercise exercise, EntryFilter filter) {

        // entry datetime and comment are filtered by the base class
        if (!super.filterEntry(exercise, filter)) {
            return false;
        }

        // if a sport type filter is specified => make sure that exercise has the same sport type
        if (filter.getSportType() != null && !filter.getSportType().equals(exercise.getSportType())) {
            return false;
        }

        // if a sport subtype filter is specified => make sure that exercise has the same sport subtype
        if (filter.getSportSubType() != null && !filter.getSportSubType().equals(exercise.getSportSubType())) {
            return false;
        }

        // if an intensity is specified => make sure that exercise has the same intensity
        if (filter.getIntensity() != null && filter.getIntensity() != exercise.getIntensity()) {
            return false;
        }

        // if an equipment filter is specified => make sure that exercise has the same equipment (is optional)
        if (filter.getEquipment() != null && !filter.getEquipment().equals(exercise.getEquipment())) {
            return false;
        }

        // all filter criteria are fulfilled
        return true;
    }
}
