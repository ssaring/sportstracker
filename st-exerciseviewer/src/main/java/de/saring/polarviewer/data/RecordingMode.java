package de.saring.polarviewer.data;

import java.io.Serializable;

/**
 * This class contains the informations about what was beeing recorded in the
 * current exercise.
 * @author  Stefan Saring
 * @version 1.0
 */
public final class RecordingMode implements Serializable
{
    /** Is altitude beeing recorded ? */
    private boolean altitude;
    /** Is bike speed beeing recorded ? */
    private boolean speed;
    /** Is bicycling cadence beeing recorded ? */
    private boolean cadence;
    /** Is bicycling power beeing recorded ? */
    private boolean power;
    /** Number of bike, when speed is beeing recorded (Polar S710 supports 2). */
    private byte bikeNumber;
    /** Has the temperature been recorded ? (Only in HAC4 devices). */
    private boolean temperature = false;
    /** Is the exercise an interval trainging (S510 only?) */
    private boolean intervalExercise = false;

    /***** BEGIN: Generated Getters and Setters *****/

    public boolean isAltitude () {
        return altitude;
    }

    public void setAltitude (boolean altitude) {
        this.altitude = altitude;
    }

    public boolean isSpeed () {
        return speed;
    }

    public void setSpeed (boolean speed) {
        this.speed = speed;
    }

    public boolean isCadence () {
        return cadence;
    }

    public void setCadence (boolean cadence) {
        this.cadence = cadence;
    }

    public boolean isPower () {
        return power;
    }

    public void setPower (boolean power) {
        this.power = power;
    }

    public byte getBikeNumber () {
        return bikeNumber;
    }

    public void setBikeNumber (byte bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public boolean isTemperature () {
        return temperature;
    }

    public void setTemperature (boolean temperature) {
        this.temperature = temperature;
    }
    
    public boolean isIntervalExercise () {
        return intervalExercise;
    }
    
    public void setIntervalExercise (boolean IntervalExercise) {
        this.intervalExercise = IntervalExercise;
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

        sBuilder.append (RecordingMode.class.getName () + ":\n");
        sBuilder.append (" [altitude=" + this.altitude + "\n");
        sBuilder.append ("  speed=" + this.speed + "\n");
        sBuilder.append ("  cadence=" + this.cadence + "\n");
        sBuilder.append ("  power=" + this.power + "\n");
        sBuilder.append ("  bikeNumber=" + this.bikeNumber + "\n");
        sBuilder.append ("  temperature=" + this.temperature + "]\n");
        sBuilder.append ("  intervalExercise=" + this.intervalExercise + "]\n");

        return sBuilder.toString ();
    }
}