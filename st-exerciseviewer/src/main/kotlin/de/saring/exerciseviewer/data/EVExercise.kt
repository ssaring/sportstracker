package de.saring.exerciseviewer.data

import java.time.LocalDateTime

/**
 * This class represents an exercise recorded with a heartrate monitor device (e.g. a Polar S710 or a Garmin Edge 520).
 *
 * @property fileType File type of an exercise (see enums).
 * @property deviceName Name of the HRM device (optional).
 * @property dateTime Timestamp of exercise.
 * @property type Exercise type (label).
 * @property recordingMode Record mode (what was recorded in exercise).
 * @property duration Duration of exercise in tenths of a second.
 * @property recordingInterval Recording interval in seconds (e.g. 5s, 15s, 60s or DYNAMIC_RECORDING_INTERVAL).
 * @property heartRateAVG Average heart rate of exercise.
 * @property heartRateMax Maximum heart rate of exercise.
 * @property speed The speed data of exercise (if recorded).
 * @property cadence The cadence data of exercise (if recorded).
 * @property altitude The altitude data of exercise (if recorded).
 * @property temperature The temperature data of exercise (if recorded).
 * @property energy Energy "wasted" for exercise (in kCal).
 * @property energyTotal Cumulative "wasted" energy of all exercises (in kCal).
 * @property sumExerciseTime Cumulative workout time (in minutes).
 * @property sumRideTime Cumulative ride time (in minutes).
 * @property odometer  Odometer (cumulative ride distance) in km.
 * @property heartRateLimits List of heartrate limit data (can be more then one).
 * @property lapList List containing the data of all exercise laps.
 * @property sampleList List containing the data of all recorded exercise samples (for each interval).
 *
 * @author Stefan Saring
 */
data class EVExercise(

    var fileType: ExerciseFileType,
    var deviceName: String? = null,
    var dateTime: LocalDateTime? = null,
    var type: String? = null,
    var recordingMode: RecordingMode = RecordingMode(),
    var duration: Int? = null,
    var recordingInterval: Short? = null,
    var heartRateAVG: Short? = null,
    var heartRateMax: Short? = null,
    var speed: ExerciseSpeed? = null,
    var cadence: ExerciseCadence? = null,
    var altitude: ExerciseAltitude? = null,
    var temperature: ExerciseTemperature? = null,
    var energy: Int? = null,
    var energyTotal: Int? = null,
    var sumExerciseTime: Int? = null,
    var sumRideTime: Int? = null,
    var odometer: Int? = null,

    var heartRateLimits: MutableList<HeartRateLimit> = mutableListOf(),
    var lapList: MutableList<Lap> = mutableListOf(),
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
