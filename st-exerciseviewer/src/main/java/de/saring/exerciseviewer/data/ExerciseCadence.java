package de.saring.exerciseviewer.data;


/**
 * This class stores the cadence informations of a recorded exercise.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class ExerciseCadence {
    /**
     * Average cadence of exercise (rpm).
     */
    private short cadenceAVG;
    /**
     * Maximum cadence of exercise (rpm).
     */
    private short cadenceMax;

    public short getCadenceAVG() {
        return cadenceAVG;
    }

    public void setCadenceAVG(short cadenceAVG) {
        this.cadenceAVG = cadenceAVG;
    }

    public short getCadenceMax() {
        return cadenceMax;
    }

    public void setCadenceMax(short cadenceMax) {
        this.cadenceMax = cadenceMax;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(ExerciseCadence.class.getName()).append(":\n");
        sBuilder.append(" [cadenceAVG=").append(cadenceAVG).append("\n");
        sBuilder.append("  cadenceMax=").append(cadenceMax).append("]\n");

        return sBuilder.toString();
    }
}
