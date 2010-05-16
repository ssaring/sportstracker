package de.saring.polarviewer.data;

import java.io.Serializable;
    
/**
 * This class contains all data recorded each interval. The altitude, speed,
 * cadence and power is optional and may be not recorded.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class ExerciseSample implements Serializable
{
    /** Heartrate at record moment. */
    private short heartRate;
    /** Altitude at record moment. */
    private short altitude;
    /** Speed at record moment (in km/h). */
    private float speed;
    /** Cadence at record moment (in rpm). */
    private short cadence;
    /** Distance at record moment (in meters). */
    private int distance;
    /** Temperature at record moment (in degrees celcius). (Relevant for HAC4.) */
    private short temperature;
    
    /***** BEGIN: Generated Getters and Setters *****/
    
    public short getHeartRate () {
        return heartRate;
    }

    public void setHeartRate (short heartRate) {
        this.heartRate = heartRate;
    }

    public short getAltitude () {
        return altitude;
    }

    public void setAltitude (short altitude) {
        this.altitude = altitude;
    }

    public float getSpeed () {
        return speed;
    }

    public void setSpeed (float speed) {
        this.speed = speed;
    }

    public short getCadence () {
        return cadence;
    }

    public void setCadence (short cadence) {
        this.cadence = cadence;
    }

    public int getDistance () {
        return distance;
    }

    public void setDistance (int distance) {
        this.distance = distance;
    }

    public short getTemperature () {
        return temperature;
    }

    public void setTemperature (short temperature) {
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

        sBuilder.append (ExerciseSample.class.getName () + ":\n");
        sBuilder.append (" [heartRate=" + this.heartRate + "\n");
        sBuilder.append ("  altitude=" + this.altitude + "\n");
        sBuilder.append ("  speed=" + this.speed + "\n");
        sBuilder.append ("  cadence=" + this.cadence + "\n");
        sBuilder.append ("  distance=" + this.distance + "\n");
        sBuilder.append ("  temperature=" + this.temperature + "]\n");

        return sBuilder.toString ();
    }
}
