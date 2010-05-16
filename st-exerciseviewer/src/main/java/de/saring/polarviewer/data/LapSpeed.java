package de.saring.polarviewer.data;

import java.io.Serializable;
    
/**
 * This class contains all speed data of a lap of an exercise.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class LapSpeed implements Serializable
{
    /** Speed at end of lap (km/h). */
    private float speedEnd;
    /** Average speed of lap (km/h). */
    private float speedAVG;
    /** Distance of lap (in meters) from the beginning of the exercise, not from the beginning of the lap! */
    private int distance;
    /** Cadence at the end of the lap (rpm). */
    private short cadence;
        
    /***** BEGIN: Generated Getters and Setters *****/
    
    public float getSpeedEnd () {
        return speedEnd;
    }

    public void setSpeedEnd (float speedEnd) {
        this.speedEnd = speedEnd;
    }

    public float getSpeedAVG () {
        return speedAVG;
    }

    public void setSpeedAVG (float speedAVG) {
        this.speedAVG = speedAVG;
    }

    public int getDistance () {
        return distance;
    }

    public void setDistance (int distance) {
        this.distance = distance;
    }

    public short getCadence () {
        return cadence;
    }

    public void setCadence (short cadence) {
        this.cadence = cadence;
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

        sBuilder.append (LapSpeed.class.getName () + ":\n");
        sBuilder.append (" [speedEnd=" + this.speedEnd + "\n");
        sBuilder.append ("  speedAVG=" + this.speedAVG + "\n");
        sBuilder.append ("  distance=" + this.distance + "\n");
        sBuilder.append ("  cadence=" + this.cadence + "]\n");

        return sBuilder.toString ();
    }
}
