package de.saring.polarviewer.data;

import java.io.Serializable;
    
/**
 * This class contains all temperature data of a lap of an exercise. It's 
 * a separate class because it's recorded optionally.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class LapTemperature implements Serializable
{
    /** Temperature at lap (in celcius degrees). */
    private short temperature;

    /***** BEGIN: Generated Getters and Setters *****/

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

        sBuilder.append (LapTemperature.class.getName () + ":\n");
        sBuilder.append (" [temperature=" + this.temperature + "]\n");

        return sBuilder.toString ();
    }
}
