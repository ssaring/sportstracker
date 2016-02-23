package de.saring.exerciseviewer.parser.impl.garminfit;

import java.util.Collection;
import java.util.LinkedList;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSpeed;

public class CalculatorFactory {

	public Collection<ExerciseSummaryCalculator> createCalculators(EVExercise exercise) {
		Collection<ExerciseSummaryCalculator> result = new LinkedList<>();

		ExerciseSpeed speed = exercise.getSpeed();
		if (speed != null) {
			if (speed.getSpeedMax() < 0.01) {
				result.add(new MaxSpeedCalculator(exercise));
			}
		}
		return result;
	}
}
