package de.saring.exerciseviewer.data

import java.time.LocalDateTime

/**
 * This class represents an exercise recorded with a heartrate monitor device (e.g. a Polar S710 or a Garmin Edge 520).
 *
 * @author Stefan Saring
 */
data class EVExercise(

    /** File type of an exercise (see enums). */
    var fileType: ExerciseFileType,
    /** Name of the HRM device (optional). */
    var deviceName: String? = null,
    /** Timestamp of exercise. */
    var dateTime: LocalDateTime? = null,
    /** Exercise type (label). */
    var type: String? = null,
    /** Record mode (what was recorded in exercise). */
    var recordingMode: RecordingMode = RecordingMode(),
    /** Duration of exercise in tenths of a second. */
    var duration: Int? = null,
    /** Recording interval in seconds (e.g. 5s, 15s, 60s or DYNAMIC_RECORDING_INTERVAL). */
    var recordingInterval: Short? = null,
    /** Average heart rate of exercise. */
    var heartRateAVG: Short? = null,
    /** Maximim heart rate of exercise. */
    var heartRateMax: Short? = null,
    /** The speed data of exercise (if recorded). */
    var speed: ExerciseSpeed? = null,
    /** The cadence data of exercise (if recorded). */
    var cadence: ExerciseCadence? = null,
    /** The altitude data of exercise (if recorded). */
    var altitude: ExerciseAltitude? = null,
    /** The temperature data of exercise. */
    var temperature: ExerciseTemperature? = null,
    /** Energy "wasted" for exercise (in kCal). */
    var energy: Int? = null,
    /** Cumulative "wasted" energy of all exercises (in kCal). */
    var energyTotal: Int? = null,
    /** Cumulative workout time (in minutes). */
    var sumExerciseTime: Int? = null,
    /** Cumulative ride time (in minutes). */
    var sumRideTime: Int? = null,
    /** Odometer (cumulative ride distance) in km. */
    var odometer: Int? = null,

    /** List of heartrate limit data (can be more then one). */
    var heartRateLimits: MutableList<HeartRateLimit> = mutableListOf(),
    /** List containing the data of all exercise laps. */
    var lapList: MutableList<Lap> = mutableListOf(),
    /** List containing the data of all recorded exercise samples (for each interval). */
    var sampleList: MutableList<ExerciseSample> = mutableListOf())
{
    /**
     * Secondary constructor for easier creation of instances from Java code, otherwise all attributes needs to get passed.
     *
     * @param fileType exercise file type
     */
    constructor(fileType: ExerciseFileType) : this(fileType, null)

    /**
     * This is the list of possible file types of an exercise.
     */
    enum class ExerciseFileType {
        S710RAW,
        S610RAW,
        S510RAW,
        HRM,
        HAC4TUR,
        RS200SDRAW,
        F6RAW,
        SSCSV,
        GARMIN_FIT,
        GARMIN_TCX,
        PED,
        TIMEX_PWX,
        GPX
    }

    /**
     * In most file formats (e.g. S710Raw, HRM) there are no distance values for each recorded sample. So they need
     * to be calculated from the sample time and speed. This calculation is sometimes not total precise, the distance
     * of last sample is smaller/larger then the exercise distance. So all the sample distances needs to get
     * recalculated in relation to the exercise distance.
     */
    fun repairSamples() {
        // is all the required speed data available ?
        if (this.speed == null || this.speed!!.distance == 0 || this.sampleList.isEmpty()
                || this.duration == null || this.recordingInterval == null) {
            return
        }

        // it's possible that there are not recorded samples for the whole exercise time
        // (e.g. connection problems) => in this case we can't repair the sample distances
        if (this.sampleList.size < this.duration!! / 10 / this.recordingInterval!!.toInt()) {
            return
        }

        // calculate relation of exercise distance to last sample distance
        val (_, _, _, _, _, distance) = this.sampleList[this.sampleList.size - 1]
        val fRelation = distance!! / this.speed!!.distance.toDouble()

        // process all samples and recalculate the sample distance in relation to exercise distance
        for (sample in this.sampleList) {
            sample.distance = Math.round(sample.distance!! / fRelation).toInt()
        }
    }

    companion object {

        /**
         * Constant for dynamic exercise sample recording interval (e.g. in Garmin TCX files).
         */
        const val DYNAMIC_RECORDING_INTERVAL: Short = -1
    }
}
