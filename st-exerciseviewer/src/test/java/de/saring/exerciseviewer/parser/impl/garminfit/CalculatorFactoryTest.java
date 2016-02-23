package de.saring.exerciseviewer.parser.impl.garminfit;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsNot.not;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSpeed;

public class CalculatorFactoryTest {

	@Test
	public void createCalculators_CreatesMaxSpeedCalculator_WhenMaxSpeedIs0() {
		CalculatorFactory classUnderTest = new CalculatorFactory();
		EVExercise exercise = createExersize();

		assertThat(classUnderTest.createCalculators(exercise), hasItem(isA(MaxSpeedCalculator.class)));
	}

	@Test
	public void createCalculators_DoesNotCreateMaxSpeedCalculator_WhenMaxSpeedIsGreater0() {
		CalculatorFactory classUnderTest = new CalculatorFactory();
		EVExercise exercise = createExersize();
		exercise.getSpeed().setSpeedMax(123);

		assertThat(classUnderTest.createCalculators(exercise), not(hasItem(isA(MaxSpeedCalculator.class))));
	}
	
	private EVExercise createExersize() {
		EVExercise exercise = new EVExercise();
		exercise.setSpeed(new ExerciseSpeed());
		return exercise;
	}

}
