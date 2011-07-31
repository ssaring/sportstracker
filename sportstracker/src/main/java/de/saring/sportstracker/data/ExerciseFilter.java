package de.saring.sportstracker.data;

import de.saring.sportstracker.data.Exercise.IntensityType;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * This class defines the criterias for filtering the exercise list (e.g. for
 * creation of statistics).
 * 
 * @author  Stefan Saring
 * @version 1.1
 */
public final class ExerciseFilter implements Serializable
{
    /** The exercise dates needs to be greater or same as this start date. */
    private Date dateStart;

    /** The exercise dates needs to be lesser or same as this end date. */
    private Date dateEnd;

    /** The exercise needs to have the same sport type (ignore, when null). */
    private SportType sportType = null;

    /** The exercise needs to have the same sport subtype (ignore, when null). */
    private SportSubType sportSubType = null;

    /** Exercises needs to have same intensity (ignore, when null). */
    private Exercise.IntensityType intensity;

    /** The exercise needs to have the same equipment (ignore, when null). */
    private Equipment equipment = null;

    /** Substring which needs to be in the exercise comments (empty string or null means ignoring). */
    private String commentSubString = null;

    /** When true then the substring in comment will be searched in regular expression mode. */
    private boolean regularExpressionMode = false;

    
    /***** BEGIN: Generated Getters and Setters *****/
    
    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
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

    public Equipment getEquipment () {
        return equipment;
    }

    public void setEquipment (Equipment equipment) {
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

    /***** END: Generated Getters and Setters *****/
    
    /**
     * Creates the default filter criteria object which will be used at application 
     * startup. This filter accepts all exercises of the current month.
     * @return the default filter object
     */
    public static ExerciseFilter createDefaultExerciseFilter () {
        
        Calendar cStart = Calendar.getInstance ();
        Calendar cEnd = Calendar.getInstance ();
        Calendar cNow = Calendar.getInstance ();
        cStart.clear ();
        cEnd.clear ();
        cStart.set (cNow.get (Calendar.YEAR), cNow.get (Calendar.MONTH), 1, 0, 0, 0);        
        cEnd.set (cNow.get (Calendar.YEAR), cNow.get (Calendar.MONTH), cNow.getActualMaximum (Calendar.DAY_OF_MONTH), 23, 59, 59);
        
        ExerciseFilter filter = new ExerciseFilter ();
        filter.dateStart = cStart.getTime ();
        filter.dateEnd = cEnd.getTime ();
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
    public void updateSportTypes (SportTypeList sportTypeList) {        
        if (sportType != null) {
            sportType = sportTypeList.getByID (sportType.getId ());
            
            if (sportType != null) {
                if (sportSubType != null) {
                    sportSubType = sportType.getSportSubTypeList ().getByID (sportSubType.getId ());
                }
                if (equipment != null) {
                    equipment = sportType.getEquipmentList ().getByID (equipment.getId ());
                }
            }
            else {
                sportSubType = null;
                equipment = null;
            }
        }        
    }
		
    @Override
    public String toString () {
        
        StringBuilder sBuilder = new StringBuilder ();
        sBuilder.append (ExerciseFilter.class.getName () + ":\n");
        sBuilder.append (" [dateStart=" + this.dateStart + "\n");
        sBuilder.append ("  dateEnd=" + this.dateEnd + "\n");
        sBuilder.append ("  sportType=" + this.sportType + "\n");
        sBuilder.append ("  sportSubType=" + this.sportSubType + "\n");
        sBuilder.append ("  intensity=" + this.intensity + "\n");
        sBuilder.append ("  equipment=" + this.equipment + "\n");
        sBuilder.append ("  commentSubString=" + this.commentSubString + "\n");
        sBuilder.append ("  regularExpressionMode=" + this.regularExpressionMode + "]\n");
        return sBuilder.toString ();
    }
}
