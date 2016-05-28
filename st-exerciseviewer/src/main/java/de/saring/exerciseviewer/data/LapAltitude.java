package de.saring.exerciseviewer.data;


/**
 * This class contains all altitude data of a lap of an exercise.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class LapAltitude {
    /**
     * Altitude at lap.
     */
    private short altitude;
    /**
     * Ascent (climbed height meters) of lap.
     */
    private int ascent;

    public short getAltitude() {
        return altitude;
    }

    public void setAltitude(short altitude) {
        this.altitude = altitude;
    }

    public int getAscent() {
        return ascent;
    }

    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(LapAltitude.class.getName()).append(":\n");
        sBuilder.append(" [altitude=").append(this.altitude).append("\n");
        sBuilder.append("  ascent=").append(this.ascent).append("]\n");

        return sBuilder.toString();
    }
}
