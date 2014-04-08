package de.saring.exerciseviewer.data;


/**
 * This class stores the speed informations of a recorded exercise.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class ExerciseSpeed {
    /**
     * Average speed of exercise (in km/h).
     */
    private float speedAVG;
    /**
     * Maximum speed of exercise (in km/h).
     */
    private float speedMax;
    /**
     * Distance of exercise (in meters).
     */
    private int distance;

    public float getSpeedAVG() {
        return speedAVG;
    }

    public void setSpeedAVG(float speedAVG) {
        this.speedAVG = speedAVG;
    }

    public float getSpeedMax() {
        return speedMax;
    }

    public void setSpeedMax(float speedMax) {
        this.speedMax = speedMax;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(ExerciseSpeed.class.getName()).append(":\n");
        sBuilder.append(" [speedAVG=").append(this.speedAVG).append("\n");
        sBuilder.append("  speedMax=").append(this.speedMax).append("\n");
        sBuilder.append("  distance=").append(this.distance).append("]\n");

        return sBuilder.toString();
    }
}
