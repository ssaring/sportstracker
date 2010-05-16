package de.saring.polarviewer.data;

import java.io.Serializable;
    
/**
 * This class contains all data of a lap of an exercise.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class Lap implements Serializable
{
    /** Lap split time (in 1/10 seconds). */
    private int timeSplit;
    /** Heartrate at lap split time. */
    private short heartRateSplit;
    /** Average heartrate at lap. */
    private short heartRateAVG;
    /** Maximum heartrate at lap. */
    private short heartRateMax;
    /** Lap speed data (if recorded). */
    private LapSpeed speed;
    /** Lap altitude data (if recorded). */
    private LapAltitude altitude;
    /** Lap temparature. */
    private LapTemperature temperature;

    /***** BEGIN: Generated Getters and Setters *****/

    public int getTimeSplit () {
        return timeSplit;
    }

    public void setTimeSplit (int timeSplit) {
        this.timeSplit = timeSplit;
    }

    public short getHeartRateSplit () {
        return heartRateSplit;
    }

    public void setHeartRateSplit (short heartRateSplit) {
        this.heartRateSplit = heartRateSplit;
    }

    public short getHeartRateAVG () {
        return heartRateAVG;
    }

    public void setHeartRateAVG (short heartRateAVG) {
        this.heartRateAVG = heartRateAVG;
    }

    public short getHeartRateMax () {
        return heartRateMax;
    }

    public void setHeartRateMax (short heartRateMax) {
        this.heartRateMax = heartRateMax;
    }

    public LapSpeed getSpeed () {
        return speed;
    }

    public void setSpeed (LapSpeed speed) {
        this.speed = speed;
    }

    public LapAltitude getAltitude () {
        return altitude;
    }

    public void setAltitude (LapAltitude altitude) {
        this.altitude = altitude;
    }

    public LapTemperature getTemperature () {
        return temperature;
    }

    public void setTemperature (LapTemperature temperature) {
        this.temperature = temperature;
    }
    
    /***** END: Generated Getters and Setters *****/

    /** 
     * Returns a string representation of this object. 
     * @return string with object content
     */
    @Override
    public String toString ()
    {
        StringBuilder sBuilder = new StringBuilder ();

        sBuilder.append (Lap.class.getName () + ":\n");
        sBuilder.append (" [timeSplit=" + this.timeSplit + "\n");
        sBuilder.append ("  heartRateSplit=" + this.heartRateSplit + "\n");
        sBuilder.append ("  heartRateAVG=" + this.heartRateAVG + "\n");
        sBuilder.append ("  heartRateMax=" + this.heartRateMax + "\n");
        sBuilder.append ("  speed=" + this.speed + "\n");
        sBuilder.append ("  altitude=" + this.altitude + "\n");
        sBuilder.append ("  temperature=" + this.temperature + "]\n");

        return sBuilder.toString ();
    }
}
