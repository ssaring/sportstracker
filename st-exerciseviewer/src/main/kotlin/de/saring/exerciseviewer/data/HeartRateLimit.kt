package de.saring.exerciseviewer.data

/**
 * This class contains the heartrate limit data of a recorded exercise. It consists of the limit range
 * and the times below, within and above.
 *
 * @property lowerHeartRate Lower heartrate limit.
 * @property upperHeartRate Upper heartrate limit.
 * @property timeBelow Time in seconds below the limit (can be missing).
 * @property timeWithin Time in seconds within the limit.
 * @property timeAbove Time in seconds above the limit (can be missing).
 * @property isAbsoluteRange Flag is true when the range is set by absolute values (default), false for percentual values (e.g. 60-80%).
 *
 * @author Stefan Saring
 */
data class HeartRateLimit(

    var lowerHeartRate: Short,
    var upperHeartRate: Short,
    var timeBelow: Int?,
    var timeWithin: Int,
    var timeAbove: Int?,
    var isAbsoluteRange: Boolean = true)
