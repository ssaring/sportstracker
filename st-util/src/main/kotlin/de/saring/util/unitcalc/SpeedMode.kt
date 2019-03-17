package de.saring.util.unitcalc

/**
 * Enumeration of supported speed display or measurement modes.
 *
 * @author Stefan Saring
 */
enum class SpeedMode {

    /** Speed is measured as distance per hour (e.g. km/h). Most sport types are using the speed. */
    SPEED,
    /** Pace is measured in minutes per distance (e.g. min/km). This is very common for running activities. */
    PACE
}
