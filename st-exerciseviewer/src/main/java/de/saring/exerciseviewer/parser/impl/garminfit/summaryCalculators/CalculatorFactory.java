package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import java.util.Collection;
import java.util.LinkedList;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSpeed;

public class CalculatorFactory {

	public Collection<ExerciseSummaryAggregator> createCalculators(EVExercise exercise) {
		Collection<ExerciseSummaryAggregator> result = new LinkedList<>();

		ExerciseSpeed speed = exercise.getSpeed();
		if (speed != null) {
			if (speed.getSpeedMax() < 0.01) {
				result.add(new MaxSpeedCalculator(exercise));
			}
		}
		
		if (exercise.getHeartRateMax() == 0) {
			result.add(new MaxHeartRateCalculator(exercise));
		}
		if (exercise.getHeartRateAVG() == 0) {
			result.add(new AVGHeartRateCalculator(exercise));
		}
		return result;
	}
}
