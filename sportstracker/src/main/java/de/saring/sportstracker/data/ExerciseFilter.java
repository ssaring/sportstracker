package de.saring.sportstracker.data;

import de.saring.sportstracker.data.Exercise.IntensityType;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * This class defines the criterias for filtering the exercise list (e.g. for
 * creation of statistics).
 *
 * @author Stefan Saring
 * @version 1.1
 */
public final class ExerciseFilter {

    /**
     * Enumeration with all possible entry types to be filtered.
     */
    public enum EntryType {
        EXERCISE,
        NOTE,
        WEIGHT
    }

    /**
     * The exercise dates needs to be greater or same as this start date.
     */
    private LocalDate dateStart;

    /**
     * The exercise dates needs to be lesser or same as this end date.
     */
    private LocalDate dateEnd;

    /**
     * The entry type to be filtered.
     */
    private EntryType entryType;

    /**
     * The exercise needs to have the same sport type (ignore, when null).
     */
    private SportType sportType = null;

    /**
     * The exercise needs to have the same sport subtype (ignore, when null).
     */
    private SportSubType sportSubType = null;

    /**
     * Exercises needs to have same intensity (ignore, when null).
     */
    private Exercise.IntensityType intensity;

    /**
     * The exercise needs to have the same equipment (ignore, when null).
     */
    private Equipment equipment = null;

    /**
     * Substring which needs to be in the exercise comments (empty string or
     * null means ignoring).
     */
    private String commentSubString = null;

    /**
     * When true then the substring in comment will be searched in regular
     * expression mode.
     */
    private boolean regularExpressionMode = false;

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }

    public SportSubType getSportSubType() {
        return sportSubType;
    }

    public void setSportSubType(SportSubType sportSubType) {
        this.sportSubType = sportSubType;
    }

    public IntensityType getIntensity() {
        return intensity;
    }

    public void setIntensity(IntensityType intensity) {
        this.intensity = intensity;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public boolean isRegularExpressionMode() {
        return regularExpressionMode;
    }

    public void setRegularExpressionMode(boolean regularExpressionMode) {
        this.regularExpressionMode = regularExpressionMode;
    }

    public String getCommentSubString() {
        return commentSubString;
    }

    public void setCommentSubString(String commentSubString) {
        this.commentSubString = commentSubString;
    }

    /**
     * Creates the default filter criteria object which will be used at
     * application startup. This filter accepts all exercises of the current
     * month.
     *
     * @return the default filter object
     */
    public static ExerciseFilter createDefaultExerciseFilter() {
        LocalDate now = LocalDate.now();

        ExerciseFilter filter = new ExerciseFilter();
        filter.dateStart = now.with(TemporalAdjusters.firstDayOfMonth());
        filter.dateEnd = now.with(TemporalAdjusters.lastDayOfMonth());
        filter.entryType = EntryType.EXERCISE;
        filter.sportType = null;
        filter.sportSubType = null;
        filter.intensity = null;
        filter.equipment = null;
        filter.commentSubString = "";
        filter.regularExpressionMode = false;
        return filter;
    }

    /**
     * This method updates the sport type, subtype and equipment objects of this
     * filter. This is necessary when the sport type objects have been edited,
     * e.g. the name of a sport type has changed. The new sport type will be a
     * new object and the exercise object which uses it needs to get the
     * reference to this new object (references the old object before).
     *
     * @param sportTypeList the sport type list to be used for update
     */
    public void updateSportTypes(SportTypeList sportTypeList) {
        if (sportType != null) {
            sportType = sportTypeList.getByID(sportType.getId());

            if (sportType != null) {
                if (sportSubType != null) {
                    sportSubType = sportType.getSportSubTypeList().getByID(sportSubType.getId());
                }
                if (equipment != null) {
                    equipment = sportType.getEquipmentList().getByID(equipment.getId());
                }
            } else {
                sportSubType = null;
                equipment = null;
            }
        }
    }

    @Override
    public String toString() {

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ExerciseFilter.class.getName()).append(":\n");
        sBuilder.append(" [dateStart=").append(this.dateStart).append("\n");
        sBuilder.append("  dateEnd=").append(this.dateEnd).append("\n");
        sBuilder.append("  sportType=").append(this.sportType).append("\n");
        sBuilder.append("  sportSubType=").append(this.sportSubType).append("\n");
        sBuilder.append("  intensity=").append(this.intensity).append("\n");
        sBuilder.append("  equipment=").append(this.equipment).append("\n");
        sBuilder.append("  commentSubString=").append(this.commentSubString).append("\n");
        sBuilder.append("  regularExpressionMode=").append(this.regularExpressionMode).append("]\n");
        return sBuilder.toString();
    }
}
