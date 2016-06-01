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
    private final double latitude;

    /**
     * Longitude of this trackpoint in degrees.
     */
    private final double longitude;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(Position.class.getName()).append(":\n");
        sBuilder.append(" [latitude=").append(this.latitude).append("\n");
        sBuilder.append("  longitude=").append(this.longitude).append("]\n");
        return sBuilder.toString();
    }
}
