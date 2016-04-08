package de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.ExerciseSpeed;
import de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators.ExerciseSummaryAggregator;
import de.saring.exerciseviewer.parser.impl.garminfit.summaryCalculators.MaxSpeedCalculator;


@RunWith(Parameterized.class)
public class AVGHeartRateCalculatorTest {

	private final short first;
	private final short second;
	private final short expected;
	
	public AVGHeartRateCalculatorTest(short first, short second, short expected) {
		this.first = first;
		this.second = second;
		this.expected = expected;
	}
	
	   @Parameterized.Parameters
	   public static Collection<Short[]> testCases() {
	      return Arrays.asList(new Short[][] {
	    	  {0,0,0},
	    	  {100,0,50},
	    	  {0,100,50},
	    	  {100,100,100},
	      });
	   }
	
	@Test
	public void avgHeartRateCalculator_SetsAvgHeartRate() {
		EVExercise exercise = new EVExercise();
		exercise.setSampleList(new ExerciseSample[2]);
		
		ExerciseSummaryAggregator classUnderTest = new AVGHeartRateCalculator(exercise);
				
		classUnderTest.collect(createSample(first));
		classUnderTest.collect(createSample(second));
		
		classUnderTest.aggregateAndUpdateExercise();
		
		assertThat(expected, is(exercise.getHeartRateAVG()));
	}
	
	@Test
	public void avgHeartRateCalculator_doesNotThrow_whenSampleListIsNull() {
		EVExercise exercise = new EVExercise();
		
		ExerciseSummaryAggregator classUnderTest = new AVGHeartRateCalculator(exercise);
		classUnderTest.aggregateAndUpdateExercise();
	}

	private ExerciseSample createSample(short heartRate) {
		ExerciseSample sample = new ExerciseSample();
		sample.setHeartRate(heartRate);
		return sample;
	}

}
