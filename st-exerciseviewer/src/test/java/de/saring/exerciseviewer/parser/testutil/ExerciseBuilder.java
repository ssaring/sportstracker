package de.saring.exerciseviewer.parser.testutil;

import static de.saring.exerciseviewer.parser.testutil.Utils.toShort;

import java.util.function.Consumer;

import de.saring.exerciseviewer.data.EVExercise;

public class ExerciseBuilder {
    private EVExercise exercise = new EVExercise();
    private AltitudeBuilder altitudeBuilder;
    private SpeedBuilder speedBuilder;
    private RecordingModeBuilder recordingModeBuilder = new RecordingModeBuilder();

    public static ExerciseBuilder createExercise() {
        return new ExerciseBuilder();
    }

    public ExerciseBuilder WithAltitude(Consumer<AltitudeBuilder> buildAltitude) {
        altitudeBuilder = new AltitudeBuilder();
        buildAltitude.accept(altitudeBuilder);
        return this;
    }

    public EVExercise get() {
        if (altitudeBuilder != null) {
            exercise.setAltitude(altitudeBuilder.get());
        }
        if (speedBuilder != null) {
            exercise.setSpeed(speedBuilder.get());
        }
        exercise.setRecordingMode(recordingModeBuilder.get());
        return exercise;
    }

    public ExerciseBuilder WithHeartRateAVG(int heartRateAVG) {
        exercise.setHeartRateAVG(toShort(heartRateAVG));
        return this;
    }

    public ExerciseBuilder withHeartRateMax(int heartRateMax) {
        exercise.setHeartRateMax(toShort(heartRateMax));
        return this;
    }

    public ExerciseBuilder withSpeed(Consumer<SpeedBuilder> buildSpeed) {
        speedBuilder = new SpeedBuilder();
        buildSpeed.accept(speedBuilder);
        return this;
    }

    public ExerciseBuilder WithRecordingMode(Consumer<RecordingModeBuilder> buildRecordingMode) {
        recordingModeBuilder = new RecordingModeBuilder();
        buildRecordingMode.accept(recordingModeBuilder);
        return this;
    }
}
