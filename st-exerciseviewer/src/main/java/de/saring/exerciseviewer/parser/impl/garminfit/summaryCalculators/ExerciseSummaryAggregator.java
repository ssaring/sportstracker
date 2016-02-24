package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import de.saring.exerciseviewer.data.ExerciseSample;

public interface ExerciseSummaryAggregator {
	void collect(ExerciseSample sample);
	void aggregateAndUpdateExercise();
}
