package de.saring.exerciseviewer.data;

    
/**
 * This class stores the speed informations of a recorded exercise.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class ExerciseSpeed
{
    /** Average speed of exercise (in km/h). */
    private float speedAVG;
    /** Maximum speed of exercise (in km/h). */
    private float speedMax;
    /** Distance of exercise (in meters). */
    private int distance;

    /***** BEGIN: Generated Getters and Setters *****/
    
    public float getSpeedAVG () {
        return speedAVG;
    }

    public void setSpeedAVG (float speedAVG) {
        this.speedAVG = speedAVG;
    }

    public float getSpeedMax () {
        return speedMax;
    }

    public void setSpeedMax (float speedMax) {
        this.speedMax = speedMax;
    }

    public int getDistance () {
        return distance;
    }

    public void setDistance (int distance) {
        this.distance = distance;
    }
    
    /***** END: Generated Getters and Setters *****/

    @Override
    public String toString ()
    {
        StringBuilder sBuilder = new StringBuilder ();

        sBuilder.append (ExerciseSpeed.class.getName () + ":\n");
        sBuilder.append (" [speedAVG=" + this.speedAVG + "\n");
        sBuilder.append ("  speedMax=" + this.speedMax + "\n");
        sBuilder.append ("  distance=" + this.distance + "]\n");

        return sBuilder.toString ();
    }
}
