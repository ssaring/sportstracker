package de.saring.exerciseviewer.parser.testutil;

import de.saring.exerciseviewer.data.ExerciseSpeed;

public class SpeedBuilder {
    private ExerciseSpeed speed = new ExerciseSpeed();

    public SpeedBuilder max(float speedMax) {
        speed.setSpeedMax(speedMax);
        return this;
    }

    public ExerciseSpeed get() {
        return speed;
    }
    
    
}
