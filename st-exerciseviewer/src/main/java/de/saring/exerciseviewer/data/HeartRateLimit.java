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

    /**
     * ** BEGIN: Generated Getters and Setters ****
     */

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

    /**
     * ** END: Generated Getters and Setters ****
     */

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(HeartRateLimit.class.getName() + ":\n");
        sBuilder.append(" [lowerHeartRate=" + this.lowerHeartRate + "\n");
        sBuilder.append("  upperHeartRate=" + this.upperHeartRate + "\n");
        sBuilder.append("  timeBelow=" + this.timeBelow + "\n");
        sBuilder.append("  timeWithin=" + this.timeWithin + "\n");
        sBuilder.append("  timeAbove=" + this.timeAbove + "\n");
        sBuilder.append("  absoluteRange=" + this.absoluteRange + "]\n");

        return sBuilder.toString();
    }
}
