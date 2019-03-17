package de.saring.util.unitcalc

/**
 * This class contains several static methods for calculating exercise data.
 *
 * @author Stefan Saring
 */
object CalculationUtils {

    /**
     * Calculates the distance for the given AVG speed and duration.
     *
     * @param avgSpeed average speed (km/h)
     * @param duration duration (seconds)
     * @return the distance (km)
     */
    @JvmStatic
    fun calculateDistance(avgSpeed: Float, duration: Int): Float =
            duration / 3600f * avgSpeed

    /**
     * Calculates the AVG speed for the given distance and duration.
     *
     * @param distance distance (km)
     * @param duration duration (seconds)
     * @return the AVG speed (km/h)
     */
    @JvmStatic
    fun calculateAvgSpeed(distance: Float, duration: Int): Float =
            distance / (duration / 3600f)

    /**
     * Calculates the duration for the given distance and AVG speed.
     *
     * @param distance distance (km)
     * @param avgSpeed average speed (km/h)
     * @return the duration (seconds)
     */
    @JvmStatic
    fun calculateDuration(distance: Float, avgSpeed: Float): Int =
            (distance / avgSpeed * 3600).toInt()
}
