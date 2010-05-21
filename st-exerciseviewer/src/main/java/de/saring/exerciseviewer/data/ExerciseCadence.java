package de.saring.exerciseviewer.data;

import java.io.Serializable;
    
/**
 * This class stores the cadence informations of a recorded exercise.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class ExerciseCadence implements Serializable
{
    /** Average cadence of exercise (rpm). */
    private short cadenceAVG;
    /** Maximum cadence of exercise (rpm). */
    private short cadenceMax;

    /***** BEGIN: Generated Getters and Setters *****/
    
    public short getCadenceAVG () {
        return cadenceAVG;
    }

    public void setCadenceAVG (short cadenceAVG) {
        this.cadenceAVG = cadenceAVG;
    }

    public short getCadenceMax () {
        return cadenceMax;
    }

    public void setCadenceMax (short cadenceMax) {
        this.cadenceMax = cadenceMax;
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

        sBuilder.append (ExerciseCadence.class.getName () + ":\n");
        sBuilder.append (" [cadenceAVG=" + cadenceAVG + "\n");
        sBuilder.append ("  cadenceMax=" + cadenceMax + "]\n");

        return sBuilder.toString ();
    }
}
