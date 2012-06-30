package de.saring.exerciseviewer.data;

    
/**
 * This class stores the temperature informations of a recorded exercise.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public final class ExerciseTemperature 
{
    /** Minimum temperature of an exercise (in degrees celcius). */
    private short temperatureMin;
    /** Average temperature of an exercise (in degrees celcius). */
    private short temperatureAVG;
    /** Maximum temperature of an exercise (in degrees celcius). */
    private short temperatureMax;
    
    /***** BEGIN: Generated Getters and Setters *****/
    
    public short getTemperatureMin () {
        return temperatureMin;
    }

    public void setTemperatureMin (short temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public short getTemperatureAVG () {
        return temperatureAVG;
    }

    public void setTemperatureAVG (short temperatureAVG) {
        this.temperatureAVG = temperatureAVG;
    }

    public short getTemperatureMax () {
        return temperatureMax;
    }

    public void setTemperatureMax (short temperatureMax) {
        this.temperatureMax = temperatureMax;
    }
    
    /***** END: Generated Getters and Setters *****/

    @Override
    public String toString ()
    {
        StringBuilder sBuilder = new StringBuilder ();

        sBuilder.append (ExerciseTemperature.class.getName () + ":\n");
        sBuilder.append (" [temperatureMin=" + this.temperatureMin + "\n");
        sBuilder.append ("  temperatureAVG=" + this.temperatureAVG + "\n");
        sBuilder.append ("  temperatureMax=" + this.temperatureMax + "]\n");

        return sBuilder.toString ();
    }
}
