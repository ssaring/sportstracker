package de.saring.exerciseviewer.data;


/**
 * This class contains the heartrate limit data of a recorded exercise. It
 * consists of the limit range and the times below / within / above.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class HeartRateLimit {

    /**
     * Lower heartrate limit.
     */
    private short lowerHeartRate;
    /**
     * Upper heartrate limit.
     */
    private short upperHeartRate;
    /**
     * Time in seconds below the limit.
     */
    private int timeBelow;
    /**
     * Time in seconds within the limit.
     */
    private int timeWithin;
    /**
     * Time in seconds above the limit.
     */
    private int timeAbove;
    /**
     * Flag is true when the range is set by absolute values (default), false for percentual values (e.g. 60-80%).
     */
    private boolean absoluteRange = true;

    public short getLowerHeartRate() {
        return lowerHeartRate;
    }

    public void setLowerHeartRate(short lowerHeartRate) {
        this.lowerHeartRate = lowerHeartRate;
    }

    public short getUpperHeartRate() {
        return upperHeartRate;
    }

    public void setUpperHeartRate(short upperHeartRate) {
        this.upperHeartRate = upperHeartRate;
    }

    public int getTimeBelow() {
        return timeBelow;
    }

    public void setTimeBelow(int timeBelow) {
        this.timeBelow = timeBelow;
    }

    public int getTimeWithin() {
        return timeWithin;
    }

    public void setTimeWithin(int timeWithin) {
        this.timeWithin = timeWithin;
    }

    public int getTimeAbove() {
        return timeAbove;
    }

    public void setTimeAbove(int timeAbove) {
        this.timeAbove = timeAbove;
    }

    public boolean isAbsoluteRange() {
        return absoluteRange;
    }

    public void setAbsoluteRange(boolean absoluteRange) {
        this.absoluteRange = absoluteRange;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(HeartRateLimit.class.getName()).append(":\n");
        sBuilder.append(" [lowerHeartRate=").append(this.lowerHeartRate).append("\n");
        sBuilder.append("  upperHeartRate=").append(this.upperHeartRate).append("\n");
        sBuilder.append("  timeBelow=").append(this.timeBelow).append("\n");
        sBuilder.append("  timeWithin=").append(this.timeWithin).append("\n");
        sBuilder.append("  timeAbove=").append(this.timeAbove).append("\n");
        sBuilder.append("  absoluteRange=").append(this.absoluteRange).append("]\n");

        return sBuilder.toString();
    }
}
