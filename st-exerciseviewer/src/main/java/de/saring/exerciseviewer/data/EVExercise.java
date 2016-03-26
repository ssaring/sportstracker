package de.saring.exerciseviewer.data;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * This class represents an recorded exercise. There is all the data of an
 * Polar S710 stored in this class right now, maybe there will be more data
 * in future to be compatible with other watches too.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class EVExercise {

    /**
     * File type of an exercise (see enums).
     */
    private ExerciseFileType fileType;
    /**
     * Name of the HRM device (optional).
     */
    private String deviceName;
    /**
     * Timestamp of exercise.
     */
    private LocalDateTime dateTime;
    /**
     * Exercise type (label).
     */
    private String type;
    /**
     * Record mode (what was recorded in exercise).
     */
    private RecordingMode recordingMode;
    /**
     * Duration of exercise in tenths of a second.
     */
    private int duration;
    /**
     * Recording interval in seconds (e.g. 5s, 15s, 60s or DYNAMIC_RECORDING_INTERVAL).
     */
    private short recordingInterval;
    /**
     * Average heart rate of exercise.
     */
    private short heartRateAVG;
    /**
     * Maximim heart rate of exercise.
     */
    private short heartRateMax;
    /**
     * The speed data of exercise (if recorded).
     */
    private ExerciseSpeed speed;
    /**
     * The cadence data of exercise (if recorded).
     */
    private ExerciseCadence cadence;
    /**
     * The altitude data of exercise (if recorded).
     */
    private ExerciseAltitude altitude;
    /**
     * The temperature data of exercise.
     */
    private ExerciseTemperature temperature;
    /**
     * Energy "wasted" for exercise (in kCal).
     */
    private int energy;
    /**
     * Cumulative "wasted" energy of all exercises (in kCal).
     */
    private int energyTotal;
    /**
     * Cumulative workout time (in minutes).
     */
    private int sumExerciseTime;
    /**
     * Cumulative ride time (in minutes).
     */
    private int sumRideTime;
    /**
     * Odometer (cumulative ride distance) in km.
     */
    private int odometer;

    /**
     * Array of heartrate limit data (can be more then one).
     */
    private HeartRateLimit[] heartRateLimits;
    /**
     * Array containing the data of all laps of exercise.
     */
    private Lap[] lapList;
    /**
     * Array containing the data of all recorded samples (for each interval) of exercise.
     */
    private ExerciseSample[] sampleList;

    /**
     * This is the list of possible file types of an exercise.
     */
    public enum ExerciseFileType {
        S710RAW, S610RAW, S510RAW, HRM, HAC4TUR, RS200SDRAW, F6RAW, SSCSV, GARMIN_FIT, GARMIN_TCX, PED, TIMEX_PWX, GPX
    }

    /**
     * Constant for dynamic exercise sample recording interval (e.g. in Garmin TCX files).
     */
    public static final short DYNAMIC_RECORDING_INTERVAL = -1;

    public ExerciseFileType getFileType() {
        return fileType;
    }

    public void setFileType(ExerciseFileType fileType) {
        this.fileType = fileType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RecordingMode getRecordingMode() {
        return recordingMode;
    }

    public void setRecordingMode(RecordingMode recordingMode) {
        this.recordingMode = recordingMode;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public short getRecordingInterval() {
        return recordingInterval;
    }

    public void setRecordingInterval(short recordingInterval) {
        this.recordingInterval = recordingInterval;
    }

    public short getHeartRateAVG() {
        return heartRateAVG;
    }

    public void setHeartRateAVG(short heartRateAVG) {
        this.heartRateAVG = heartRateAVG;
    }

    public short getHeartRateMax() {
        return heartRateMax;
    }

    public void setHeartRateMax(short heartRateMax) {
        this.heartRateMax = heartRateMax;
    }

    public ExerciseSpeed getSpeed() {
        return speed;
    }

    public void setSpeed(ExerciseSpeed speed) {
        this.speed = speed;
    }

    public ExerciseCadence getCadence() {
        return cadence;
    }

    public void setCadence(ExerciseCadence cadence) {
        this.cadence = cadence;
    }

    public ExerciseAltitude getAltitude() {
        return altitude;
    }

    public void setAltitude(ExerciseAltitude altitude) {
        this.altitude = altitude;
    }

    public ExerciseTemperature getTemperature() {
        return temperature;
    }

    public void setTemperature(ExerciseTemperature temperature) {
        this.temperature = temperature;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergyTotal() {
        return energyTotal;
    }

    public void setEnergyTotal(int energyTotal) {
        this.energyTotal = energyTotal;
    }

    public int getSumExerciseTime() {
        return sumExerciseTime;
    }

    public void setSumExerciseTime(int sumExerciseTime) {
        this.sumExerciseTime = sumExerciseTime;
    }

    public int getSumRideTime() {
        return sumRideTime;
    }

    public void setSumRideTime(int sumRideTime) {
        this.sumRideTime = sumRideTime;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public HeartRateLimit[] getHeartRateLimits() {
        return heartRateLimits;
    }

    public void setHeartRateLimits(HeartRateLimit[] heartRateLimits) {
        this.heartRateLimits = heartRateLimits;
    }

    public Lap[] getLapList() {
        return lapList;
    }

    public void setLapList(Lap[] lapList) {
        this.lapList = lapList;
    }

    public ExerciseSample[] getSampleList() {
        return sampleList;
    }

    public void setSampleList(ExerciseSample[] sampleList) {
        this.sampleList = sampleList;
    }

    /**
     * In most file formats (e.g. S710Raw, HRM) there are no distance values for each
     * recorded sample. So they need to be calculated from the sample time and speed.
     * This calculation is sometimes not total precise, the distance of last sample is
     * smaller/larger then the exercise distance. So all the sample distances needs to
     * get recalculated in relation to the exercise distance.
     */
    public void repairSamples() {
        // is all the required speed data available ?
        if ((this.speed == null) || (this.speed.getDistance() == 0) ||
                (this.sampleList == null) || (this.sampleList.length == 0)) {
            return;
        }

        // it's possible that there are not recorded samples for the whole exercise time
        // (e.g. connection problems) => in this case we can't repair the sample distances
        if (this.sampleList.length < (duration / 10 / recordingInterval)) {
            return;
        }

        // calculate relation of exercise distance to last sample distance
        ExerciseSample lastSample = this.sampleList[this.sampleList.length - 1];
        double fRelation = lastSample.getDistance() / (double) this.speed.getDistance();

        // process all samples and recalculate the sample distance in relation to exercise distance 
        for (ExerciseSample sample : this.sampleList) {
            sample.setDistance((int) Math.round(sample.getDistance() / fRelation));
        }
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(EVExercise.class.getName()).append(":\n");
        sBuilder.append(" [fileType=").append(this.fileType).append("\n");
        sBuilder.append("  deviceName=").append(this.deviceName).append("]\n");
        sBuilder.append("  dateTime=").append(this.dateTime).append("\n");
        sBuilder.append("  type=").append(this.type).append("\n");
        sBuilder.append("  duration=").append(this.duration).append("\n");
        sBuilder.append("  recordingInterval=").append(this.recordingInterval).append("\n");
        sBuilder.append("  heartRateAVG=").append(this.heartRateAVG).append("\n");
        sBuilder.append("  heartRateMax=").append(this.heartRateMax).append("\n");
        sBuilder.append("  energy=").append(this.energy).append("\n");
        sBuilder.append("  energyTotal=").append(this.energyTotal).append("\n");
        sBuilder.append("  sumExerciseTime=").append(this.sumExerciseTime).append("\n");
        sBuilder.append("  sumRideTime=").append(this.sumRideTime).append("\n");
        sBuilder.append("  odometer=").append(this.odometer).append("]\n");

        if (this.recordingMode != null) sBuilder.append(this.recordingMode);
        if (this.speed != null) sBuilder.append(this.speed);
        if (this.cadence != null) sBuilder.append(this.cadence);
        if (this.altitude != null) sBuilder.append(this.altitude);
        if (this.temperature != null) sBuilder.append(this.temperature);

        if (this.heartRateLimits != null) {
            Stream.of(this.heartRateLimits).forEach(sBuilder::append);
        }

        if (this.lapList != null) {
            Stream.of(this.lapList).forEach(sBuilder::append);
        }

        if (this.sampleList != null) {
            Stream.of(this.sampleList).forEach(sBuilder::append);
        }

        return sBuilder.toString();
    }
}
