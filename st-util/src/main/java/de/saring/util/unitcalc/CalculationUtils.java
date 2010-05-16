package de.saring.util.unitcalc;

/** 
 * This class contains several static methods for calculating exercise data.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class CalculationUtils
{
    /**
     * Calculates the distance for the given AVG speed and duration.
     * 
     * @param avgSpeed avarage speed (km/h)
     * @param duration duration (seconds)
     * @return the distance (km)
     */
    public static float calculateDistance (float avgSpeed, int duration) {
        return (duration / 3600f) * avgSpeed;
    }
    
    /**
     * Calculates the AVG speed for the given distance and duration.
     * 
     * @param distance distance (km)
     * @param duration duration (seconds)
     * @return the AVG speed (km/h)
     */
    public static float calculateAvgSpeed (float distance, int duration) {
        return distance / (duration / 3600f);
    }
    
    /**
     * Calculates the duration for the given distance and AVG speed.
     * 
     * @param distance distance (km)
     * @param avgSpeed avarage speed (km/h)
     * @return the duration (seconds)
     */
    public static int calculateDuration (float distance, float avgSpeed) {
        return (int) ((distance / avgSpeed) * 3600);
    }
}