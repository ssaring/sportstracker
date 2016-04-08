package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;

public class MaxHeartRateCalculator implements ExerciseSummaryAggregator {

	private EVExercise exercise;
	private short max = 0;

	public MaxHeartRateCalculator(EVExercise exercise) {
		this.exercise = exercise;
	}
	
	@Override
	public void collect(ExerciseSample sample) {
		if (max <= sample.getHeartRate()) {
			max = sample.getHeartRate();
		}
	}

	@Override
	public void aggregateAndUpdateExercise() {
		exercise.setHeartRateMax(max);
	}

}
