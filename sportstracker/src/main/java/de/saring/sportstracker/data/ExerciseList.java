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
     * This method checks whether the specified exercise entry matches the specified entry filter criteria.
     * It extends the default filter (date time and comment) by sport type, subtype, intensity and equipment criteria.
     *
     * @param exercise the exercise to check
     * @param filter the entry filter criterias
     * @return true if the exercise matches the filter criteria
     * @throws PatternSyntaxException thrown on parsing problems of the regular expression for comment searching
     */
    @Override
    protected boolean filterEntry(Exercise exercise, EntryFilter filter) {

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
