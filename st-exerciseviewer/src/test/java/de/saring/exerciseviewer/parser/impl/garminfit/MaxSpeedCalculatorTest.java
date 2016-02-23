package de.saring.exerciseviewer.parser.impl.garminfit;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.ExerciseSpeed;


@RunWith(Parameterized.class)
public class MaxSpeedCalculatorTest {

	private final float first;
	private final float second;
	private final float expected;
	
	public MaxSpeedCalculatorTest(float first, float second, float expected) {
		this.first = first;
		this.second = second;
		this.expected = expected;
	}
	
	   @Parameterized.Parameters
	   public static Collection<Float[]> testCases() {
	      return Arrays.asList(new Float[][] {
	    	  {0f,0f,0f},
	    	  {1f,0f,1f},
	    	  {0f,1f,1f},
	    	  {1f,1f,1f},
	      });
	   }
	
	@Test
	public void MaxSpeedCalculator_SetsMaxSpeed() {
		EVExercise exercise = new EVExercise();
		exercise.setSpeed(new ExerciseSpeed());
		
		ExerciseSummaryCalculator classUnderTest = new MaxSpeedCalculator(exercise);
				
		classUnderTest.visitSample(createSample(first));
		classUnderTest.visitSample(createSample(second));
		
		classUnderTest.finished();
		
		assertEquals(expected, exercise.getSpeed().getSpeedMax(), 0.01);
	}

	private ExerciseSample createSample(float speed) {
		ExerciseSample sample = new ExerciseSample();
		sample.setSpeed(speed);
		return sample;
	}

}
