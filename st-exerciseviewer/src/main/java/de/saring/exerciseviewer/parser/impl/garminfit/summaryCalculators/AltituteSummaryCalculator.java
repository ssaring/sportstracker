package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import java.util.Arrays;
import java.util.OptionalDouble;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseAltitude;
import de.saring.exerciseviewer.data.ExerciseSample;

public class AltituteSummaryCalculator implements ExerciseSummaryAggregator {

	private EVExercise exercise;
	private short altMin = Short.MAX_VALUE;
	private short altMax= Short.MIN_VALUE;

    public AltituteSummaryCalculator(EVExercise exercise) {
        this.exercise = exercise;
    }

    @Override
	public void collect(ExerciseSample sample) {
		if (sample.getAltitude() < altMin) {
		    altMin = sample.getAltitude();
		}
		if (sample.getAltitude() > altMax) {
		    altMax = sample.getAltitude();
		}
	}

	@Override
	public void aggregateAndUpdateExercise() {
	    if (exercise.getAltitude() == null) {
	        exercise.setAltitude(new ExerciseAltitude());
	    }
	    exercise.getAltitude().setAltitudeMin(altMin);
	    exercise.getAltitude().setAltitudeMax(altMax);
	    updateAltitudeAvg();
	}

    private void updateAltitudeAvg() {
        OptionalDouble altAvg = Arrays.asList(exercise.getSampleList()).stream()
	            .mapToDouble(s -> s.getAltitude()).average();
	    altAvg.ifPresent(this::setAvgAltitude);
    }

    private void setAvgAltitude(double value) {
        exercise.getAltitude().setAltitudeAVG((short) Math.round(value));
    }

}
