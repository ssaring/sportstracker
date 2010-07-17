package de.saring.exerciseviewer.parser.impl.garminfit;

import java.util.Date;

import de.saring.exerciseviewer.data.Lap;

/**
 * Extension class for Lap data. The lap split time needs to be calculated at the end,
 * because the exercise start time is not known at time of lap parsing. The timestamp
 * can't be stores in Lap.timeSplit, it's data type int is too small for full timestamps.
 */
final class FitLap {
    
    /** The Lap object to wrap. */
    private Lap lap;
    /** The timestamp of lap split. */
    private Date splitTime;

    public FitLap(Lap lap, Date splitTime) {
        this.lap = lap;
        this.splitTime = splitTime;
    }
    
    public Lap getLap() {
        return lap;
    }
    
    public Date getSplitTime() {
        return splitTime;
    }
}
