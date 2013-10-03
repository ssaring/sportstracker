package de.saring.exerciseviewer.data;

/**
 * The Position class defines the geographical location of one specific
 * point of the exercise track (also known as track point).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class Position {

    /**
     * Latitude of this trackpoint in degrees.
     */
    private double latitude;

    /**
     * Longitude of this trackpoint in degrees.
     */
    private double longitude;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * ** BEGIN: Generated Getters and Setters ****
     */
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * ** END: Generated Getters and Setters ****
     */

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(Position.class.getName() + ":\n");
        sBuilder.append(" [latitude=" + this.latitude + "\n");
        sBuilder.append("  longitude=" + this.longitude + "]\n");
        return sBuilder.toString();
    }
}
