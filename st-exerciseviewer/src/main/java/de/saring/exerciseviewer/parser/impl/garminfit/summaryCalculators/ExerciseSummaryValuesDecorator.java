package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import java.util.Collection;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;

public class ExerciseSummaryValuesDecorator {
	
	CalculatorFactory factory = new CalculatorFactory();

	public void addMissingSummaries(EVExercise exercise) {
		Collection<ExerciseSummaryAggregator> calculators = factory.createCalculators(exercise);
		
		for(ExerciseSample sample : exercise.getSampleList()) {
			calculators.forEach(calc -> calc.collect(sample));
		}
		calculators.forEach(calc -> calc.aggregateAndUpdateExercise());
	}
}
