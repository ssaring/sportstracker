package de.saring.sportstracker.data;

import de.saring.util.ResourceReader;
import de.saring.util.data.IdDateObject;

/**
 * This class contains all informations of a single exercise (or workout).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class Exercise extends IdDateObject implements Cloneable {

    /**
     * The related SportType object.
     */
    private SportType sportType;

    /**
     * The related SportSubType object.
     */
    private SportSubType sportSubType;

    /**
     * Duration of exercise in seconds.
     */
    private int duration;

    /**
     * Intensity of exercise.
     */
    private IntensityType intensity;

    /**
     * Distance of exercise in kilometers.
     */
    private float distance;

    /**
     * Average speed of exercise in kilometers per hour.
     */
    private float avgSpeed;

    /**
     * Average heartrate of exercise in beats per minute (optional).
     */
    private int avgHeartRate;

    /**
     * Ascent (height meters) of exercise in meters (optional).
     */
    private int ascent;

    /**
     * Amount of calories consumed (optional).
     */
    private int calories;

    /**
     * Name of heart rate monitor file (optional).
     */
    private String hrmFile;

    /**
     * The equipment used in this exercise (optional).
     */
    private Equipment equipment;

    /**
     * Some exercise comments (optional).
     */
    private String comment;

    /**
     * This is the list of possible file types of an exercise.
     */
    public enum IntensityType {

        MINIMUM(0), LOW(1), NORMAL(2), HIGH(3), MAXIMUM(4), INTERVALS(5);

        /**
         * Value of the intensity type (needed for sorting).
         */
        private final int value;

        /**
         * Static resource reader is needed for string creation.
         */
        private static ResourceReader resReader;

        public static void setResReader(ResourceReader resReader) {
            IntensityType.resReader = resReader;
        }

        /**
         * Standard c'tor.
         */
        IntensityType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        /**
         * Returns the translated name (to be displayed) for this intensity.
         *
         * @return name of this intensity
         */
        @Override
        public String toString() {
            switch (this) {
                case MINIMUM:
                    return IntensityType.resReader.getString("st.intensity.minimum");
                case LOW:
                    return IntensityType.resReader.getString("st.intensity.low");
                case NORMAL:
                    return IntensityType.resReader.getString("st.intensity.normal");
                case HIGH:
                    return IntensityType.resReader.getString("st.intensity.high");
                case MAXIMUM:
                    return IntensityType.resReader.getString("st.intensity.maximum");
                case INTERVALS:
                    return IntensityType.resReader.getString("st.intensity.intervals");
            }
            return "???";
        }

        /**
         * Returns the string representation of this enum value created by the
         * toString()) method of the superclass.
         *
         * @return String representation of the value
         */
        public String toStringEnum() {
            return super.toString();
        }
    }

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object
     */
    public Exercise(int id) {
        super(id);
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public IntensityType getIntensity() {
        return intensity;
    }

    public void setIntensity(IntensityType intensity) {
        this.intensity = intensity;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public int getAscent() {
        return ascent;
    }

    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getHrmFile() {
        return hrmFile;
    }

    public void setHrmFile(String hrmFile) {
        this.hrmFile = hrmFile;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns a complete clone of this Exercise object. All the attributes are
     * the same, but the ID of the clone is the specified one.
     *
     * @param cloneId ID of the cloned Exercise
     * @return the Exercise clone
     */
    public Exercise clone(int cloneId) {
        Exercise clone = new Exercise(cloneId);
        clone.setDate(this.getDate());
        clone.setSportType(this.getSportType());
        clone.setSportSubType(this.getSportSubType());
        clone.setDuration(this.getDuration());
        clone.setIntensity(this.getIntensity());
        clone.setDistance(this.getDistance());
        clone.setAvgSpeed(this.getAvgSpeed());
        clone.setAvgHeartRate(this.getAvgHeartRate());
        clone.setAscent(this.getAscent());
        clone.setCalories(this.getCalories());
        clone.setHrmFile(this.getHrmFile());
        clone.setEquipment(this.getEquipment());
        clone.setComment(this.getComment());
        return clone;
    }

    @Override
    public String toString() {

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.getId()).append("\n");
        sBuilder.append("  date=").append(this.getDate()).append("\n");
        sBuilder.append("  sportType=").append(this.sportType).append("\n");
        sBuilder.append("  sportSubType=").append(this.sportSubType).append("\n");
        sBuilder.append("  duration=").append(this.duration).append("\n");
        sBuilder.append("  intensity=").append(this.intensity).append("\n");
        sBuilder.append("  distance=").append(this.distance).append("\n");
        sBuilder.append("  avgSpeed=").append(this.avgSpeed).append("\n");
        sBuilder.append("  avgHeartRate=").append(this.avgHeartRate).append("\n");
        sBuilder.append("  ascent=").append(this.ascent).append("\n");
        sBuilder.append("  calories=").append(this.calories).append("\n");
        sBuilder.append("  hrmFile=").append(this.hrmFile).append("\n");
        sBuilder.append("  equipment=").append(this.equipment).append("\n");
        sBuilder.append("  comment=").append(this.comment).append("]\n");
        return sBuilder.toString();
    }

}
