package de.saring.exerciseviewer.parser.impl.garminfit;

import de.saring.exerciseviewer.data.ExerciseSample;

public interface ExerciseSummaryCalculator {
	void visitSample(ExerciseSample sample);
	void finished();
}
