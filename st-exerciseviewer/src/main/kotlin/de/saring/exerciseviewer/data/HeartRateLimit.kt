package de.saring.exerciseviewer.data

/**
 * This class contains the heartrate limit data of a recorded exercise. It consists of the limit range
 * and the times below, within and above.
 *
 * @author Stefan Saring
 */
data class HeartRateLimit(

    /** Lower heartrate limit. */
    var lowerHeartRate: Short,
    /** Upper heartrate limit. */
    var upperHeartRate: Short,
    /** Time in seconds below the limit (can be missing). */
    var timeBelow: Int?,
    /** Time in seconds within the limit. */
    var timeWithin: Int,
    /** Time in seconds above the limit (can be missing). */
    var timeAbove: Int?,
    /** Flag is true when the range is set by absolute values (default), false for percentual values (e.g. 60-80%). */
    var isAbsoluteRange: Boolean = true)
