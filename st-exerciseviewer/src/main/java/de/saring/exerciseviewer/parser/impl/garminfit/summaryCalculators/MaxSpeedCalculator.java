package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;

public class MaxSpeedCalculator implements ExerciseSummaryAggregator {

	private EVExercise exercise;
	private float max = 0;

	public MaxSpeedCalculator(EVExercise exercise) {
		this.exercise = exercise;
	}

	@Override
	public void collect(ExerciseSample sample) {
		if (sample.getSpeed() > max) {
			max = sample.getSpeed();
		}

	}

	@Override
	public void aggregateAndUpdateExercise() {
		if (exercise.getSpeed() != null) {
			exercise.getSpeed().setSpeedMax(max);
		}
	}

}
