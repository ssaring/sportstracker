package de.saring.exerciseviewer.parser.testutil;

import static de.saring.exerciseviewer.parser.testutil.Utils.toShort;

import de.saring.exerciseviewer.data.ExerciseAltitude;

public class AltitudeBuilder {
    private ExerciseAltitude altitude = new ExerciseAltitude();

    public ExerciseAltitude get() {
        return altitude;
    }

    public AltitudeBuilder avg(int avgValue) {
        altitude.setAltitudeAVG(toShort(avgValue));
        return this;
    }

    public AltitudeBuilder max(int maxValue) {
        altitude.setAltitudeMax(toShort(maxValue));
        return this;
    }

    public AltitudeBuilder min(int minValue) {
        altitude.setAltitudeMin(toShort(minValue));
        return this;
    }


}
