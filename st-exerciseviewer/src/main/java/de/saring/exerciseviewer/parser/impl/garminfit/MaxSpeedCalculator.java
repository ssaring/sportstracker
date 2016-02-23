package de.saring.exerciseviewer.parser.impl.garminfit;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;

public class MaxSpeedCalculator implements ExerciseSummaryCalculator {

	private EVExercise exercise;
	private float max = 0;

	public MaxSpeedCalculator(EVExercise exercise) {
		this.exercise = exercise;
	}

	@Override
	public void visitSample(ExerciseSample sample) {
		if (sample.getSpeed() > max) {
			max = sample.getSpeed();
		}

	}

	@Override
	public void finished() {
		if (exercise.getSpeed() != null) {
			exercise.getSpeed().setSpeedMax(max);
		}
	}

}
