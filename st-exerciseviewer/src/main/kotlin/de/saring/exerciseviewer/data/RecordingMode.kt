package de.saring.exerciseviewer.data

/**
 * This class contains the information about what has been recorded in an exercise.
 *
 * @property isHeartRate Has heart rate been recorded?
 * @property isSpeed Has speed been recorded?
 * @property isAltitude Has altitude been recorded?
 * @property isCadence Has cadence been recorded?
 * @property isPower Has bicycling power been recorded?
 * @property isTemperature Has the temperature been recorded ? (Only in HAC4 devices).
 * @property isLocation Has the location of the trackpoints been recorded? (GPS data)
 * @property isIntervalExercise Is the exercise an interval training (S510 only?)
 * @property bikeNumber Number of bike, when speed has been recorded (Polar S710 supports 2).
 *
 * @author Stefan Saring
 */
data class RecordingMode(

        var isHeartRate: Boolean = false,
        var isSpeed: Boolean = false,
        var isAltitude: Boolean = false,
        var isCadence: Boolean = false,
        var isPower: Boolean = false,
        var isTemperature: Boolean = false,
        var isLocation: Boolean = false,
        var isIntervalExercise: Boolean = false,
        var bikeNumber: Byte? = null)
