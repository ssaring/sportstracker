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
public class MaxHeartRateCalculatorTest {

	private final short first;
	private final short second;
	private final short expected;
	
	public MaxHeartRateCalculatorTest(short first, short second, short expected) {
		this.first = first;
		this.second = second;
		this.expected = expected;
	}
	
	   @Parameterized.Parameters
	   public static Collection<Short[]> testCases() {
	      return Arrays.asList(new Short[][] {
	    	  {0,0,0},
	    	  {100,0,100},
	    	  {0,100,100},
	    	  {100,100,100},
	      });
	   }
	
	@Test
	public void maxHeartRateCalculator_SetsMaxHeartRate() {
		EVExercise exercise = new EVExercise();
		
		ExerciseSummaryAggregator classUnderTest = new MaxHeartRateCalculator(exercise);
				
		classUnderTest.collect(createSample(first));
		classUnderTest.collect(createSample(second));
		
		classUnderTest.aggregateAndUpdateExercise();
		
		assertThat(expected, is(exercise.getHeartRateMax()));
	}

	private ExerciseSample createSample(short heartRate) {
		ExerciseSample sample = new ExerciseSample();
		sample.setHeartRate(heartRate);
		return sample;
	}

}
