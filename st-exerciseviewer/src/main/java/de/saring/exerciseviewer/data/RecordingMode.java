package de.saring.exerciseviewer.data;


/**
 * This class contains the informations about what was beeing recorded in the
 * current exercise.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class RecordingMode {
    /**
     * Is altitude beeing recorded ?
     */
    private boolean altitude;
    /**
     * Is bike speed beeing recorded ?
     */
    private boolean speed;
    /**
     * Is bicycling cadence beeing recorded ?
     */
    private boolean cadence;
    /**
     * Is bicycling power beeing recorded ?
     */
    private boolean power;
    /**
     * Number of bike, when speed is beeing recorded (Polar S710 supports 2).
     */
    private byte bikeNumber;
    /**
     * Has the temperature been recorded ? (Only in HAC4 devices).
     */
    private boolean temperature = false;
    /**
     * Has the location of the trackpoints been recorded? (GPS data)
     */
    private boolean location = false;
    /**
     * Is the exercise an interval trainging (S510 only?)
     */
    private boolean intervalExercise = false;
    /**
     * Is heartRate beeing recorded ?
     */
    private boolean heartRate;

    public boolean isAltitude() {
        return altitude;
    }

    public void setAltitude(boolean altitude) {
        this.altitude = altitude;
    }

    public boolean isSpeed() {
        return speed;
    }

    public void setSpeed(boolean speed) {
        this.speed = speed;
    }

    public boolean isCadence() {
        return cadence;
    }

    public void setCadence(boolean cadence) {
        this.cadence = cadence;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public byte getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(byte bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public boolean isTemperature() {
        return temperature;
    }

    public void setTemperature(boolean temperature) {
        this.temperature = temperature;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public boolean isIntervalExercise() {
        return intervalExercise;
    }

    public void setIntervalExercise(boolean IntervalExercise) {
        this.intervalExercise = IntervalExercise;
    }

    public boolean isHeartRate() {
        return heartRate;
    }

    public void setHeartRate(boolean heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(RecordingMode.class.getName()).append(":\n");
        sBuilder.append(" [altitude=").append(this.altitude).append("\n");
        sBuilder.append("  speed=").append(this.speed).append("\n");
        sBuilder.append("  cadence=").append(this.cadence).append("\n");
        sBuilder.append("  power=").append(this.power).append("\n");
        sBuilder.append("  bikeNumber=").append(this.bikeNumber).append("\n");
        sBuilder.append("  temperature=").append(this.temperature).append("\n");
        sBuilder.append("  location=").append(this.location).append("\n");
        sBuilder.append("  intervalExercise=").append(this.intervalExercise).append("\n");
        sBuilder.append("  heartRate=").append(this.heartRate).append("]\n");

        return sBuilder.toString();
    }
}
