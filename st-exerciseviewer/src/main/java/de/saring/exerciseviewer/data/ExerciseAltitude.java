package de.saring.exerciseviewer.data;


/**
 * This class stores the altitude informations of a recorded exercise.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class ExerciseAltitude {
    /**
     * Minimum altitude of exercise.
     */
    private short altitudeMin;
    /**
     * Average altitude of exercise.
     */
    private short altitudeAVG;
    /**
     * Maximum altitude of exercise.
     */
    private short altitudeMax;
    /**
     * Ascent of exercise (climbed height meters).
     */
    private int ascent;

    /**
     * ** BEGIN: Generated Getters and Setters ****
     */

    public short getAltitudeMin() {
        return altitudeMin;
    }

    public void setAltitudeMin(short altitudeMin) {
        this.altitudeMin = altitudeMin;
    }

    public short getAltitudeAVG() {
        return altitudeAVG;
    }

    public void setAltitudeAVG(short altitudeAVG) {
        this.altitudeAVG = altitudeAVG;
    }

    public short getAltitudeMax() {
        return altitudeMax;
    }

    public void setAltitudeMax(short altitudeMax) {
        this.altitudeMax = altitudeMax;
    }

    public int getAscent() {
        return ascent;
    }

    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    /**
     * ** END: Generated Getters and Setters ****
     */

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(ExerciseAltitude.class.getName() + ":\n");
        sBuilder.append(" [altitudeMin=" + this.altitudeMin + "\n");
        sBuilder.append("  altitudeAVG=" + this.altitudeAVG + "\n");
        sBuilder.append("  altitudeMax=" + this.altitudeMax + "\n");
        sBuilder.append("  ascent=" + this.ascent + "]\n");

        return sBuilder.toString();
    }
}
