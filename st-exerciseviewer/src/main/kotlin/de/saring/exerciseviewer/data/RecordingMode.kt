package de.saring.exerciseviewer.data

/**
 * This class contains the information about what has been recorded in an exercise.
 *
 * @author Stefan Saring
 */
data class RecordingMode(

        /** Has heart rate been recorded ? */
        var isHeartRate: Boolean = false,
        /** Has speed been recorded? */
        var isSpeed: Boolean = false,
        /** Has altitude been recorded? */
        var isAltitude: Boolean = false,
        /** Has cadence been recorded? */
        var isCadence: Boolean = false,
        /** Has bicycling power been recorded? */
        var isPower: Boolean = false,
        /** Has the temperature been recorded ? (Only in HAC4 devices). */
        var isTemperature: Boolean = false,
        /** Has the location of the trackpoints been recorded? (GPS data) */
        var isLocation: Boolean = false,
        /** Is the exercise an interval training (S510 only?) */
        var isIntervalExercise: Boolean = false,
        /** Number of bike, when speed has been recorded (Polar S710 supports 2). */
        var bikeNumber: Byte? = null)
