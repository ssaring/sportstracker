package de.saring.exerciseviewer.parser.impl.garminfit;

import java.util.Collection;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;

public class ExerciseSummaryValuesDecorator {
	
	CalculatorFactory factory = new CalculatorFactory();

	public void addMissingSummaries(EVExercise exercise) {
		Collection<ExerciseSummaryCalculator> calculators = factory.createCalculators(exercise);
		
		for(ExerciseSample sample : exercise.getSampleList()) {
			calculators.forEach(calc -> calc.visitSample(sample));
		}
		calculators.forEach(calc -> calc.finished());
	}
}
