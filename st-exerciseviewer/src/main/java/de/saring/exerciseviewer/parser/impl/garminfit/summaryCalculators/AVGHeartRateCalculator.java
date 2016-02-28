package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;

public class AVGHeartRateCalculator implements ExerciseSummaryAggregator{

	private EVExercise exercise;
	private int heartRateSum = 0;

	public AVGHeartRateCalculator(EVExercise exercise) {
		this.exercise = exercise;
	}

	@Override
	public void collect(ExerciseSample sample) {
		heartRateSum += sample.getHeartRate();
	}

	@Override
	public void aggregateAndUpdateExercise() {
		if (exercise.getSampleList() != null) {
			short heartRateAvg = (short) Math.round(heartRateSum /exercise.getSampleList().length);
			exercise.setHeartRateAVG(heartRateAvg);
		}
	}

}
