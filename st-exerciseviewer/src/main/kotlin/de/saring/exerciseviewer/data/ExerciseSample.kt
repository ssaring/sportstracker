package de.saring.exerciseviewer.data

/**
 * This data class contains all the information recorded at each interval. All the attributes are optional,
 * it depends on the file type (or heartrate monitor model) whether the data is available or not.
 *
 * @author Stefan Saring
 */
data class ExerciseSample(

        /** Timestamp since exercise start of this sample (in 1/1000 sec). */
        var timestamp: Long? = null,
        /** Heartrate at record moment. */
        var heartRate: Short? = null,
        /** Altitude at record moment. */
        var altitude: Short? = null,
        /** Speed at record moment (in km/h). */
        var speed: Float? = null,
        /** Cadence at record moment (in rpm). */
        var cadence: Short? = null,
        /** Distance at record moment (in meters). */
        var distance: Int? = null,
        /** Temperature at record moment (in degrees celcius, optional). */
        var temperature: Short? = null,
        /** The geographical location of this sample in the exercise track (optional). */
        var position: Position? = null)
