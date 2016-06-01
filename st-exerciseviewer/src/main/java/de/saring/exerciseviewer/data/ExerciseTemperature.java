package de.saring.exerciseviewer.data;


/**
 * This class stores the temperature informations of a recorded exercise.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class ExerciseTemperature {
    /**
     * Minimum temperature of an exercise (in degrees celcius).
     */
    private short temperatureMin;
    /**
     * Average temperature of an exercise (in degrees celcius).
     */
    private short temperatureAVG;
    /**
     * Maximum temperature of an exercise (in degrees celcius).
     */
    private short temperatureMax;

    public short getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(short temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public short getTemperatureAVG() {
        return temperatureAVG;
    }

    public void setTemperatureAVG(short temperatureAVG) {
        this.temperatureAVG = temperatureAVG;
    }

    public short getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(short temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(ExerciseTemperature.class.getName()).append(":\n");
        sBuilder.append(" [temperatureMin=").append(this.temperatureMin).append("\n");
        sBuilder.append("  temperatureAVG=").append(this.temperatureAVG).append("\n");
        sBuilder.append("  temperatureMax=").append(this.temperatureMax).append("]\n");

        return sBuilder.toString();
    }
}
