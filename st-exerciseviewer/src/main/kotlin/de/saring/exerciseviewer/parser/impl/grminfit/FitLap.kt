package de.saring.exerciseviewer.parser.impl.garminfit

import de.saring.exerciseviewer.data.Lap
import java.time.LocalDateTime

/**
 * Extension class for Lap data. The lap split time needs to be calculated at the end,  because the exercise start time
 * is not known at time of lap parsing. The timestamp can't be stores in Lap.timeSplit, it's data type int is too small
 * for full timestamps.
 *
 * @property lap the Lap object to wrap
 * @property splitDatTime the date-timestamp of lap split
 */
internal class FitLap(
        val lap: Lap,
        val splitDatTime: LocalDateTime)
