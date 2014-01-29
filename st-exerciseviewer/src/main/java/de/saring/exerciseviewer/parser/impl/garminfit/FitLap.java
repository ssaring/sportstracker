package de.saring.exerciseviewer.parser.impl.garminfit;

import de.saring.exerciseviewer.data.Lap;

import java.time.LocalDateTime;

/**
 * Extension class for Lap data. The lap split time needs to be calculated at the end,
 * because the exercise start time is not known at time of lap parsing. The timestamp
 * can't be stores in Lap.timeSplit, it's data type int is too small for full timestamps.
 */
final class FitLap {

    /**
     * The Lap object to wrap.
     */
    private final Lap lap;

    /**
     * The date-timestamp of lap split.
     */
    private final LocalDateTime splitDateTime;

    public FitLap(Lap lap, LocalDateTime splitDateTime) {
        this.lap = lap;
        this.splitDateTime = splitDateTime;
    }

    public Lap getLap() {
        return lap;
    }

    public LocalDateTime getSplitDatTime() {
        return splitDateTime;
    }
}
