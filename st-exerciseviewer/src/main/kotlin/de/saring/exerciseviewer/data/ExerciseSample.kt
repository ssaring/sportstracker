package de.saring.exerciseviewer.data

/**
 * This data class contains all the information recorded at each interval. All the attributes are optional,
 * it depends on the file type (or heartrate monitor model) whether the data is available or not.
 *
 * @property timestamp Timestamp since exercise start of this sample (in 1/1000 sec).
 * @property heartRate Heartrate at record moment.
 * @property altitude Altitude at record moment.
 * @property speed Speed at record moment (in km/h).
 * @property cadence Cadence at record moment (in rpm).
 * @property distance Distance at record moment (in meters).
 * @property temperature Temperature at record moment (in degrees celcius, optional).
 * @property position The geographical location of this sample in the exercise track (optional).
 *
 * @author Stefan Saring
 */
data class ExerciseSample(

        var timestamp: Long? = null,
        var heartRate: Short? = null,
        var altitude: Short? = null,
        var speed: Float? = null,
        var cadence: Short? = null,
        var distance: Int? = null,
        var temperature: Short? = null,
        var position: Position? = null)
