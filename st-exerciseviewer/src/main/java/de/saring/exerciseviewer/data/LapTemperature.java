package de.saring.exerciseviewer.data;


/**
 * This class contains all temperature data of a lap of an exercise. It's
 * a separate class because it's recorded optionally.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class LapTemperature {
    /**
     * Temperature at lap (in celcius degrees).
     */
    private short temperature;

    public short getTemperature() {
        return temperature;
    }

    public void setTemperature(short temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append(LapTemperature.class.getName() + ":\n");
        sBuilder.append(" [temperature=" + this.temperature + "]\n");

        return sBuilder.toString();
    }
}
