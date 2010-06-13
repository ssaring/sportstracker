package de.saring.exerciseviewer.data;

import java.io.Serializable;
    
/**
 * This class contains all data recorded each interval. The altitude, speed,
 * cadence and power is optional and may be not recorded.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class ExerciseSample implements Serializable {
    
    /** Timestamp since exercise start of this sample (in 1/1000 sec). */
    private long timestamp;
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
    /** Temperature at record moment (in degrees celcius, optional). (Relevant for HAC4.) */
    private short temperature;
    /** The geographical location of this sample in the exercise track (optional). */
    private Position position;
    
    /***** BEGIN: Generated Getters and Setters *****/
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
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
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }

    /***** END: Generated Getters and Setters *****/

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder ();

        sBuilder.append (ExerciseSample.class.getName () + ":\n");
        sBuilder.append (" [timestamp=" + this.timestamp + "\n");
        sBuilder.append ("  heartRate=" + this.heartRate + "\n");
        sBuilder.append ("  altitude=" + this.altitude + "\n");
        sBuilder.append ("  speed=" + this.speed + "\n");
        sBuilder.append ("  cadence=" + this.cadence + "\n");
        sBuilder.append ("  distance=" + this.distance + "\n");
        sBuilder.append ("  temperature=" + this.temperature + "\n");
        sBuilder.append ("  position=" + this.position + "]\n");

        return sBuilder.toString ();
    }
}
