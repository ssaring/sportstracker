package de.saring.exerciseviewer.data;

    
/**
 * This class contains all altitude data of a lap of an exercise.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class LapAltitude
{
    /** Altitude at lap. */
    private short altitude;
    /** Ascent (climbed height meters) of lap. */
    private int ascent;
        
    /***** BEGIN: Generated Getters and Setters *****/

    public short getAltitude () {
        return altitude;
    }

    public void setAltitude (short altitude) {
        this.altitude = altitude;
    }

    public int getAscent () {
        return ascent;
    }

    public void setAscent (int ascent) {
        this.ascent = ascent;
    }
    
    /***** END: Generated Getters and Setters *****/

    @Override
    public String toString ()
    {
        StringBuilder sBuilder = new StringBuilder ();

        sBuilder.append (LapAltitude.class.getName () + ":\n");
        sBuilder.append (" [altitude=" + this.altitude + "\n");
        sBuilder.append ("  ascent=" + this.ascent + "]\n");

        return sBuilder.toString ();
    }
}
