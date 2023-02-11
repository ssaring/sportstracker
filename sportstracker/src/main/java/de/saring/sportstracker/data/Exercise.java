package de.saring.sportstracker.data;

/**
 * This class contains all information of a single exercise (or workout).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class Exercise extends Entry implements Cloneable {

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
     * Descent (height meters) of exercise in meters (optional).
     */
    private int descent;

    /**
     * Amount of kCalories consumed (optional).
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
     * This is the list of possible file types of an exercise.
     */
    public enum IntensityType {

        MINIMUM(0, "st.intensity.minimum"),
        LOW(1, "st.intensity.low"),
        NORMAL(2, "st.intensity.normal"),
        HIGH(3, "st.intensity.high"),
        MAXIMUM(4, "st.intensity.maximum"),
        INTERVALS(5, "st.intensity.intervals");

        /** Value of the intensity type (needed for sorting). */
        private final int value;

        /** Resource key of the intensity name. */
        private final String resourceKey;

        private IntensityType(int value, String resourceKey) {
            this.value = value;
            this.resourceKey = resourceKey;
        }

        public int getValue() {
            return value;
        }

        /**
         * Returns the I18N resource key of the intensity name.
         *
         * @return resource key
         */
        public String getResourceKey() {
            return resourceKey;
        }
    }

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object
     */
    public Exercise(Integer id) {
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

    public int getDescent() {
        return descent;
    }

    public void setDescent(int descent) {
        this.descent = descent;
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

    /**
     * Returns a complete clone of this Exercise object. All the attributes are
     * the same, but the ID of the clone is the specified one.
     *
     * @param cloneId ID of the cloned Exercise
     * @return the Exercise clone
     */
    public Exercise clone(int cloneId) {
        Exercise clone = new Exercise(cloneId);
        clone.setDateTime(this.getDateTime());
        clone.setSportType(this.getSportType());
        clone.setSportSubType(this.getSportSubType());
        clone.setDuration(this.getDuration());
        clone.setIntensity(this.getIntensity());
        clone.setDistance(this.getDistance());
        clone.setAvgSpeed(this.getAvgSpeed());
        clone.setAvgHeartRate(this.getAvgHeartRate());
        clone.setAscent(this.getAscent());
        clone.setDescent(this.getDescent());
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
        sBuilder.append("  date=").append(this.getDateTime()).append("\n");
        sBuilder.append("  sportType=").append(this.sportType).append("\n");
        sBuilder.append("  sportSubType=").append(this.sportSubType).append("\n");
        sBuilder.append("  duration=").append(this.duration).append("\n");
        sBuilder.append("  intensity=").append(this.intensity).append("\n");
        sBuilder.append("  distance=").append(this.distance).append("\n");
        sBuilder.append("  avgSpeed=").append(this.avgSpeed).append("\n");
        sBuilder.append("  avgHeartRate=").append(this.avgHeartRate).append("\n");
        sBuilder.append("  ascent=").append(this.ascent).append("\n");
        sBuilder.append("  descent=").append(this.descent).append("\n");
        sBuilder.append("  calories=").append(this.calories).append("\n");
        sBuilder.append("  hrmFile=").append(this.hrmFile).append("\n");
        sBuilder.append("  equipment=").append(this.equipment).append("\n");
        sBuilder.append("  comment=").append(this.getComment()).append("]\n");
        return sBuilder.toString();
    }

}
